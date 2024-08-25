package com.brahvim.college_assignments;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brahvim.college_assignments.enums.AppConfigEntry;

public class AppDbUtils {

	private AppDbUtils() {
		throw new IllegalAccessError();
	}

	public static Connection connectToDb(
			final String p_urlString,
			final Map<AppConfigEntry, String> p_config) throws SQLException {
		final String
		/*	 */ pass = p_config.get(AppConfigEntry.PASS),
				user = p_config.get(AppConfigEntry.USER);
		return DriverManager.getConnection(p_urlString, user, pass);
	}

	public static String formJdbcUrl(final Map<AppConfigEntry, String> p_config) {
		final String
		/*	 */ db = p_config.get(AppConfigEntry.DB),
				host = p_config.get(AppConfigEntry.HOST),
				port = p_config.get(AppConfigEntry.PORT),
				driver = p_config.get(AppConfigEntry.DRIVER);

		// First, we derive the URL to send HTTPS requests to:
		return String.format(

				"jdbc:%s://%s:%s/%s",
				driver,
				host,
				port,
				db

		);
	}

	public static void runQueryForConnection(final String p_query, final Connection p_connection) {
		try (final Statement statement = p_connection.createStatement()) {

			boolean resultsAvailable = statement.execute(p_query);
			AppDbUtils.printStatementUpdateCount(statement);

			if (!resultsAvailable)
				return;

			// ^^^ Doing this beforehand may be bad for branch prediction, since the loop
			// would be skipped if it was false anyway, but this is necessary - because the
			// loop calls `Statement::getResultSet()` which *can* cause a crash if
			// update-count is below `1`.

			while (resultsAvailable) {
				final ResultSet result = statement.getResultSet();
				resultsAvailable = statement.getMoreResults();
				AppDbUtils.prettyPrintResultSet(result);
			}

		} catch (SQLException e) {

			if (e instanceof SQLTimeoutException) {
				System.out.println("Request timed out. No changes made.");
				return;
			}

			int i = 0; // Java arrays are limited to `Integer.MAX_VALUE`.
			System.out.println("Errors occurred!:");

			while (e != null) {
				System.out.printf("""
						Error `%d`:
						\tCode `%d`.
						\tDB state:`%s`.
						Error message: %s
						""", // Reminder: Bringing the `"""` to the next line adds a line to the output.
						// PS SonarLint suggested avoiding `\t` (UTF[-8] `u/0009`).
						// `Tab`s are scaled according to environment variables!
						i + 1,
						e.getErrorCode(),
						e.getSQLState(),
						e.getMessage()

				);

				++i;
				e = e.getNextException();
			}
		}
	}

	public static void printStatementUpdateCount(final Statement p_statement) throws SQLException {
		final int updateCount = p_statement.getUpdateCount();

		switch (updateCount) {
			default -> System.out.printf("(`%d` rows affected.)%n", updateCount);
			case -1 -> System.out.println("(No rows affected by DML query.)");
			case 0 -> System.out.println("(No rows affected.)");
		}
	}

	public static void prettyPrintResultSet(final ResultSet p_set) throws SQLException {
		final ResultSetMetaData metaData = p_set.getMetaData();
		final int columnCount = metaData.getColumnCount();

		// Row-major order, mind you!:
		final List<List<String>> cellData = new ArrayList<>(columnCount); // Can't guarantee size!...

		// These are needed per column:
		final int[] rowStrLenMaxes = new int[columnCount];
		final String[] columnLabels = new String[columnCount];

		// Parse out column labels and the first row. ...Also compare their lengths!:
		{ // New scope to hide `firstRowData`.
			p_set.next(); // For some reason...? Yeah... This has to be called FIRST.
			final List<String> firstRowData = new ArrayList<>(columnCount);
			cellData.add(firstRowData);

			// First row has all the column labels:
			for (int i = 1; i <= columnCount; ++i) {
				final int lastId = i - 1;
				final String label = metaData.getColumnLabel(i);
				final String string = AppDbUtils.ensureNullCellDataAsString(p_set.getString(i));

				rowStrLenMaxes[lastId] = Math.max(string.length(), label.length());
				columnLabels[lastId] = label;
				firstRowData.add(string);
			}
		}

		// Okay. It's time!
		// For every row,
		while (p_set.next()) {
			final List<String> rowData = new ArrayList<>(columnCount);
			cellData.add(rowData);

			// For every cell in said row,
			for (int i = 1; i <= columnCount; ++i) {
				final int lastId = i - 1;
				final String string = AppDbUtils.ensureNullCellDataAsString(p_set.getString(i));

				rowStrLenMaxes[lastId] = Math.max(rowStrLenMaxes[lastId], string.length());
				rowData.add(string);
			}
		}

		// Print the row with column labels. And the padding:
		for (int i = 0; i < columnCount; ++i) {
			final String label = columnLabels[i];
			final int labelLength = label.length();
			final int maxLength = rowStrLenMaxes[i];
			final int paddingLength = maxLength - labelLength;
			final String padding = " ".repeat(labelLength + (paddingLength * 2));

			System.out.printf("%s%s", label, padding);
		}

		System.out.println();

		// Print everything else. WITH padding!:
		final int rowCount = cellData.size();
		for (int i = 0; i < rowCount; ++i) {
			final List<String> row = cellData.get(i);

			for (int j = 0; j < columnCount; ++j) {
				final String cell = row.get(j);
				final int maxLength = rowStrLenMaxes[j];

				if (cell == null) {
					System.out.print(" ".repeat(maxLength));
					continue;
				}

				final int cellLength = cell.length();
				final int paddingLength = maxLength - cellLength;
				final String padding = " ".repeat(cellLength + (paddingLength * 2));

				System.out.printf("%s%s", cell, padding);
			}

			System.out.println();
		}
	}

	public static String ensureNullCellDataAsString(final String p_label) {
		// Funny JDBC behavior, ha-ha-haa!...:
		return p_label == null ? "null" : p_label; // ...
	}

}
