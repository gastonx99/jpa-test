package se.dandel.test.jpa.junit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class JpaGuiceRunner extends BlockJUnit4ClassRunner {

	public JpaGuiceRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

}
