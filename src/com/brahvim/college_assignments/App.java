package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

	public enum ConfigFileEntry {

		DB(),
		HOST(),
		PASS(),
		PORT(),
		USER(),
		DRIVER();

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

		final Map<String, String> config = new HashMap<>(4);
		App.readConfigFile(config);

		// Now we'll establish a connection with the DB:
		final Connection connection = App.connect(config);
	}

	public static Connection connect(final Map<String, String> p_config) {
		try {

			final String
			/*	 */ db = p_config.get(ConfigFileEntry.DB.toString()),
					host = p_config.get(ConfigFileEntry.HOST.toString()),
					port = p_config.get(ConfigFileEntry.PORT.toString()),
					pass = p_config.get(ConfigFileEntry.PASS.toString()),
					user = p_config.get(ConfigFileEntry.USER.toString()),
					driver = p_config.get(ConfigFileEntry.DRIVER.toString());

			// HACK ALERT! We have the entry now, so we can remove it for now:
			p_config.put(ConfigFileEntry.DB.toString(), null);

			final Scanner sc = new Scanner(System.in);

			for (final Map.Entry<String, String> e : p_config.entrySet()) {
				if (!"".equals(e.getValue())) // Why is `""` first? `NullPointerException`s!
					continue;

				System.out.printf(

						"Did not find an entry for `%s` in the config file. Please provide it: ",
						e.getKey()

				);

				e.setValue(sc.nextLine());
			}

			sc.close();

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
