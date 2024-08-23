package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class App {

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

		if (App.readConfigFile(configuration)) {
			App.endConfigFileErrors();
			App.exitApp(AppExitCode.INVALID_CONFIG_FILE);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("\nBye! ^-^")));

		if (flags.contains(AppFlag.CHECK_CONF))
			System.exit(AppExitCode.OKAY.ordinal());

		// Now we'll establish a connection with the DB:
		try (final Connection connection = AppDbUtils.ensureConnection(configuration)) {

			System.out.println("Connected! Please go on:\n");

			final StringBuilder fullStatementStringBuilder = new StringBuilder();
			final String statementBegin = String.format(

					"[%s][%s])> ",
					configuration.get(AppConfigEntry.DRIVER),
					configuration.get(AppConfigEntry.DB)

			);

			System.out.print(statementBegin);
			System.out.flush(); // Good for interactive mode!
			// Even if it doesn't fix that UNKNOWN BUG!

			while (true)
				App.interactiveModeIteration(connection, fullStatementStringBuilder, statementBegin);

		} catch (final SQLException e) {
			// e.printStackTrace();

			// if (e instanceof SQLInvalidAuthorizationSpecException) // NOSONAR! Look at
			// // the exception's class hierarchy!
			// System.out.println("Access denied! Password incorrect...!");
			// else

			System.out.println(e.getMessage());
		}

		App.exitApp(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
	}

	private static void interactiveModeIteration(final Connection connection,
			final StringBuilder fullStatementStringBuilder,
			final String statementBegin) {
		try {

			final String line = System.console().readLine();
			fullStatementStringBuilder.append(line);

			// Using these instead of `String::endsWith()` for performance:
			if (';' == line.charAt(line.length() - 1)) {
				AppDbUtils.runQueryForConnection(fullStatementStringBuilder.toString(), connection);
				System.out.printf("%n%s", statementBegin);
				fullStatementStringBuilder.setLength(0);
				System.out.flush();
				return;
			}

			fullStatementStringBuilder.append(' ');
			System.out.print("-> ");
			System.out.flush();

		} catch (final NullPointerException e) {
			App.exitApp(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
		}
	}

	public static void exitApp(final AppExitCode p_flag) {
		System.exit(p_flag.ordinal());
	}

	public static void endConfigFileErrors() {
		System.out.println("\n====END OF CONFIG FILE ERRORS.====\n");
	}

	public static void beginConfigFileErrors() {
		System.out.println("\n=======CONFIG FILE ERRORS!=======\n");
	}

	/**
	 * Reads the config file and stores it in the given
	 * {@linkplain Map Map&lt;String,String&gt;}.
	 */
	public static boolean readConfigFile(final Map<AppConfigEntry, String> p_config) {
		try (

				final FileReader fileReader = new FileReader("./.env");
				final BufferedReader reader = new BufferedReader(fileReader);

		) {

			String line = null;
			boolean hasErrors = false;

			for (int i = 0; reader.ready();) {

				if (line == null)
					line = reader.readLine();

				final int commentStart = line.indexOf('#');

				switch (commentStart) {
					case -1 -> {
						// Nothing!
						// DON'T remove this case!
						// It's here to ensure that `default` isn't executed!
					}
					case 0 -> {
						continue;
					}
					default -> {
						line = line.substring(0, commentStart - 1);
					}
				}

				final int separatorId = line.indexOf('=');

				if (separatorId == -1) {
					if (!(line.isBlank() || (commentStart == 0))) {
						if (!hasErrors) {
							hasErrors = true;
							App.beginConfigFileErrors();
						}

						System.out.printf("\t(Line content: \"%s\".)%n", line);
						System.out.printf("- Line `%d`: Missing `=`.%n", i + 1);
					}

					++i; // NOSONAR, you don't understand the logic!
					line = null;
					continue;
				}

				final String value = line.substring(separatorId + 1);
				final AppConfigEntry configEntry = AppConfigEntry.valueOf(line.substring(0, separatorId));
				final String status = configEntry.getChecker().apply(value);

				if (status != null) {
					if (!hasErrors) {
						hasErrors = true;
						App.beginConfigFileErrors();
					}

					System.out.print("- ");
					System.out.println(status);
				}

				p_config.put(configEntry, value);
				line = null;
				++i; // NOSONAR, you don't understand the logic!
			}

			return hasErrors;

		} catch (final IOException e) {
			System.out.println("Could not read file...");
			e.printStackTrace();
			return true;
		}
	}

}
