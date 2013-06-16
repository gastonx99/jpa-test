package se.dandel.test.jpa.dao;

import se.dandel.test.jpa.domain.DepartmentEO;

public class DepartmentDAOImpl extends AbstractDaoImpl<DepartmentEO> implements DepartmentDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.dandel.test.jpa.dao.DepartmentDAOI#create(java.lang.String)
	 */
	@Override
	public DepartmentEO create(String name) {
		DepartmentEO department = new DepartmentEO();
		department.setName(name);
		persist(department);
		return department;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.dandel.test.jpa.dao.DepartmentDAOI#update(long, java.lang.String)
	 */
	@Override
	public void update(long id, String name) {
		DepartmentEO department = internalGet(id);
		department.setName(name);
	}

}
