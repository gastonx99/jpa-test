package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.List;

import javax.persistence.PersistenceUnit;

import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;

@PersistenceUnit(unitName = "persistenceUnit-hsqldb")
public class OneToManySlavesTest extends AbstractJpaTest {

	private DepartmentDAO dao;

	@Test
	public void crudWithChildren() {
		String name = "A department";
		DepartmentEO department = new DepartmentEO();
		department.setName(name);
		dao.persist(department);

		reset();

		List<DepartmentEO> list = dao.findAll();
		assertEquals(1, list.size());
		DepartmentEO found = list.iterator().next();
		assertNotSame(department, found);
		assertEquals(department.getId(), found.getId());

		reset();

		String newName = "Another department";
		dao.update(department.getId(), newName);

		reset();

		list = dao.findAll();
		found = list.iterator().next();
		assertEquals(newName, found.getName());

		reset();
		dao.delete(department.getId());

		reset();

		assertEquals(0, dao.findAll().size());
	}

	@Override
	protected void setupDaos() {
		dao = injector.getInstance(DepartmentDAO.class);
	}
}
