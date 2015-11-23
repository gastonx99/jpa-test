package se.dandel.test.jpa.department.dao;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import se.dandel.test.jpa.dao.AbstractDaoImpl;
import se.dandel.test.jpa.department.domain.DepartmentEO;

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
    public DepartmentEO findWithEagerFetch(long id) {
        String queryStr = "select d from DepartmentEO d join fetch d.employees join fetch d.agendas where d.id = :id";

        Query query = em().createQuery(queryStr);
        query.setParameter("id", id);
        return singleResult(query);
    }

    @Override
    public List<DepartmentEO> findWithEagerFetch(Long... id) {
        String queryStr = "select distinct d from DepartmentEO d join fetch d.employees join fetch d.agendas where d.id in :id";
        Query query = em().createQuery(queryStr);
        query.setParameter("id", Arrays.asList(id));
        return listResult(query);
    }

    @Override
    public List<DepartmentEO> findAllReadOnly() {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<DepartmentEO> cq = cb.createQuery(DepartmentEO.class);
        cq.select(cq.from(DepartmentEO.class));
        return em().createQuery(cq).setHint("eclipselink.read-only", "true").getResultList();
    }

}
