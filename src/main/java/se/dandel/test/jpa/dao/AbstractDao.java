package se.dandel.test.jpa.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractDao<T> {

	@Inject
	protected Provider<EntityManager> entityManagerProvider;

	private Class<T> persistentClazz;

	@SuppressWarnings("unchecked")
	public AbstractDao() {
		this.persistentClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public List<T> findAll() {
		List<T> resultList = em().createQuery(findAllStmt(), persistentClazz).getResultList();
		return resultList;
	}

	private String findAllStmt() {
		return "Select t From " + persistentClazz.getSimpleName() + " t";
	}

	protected EntityManager em() {
		return entityManagerProvider.get();
	}

	protected T internalGet(Object id) {
		return em().find(persistentClazz, id);
	}

	public T get(Object id) {
		return internalGet(id);
	}

	public void delete(long id) {
		em().remove(internalGet(id));
	}

	public void persist(T department) {
		em().persist(department);
	}

}
