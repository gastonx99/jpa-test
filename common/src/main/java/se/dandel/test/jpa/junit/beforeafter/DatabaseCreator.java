package se.dandel.test.jpa.junit.beforeafter;

import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.LogLevel;
import liquibase.resource.ClassLoaderResourceAccessor;
import se.dandel.test.jpa.junit.ConfigHelper;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.Config;

public class DatabaseCreator implements BeforeAfter<DatabaseCreatorBeforeAfterContext> {
	private static final Logger logger = Logger.getLogger(DatabaseCreator.class);
	private static final String DATABASE_ALREADY_CREATED_TABLENAME = "DATABASE_ALREADY_CREATED_TABLE";
	private static final String DATABASE_ALREADY_CREATED_TABLE_SQL = "create table "
			+ DATABASE_ALREADY_CREATED_TABLENAME + " (a integer)";

	private Boolean databaseAlreadyCreated;
	private boolean createDatabaseWithLiquibase;
	private boolean dropBetweenExecutions = true;
	private String changeLogFile = "change-log.xml";

	public DatabaseCreator(Config config) {
		this.createDatabaseWithLiquibase = ConfigHelper.isLiquibased(config);
	}

	@Override
	public void before(DatabaseCreatorBeforeAfterContext ctx) {
		if (createDatabaseWithLiquibase) {
			if (!isDatabaseAlreadyCreated(ctx.getConnection())) {
				liquibaseCreateDatabase(ctx.getConnection());
				createDatabaseAlreadyCreatedTable(ctx.getConnection());
			}
		}
	}

	@Override
	public void after(DatabaseCreatorBeforeAfterContext ctx) {
		if (createDatabaseWithLiquibase) {
			if (dropBetweenExecutions) {
				liquibaseDropDatabase(ctx.getConnection());
			}
		}
	}

	private void createDatabaseAlreadyCreatedTable(Connection connection) {
		java.sql.Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(DATABASE_ALREADY_CREATED_TABLE_SQL);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				// fall through
			}
		}
	}

	private boolean isDatabaseAlreadyCreated(Connection connection) {
		if (databaseAlreadyCreated != null) {
			return databaseAlreadyCreated;
		}
		ResultSet tables = null;
		try {
			logger.debug("Fetching metadata to check if database is already created");
			tables = connection.getMetaData().getTables(null, null, DATABASE_ALREADY_CREATED_TABLENAME, null);
			logger.debug("Fetced metadata");
			databaseAlreadyCreated = tables.next();
			return databaseAlreadyCreated;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				tables.close();
			} catch (Exception e) {
				// fall through
			}
		}
	}

	private void liquibaseCreateDatabase(Connection connection) {
		logger.debug("Creating database with liquibase");
		try {
			Liquibase liquibase = getLiquibase(connection, changeLogFile);
			LogFactory.getLogger().setLogLevel(LogLevel.DEBUG);
			liquibase.update((String) null);
			logger.debug("Database created");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Liquibase getLiquibase(Connection connection, String changeLogFile) throws LiquibaseException {
		JdbcConnection c = new JdbcConnection(connection);
		return new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), c);
	}

	private void liquibaseDropDatabase(Connection connection) {
		logger.debug("Dropping database with liquibase");
		try {
			getLiquibase(connection, changeLogFile).dropAll();
			logger.debug("Database dropped");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
