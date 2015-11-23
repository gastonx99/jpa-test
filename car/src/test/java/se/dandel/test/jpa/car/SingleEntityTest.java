package se.dandel.test.jpa.car;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import javax.persistence.Column;
import javax.persistence.PersistenceException;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.car.dao.CarDAO;
import se.dandel.test.jpa.car.domain.CarEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class SingleEntityTest {

	@Rule
	@CarJpaTestConfig
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private CarDAO dao;

	@Test
	public void crud() {
		CarEO department = dao.create("A department");

		mgr.reset();

		assertEquals(1, dao.findAll().size());

		mgr.reset();

		CarEO found = dao.get(department.getId());
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

	@Test(expected = PersistenceException.class)
	public void nameUniqueness() throws Exception {
		Column annotation = CarEO.class.getDeclaredField("name").getAnnotation(Column.class);
		annotation.unique();
		dao.create("A department");
		dao.create("A department");
		mgr.reset();
	}
}
