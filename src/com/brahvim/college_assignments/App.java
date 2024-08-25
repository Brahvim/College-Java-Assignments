package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brahvim.college_assignments.enums.AppConfigEntry;
import com.brahvim.college_assignments.enums.AppExitCode;

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

		final Map<AppConfigEntry, String> configuration = /* */
				AppParser.beginAllParsing(new String[] { "-r", "./Test.sql", "-r", "./Test copy.sql" });
		// AppParser.beginAllParsing(p_args);
		System.out.println("Welcome to \"Yet Another DB CLI\"...");

		App.askUserForAbsentConfigs(configuration);

		final String dbUrl = AppDbUtils.formJdbcUrl(configuration);

		// Now we'll establish a connection with the DB:
		try (final Connection connection = AppDbUtils.connectToDb(dbUrl, configuration)) {

			System.out.println("Connected!");
			final StringBuilder fullStatementStringBuilder = new StringBuilder();
			App.runSqlFiles(connection, configuration);

			final String statementBegin = String.format("[%s])> ", configuration.get(AppConfigEntry.DRIVER));
			System.out.println("Please go on:");

			System.out.print(statementBegin);
			System.out.flush(); // Good for interactive mode!
			// Even if it doesn't fix that UNKNOWN BUG!

			while (true) {
				App.interactiveModeIteration(statementBegin, connection, fullStatementStringBuilder);
			}

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

	private static void askUserForAbsentConfigs(final Map<AppConfigEntry, String> p_config) {
		for (final var e : AppConfigEntry.values()) {
			final String oldValue = p_config.get(e);

			if (oldValue != null)
				continue;

			System.out.printf(

					"".equals(p_config.get(e))
							? "Entry for `%s` in the file ignored. Please provide new entry data: "
							: "Did not find an entry for `%s` in the config file. Please provide it: ",
					e.toString()

			);

			String value = "";
			boolean valid = false;

			while (!valid) {
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
	}

	public static void runSqlFiles(final Connection p_connection, final Map<AppConfigEntry, String> p_configuration) {
		final String[] quotedPaths = p_configuration.get(AppConfigEntry.SQL_PATH).split(", ");

		if (quotedPaths.length < 2 || quotedPaths[0].isEmpty())
			return;

		System.out.println("Running scripts:");
		final String[] paths = new String[quotedPaths.length];
		for (int i = 0; i < paths.length; i++) {
			final String p = quotedPaths[i];
			paths[i] = p.substring(1, p.length() - 1);
		}

		for (int i = 0; i < paths.length; i++)
			App.runSingleSqlFile(paths[i], p_connection);

		System.out.println("Successfully run all scripts!");
	}

	public static void runSingleSqlFile(final String p_path, final Connection p_connection) {
		final File file = new File(p_path);
		final StringBuilder fullStatementStringBuilder = new StringBuilder();

		try (final var reader = new BufferedReader(new FileReader(file))) {

			System.out.printf("- Executing script at path `%s`:%n", p_path);

			while (reader.ready()) {
				final String line = reader.readLine();

				System.out.print('\t');
				System.out.println(line);
				fullStatementStringBuilder.append(line);

				// Using these instead of `String::endsWith()` for performance:
				if (line.isEmpty() || ';' != line.charAt(line.length() - 1)) {
					fullStatementStringBuilder.append(' ');
					continue;
				}

				final var statement = p_connection.createStatement();
				statement.execute(fullStatementStringBuilder.toString());
				statement.close();

				fullStatementStringBuilder.setLength(0);
			}

		} catch (final SecurityException e) {
			System.out.printf("VM not allowed to open script file at `%s%`.%n", p_path);
			App.exit(AppExitCode.SQL_PATH_INVALID);
		} catch (final SQLException e) {
			System.out.println("ERROR! Crashing...");
			App.exit(AppExitCode.SQL_ERROR);
		} catch (final IOException e) {
			if (e instanceof FileNotFoundException) {
				if (file.isDirectory())
					System.out.printf("File `%s` does not exist.%n", p_path);

				System.out.printf("File `%s` does not exist.%n", p_path);
				App.exit(AppExitCode.SQL_PATH_INVALID);
			}

			System.out.println("I/O issues reading script; crashing...");
			App.exit(AppExitCode.IO_ISSUE);
		}

	}

	public static void interactiveModeIteration(
			final String p_statementBegin,
			final Connection p_connection,
			final StringBuilder p_fullStatementStringBuilder) {
		try {

			final String line = System.console().readLine();
			p_fullStatementStringBuilder.append(line);

			// Using these instead of `String::endsWith()` for performance:
			if (';' == line.charAt(line.length() - 1)) {
				AppDbUtils.runQueryForConnection(p_fullStatementStringBuilder.toString(), p_connection);
				System.out.printf("%n%s", p_statementBegin);
				p_fullStatementStringBuilder.setLength(0);
				System.out.flush();
				return;
			}

			p_fullStatementStringBuilder.append(' ');
			System.out.print("-> ");
			System.out.flush();

		} catch (final NullPointerException e) {
			App.exit(AppExitCode.OKAY); // This is a safeguard for those who copy/refactor code!
		}
	}

	public static AtomicBoolean startApologyThread(final int p_millisWaits, final String p_apologyLog) {
		final AtomicBoolean toRet = new AtomicBoolean();

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(p_millisWaits);

					if (!toRet.get())
						System.out.println(p_apologyLog);
				} catch (final InterruptedException e) {
					super.interrupt();
				}
			}

		}.start();

		return toRet;
	}

	public static void exit(final AppExitCode p_flag) {
		System.exit(p_flag.ordinal());
	}

}
