package se.dandel.test.jpa.junit;

import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.Config;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.DdlGeneration;

public class ConfigHelper {

    public static boolean isLiquibased(Config config) {
        return DdlGeneration.LIQUIBASE.equals(config.ddlGeneration());
    }

}
