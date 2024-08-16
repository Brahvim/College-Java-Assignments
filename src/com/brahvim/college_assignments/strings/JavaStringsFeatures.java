package com.brahvim.college_assignments.strings;

public class JavaStringsFeatures {

	private JavaStringsFeatures() {
		throw new IllegalAccessError();
	}

	public static void main(final String[] p_args) {
		// Welcome to the section for strings!

		// So - Java is a pretty "safe" language with a garbage collector and whatnot.
		// ...And strings are a huge part of it all.
		// I'll make sure to teach off as much as I can remember to tell.
		// JVM strings are a pretty large topic.

		// Firstly, let's discuss what a string really is.
		// A string is a set of characters, right?
		// But ust like audio, images, and video, a computer has to "encode" even text
		// into a format that it can work with - a format that relies on numbers.

		// As long as you can represent it with a C `struct`, you're good.
		// Now, there are many ways of doing this. ASCII, *EBCDIC*, Unicode (which is
		// made of UTF-8, UTF-16, and the like!...)

		// Now, Java strings are, ...like in C++, stored as objects.
		// In C++ we had `std::string` from the `std` *namespace*'s' `string` header,
		// in Java, we have he `java.lang.String` class from the `java.lang` *package*.
		// ...Which means that the `String` class is available everywhere without us
		// having to tell the compiler to `import` it in.

		// You know what? Let's make one:
		@SuppressWarnings("unused")
		final String name = "Brahvim";

		// ...And that was a string.

		// You could use newer Java features to avoid writing the type too, by the way!:
		@SuppressWarnings("unused")
		final var nameNoType = "`nameNoType` has the type `java.lang.String`!";

		// Now, watch this:

		@SuppressWarnings("unused")
		final String[] nameParts = "Brahvim Bhaktvatsal".split(" ");
		// Even strings constants are objects!
		// You can't do this with numbers *even if* they are in parenthesis, sorry.
		// I mean to say, that `byte a = (5).byteValue();` won't work.

		// `String`s have hash-codes too!:

		@SuppressWarnings("unused")
		final int nameHash = "Brahvim".hashCode();

		// In fact, the hashing function for Java `String`s has a chance of collision
		// low enough for the Java Language Specification ("JLS") to consider this crazy
		// feature for `switch`-case blocks!:

		@SuppressWarnings("unused")
		boolean forceChanges;

		for (final String s : p_args)
			switch (s) {
				case "-f": {
					forceChanges = true; // NOSONAR! This is example code!...
				}
					break;

				case "-v": {
					System.out.println("This example program's version number is `1.0.0`.");
				}
					break;

				default: { // NOSONAR! This is example code, I swear for the ~11th time in my life!
				}
					break;
			}

		// Yep! Java allows you to put `String`s in `switch`-`case` blocks too!
		// ...DON'T WRITE THAT ON THE EXAM! They might not even know of the feature yet!

		// Anyway, I think that feature has been here since JDK 11. 8 even? Who knows!

		// Also, here's a feature from JDK 15 that I've used in these assignments
		// before (in that "`fcpy`"" Unix-like utility program):

		final String multilineString = """
				This is a string that spans at least a few lines!
				Yep! JDK 15 feature, as I said!...
				C++ has "raw strings" for this.
				Those are different.
				""";

		System.out.println(multilineString);
		System.out.println("Notice how that string had an extra `\n` printing out at the end ;(!...");

		// Oh, and those `"""` need not be present on the next line:

		System.out.println("""
				Oh, and this string?
				It doesn't!""");
		System.out.println("And THIS line of text serves as proof of that!");

		// Just remember not to start from the same line as the first `"""`.

		// In C/C+, macros can be many lines long.
		// This is done by using a backslash at the end of the line.

		// Java multi-line `String`s provide a similar feature:
		System.out.println("""
				PS This string is \
				going to be on the same line. \
				It's written with backslashes at the end like a long C/C++ macro.
				""");

		// The difference? C/C++ macros get combined into a single line regardless of
		// the spaces before the `\`s!

		// And that should be a bunch about the syntax for one file. I've crossed a
		// `100` lines already.

		// PS Multi-line strings are still objects (OF COURSE THEY ARE!) if you were
		// unable to grasp that concept from earlier well-enough:
		System.out.println(

				/* NOSONAR! */ """
						This is indeed an object.
						And now I'll leave trailing spaaaceeeess!:		""".strip()

		);

		// ^^^ (That, shows off the `String::strip()` method too.)
		// (I'll talk about `String` methods next.)
		// (See you there!)
	}

}
