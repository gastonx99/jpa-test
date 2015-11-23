package se.dandel.test.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTest {

	private String asdf;

	public SimpleTest() {
		System.out.println("Instantiating test + " + this);
	}

	@Test
	public void foo1() {
		System.out.println("Running foo1 test");
		asdf = "qwer";
		System.out.println(asdf);
	}

	@Test
	public void foo2() {
		System.out.println("Running foo2 test");
		System.out.println(asdf);
	}

}
