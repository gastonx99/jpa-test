package se.dandel.test.jpa.dao;

import java.util.List;

import se.dandel.test.jpa.domain.DepartmentEO;

public interface DepartmentDAO extends AbstractDAO<DepartmentEO> {

    DepartmentEO create(String name);

    void update(long id, String name);

    DepartmentEO findWithEagerFetch(long id);

    List<DepartmentEO> findWithEagerFetch(Long... id);

    List<DepartmentEO> findAllReadOnly();
}