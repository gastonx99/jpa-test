package se.dandel.test.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Provider;

public class EntityManagerProvider implements Provider<EntityManager> {

	private String persistenceUnitName;
	private EntityManagerFactory factory;

	public EntityManagerProvider() {
		this.persistenceUnitName = "persistenceUnit-hsqldb";
	}

	@Override
	public EntityManager get() {
		EntityManager em = getFactory().createEntityManager();
		em.getTransaction().begin();
		return em;
	}

	private EntityManagerFactory getFactory() {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory(persistenceUnitName);
		}
		return factory;
	}

}
