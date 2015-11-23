package se.dandel.test.jpa.junit.beforeafter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import se.dandel.test.jpa.junit.ConfigHelper;
import se.dandel.test.jpa.junit.DataResource;
import se.dandel.test.jpa.junit.GuiceJpaLiquibaseManager.Config;

public class Dataloader implements BeforeAfter<DataloaderBeforeAfterContext> {
	private static final Logger logger = Logger.getLogger(Dataloader.class);

	private List<String> deleteFromTableStatements;

	private DataResource dataResource;

	private Config config;

	public Dataloader(Config config, DataResource dataResource) {
		this.dataResource = dataResource;
		this.config = config;
	}

	@Override
	public void before(DataloaderBeforeAfterContext ctx) {
		if (StringUtils.isNotBlank(dataResource.resource())) {
			if (ConfigHelper.isLiquibased(config)) {
				loadData(ctx);
			} else {
				throw new IllegalArgumentException("Unsupported");
			}
		}
	}

	private void loadData(DataloaderBeforeAfterContext ctx) {
		String resource = dataResource.resource();
		logger.debug("Loading data from " + resource);
		try {
			Liquibase liquibase = getLiquibase(ctx.getConnection(), resource);
			liquibase.update((String) null);
		} catch (LiquibaseException e) {
			throw new RuntimeException(e);
		}
	}

	private Liquibase getLiquibase(Connection connection, String changeLogFile) throws LiquibaseException {
		JdbcConnection c = new JdbcConnection(connection);
		return new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), c);
	}

	@Override
	public void after(DataloaderBeforeAfterContext ctx) {
		deleteFromAllTables(ctx.getConnection());
	}

	private void deleteFromAllTables(Connection connection) {
		logger.debug("Deleting from all tables");
		java.sql.Statement statement = null;
		try {
			statement = connection.createStatement();
			deleteFromTables(statement, getDeleteFromTableStatements(connection), 0, connection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e) {
				// fall through
			}
		}
		logger.debug("Deleted from all tables");
	}

	private void deleteFromTables(java.sql.Statement statement, List<String> stmts, int statementsExecuted,
			Connection connection) {
		List<String> goodStatements = new ArrayList<String>();
		try {
			for (String str : stmts) {
				logger.debug("Executing " + str);
				statement.execute(str);
				connection.commit();
				goodStatements.add(str);
			}
		} catch (Exception e) {
			int newStatementsExecuted = goodStatements.size();
			if (goodStatements.size() == statementsExecuted) {
				throw new RuntimeException(e);
			}
			String badStatement = stmts.get(goodStatements.size());
			goodStatements.addAll(stmts.subList(goodStatements.size() + 1, stmts.size()));
			goodStatements.add(badStatement);
			deleteFromTableStatements = goodStatements;
			deleteFromTables(statement, goodStatements, newStatementsExecuted, connection);
		}
	}

	private List<String> getDeleteFromTableStatements(Connection connection) {
		deleteFromTableStatements = new ArrayList<String>();
		ResultSet tables = null;
		try {
			tables = connection.getMetaData().getTables(null, null, null, null);
			while (tables.next()) {
				String tableType = tables.getString("TABLE_TYPE");
				if ("TABLE".equals(tableType)) {
					String tablename = tables.getString("TABLE_NAME");
					if (isOkToDeleteFrom(tablename)) {
						deleteFromTableStatements.add("delete from " + tablename);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				tables.close();
			} catch (Exception e) {
				// fall through
			}
		}

		return deleteFromTableStatements;
	}

	private static Set<String> EXCLUDES_TABLE = new HashSet<String>();
	static {
		// EXCLUDES_TABLE.add("DATABASECHANGELOG");
		EXCLUDES_TABLE.add("DATABASECHANGELOGLOCK");
		EXCLUDES_TABLE.add("DATABASE_ALREADY_CREATED_TABLE");
	}

	private boolean isOkToDeleteFrom(String tablename) {
		return !EXCLUDES_TABLE.contains(tablename);
	}
}
