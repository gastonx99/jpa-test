package se.dandel.test.jpa.dao;

import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;

public class EmployeeDAO extends AbstractDao<EmployeeEO> {

	public EmployeeEO create(String name, DepartmentEO department) {
		EmployeeEO employee = new EmployeeEO();
		employee.setDepartment(department);
		employee.setName(name);
		persist(employee);
		return employee;
	}

	public void update(long id, String name) {
		EmployeeEO employee = internalGet(id);
		employee.setName(name);
	}

}
