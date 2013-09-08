package se.dandel.test.jpa.junit.beforeafter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Dataloader implements BeforeAfter<DataloaderBeforeAfterContext> {
	private static final Logger logger = Logger.getLogger(Dataloader.class);

	private List<String> deleteFromTableStatements;

	@Override
	public void before(DataloaderBeforeAfterContext ctx) {
		// TODO Auto-generated method stub

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
			logger.debug("Fetching metadata to check if database is already created");
			tables = connection.getMetaData().getTables(null, null, null, null);
			while (tables.next()) {
				String tableType = tables.getString("TABLE_TYPE");
				if ("TABLE".equals(tableType)) {
					deleteFromTableStatements.add("delete from " + tables.getString("TABLE_NAME"));
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
}
