package se.dandel.test.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.rules.MethodRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
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

	protected Injector injector;
	private List<Module> modules;
	private Boolean sqlExplorer;

	public GuiceJpaLiquibaseManager() {
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
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				logger.debug("Before evaluating base statement");
				GuiceJpaLiquibaseManager.this.target = target;
				before();
				base.evaluate();
				after();
				logger.debug("After evaluating base statement");
			}

		};
	}

	@Deprecated
	public Statement apply(final Statement base, Description description) {

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				logger.debug("Before evaluating base statement");
				before();
				base.evaluate();
				after();
				logger.debug("After evaluating base statement");
			}

		};
	}

	protected void before() {
		startupFactory();
		createAndBegin();
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
		factory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
	}

	private String getPersistenceUnitName() {
		if (persistenceUnitName == null) {
			persistenceUnitName = getConfig().persistenceUnitName();
		}
		return persistenceUnitName;
	}

	private boolean isSqlExplorerEnabled() {
		if (sqlExplorer == null) {
			sqlExplorer = getConfig().sqlExplorer();
		}
		return sqlExplorer;
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
			try {
				factory.close();
			} catch (Throwable t) {
				// Silent
			}
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
		injector = Guice.createInjector(createModules());
		injector.injectMembers(target);
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
				tx.commit();
			} catch (Throwable t) {
				// Silent
			}
			tx = null;
		}
		if (em != null) {
			try {
				em.close();
			} catch (Throwable t) {
				// Silent
			}
			em = null;
		}
	}

	protected void after() {
		commitAndClose();
		closeFactory();
	}

}
