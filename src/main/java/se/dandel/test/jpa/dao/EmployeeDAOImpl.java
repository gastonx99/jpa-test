package se.dandel.test.jpa.dao;

import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;

public class EmployeeDAOImpl extends AbstractDaoImpl<EmployeeEO> implements EmployeeDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.dandel.test.jpa.dao.EmployeeI#create(java.lang.String,
	 * se.dandel.test.jpa.domain.DepartmentEO)
	 */
	@Override
	public EmployeeEO create(String name, DepartmentEO department) {
		EmployeeEO employee = new EmployeeEO();
		employee.setDepartment(department);
		employee.setName(name);
		persist(employee);
		return employee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.dandel.test.jpa.dao.EmployeeI#update(long, java.lang.String)
	 */
	@Override
	public void update(long id, String name) {
		EmployeeEO employee = internalGet(id);
		employee.setName(name);
	}

}
