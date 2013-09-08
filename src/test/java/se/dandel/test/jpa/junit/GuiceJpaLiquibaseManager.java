package se.dandel.test.jpa.junit;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import se.dandel.test.jpa.junit.beforeafter.DatabaseCreator;
import se.dandel.test.jpa.junit.beforeafter.DatabaseCreatorBeforeAfterContext;
import se.dandel.test.jpa.junit.beforeafter.Dataloader;
import se.dandel.test.jpa.junit.beforeafter.DataloaderBeforeAfterContext;
import se.dandel.test.jpa.junit.beforeafter.Guicer;
import se.dandel.test.jpa.junit.beforeafter.GuicerBeforeAfterContext;

import com.google.inject.Module;

public class GuiceJpaLiquibaseManager implements MethodRule {

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

	private Config config;

	protected Object target;

	private Connection connection;
	private Guicer guicer;
	private Dataloader dataloader;
	private DatabaseCreator databaseCreator;

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
		this.target = target;
		config = loadConfig();
		guicer = new Guicer(config.modules(), target);
		dataloader = new Dataloader();
		databaseCreator = new DatabaseCreator(DdlGeneration.LIQUIBASE.equals(config.ddlGeneration()));

		logger.debug("Method " + method.getName());
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
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

		databaseCreator.before(DatabaseCreatorBeforeAfterContext.of(connection));
		if (isSqlExplorerEnabled()) {
			openSqlExplorer();
		}
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
		switch (config.ddlGeneration()) {
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
			persistenceUnitName = config.persistenceUnitName();
		}
		return persistenceUnitName;
	}

	private boolean isSqlExplorerEnabled() {
		return config.sqlExplorer();
	}

	private Config loadConfig() {
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

		guicer.before(GuicerBeforeAfterContext.of(em));
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
		dataloader.after(DataloaderBeforeAfterContext.of(connection));
		databaseCreator.after(DatabaseCreatorBeforeAfterContext.of(connection));
		closeFactory();
	}

}
