package se.dandel.test.jpa.junit.beforeafter;

import java.sql.Connection;
import java.sql.ResultSet;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.apache.log4j.Logger;

public class DatabaseCreator implements BeforeAfter<DatabaseCreatorBeforeAfterContext> {
	private static final Logger logger = Logger.getLogger(DatabaseCreator.class);
	private static final String DATABASE_ALREADY_CREATED_TABLENAME = "DATABASE_ALREADY_CREATED_TABLE";
	private static final String DATABASE_ALREADY_CREATED_TABLE_SQL = "create table "
			+ DATABASE_ALREADY_CREATED_TABLENAME + " (a integer)";

	private Liquibase liquibase;
	private Boolean databaseAlreadyCreated;
	private boolean createDatabaseWithLiquibase;
	private boolean dropBetweenExecutions = false;

	public DatabaseCreator(boolean createDatabaseWithLiquibase) {
		this.createDatabaseWithLiquibase = createDatabaseWithLiquibase;
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
		liquibase = null;
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
			Liquibase liquibase = getLiquibase(connection);
			liquibase.update(null);
			logger.debug("Database created");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Liquibase getLiquibase(Connection connection) throws LiquibaseException {
		if (liquibase == null) {
			JdbcConnection c = new JdbcConnection(connection);
			liquibase = new Liquibase("change-log.xml", new ClassLoaderResourceAccessor(), c);
		}
		return liquibase;
	}

	private void liquibaseDropDatabase(Connection connection) {
		logger.debug("Dropping database with liquibase");
		try {
			getLiquibase(connection).dropAll();
			logger.debug("Database dropped");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
