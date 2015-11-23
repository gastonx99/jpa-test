package se.dandel.test.jpa.car.guice;

import com.google.inject.AbstractModule;

import se.dandel.test.jpa.car.dao.CarDAO;
import se.dandel.test.jpa.car.dao.CarDAOImpl;

public class CarGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CarDAO.class).to(CarDAOImpl.class);
		// bind(EntityManager.class).toProvider(EntityManagerProvider.class);
	}

}
