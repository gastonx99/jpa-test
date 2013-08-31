package se.dandel.test.jpa.junit;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.apache.log4j.Logger;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;

public class GuiceJpaLiquibaseManager implements MethodRule {

	private static final String DATABASE_ALREADY_CREATED_TABLENAME = "DATABASE_ALREADY_CREATED_TABLE";
	private static final String DATABASE_ALREADY_CREATED_TABLE_SQL = "create table "
			+ DATABASE_ALREADY_CREATED_TABLENAME + " (a integer)";

	public enum DdlGeneration {
		DROP_CREATE, NONE, LIQUIBASE;
	}

	@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Config {

		Class<? extends Module>[] modules();

		String persistenceUnitName();

		boolean sqlExplorer() default false;

		DdlGeneration ddlGeneration() default DdlGeneration.DROP_CREATE;

		String liquibaseChangelog() default "";

	}

	private static final Logger logger = Logger.getLogger(GuiceJpaLiquibaseManager.class);
	private String persistenceUnitName;
	private EntityManagerFactory factory;
	protected EntityManager em;
	protected EntityTransaction tx;

	protected Object target;

	private List<Module> modules;
	private Connection connection;
	private Liquibase liquibase;
	private Boolean databaseAlreadyCreated;
	private boolean dropBetweenExecutions = false;
	private List<String> deleteFromTableStatements;

	public GuiceJpaLiquibaseManager() {
		logger.debug("instantiating");
	}

	public void reset() {
		logger.debug("Resetting");
		commitAndClose();
		createAndBegin();
	}

	public EntityManager getEntityManager() {
		return em;
	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
		logger.debug("Method " + method.getName());
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				GuiceJpaLiquibaseManager.this.target = target;
				before();
				logger.debug("Before evaluating base statement");
				List<Throwable> throwables = new ArrayList<Throwable>();
				try {
					base.evaluate();
					logger.debug("After evaluating base statement");
				} catch (Throwable t) {
					throwables.add(t);
				} finally {
					try {
						after();
					} catch (Throwable t) {
						throwables.add(t);
					}
				}
				if (!throwables.isEmpty()) {
					if (throwables.size() == 1) {
						throw throwables.get(0);
					} else {
						// Currently disregarding the exception thrown in the
						// after() method
						throw throwables.get(0);
					}
				}
			}

		};
	}

	protected void before() {
		startupFactory();
		createAndBegin();
		if (DdlGeneration.LIQUIBASE.equals(getConfig().ddlGeneration())) {
			if (!isDatabaseAlreadyCreated()) {
				liquibaseCreateDatabase();
				createDatabaseAlreadyCreatedTable();
			}
		}
		if (isSqlExplorerEnabled()) {
			openSqlExplorer();
		}
	}

	private void createDatabaseAlreadyCreatedTable() {
		java.sql.Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(DATABASE_ALREADY_CREATED_TABLE_SQL);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				// fall through
			}
		}
	}

	private boolean isDatabaseAlreadyCreated() {
		if (databaseAlreadyCreated != null) {
			return databaseAlreadyCreated;
		}
		ResultSet tables = null;
		try {
			logger.debug("Fetching metadata to check if database is already created");
			tables = connection.getMetaData().getTables(null, null, DATABASE_ALREADY_CREATED_TABLENAME, null);
			logger.debug("Fetced metadata");
			databaseAlreadyCreated = tables.next();
			return databaseAlreadyCreated;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				tables.close();
			} catch (Exception e) {
				// fall through
			}
		}
	}

	private void liquibaseCreateDatabase() {
		logger.debug("Creating database with liquibase");
		try {
			Liquibase liquibase = getLiquibase();
			liquibase.update(null);
			logger.debug("Database created");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void liquibaseDropDatabase() {
		logger.debug("Dropping database with liquibase");
		try {
			getLiquibase().dropAll();
			logger.debug("Database dropped");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Liquibase getLiquibase() throws LiquibaseException {
		if (liquibase == null) {
			JdbcConnection c = new JdbcConnection(connection);
			liquibase = new Liquibase("change-log.xml", new ClassLoaderResourceAccessor(), c);
		}
		return liquibase;
	}

	private void openSqlExplorer() {
		Map<String, Object> properties = em.getProperties();
		String url = (String) properties.get("javax.persistence.jdbc.url");
		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url", url, "--user", "", "--noexit" });
	}

	private void startupFactory() {
		logger.debug("Starting factory");
		Map<String, String> props = new HashMap<String, String>();
		String ddlGeneration = "none";
		switch (getConfig().ddlGeneration()) {
		case DROP_CREATE:
			ddlGeneration = "drop-and-create-tables";
			break;
		}
		props.put("eclipselink.ddl-generation", ddlGeneration);
		factory = Persistence.createEntityManagerFactory(getPersistenceUnitName(), props);
		logger.debug("Factory started");
	}

	private String getPersistenceUnitName() {
		if (persistenceUnitName == null) {
			persistenceUnitName = getConfig().persistenceUnitName();
		}
		return persistenceUnitName;
	}

	private boolean isSqlExplorerEnabled() {
		return getConfig().sqlExplorer();
	}

	private List<Module> getModules() {
		try {
			if (modules == null) {
				List<Module> list = new ArrayList<Module>();
				for (Class<? extends Module> moduleClazz : getConfig().modules()) {
					list.add(moduleClazz.newInstance());
				}
				modules = list;
			}
			return modules;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Config getConfig() {
		Field f = getConfigField();
		if (f == null) {
			throw new IllegalArgumentException("A " + getClass().getName() + " field must exist and be annotated with "
					+ Config.class.getName());
		}
		Config annotation = f.getAnnotation(Config.class);
		if (annotation == null) {
			Annotation[] annotations = f.getAnnotations();
			for (Annotation a : annotations) {
				Annotation[] tmp = a.annotationType().getAnnotations();
				for (Annotation atmp : tmp) {
					if (atmp.annotationType().equals(Config.class)) {
						annotation = (Config) atmp;
						break;
					}
				}
			}
		}
		if (annotation == null) {
			throw new IllegalArgumentException("Field " + f.getName() + " must be annoted with "
					+ Config.class.getName());
		}
		return annotation;
	}

	private Field getConfigField() {
		try {
			Field[] fields = target.getClass().getFields();
			Field f = null;
			for (Field field : fields) {
				if (field.get(target) == this) {
					f = field;
					break;
				}
			}
			return f;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void closeFactory() {
		logger.debug("Closing factory");
		if (factory != null) {
			factory.close();
			factory = null;
		}
	}

	private void createAndBegin() {
		logger.debug("Create and begin");
		if (em != null || tx != null) {
			throw new IllegalStateException("Manager " + em + " and tx " + tx + " should all be null");
		}
		em = factory.createEntityManager();
		logger.debug("Factory created");
		tx = em.getTransaction();
		tx.begin();
		connection = em.unwrap(Connection.class);
		Guice.createInjector(createModules()).injectMembers(target);
	}

	private Module[] createModules() {
		List<Module> list = new ArrayList<Module>(getModules());
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(EntityManager.class).toInstance(em);
			}
		};
		list.add(module);
		return list.toArray(new Module[0]);
	}

	private void commitAndClose() {
		logger.debug("Commit and close");
		if (tx != null) {
			try {
				if (!tx.getRollbackOnly()) {
					tx.commit();
				}
			} finally {
				tx = null;
			}
		}
		if (em != null) {
			try {
				em.close();
			} finally {
				em = null;
			}
		}
	}

	protected void after() {
		commitAndClose();
		if (DdlGeneration.LIQUIBASE.equals(getConfig().ddlGeneration())) {
			if (dropBetweenExecutions) {
				liquibaseDropDatabase();
			} else {
				deleteFromAllTables();
			}
		}
		closeFactory();
		liquibase = null;
	}

	private void deleteFromAllTables() {
		logger.debug("Deleting from all tables");
		java.sql.Statement statement = null;
		try {
			statement = connection.createStatement();
			deleteFromTables(statement, getDeleteFromTableStatements(), 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				// fall through
			}
		}
		logger.debug("Deleted from all tables");
	}

	private void deleteFromTables(java.sql.Statement statement, List<String> stmts, int statementsExecuted) {
		List<String> goodStatements = new ArrayList<String>();
		try {
			for (String str : stmts) {
				logger.debug("Executing " + str);
				statement.execute(str);
				connection.commit();
				goodStatements.add(str);
			}
		} catch (Exception e) {
			int newStatementsExecuted = goodStatements.size();
			if (goodStatements.size() == statementsExecuted) {
				throw new RuntimeException(e);
			}
			String badStatement = stmts.get(goodStatements.size());
			goodStatements.addAll(stmts.subList(goodStatements.size() + 1, stmts.size()));
			goodStatements.add(badStatement);
			deleteFromTableStatements = goodStatements;
			deleteFromTables(statement, goodStatements, newStatementsExecuted);
		}
	}

	private List<String> getDeleteFromTableStatements() {
		deleteFromTableStatements = new ArrayList<String>();
		ResultSet tables = null;
		try {
			logger.debug("Fetching metadata to check if database is already created");
			tables = connection.getMetaData().getTables(null, null, null, null);
			while (tables.next()) {
				String tableType = tables.getString("TABLE_TYPE");
				if ("TABLE".equals(tableType)) {
					deleteFromTableStatements.add("delete from " + tables.getString("TABLE_NAME"));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				tables.close();
			} catch (Exception e) {
				// fall through
			}
		}

		return deleteFromTableStatements;
	}

}
