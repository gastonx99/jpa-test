package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.guice.GuiceModule;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class OneToManySlavesTest {

	@Rule
	@GuiceJpaLiquibaseManager.Config(modules = GuiceModule.class, persistenceUnitName = "persistenceUnit-hsqldb")
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private DepartmentDAO dao;

	@Test
	public void crudWithChildren() {
		String name = "A department";
		DepartmentEO department = new DepartmentEO();
		department.setName(name);
		dao.persist(department);

		mgr.reset();

		List<DepartmentEO> list = dao.findAll();
		assertEquals(1, list.size());
		DepartmentEO found = list.iterator().next();
		assertNotSame(department, found);
		assertEquals(department.getId(), found.getId());

		mgr.reset();

		String newName = "Another department";
		dao.update(department.getId(), newName);

		mgr.reset();

		list = dao.findAll();
		found = list.iterator().next();
		assertEquals(newName, found.getName());

		mgr.reset();
		dao.delete(department.getId());

		mgr.reset();

		assertEquals(0, dao.findAll().size());
	}

}
