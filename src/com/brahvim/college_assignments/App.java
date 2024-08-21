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

		final String[] missingConfigEntries = App.checkConfig(config);
		App.promptConfigCompletion(missingConfigEntries, config);

		// Now we'll establish a connection with the DB:
		final Connection connection = App.connect(config);
	}

	public static Connection connect(final Map<String, String> p_config) {
		// First, we derive the URL to send HTTPS requests to:
		try {

			final String dbUrl = String.format(

					"jdbc:mariadb://localhost:%s/%s",
					p_config.get(ConfigFileEntry.PORT.toString()),
					p_config.get(ConfigFileEntry.DB.toString())

			);

			System.out.println(dbUrl);

			return DriverManager.getConnection(

					dbUrl,
					p_config.get(ConfigFileEntry.HOST.toString()),
					p_config.get(ConfigFileEntry.PASS.toString())

			);

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** @return A {@code String[]} with names of missing entries in uppercase. */
	public static String[] checkConfig(final Map<String, String> p_config) {
		final ConfigFileEntry[] allPossibleEntries = ConfigFileEntry.values();
		final String[] toRet = new String[allPossibleEntries.length];

		int i = 0;

		for (final ConfigFileEntry e : allPossibleEntries) {
			final String entryName = e.toString();

			if (!p_config.containsKey(entryName)) {
				toRet[i] = entryName;
				++i;
			}
		}

		return toRet;
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

				final String value = line.substring(separatorId);
				final String key = line.substring(0, separatorId);

				p_config.put(key, value);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void promptConfigCompletion(final String[] p_missings, final Map<String, String> p_config) {
		int actualEntryCount = 0;

		for (; actualEntryCount < p_missings.length; ++actualEntryCount)
			if (p_missings[actualEntryCount] == null)
				break;

		// WAY less cost than making an entire new `Scanner`!:
		if (actualEntryCount == 0)
			return;

		final Scanner sc = new Scanner(System.in);
		while (actualEntryCount-- > 0) {
			final String s = p_missings[actualEntryCount];
			System.out.printf("Did not find an entry for `%s` in the config file. Please provide it: ", s);
			p_config.put(s, sc.nextLine());
		}

		sc.close();
	}

}
