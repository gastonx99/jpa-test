package se.dandel.test.jpa.department;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import se.dandel.test.jpa.department.domain.DepartmentEO;
import se.dandel.test.jpa.department.domain.EmployeeEO;

public class JpaTest {

	private EntityManager manager;

	public JpaTest(EntityManager manager) {
		this.manager = manager;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistenceUnit");
		EntityManager manager = factory.createEntityManager();
		JpaTest test = new JpaTest(manager);

		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		try {
			test.createEmployees();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tx.commit();

		test.listEmployees();

		System.out.println(".. done");
	}

	private void createEmployees() {
		int numOfEmployees = manager.createQuery("Select a From Employee a", EmployeeEO.class).getResultList().size();
		if (numOfEmployees == 0) {
			DepartmentEO department = new DepartmentEO("java");
			manager.persist(department);

			EmployeeEO entity = new EmployeeEO();
			entity.setName("Jakab Gipsz");
			entity.setDepartment(department);
			manager.persist(entity);
			EmployeeEO employee2 = new EmployeeEO();
			employee2.setName("Captain Nemo");
			employee2.setDepartment(department);
			manager.persist(employee2);

		}
	}

	private void listEmployees() {
		List<EmployeeEO> resultList = manager.createQuery("Select a From Employee a", EmployeeEO.class).getResultList();
		System.out.println("num of employess:" + resultList.size());
		for (EmployeeEO next : resultList) {
			System.out.println("next employee: " + next);
		}
	}

}
