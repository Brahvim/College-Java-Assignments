package com.brahvim.college_assignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

public class App {

	// "Get a file at runtime", they said:

	// public static void showHelp() {
	// System.out.println("""
	// Usage: fstats FILE
	// ...Or: fstats FILE
	// """);
	// }

	// And I **KNOW** what they meant.
	// Ugh.

	// (They want an interactive program. Ugh. Unix philosophy, anyone?)

	public static void main(final String[] p_args) {
		final Scanner sc = new Scanner(System.in);
		System.out.print("Please enter the path to a file (absolute/relative): ");
		final String filePath = sc.nextLine();
		sc.close();

		final long wordCount, lineCount, charCount;
		final File file = new File(filePath);

		// ...Or do you prefer this?!:
		try (

				Reader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);

		) {
		} catch (final IOException e) {
			System.out.println("Could not read file. Does it exist?");
		} catch (final SecurityException e) {
			System.out.printf("Program JVM not permitted to read FILE at `%s`%n", filePath);
		}
		// (Read previous commit!)

	}

}
