package se.dandel.test.jpa.junit.beforeafter;

public interface BeforeAfter<T extends BeforeAfterContext> {

	void before(T ctx);

	void after(T ctx);

}
