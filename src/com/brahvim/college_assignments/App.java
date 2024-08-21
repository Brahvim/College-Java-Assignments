package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class App {

	public enum AppConfigEntry {

		DB(),
		HOST(),
		PASS(),
		PORT(),
		USER(),
		DRIVER();

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
		final Map<String, String> config = new HashMap<>(AppConfigEntry.values().length);

		// Add flags in:
		for (final var s : p_args) {
			final AppFlag f = AppFlag.identifierToFlag(s);

			if (f == null)
				continue;

			flags.add(f);
		}

		App.readConfigFile(config);

		// Now we'll establish a connection with the DB:
		final Connection connection = App.ensureConnection(config);
	}

	public static Connection ensureConnection(final Map<String, String> p_config) {
		try {

			final String
			/*	 */ db = p_config.get(AppConfigEntry.DB.toString()),
					host = p_config.get(AppConfigEntry.HOST.toString()),
					port = p_config.get(AppConfigEntry.PORT.toString()),
					pass = p_config.get(AppConfigEntry.PASS.toString()),
					user = p_config.get(AppConfigEntry.USER.toString()),
					driver = p_config.get(AppConfigEntry.DRIVER.toString());

			final Scanner sc = new Scanner(System.in);

			for (final Map.Entry<String, String> e : p_config.entrySet()) { // ALWAYS use `var`! I adapted this late...
				if (!"".equals(e.getValue())) // Why is `""` first? `NullPointerException`s!
					continue;

				System.out.printf(

						"Did not find an entry for `%s` in the config file. Please provide it: ",
						e.getKey()

				);

				e.setValue(sc.nextLine());
			}

			sc.close(); // :(
			// This was supposed to be needed again for interactive CLI program...

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

	/**
	 * Reads the config file and stores it in the given
	 * {@linkplain Map Map&lt;String,String&gt;}.
	 */
	public static void readConfigFile(final Map<String, String> p_config) {
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

				final String value = line.substring(separatorId + 1);
				final String key = line.substring(0, separatorId);

				p_config.put(key, value);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
