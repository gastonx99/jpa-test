package se.dandel.test.jpa.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import javax.persistence.Column;
import javax.persistence.PersistenceException;

import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Inject;

import se.dandel.test.jpa.car.dao.CarDAO;
import se.dandel.test.jpa.car.domain.CarEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

public class SingleEntityTest {

	@Rule
	@IntegrationJpaTestConfig
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private CarDAO dao;

	@Test
	public void crud() {
		CarEO car = dao.create("A car");

		mgr.reset();

		assertEquals(1, dao.findAll().size());

		mgr.reset();

		CarEO found = dao.get(car.getId());
		assertNotSame(car, found);
		assertEquals(car.getId(), found.getId());

		mgr.reset();

		String newName = "Another car";
		dao.update(car.getId(), newName);

		mgr.reset();

		found = dao.get(car.getId());
		assertEquals(newName, found.getName());

		mgr.reset();
		dao.delete(car.getId());

		mgr.reset();

		assertEquals(0, dao.findAll().size());
	}

	@Test(expected = PersistenceException.class)
	public void nameUniqueness() throws Exception {
		Column annotation = CarEO.class.getDeclaredField("name").getAnnotation(Column.class);
		annotation.unique();
		dao.create("A car");
		dao.create("A car");
		mgr.reset();
	}
}
