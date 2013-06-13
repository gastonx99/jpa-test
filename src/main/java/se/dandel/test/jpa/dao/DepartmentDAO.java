package se.dandel.test.jpa.dao;

import se.dandel.test.jpa.domain.DepartmentEO;

public class DepartmentDAO extends AbstractDao<DepartmentEO> {

	public DepartmentEO create(String name) {
		DepartmentEO department = new DepartmentEO();
		department.setName(name);
		persist(department);
		return department;
	}

	public void update(long id, String name) {
		DepartmentEO department = internalGet(id);
		department.setName(name);
	}

}
