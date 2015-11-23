package se.dandel.test.jpa.car;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.dandel.test.jpa.car.guice.CarGuiceModule;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.DdlGeneration;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GuiceJpaLiquibaseManager.Config(modules = CarGuiceModule.class, persistenceUnitName = "car-test", ddlGeneration = DdlGeneration.LIQUIBASE, sqlExplorer = false)
public @interface CarJpaTestConfig {

}
