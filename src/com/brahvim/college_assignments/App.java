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

		final File file = new File(filePath);
		long lineCount = 0, wordCount = 0, charCount = 0;

		// I guess I'm starting to prefer formatting like QtCreator!...
		// (They like their commas on the next line so you can delete-off function
		// parameters quick!)
		try (

				Reader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr)

		) {
			while (br.ready()) {
				final String line = br.readLine();
				final long length = line.length();

				++lineCount; // New line, ain't it?
				charCount += length;
				++charCount; // Accounting for the `\n`!

				if (length != 0) // If there's any content,
					++wordCount; // ...there's always at least one word on the line.

				final char[] chars = line.toCharArray(); // Hopefully more vectorizable.
				for (final char c : chars)
					// No QtCreator-styled formatting here.
					// VSCode for Java makes it look weird...
					if (c == ' ' || c == '\t')
						++wordCount;

				// A space is two words joined together!
				// That's the number of whitespace characters plus one!
			}
		} catch (final IOException e) {
			System.out.println("Could not read file. Does it exist?");
		} catch (final SecurityException e) {
			System.out.printf("Program JVM not permitted to read FILE at `%s`%n", filePath);
		}

		if (lineCount != 0)
			--charCount;

		System.out.printf("Line count: `%d`.%n", lineCount);
		System.out.printf("Word count: `%d`.%n", wordCount);
		System.out.printf("Character count: `%d`.%n", charCount);
	}

}
