package se.dandel.test.jpa.dao;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

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

    @Override
    public DepartmentEO findWithOptimalEagerFetch(long id) {
        String queryStr = "select d from DepartmentEO d join fetch d.employees join fetch d.agendas where d.id = :id";

        Query query = em().createQuery(queryStr);
        query.setParameter("id", id);
        return singleResult(query);
    }

    @Override
    public List<DepartmentEO> findWithOptimalEagerFetch(Long... id) {
        String queryStr = "select distinct d from DepartmentEO d join fetch d.employees join fetch d.agendas where d.id in :id";
        Query query = em().createQuery(queryStr);
        query.setParameter("id", Arrays.asList(id));
        return listResult(query);
    }

}
