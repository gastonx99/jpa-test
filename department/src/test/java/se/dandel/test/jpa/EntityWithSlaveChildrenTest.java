package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.department.dao.DepartmentDAO;
import se.dandel.test.jpa.department.domain.AgendaEO;
import se.dandel.test.jpa.department.domain.DepartmentEO;
import se.dandel.test.jpa.department.domain.ResponsibilityEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class EntityWithSlaveChildrenTest {

	@Rule
	@JpaTestConfig
	public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

	@Inject
	private DepartmentDAO departmentDAO;

	@Test
	public void indirectListManipulation() {
		DepartmentEO department = new DepartmentEO("Department");
		departmentDAO.persist(department);

		department.addAgenda(new AgendaEO("A agenda", department));

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(1, department.getAgendas().size());
		department.addAgenda(new AgendaEO("Another agenda", department));

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(2, department.getAgendas().size());
		department.removeAgenda(department.getAgendas().iterator().next());

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(1, department.getAgendas().size());
		department.removeAgenda(department.getAgendas().iterator().next());

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(0, department.getAgendas().size());
	}

	@Test
	public void directListManipulation() {
		DepartmentEO department = new DepartmentEO("Department");
		departmentDAO.persist(department);

		department.getResponsibilities().add(new ResponsibilityEO("A agenda", department));

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(1, department.getResponsibilities().size());
		department.getResponsibilities().add(new ResponsibilityEO("Another agenda", department));

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(2, department.getResponsibilities().size());
		department.getResponsibilities().remove(department.getResponsibilities().iterator().next());

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(1, department.getResponsibilities().size());
		department.getResponsibilities().remove(department.getResponsibilities().iterator().next());

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(0, department.getResponsibilities().size());
	}

	@Test
	public void directListReplacement() {
		DepartmentEO department = new DepartmentEO("Department");
		departmentDAO.persist(department);

		List<ResponsibilityEO> list = new ArrayList<ResponsibilityEO>();
		list.add(new ResponsibilityEO("A agenda", department));
		department.setResponsibilities(list);

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(1, department.getResponsibilities().size());
		list = new ArrayList<ResponsibilityEO>();
		list.add(department.getResponsibilities().iterator().next());
		list.add(new ResponsibilityEO("Another agenda", department));
		department.setResponsibilities(list);

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(2, department.getResponsibilities().size());
		list = new ArrayList<ResponsibilityEO>();
		department.setResponsibilities(list);

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(0, department.getResponsibilities().size());
	}

	@Test
	public void merge() {
		DepartmentEO department = new DepartmentEO("Department");
		department.getResponsibilities().add(new ResponsibilityEO("A agenda", department));
		department.getResponsibilities().add(new ResponsibilityEO("Another agenda", department));
		departmentDAO.persist(department);

		mgr.reset();

		department = departmentDAO.get(department.getId());
		assertEquals(2, department.getResponsibilities().size());

		mgr.reset();

		List<ResponsibilityEO> list = new ArrayList<ResponsibilityEO>();
		list.add(new ResponsibilityEO("A agenda", department));
		department.setResponsibilities(list);
		departmentDAO.merge(department);

		mgr.reset();

		assertEquals(1, departmentDAO.get(department.getId()).getResponsibilities().size());
	}
}
