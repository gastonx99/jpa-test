package se.dandel.test.jpa;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import se.dandel.test.jpa.dao.DepartmentDAO;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;

import com.google.inject.Inject;

public class ManagedPerformanceTest {
    private final Logger logger = Logger.getLogger(getClass());

    @Rule
    @JpaTestConfig
    public GuiceJpaLiquibaseManager mgr = new GuiceJpaLiquibaseManager();

    @Inject
    private DepartmentDAO departmentDAO;

    private long end;

    private long start;

    public void createEntities(int c) {
        start = System.currentTimeMillis();
        for (int i1 = 1; i1 <= c; i1++) {
            departmentDAO.create(String.valueOf(i1));
        }
        end = System.currentTimeMillis();
        logger.info("Create took " + (end - start) + " ms");
    }

    @Before
    public void before() {
        mgr.getEntityManager().createQuery("delete from DepartmentEO").executeUpdate();
        mgr.getEntityManager().flush();
    }

    @Test
    public void unmanaged() {
        createEntities(10000);

        mgr.reset();

        departmentDAO.findAllReadOnly();

        start = System.currentTimeMillis();
        mgr.getEntityManager().flush();
        end = System.currentTimeMillis();
        logger.info("Readonly flush took " + (end - start) + " ms");
    }

    @Test
    public void managed() {
        createEntities(10000);

        mgr.reset();

        departmentDAO.findAll();

        start = System.currentTimeMillis();
        mgr.getEntityManager().flush();
        end = System.currentTimeMillis();
        logger.info("Managed flush took " + (end - start) + " ms");
    }

}
