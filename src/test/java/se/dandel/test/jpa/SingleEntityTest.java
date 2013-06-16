package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;

import com.google.inject.Inject;

public class SingleEntityTest {
	@Rule
	@GuiceJpaLiquibaseManager.Config(modules = GuiceModule.class, persistenceUnitName = "persistenceUnit-hsqldb")
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private DepartmentDAO dao;

	@Test
	public void crud() {
		DepartmentEO department = dao.create("A department");

		mgr.reset();

		assertEquals(1, dao.findAll().size());

		mgr.reset();

		DepartmentEO found = dao.get(department.getId());
		assertNotSame(department, found);
		assertEquals(department.getId(), found.getId());

		mgr.reset();

		String newName = "Another department";
		dao.update(department.getId(), newName);

		mgr.reset();

		found = dao.get(department.getId());
		assertEquals(newName, found.getName());

		mgr.reset();
		dao.delete(department.getId());

		mgr.reset();

		assertEquals(0, dao.findAll().size());
	}

}
