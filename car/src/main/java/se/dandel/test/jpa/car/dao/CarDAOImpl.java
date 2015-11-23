package se.dandel.test.jpa.car.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import se.dandel.test.jpa.car.domain.CarEO;
import se.dandel.test.jpa.dao.AbstractDaoImpl;

public class CarDAOImpl extends AbstractDaoImpl<CarEO> implements CarDAO {

	@Override
	public CarEO create(String name) {
		CarEO car = new CarEO();
		car.setName(name);
		persist(car);
		return car;
	}

	@Override
	public void update(long id, String name) {
		CarEO car = internalGet(id);
		car.setName(name);
	}

	@Override
	public List<CarEO> findAllReadOnly() {
		CriteriaBuilder cb = em().getCriteriaBuilder();
		CriteriaQuery<CarEO> cq = cb.createQuery(CarEO.class);
		cq.select(cq.from(CarEO.class));
		return em().createQuery(cq).setHint("eclipselink.read-only", "true").getResultList();
	}

}
