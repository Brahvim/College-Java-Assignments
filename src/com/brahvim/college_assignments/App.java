package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {

	public static void main(final String[] p_args) {
		// `^C`/`^D` Unix signal handler:
		Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("\nBye! ^-^")));

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

		final AtomicBoolean parsingOver = new AtomicBoolean();

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					synchronized (parsingOver) {
						if (parsingOver.get())
							return;

						System.out.println("(Still parsing flags! Please wait...)");
						parsingOver.notifyAll();
					}
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

		}.start();

		final Map<AppFlag, String> flags = new EnumMap<>(AppFlag.class);
		final Map<AppConfigEntry, String> configuration = new EnumMap<>(AppConfigEntry.class);

		// Parse flags in:
		for (final var s : p_args) {
			final int length = s.length();

			switch (length) {
				case 0 -> {
				}

				case 1 -> {
					final AppFlag parsed = AppFlag.identifierToFlag(s);

					if (parsed == null)
						App.exit(AppExitCode.UNKNOWN_FLAG_PASSED);

					flags.put(parsed, null);
				}

				default -> {
					for (int i = 0; i < length; ++i) {
						final char c = s.charAt(i);

						if (c == '-')
							continue;

						final AppFlag parsed = AppFlag.identifierToFlag(Character.toString(c));

						if (parsed == null)
							App.exit(AppExitCode.UNKNOWN_FLAG_PASSED);

						// ...Store it already:
						flags.put(parsed, null);
					}
				}
			}
		}

		if (!flags.containsKey(AppFlag.IGNORE_CONF) && App.readConfigFile(flags, configuration)) {
			App.endConfigFileErrors();
			App.exit(AppExitCode.INVALID_CONFIG_FILE);
		}

		if (flags.containsKey(AppFlag.CHECK_CONF_AND_EXIT))
			System.exit(AppExitCode.OKAY.ordinal());

		synchronized (parsingOver) {
			parsingOver.set(true);
		}

		System.out.println("Welcome to \"Yet Another DB CLI\"...");

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

		App.exit(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
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
			App.exit(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
		}
	}

	public static void exit(final AppExitCode p_flag) {
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
	public static boolean readConfigFile(
			final Map<AppFlag, String> p_flags,
			final Map<AppConfigEntry, String> p_config) {
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

					++i;
					line = null;
					continue;
				}

				final String value = line.substring(separatorId + 1).trim();
				final String entryName = line.substring(0, separatorId).trim();
				final AppConfigEntry configEntry = AppConfigEntry.valueOf(entryName);

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
				++i;
			}

			return hasErrors;

		} catch (final IOException e) {
			System.out.println("Could not read file...");
			e.printStackTrace();
			return true;
		}
	}

}
