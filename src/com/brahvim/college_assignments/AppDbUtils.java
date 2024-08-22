package com.brahvim.college_assignments;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        try (

                final Statement statement = p_connection.createStatement();
                final ResultSet result = statement.executeQuery(p_query);

        ) {

            System.out.println("Made your query...");

        } catch (SQLException e) {
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

}
