package se.dandel.test.jpa.car.dao;

import java.util.List;

import se.dandel.test.jpa.car.domain.CarEO;
import se.dandel.test.jpa.dao.AbstractDAO;

public interface CarDAO extends AbstractDAO<CarEO> {

	CarEO create(String name);

	void update(long id, String name);

	List<CarEO> findAllReadOnly();
}