package se.dandel.test.jpa.junit;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import se.dandel.test.jpa.guice.GuiceModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class AbstractJpaTest {
	protected final Logger logger = Logger.getLogger(getClass());

	protected EntityManager em;
	protected EntityTransaction tx;

	protected EntityManagerFactory factory;

	protected boolean sqlExplorer;

	protected Injector injector;

	private String persistenceUnitName;

	protected AbstractJpaTest() {
		persistenceUnitName = getClass().getAnnotation(PersistenceUnit.class).unitName();
	}

	@Before
	public void before() {
		startupFactory();
		createAndBegin();
		if (sqlExplorer) {
			openSqlExplorer();
		}
	}

	private void openSqlExplorer() {
		Map<String, Object> properties = em.getProperties();
		String url = (String) properties.get("javax.persistence.jdbc.url");
		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url", url, "--user", "", "--noexit" });
	}

	@After
	public void after() throws Exception {
		commitAndClose();
		closeFactory();
	}

	protected void reset() {
		logger.debug("Resetting");
		commitAndClose();
		createAndBegin();
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
		injector = Guice.createInjector(module, new GuiceModule());
		setupDaos();
	}

	protected abstract void setupDaos();

	private void startupFactory() {
		logger.debug("Starting factory");
		factory = Persistence.createEntityManagerFactory(persistenceUnitName);
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

}
