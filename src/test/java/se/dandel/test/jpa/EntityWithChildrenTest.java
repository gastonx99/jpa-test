package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.dao.EmployeeDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.domain.EmployeeEO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class EntityWithChildrenTest {

    @Rule
    @JpaTestConfig
    public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

    @Inject
    private DepartmentDAO departmentDAO;

    @Inject
    private EmployeeDAO employeeDAO;

    @Test
    public void aChild() {
        DepartmentEO department = new DepartmentEO();
        department.setName("Department");
        departmentDAO.persist(department);

        EmployeeEO employee = new EmployeeEO();
        employee.setName("Gaston");
        department.addEmployee(employee);

        mgr.reset();

        List<EmployeeEO> list = employeeDAO.findAll();
        assertEquals(1, list.size());

        DepartmentEO found = departmentDAO.get(department.getId());
        assertEquals(department.getName(), found.getName());
    }
}
