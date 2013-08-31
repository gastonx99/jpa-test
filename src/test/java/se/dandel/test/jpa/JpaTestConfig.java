package se.dandel.test.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.dandel.test.jpa.guice.GuiceModule;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.DdlGeneration;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GuiceJpaLiquibaseManager.Config(modules = GuiceModule.class, persistenceUnitName = "persistenceUnit-hsqldb", ddlGeneration = DdlGeneration.LIQUIBASE)
public @interface JpaTestConfig {

}
