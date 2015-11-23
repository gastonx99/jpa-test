package se.dandel.test.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.domain.DepartmentEO;
import se.dandel.test.jpa.junit.DataResource;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class ReadOnlyTest {

    @Rule
    @JpaTestConfig
    @DataResource(resource = "department-data.xml")
    public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

    @Inject
    private DepartmentDAO departmentDAO;

    @Test
    public void readonly() {
        List<DepartmentEO> departments = departmentDAO.findAllReadOnly();

        DepartmentEO department = departments.get(0);
        assertNotNull(department);

        assertFalse(mgr.getEntityManager().contains(department));
    }

    @Test
    public void managed() {
        List<DepartmentEO> departments = departmentDAO.findAll();

        DepartmentEO department = departments.get(0);
        assertNotNull(department);

        assertTrue(mgr.getEntityManager().contains(department));
    }

}
