package se.dandel.test.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.department.dao.DepartmentDAO;
import se.dandel.test.jpa.department.domain.DepartmentEO;
import se.dandel.test.jpa.junit.DataResource;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class DataLoaderTest {

    @Rule
    @JpaTestConfig
    @DataResource(resource = "department-data.xml")
    public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

    @Inject
    private DepartmentDAO departmentDAO;

    @Test
    public void singleEagerFetch() {
        DepartmentEO found = departmentDAO.findWithEagerFetch(1L);
        assertEquals("My department", found.getName());
        assertEquals(2, found.getEmployees().size());
        assertEquals(3, found.getAgendas().size());
    }

    @Test
    public void inClauseEagerFetch() {
        List<DepartmentEO> founds = departmentDAO.findWithEagerFetch(1L, 2L);

        assertEquals(2, founds.size());
        Iterator<DepartmentEO> it = founds.iterator();
        DepartmentEO found1 = it.next();
        DepartmentEO found2 = it.next();

        assertEquals(2, found1.getEmployees().size());
        assertEquals(3, found1.getAgendas().size());

        assertEquals(1, found2.getEmployees().size());
        assertEquals(2, found2.getAgendas().size());
    }
}
