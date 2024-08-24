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

public class AppDbUtils {

    private AppDbUtils() {
        throw new IllegalAccessError();
    }

    public static Connection ensureConnection(final Map<AppConfigEntry, String> p_config) throws SQLException {
        // final Scanner sc = new Scanner(System.in);

        for (final var e : AppConfigEntry.values()) {
            if (p_config.containsKey(e))
                continue;

            System.out.printf(

                    "Did not find an entry for `%s` in the config file. Please provide it: ",
                    e.toString()

            );

            String value = "";
            boolean valid = false;

            while (!valid) {
                // value = sc.nextLine();

                switch (e) {
                    default -> value = System.console().readLine();
                    case PASS -> value = new String(System.console().readPassword());
                }

                valid = e.getChecker().apply(value) == null;

                if (valid)
                    break;

            }

            p_config.put(e, value);
        }

        // sc.close(); // :(
        // This was supposed to be needed again for interactive CLI program...

        final String
        /*	 */ db = p_config.get(AppConfigEntry.DB),
                host = p_config.get(AppConfigEntry.HOST),
                port = p_config.get(AppConfigEntry.PORT),
                pass = p_config.get(AppConfigEntry.PASS),
                user = p_config.get(AppConfigEntry.USER),
                driver = p_config.get(AppConfigEntry.DRIVER);

        // First, we derive the URL to send HTTPS requests to:
        final String urlString = String.format(

                "jdbc:%s://%s:%s/%s",
                driver,
                host,
                port,
                db

        );

        System.out.printf("asd `%s` server at: `%s`.%n", driver, urlString);
        return DriverManager.getConnection(urlString, user, pass);
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
        final List<String> data = new ArrayList<>(columnCount); // Can't guarantee size!...
        final List<String> columnLabels = new ArrayList<>(columnCount);
        final List<Integer> dataLengthMaxes = new ArrayList<>(columnCount);

        p_set.next();

        for (int i = 1; i <= columnCount; ++i) {
            final String string = p_set.getString(i);
            final String columnLabel = metaData.getColumnLabel(i);

            dataLengthMaxes.add(columnLabel.length());
            dataLengthMaxes.add(string.length());
            columnLabels.add(columnLabel);
            data.add(string);
        }

        // For every row,
        while (p_set.next())
            // For every cell in said row,
            for (int i = 1; i <= columnCount; ++i) {
                final int lastId = i - 1;
                final String string = p_set.getString(i);
                final int currentStrLen = string.length();
                final Integer lastBest = dataLengthMaxes.get(lastId);

                data.add(string);
                dataLengthMaxes.set(

                        lastId,
                        lastBest > currentStrLen
                                ? lastBest
                                : currentStrLen

                );
            }

        final int rowCount = data.size();

        for (int i = 0; i < rowCount; i++) {
            System.out.printf(

                    "%s%n",
                    data.get(i)

            );

        }
    }

}
