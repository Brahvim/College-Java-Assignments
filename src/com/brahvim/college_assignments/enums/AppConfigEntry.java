package com.brahvim.college_assignments.enums;

import java.io.File;
import java.sql.DriverManager;
import java.util.function.UnaryOperator;

public enum AppConfigEntry {

	DB(),
	HOST(),
	USER(), // Keep this before `PASS()`! Order important when prompting users...
	PASS(),
	PORT(v -> {
		try {
			final int port = Integer.parseInt(v);

			if (port > 65535 || port < 0)
				return "`PORT` must be in the range `[0, 65535]`.";

			return null;
		} catch (

	final NumberFormatException e) {
			return "`PORT` must be an integer.";
		}
	}), CONF_PATH(

			v -> new File(v).exists() ? null
					: "The configuration file certainly doesn't exist. This is a weird error you should never see."

	), SQL_PATH(v -> {
		final boolean exists = new File(v).exists();
		final boolean isSqlFile = v.endsWith(".sql");

		if (!exists)
			return String.format("File `%s`.%n", v);

		if (!isSqlFile)
			return String.format("File `%s` doesn't seem to be an SQL script.%n", v);

		return null;
	}), DRIVER(v -> {
		final String toRetPrefix = DriverManager.drivers().anyMatch(d -> d.toString().contains(v))
				? null
				: String.format(

						"Driver `%s` is not available. Please recompile with the required JDBC Java Archive (\"JAR\").%n",
						v.substring(v.indexOf('=') + 1) // Pretty good code!

		);

		if (toRetPrefix == null)
			return null;

		final StringBuilder toRetBuilder = new StringBuilder(toRetPrefix);
		toRetBuilder.append("\tList of currently available drivers:\n");

		// Have to call `DriverManager.drivers()` twice because `Stream`s cannot be
		// iterated more than once!:
		DriverManager.drivers().forEach(d -> toRetBuilder.append("\t-> ").append('`').append(d.toString().split("@")[0])
				.append('`').append('\n')); // Most
											// failure
											// resistant
											// code
											// 👍️
		return toRetBuilder.toString();
	});

	// region Class stuff.
	private final UnaryOperator<String> checker;

	private AppConfigEntry() {
		this.checker = v -> null;
	}

	private AppConfigEntry(final UnaryOperator<String> p_checker) {
		this.checker = p_checker;
	}

	public UnaryOperator<String> getChecker() {
		return this.checker;
	}
	// endregion

}
