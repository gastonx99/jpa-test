package se.dandel.test.jpa.dao;

import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;

public interface EmployeeDAO extends AbstractDAO<EmployeeEO> {

	public abstract EmployeeEO create(String name, DepartmentEO department);

	public abstract void update(long id, String name);

}