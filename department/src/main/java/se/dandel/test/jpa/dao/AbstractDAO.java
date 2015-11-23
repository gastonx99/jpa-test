package se.dandel.test.jpa.dao;

import java.util.List;

public interface AbstractDAO<T> {

	public abstract List<T> findAll();

	public abstract T get(Object id);

	public abstract void delete(long id);

	public abstract void persist(T entity);

	public abstract void merge(T entity);

}