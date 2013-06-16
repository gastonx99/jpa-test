package se.dandel.test.jpa;

import java.util.List;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
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
