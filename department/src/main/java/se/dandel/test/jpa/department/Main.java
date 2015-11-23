package se.dandel.test.jpa.department;

import java.util.List;

import se.dandel.test.jpa.department.dao.DepartmentDAO;
import se.dandel.test.jpa.department.domain.DepartmentEO;
import se.dandel.test.jpa.guice.GuiceModule;

import com.google.inject.Guice;
import com.google.inject.Inject;

public class Main {

	@Inject
	private DepartmentDAO dao;

	public static void main(String[] args) {
		Main m = new Main();

		Guice.createInjector(new GuiceModule()).injectMembers(m);

		m.foo();
	}

	private void foo() {
		dao.create("Department");
		List<DepartmentEO> list = dao.findAll();
		if (list.size() != 1) {
			throw new RuntimeException("Wrong number of departments, found " + list.size());
		}
	}
}
