package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import javax.persistence.PersistenceUnit;

import org.junit.Before;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.dao.EmployeeDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;

@PersistenceUnit(unitName = "persistenceUnit-hsqldb")
public class StandaloneEntityButDependentToAnotherTest extends AbstractJpaTest {

	private DepartmentDAO departmentDAO;
	private EmployeeDAO employeeDAO;
	private DepartmentEO department;

	@Before
	public void createDepartment() {
		department = departmentDAO.create("A department");
	}

	@Test
	public void crud() {
		EmployeeEO employee = employeeDAO.create("A employee", department);

		reset();

		assertEquals(1, employeeDAO.findAll().size());

		reset();

		EmployeeEO found = employeeDAO.get(employee.getId());
		assertNotSame(employee, found);
		assertEquals(employee.getId(), found.getId());

		reset();

		String newName = "Another employee";
		employeeDAO.update(employee.getId(), newName);

		reset();

		found = employeeDAO.get(employee.getId());
		assertEquals(newName, found.getName());

		reset();
		employeeDAO.delete(employee.getId());

		reset();

		assertEquals(0, employeeDAO.findAll().size());
	}

	@Override
	protected void setupDaos() {
		departmentDAO = injector.getInstance(DepartmentDAO.class);
		employeeDAO = injector.getInstance(EmployeeDAO.class);
	}

}
