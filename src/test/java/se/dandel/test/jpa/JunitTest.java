package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class JunitTest {
	private static final Logger logger = Logger.getLogger(JunitTest.class);

	@Rule
	@JpaTestConfig
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private DepartmentDAO dao;

	@Before
	public void before() {
		logger.debug("Before");
	}

	@After
	public void after() {
		logger.debug("After");
	}

	@Test
	public void isolationBetweenTests1() {
		logger.debug("isolationBetweenTests1");
		dao.persist(new DepartmentEO("A name"));
		mgr.reset();
		assertEquals(1, dao.findAll().size());
	}

	@Test
	public void isolationBetweenTests2() {
		logger.debug("isolationBetweenTests1");
		dao.persist(new DepartmentEO("Another name"));
		mgr.reset();
		assertEquals(1, dao.findAll().size());
	}

	@Test
	public void attached() {
		DepartmentEO eo = new DepartmentEO("A name");
		dao.persist(eo);
		mgr.reset();
		eo = dao.get(eo.getId());
		assertEquals("A name", eo.getName());
		eo.setName("Another name");
		mgr.reset();
		eo = dao.get(eo.getId());
		assertEquals("Another name", eo.getName());
	}

	@Test
	public void detachedAfterReset() {
		DepartmentEO eo = new DepartmentEO("A name");
		dao.persist(eo);
		mgr.reset();
		eo.setName("Another name");
		mgr.reset();
		assertEquals("A name", dao.get(eo.getId()).getName());
	}

	@Test
	public void instanceAttachmentDetachment() {
		DepartmentEO eo = new DepartmentEO("A name");
		dao.persist(eo);
		assertTrue(eo == dao.get(eo.getId()));
		mgr.reset();
		assertFalse(eo == dao.get(eo.getId()));
	}

}
