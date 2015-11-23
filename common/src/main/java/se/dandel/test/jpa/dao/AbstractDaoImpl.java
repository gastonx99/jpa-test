package se.dandel.test.jpa.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractDaoImpl<T> implements AbstractDAO<T> {
    protected final Logger logger = Logger.getLogger(getClass());

    @Inject
    protected Provider<EntityManager> entityManagerProvider;

    private Class<T> persistentClazz;

    @SuppressWarnings("unchecked")
    public AbstractDaoImpl() {
        this.persistentClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.dandel.test.jpa.dao.AbstractDAOI#findAll()
     */
    @Override
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
        EntityManager em = em();
        logger.debug(em.getTransaction());
        return em.find(persistentClazz, id);
    }

    @SuppressWarnings("unchecked")
    protected T singleResult(Query query) {
        return (T) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    protected List<T> listResult(Query query) {
        return query.getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.dandel.test.jpa.dao.AbstractDAOI#get(java.lang.Object)
     */
    @Override
    public T get(Object id) {
        return internalGet(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.dandel.test.jpa.dao.AbstractDAOI#delete(long)
     */
    @Override
    public void delete(long id) {
        em().remove(internalGet(id));
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.dandel.test.jpa.dao.AbstractDAOI#persist(T)
     */
    @Override
    public void persist(T entity) {
        EntityManager em = em();
        logger.debug(em.getTransaction());
        em.persist(entity);
    }

    public void merge(T entity) {
        em().merge(entity);
    };

}
