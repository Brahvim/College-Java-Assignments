package com.brahvim.college_assignments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class App {

	public static void main(final String[] p_args) {
		final File file = new File("./res/File.f");

		try (final FileReader reader = new FileReader(file)) {

			long lineCount = 0;
			System.out.println("File contents:\n");

			for (int i = 0; reader.ready(); i = reader.read())
				if (i == '\n')
					++lineCount;

			System.out.println(

					"\n" + (lineCount == 0
							? "(End of EMPTY file.)"
							: "(End of file contents.)")

			);

		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SecurityException e) {
			System.out.printf(

					"VM disallowed from reading %s `%s`.%n",
					file.isDirectory() ? "directory" : "file",
					file.getPath()

			);
		}
	}

}
