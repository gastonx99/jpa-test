package se.dandel.test.jpa.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.dandel.test.jpa.integration.guice.IntegrationtestGuiceModule;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.DdlGeneration;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@GuiceJpaLiquibaseManager.Config(modules = IntegrationtestGuiceModule.class, persistenceUnitName = "integration-test", ddlGeneration = DdlGeneration.LIQUIBASE, sqlExplorer = false)
public @interface IntegrationJpaTestConfig {

}
