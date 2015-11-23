package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Inject;

import se.dandel.test.jpa.department.dao.DepartmentDAO;
import se.dandel.test.jpa.department.dao.EmployeeDAO;
import se.dandel.test.jpa.department.domain.DepartmentEO;
import se.dandel.test.jpa.department.domain.EmployeeEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

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
		EmployeeEO employee = new EmployeeEO();
		employee.setDepartment(department);
		employee.setName("A employee");
		employeeDAO.persist(employee);

		mgr.reset();

		assertEquals(1, employeeDAO.findAll().size());

		mgr.reset();

		EmployeeEO found = employeeDAO.get(employee.getId());
		assertNotSame(employee, found);
		assertEquals(employee.getId(), found.getId());

		mgr.reset();

		String newName = "Another employee";
		employee = employeeDAO.get(employee.getId());
		employee.setName(newName);

		mgr.reset();

		found = employeeDAO.get(employee.getId());
		assertEquals(newName, found.getName());

		mgr.reset();
		employeeDAO.delete(employee.getId());

		mgr.reset();

		assertEquals(0, employeeDAO.findAll().size());
	}

}
