package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import javax.persistence.PersistenceUnit;

import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;

@PersistenceUnit(unitName = "persistenceUnit-hsqldb")
public class SingleEntityTest extends AbstractJpaTest {

	private DepartmentDAO dao;

	@Test
	public void crud() {
		DepartmentEO department = dao.create("A department");

		reset();

		assertEquals(1, dao.findAll().size());

		reset();

		DepartmentEO found = dao.get(department.getId());
		assertNotSame(department, found);
		assertEquals(department.getId(), found.getId());

		reset();

		String newName = "Another department";
		dao.update(department.getId(), newName);

		reset();

		found = dao.get(department.getId());
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
