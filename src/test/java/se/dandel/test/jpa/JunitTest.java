package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;

import com.google.inject.Inject;

public class JunitTest {
	private static final Logger logger = Logger.getLogger(JunitTest.class);

	@Rule
	@GuiceJpaLiquibaseManager.Config(modules = GuiceModule.class, persistenceUnitName = "persistenceUnit-hsqldb")
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
	public void dummy1() {
		logger.debug("Dummy1");
		dao.create("A department");
		mgr.reset();
		assertEquals(1, dao.findAll().size());
	}

	@Test
	public void dummy2() {
		logger.debug("Dummy2");
		dao.create("Another department");
		mgr.reset();
		List<DepartmentEO> list = dao.findAll();
		assertEquals(1, list.size());
		assertEquals("Another department", list.iterator().next().getName());
	}

}
