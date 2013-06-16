package se.dandel.test.jpa.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
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
	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Config {

		Class<? extends Module>[] modules();

		String persistenceUnitName();

		boolean sqlExplorer() default false;

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
				try {
					base.evaluate();
					logger.debug("After evaluating base statement");
				} finally {
					after();
				}
			}

		};
	}

	protected void before() {
		startupFactory();
		createAndBegin();
		liquibaseCreateDatabase();
		if (isSqlExplorerEnabled()) {
			openSqlExplorer();
		}
	}

	private void liquibaseCreateDatabase() {
		try {
			Liquibase liquibase = getLiquibase();
			liquibase.update(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void liquibaseDropDatabase() {
		try {
			getLiquibase().dropAll();
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
		factory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
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
		liquibaseDropDatabase();
		commitAndClose();
		closeFactory();
		liquibase = null;
	}

}
