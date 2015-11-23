package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.Inject;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.dao.EmployeeDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

public class EntityWithChildrenTest {

	@Rule
	@JpaTestConfig
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private DepartmentDAO departmentDAO;

	@Inject
	private EmployeeDAO employeeDAO;

	private Long departmentId;

	@Before
	public void before() {
		DepartmentEO department = new DepartmentEO();
		department.setName("Department");
		departmentDAO.persist(department);
		mgr.reset();
		departmentId = department.getId();
	}

	@Test
	public void aChildsLifecycleManagedByParent() {
		// Add child
		DepartmentEO department = departmentDAO.get(departmentId);
		EmployeeEO employee = new EmployeeEO();
		employee.setDepartment(department);
		employee.setName("Gaston");
		department.addEmployee(employee);

		mgr.reset();

		department = departmentDAO.get(departmentId);
		List<EmployeeEO> list = department.getEmployees();

		assertEquals(1, list.size());
		EmployeeEO foundEmployee = list.get(0);
		assertTrue(foundEmployee.getId() > 0);
		assertNotNull(foundEmployee.getDepartment());

		DepartmentEO found = departmentDAO.get(department.getId());
		assertEquals(department.getName(), found.getName());

		// Update child
		foundEmployee.setName("Gurra");

		mgr.reset();

		department = departmentDAO.get(departmentId);
		foundEmployee = department.getEmployees().get(0);

		assertEquals("Gurra", foundEmployee.getName());

		department.deleteEmployee(foundEmployee);

		mgr.reset();

		department = departmentDAO.get(departmentId);
		assertTrue(department.getEmployees().isEmpty());

	}
}
