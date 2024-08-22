package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class App {

	public enum AppConfigEntry {

		DB(),
		HOST(),
		PASS(),
		USER(),
		DRIVER(),
		PORT();

		// region Class stuff.
		private final Class<?> specialType;

		private AppConfigEntry() {
			this.specialType = String.class;
		}

		private AppConfigEntry(final Class<?> p_specialType) {
			this.specialType = p_specialType;
		}

		public Class<?> getSpecialType() {
			return this.specialType;
		}
		// endregion

	}

	public enum AppFlag {

		CHECK_CONF("-c");

		// region Class stuff.
		private static final Map<String, AppFlag> IDENTIFIERS_MAP = new HashMap<>(AppFlag.values().length);

		private final String identifier;

		static {
			for (final var e : AppFlag.values())
				AppFlag.IDENTIFIERS_MAP.put(e.getIdentifier(), e);
		}

		private AppFlag(final String p_identifier) {
			this.identifier = p_identifier;
		}

		public String getIdentifier() {
			return this.identifier;
		}

		public static AppFlag identifierToFlag(final String p_identifier) {
			return AppFlag.IDENTIFIERS_MAP.get(p_identifier);
		}
		// endregion

	}

	public enum AppExitCode {

		OKAY(0),
		UNKNOWN_FLAG_PASSED(1); // <- I hated this idea till I realized that VERSIONING APPS is a thing!
		// (NEW VERSIONS may come with NEW FLAGS!)

		// region Class stuff.
		private final int exitCode;

		private AppExitCode(final int p_exitCode) {
			this.exitCode = p_exitCode;
		}

		public int getExitCode() {
			return this.exitCode;
		}
		// endregion

	}

	public static void main(final String[] p_args) {
		// Learn to use Maven/Gradle/Ant (okay, NOT ANT!) or simply add in this JAR when
		// compiling - build systems will allow you to update as needed: [
		// https://dlm.mariadb.com/3852266/Connectors/java/connector-java-3.4.1/mariadb-java-client-3.4.1.jar
		// ]. Personally, I like Maven a little more than Gradle, but these decisions
		// are upto you. The easiest solution is to download and compile with the JAR.

		// Load the MariaDB driver like this if you're not working with a new-enough
		// version of the JDK (really, JDBC, but newer JDKs *have* newer JDBCs):

		// Class.forName("org.mariadb.jdbc.Driver");

		// What is this doing? It's forcing the JVM's "application class loader" (there
		// are two; the other one's used for JDK classes and is called the "bootstrap
		// loader", ...I think.)

		System.out.println("Welcome to \"Yet Another DB CLI\"...");

		final Set<AppFlag> flags = new HashSet<>(AppFlag.values().length);
		final Map<AppConfigEntry, String> configuration = new EnumMap<>(AppConfigEntry.class);

		// Add flags in:
		for (final var s : p_args) {
			final AppFlag f = AppFlag.identifierToFlag(s);

			if (f == null)
				continue;

			flags.add(f);
		}

		// for (final var f : flags)
		// switch (f) {
		//
		// }

		App.readConfigFile(configuration);

		if (flags.contains(AppFlag.CHECK_CONF))
			System.exit(AppExitCode.OKAY.getExitCode());

		// Now we'll establish a connection with the DB:
		try (final Connection connection = App.ensureConnection(configuration)) {

			System.out.println("Connected! Please go on:\n");

			final StringBuilder fullStatementStringBuilder = new StringBuilder();
			final String statementBegin = String.format(

					"[%s][%s])> ",
					configuration.get(AppConfigEntry.DRIVER),
					configuration.get(AppConfigEntry.DB)

			);

			final Scanner sc = new Scanner(System.in);
			System.out.print(statementBegin);
			System.out.flush();

			while (true) {
				try {
					final String line = sc.nextLine();

					if (line == null)
						continue;

					fullStatementStringBuilder.append(line);

					// Using these instead of `String::endsWith()` for performance:
					if (';' == line.charAt(line.length() - 1)) {
						App.runQueryForConnection(fullStatementStringBuilder.toString(), connection);
						fullStatementStringBuilder.delete(0, fullStatementStringBuilder.length());
						System.out.printf("%n%s", statementBegin);
						System.out.flush();
						continue;
					}

					fullStatementStringBuilder.append(' ');
					System.out.print("-> ");
					System.out.flush();

				} catch (final NoSuchElementException e) {
					// System.out.println("(No input yet.)");
					// continue;
				} catch (final IllegalStateException e) {
					sc.close();
					System.out.println("\nBye! ^-^");
					App.exitApp(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}

		App.exitApp(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
	}

	public static String read(final Scanner p_scanner) {
		try {
			return p_scanner.nextLine();
		} catch (final NoSuchElementException e) {
			p_scanner.close();
			return null;
		}
	}

	public static void exitApp(final AppExitCode p_flag) {
		System.exit(p_flag.getExitCode());
	}

	/**
	 * Reads the config file and stores it in the given
	 * {@linkplain Map Map&lt;String,String&gt;}.
	 */
	public static void readConfigFile(final Map<AppConfigEntry, String> p_config) {
		try (

				final FileReader fileReader = new FileReader("./.env");
				final BufferedReader reader = new BufferedReader(fileReader);

		) {

			for (int i = 0; reader.ready(); ++i) {
				final String line = reader.readLine();
				final int separatorId = line.indexOf('=');

				if (separatorId == -1) {
					System.out.printf("Line `%d`: Missing `=`.%n", i);
					continue;
				}

				p_config.put(

						AppConfigEntry.valueOf(line.substring(0, separatorId)),
						line.substring(separatorId + 1)

				);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Is the port valid?:
		try {
			Integer.valueOf(p_config.get(AppConfigEntry.PORT));
		} catch (final NumberFormatException e) {
			p_config.put(AppConfigEntry.PASS, null);
			System.out.println("Port number is not an integer! This will be asked for later.");
		}

	}

	public static Connection ensureConnection(final Map<AppConfigEntry, String> p_config) {
		try {

			final Scanner sc = new Scanner(System.in);

			for (final var e : AppConfigEntry.values()) {
				if (p_config.containsKey(e))
					continue;

				System.out.printf(

						"Did not find an entry for `%s` in the config file. Please provide it: ",
						e.toString()

				);

				p_config.put(e, sc.nextLine());
			}

			sc.close(); // :(
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

			System.out.printf("Connecting to `%s` server at: `%s`.%n", driver, urlString);
			return DriverManager.getConnection(urlString, user, pass);

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
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
