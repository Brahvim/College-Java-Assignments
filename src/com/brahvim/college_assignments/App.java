package com.brahvim.college_assignments;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

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

		final Map<AppConfigEntry, String> configuration = AppParser.beginAllParsing(p_args);
		System.out.println("Welcome to \"Yet Another DB CLI\"...");

		// Now we'll establish a connection with the DB:
		try (final Connection connection = AppDbUtils.ensureConnection(configuration)) {

			System.out.println("Connected! Please go on:\n");
			final StringBuilder fullStatementStringBuilder = new StringBuilder();
			final String statementBegin = String.format("[%s])> ", configuration.get(AppConfigEntry.DRIVER));

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

	public static void exit(final AppExitCode p_flag) {
		System.exit(p_flag.ordinal());
	}

}
