package com.brahvim.college_assignments;

import java.sql.DriverManager;
import java.util.function.UnaryOperator;

public enum AppConfigEntry {

	DB(),
	HOST(),
	USER(),
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
	}),

	DRIVER(

			v -> (

			DriverManager.drivers()
					.anyMatch(d -> d.toString().contains(v))
							? null
							: String.format(

									"Driver `%s` is not available. Please recompile with the required JDBC JAR.",
									v.substring(v.indexOf('=') + 1)

							)

			)

	);

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