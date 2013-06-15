package se.dandel.test.jpa;

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

public class EmManager implements MethodRule {

	private static final Logger logger = Logger.getLogger(EmManager.class);
	private String persistenceUnitName;
	private EntityManagerFactory factory;
	protected EntityManager em;
	protected EntityTransaction tx;

	protected Injector injector;

	public EmManager(String persistenceUnit) {
		this.persistenceUnitName = persistenceUnit;
	}

	public void reset() {
		logger.debug("Resetting");
	}

	public EntityManager em() {
		return em;
	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
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
	}

	private void startupFactory() {
		logger.debug("Starting factory");
		factory = Persistence.createEntityManagerFactory(persistenceUnitName);
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
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(EntityManager.class).toInstance(em);
			}
		};
		injector = Guice.createInjector(module);
		// setupDaos();
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
