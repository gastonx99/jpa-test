package se.dandel.test.jpa.junit;

public interface BeforeAfter<T extends BeforeAfterContext> {

	void before(T ctx);

	void after(T ctx);

}
