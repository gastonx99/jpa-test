package se.dandel.test.jpa.junit;

import javax.persistence.EntityManager;

public class GuicerBeforeAfterContext implements BeforeAfterContext {
	private EntityManager entityManager;

	private GuicerBeforeAfterContext(EntityManager em) {
		this.entityManager = em;
	}

	public static GuicerBeforeAfterContext of(EntityManager em) {
		return new GuicerBeforeAfterContext(em);
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
}