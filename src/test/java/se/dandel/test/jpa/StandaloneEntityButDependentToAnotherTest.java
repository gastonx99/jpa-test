package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.dao.EmployeeDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class StandaloneEntityButDependentToAnotherTest {
	@Rule
	@JpaTestConfig
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private DepartmentDAO departmentDAO;
	@Inject
	private EmployeeDAO employeeDAO;
	private DepartmentEO department;

	@Before
	public void createDepartment() {
		department = departmentDAO.create("A department");
	}

	@Test
	public void crud() {
		EmployeeEO employee = employeeDAO.create("A employee", department);

		mgr.reset();

		assertEquals(1, employeeDAO.findAll().size());

		mgr.reset();

		EmployeeEO found = employeeDAO.get(employee.getId());
		assertNotSame(employee, found);
		assertEquals(employee.getId(), found.getId());

		mgr.reset();

		String newName = "Another employee";
		employeeDAO.update(employee.getId(), newName);

		mgr.reset();

		found = employeeDAO.get(employee.getId());
		assertEquals(newName, found.getName());

		mgr.reset();
		employeeDAO.delete(employee.getId());

		mgr.reset();

		assertEquals(0, employeeDAO.findAll().size());
	}

}
