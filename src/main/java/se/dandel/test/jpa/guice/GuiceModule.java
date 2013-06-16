package se.dandel.test.jpa.guice;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.dao.DepartmentDAOImpl;
import se.dandel.test.jpa.dao.EmployeeDAO;
import se.dandel.test.jpa.dao.EmployeeDAOImpl;

import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DepartmentDAO.class).to(DepartmentDAOImpl.class);
		bind(EmployeeDAO.class).to(EmployeeDAOImpl.class);
		// bind(EntityManager.class).toProvider(EntityManagerProvider.class);
	}

}
