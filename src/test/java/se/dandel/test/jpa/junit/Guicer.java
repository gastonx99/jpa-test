package se.dandel.test.jpa.junit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;

public class Guicer implements BeforeAfter<GuicerBeforeAfterContext> {

	private final List<Module> modules = new ArrayList<Module>();
	private final Object target;

	public Guicer(List<Module> modules, Object target) {
		this.modules.addAll(modules);
		this.target = target;
	}

	private Module[] createModules(final GuicerBeforeAfterContext ctx) {
		List<Module> list = new ArrayList<Module>(modules);
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(EntityManager.class).toInstance(ctx.getEntityManager());
			}
		};
		list.add(module);
		return list.toArray(new Module[0]);
	}

	@Override
	public void before(GuicerBeforeAfterContext ctx) {
		Guice.createInjector(createModules(ctx)).injectMembers(target);
	}

	@Override
	public void after(GuicerBeforeAfterContext ctx) {
	}

}
