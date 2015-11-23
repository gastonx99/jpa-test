package se.dandel.test.jpa.department.guice;

import com.google.inject.AbstractModule;

import se.dandel.test.jpa.department.dao.DepartmentDAO;
import se.dandel.test.jpa.department.dao.DepartmentDAOImpl;
import se.dandel.test.jpa.department.dao.EmployeeDAO;
import se.dandel.test.jpa.department.dao.EmployeeDAOImpl;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DepartmentDAO.class).to(DepartmentDAOImpl.class);
		bind(EmployeeDAO.class).to(EmployeeDAOImpl.class);
		// bind(EntityManager.class).toProvider(EntityManagerProvider.class);
	}

}
