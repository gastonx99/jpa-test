package se.dandel.test.jpa;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JunitTest {
	private static final Logger logger = Logger.getLogger(JunitTest.class);

	@Rule
	public EmManager emManager = new EmManager("persistenceUnit-hsqldb");

	@Before
	public void before() {
		logger.debug("Before");
	}

	@After
	public void after() {
		logger.debug("After");
	}

	@Test
	public void dummy() {
		emManager.reset();
	}

}
