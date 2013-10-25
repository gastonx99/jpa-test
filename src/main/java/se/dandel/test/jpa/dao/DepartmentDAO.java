package se.dandel.test.jpa.dao;

import java.util.List;

import se.dandel.test.jpa.domain.DepartmentEO;

public interface DepartmentDAO extends AbstractDAO<DepartmentEO> {

    public abstract DepartmentEO create(String name);

    public abstract void update(long id, String name);

    public abstract DepartmentEO findWithOptimalEagerFetch(long id);

    public abstract List<DepartmentEO> findWithOptimalEagerFetch(Long... id);

}