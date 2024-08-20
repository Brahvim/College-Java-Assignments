package com.brahvim.college_assignments.strings;

public class JavaStringMethods {

	// I sure hope you read `JavaStringFeatures.java` first!

	public static void main(final String[] p_args) {
		// Let's get through these quickly.
		// I'll not go through all of them, but hopefully the most used ones.
		// And definitely some new ones!

		final String firstName = "Brahvim";

		final int letterId = firstName.indexOf('a');
		System.out.println("In string `" + firstName + "`, the position of `a` is `" + letterId + "`.");

		// Here, I have a palindrome:
		final String spacedButPalindromic = "Brahvim! !mivharB";

		// Using `String::split()` (an instance method! Note the `::`!), I can split
		// a string into a list of strings - an *array* of strings, rather!
		// All I need to tell `String::split()` is the text that separates all parts of
		// it. This can use regular expression (*a.k.a.* "RegEx"). I didn't show that:

		final String[] stringAndPalindrome = spacedButPalindromic.split(" ");

		// PS Java has C's `printf()`! The format strings differ, is all.
		// `String.format()` (NOTE THE `.`! That method is `static`!) formats the exact
		// same way as `System.out.printf()`, so I'll show that here first:

		System.out.println(

				// PS Don't use `\`s before `'`s and `"`s thinking that you'd save yourself from
				// having to type them out when the multi-line string has to be broken into
				// many single-line later, because corporate decided to move to an older Java
				// version. `\`s in multi-line strings already have a purpose; they combine a
				// line with the next one! Writing `\"` will confuse you instead. Use only `"`!

				String.format(

						"""
								Useless facts `101`!:
								%d) Here\'s a string!: "%s".
								%d) And here\'s its reverse!: "%s".
								""", // Reminder: `printf()` needs an extra `\n` at the end!
						// It's best to use a `%n` for this in Java (so you have `\n` on *nix OSs but
						// `\r\n` on Windows and now-missing typewriters and printers.)

						1, stringAndPalindrome[0],
						2, stringAndPalindrome[1] // Format these however you like!...

				) // End of `String.format()` call.

		); // End of `System.out.println()` call.

		// I will use `System.out.printf()` going forward.

		// Now - `String::substring()` is a suuuuuper common and suuuuuper important
		// method. MEMORIZE IT WELL!
		// I did too! Over all these years. Anyway:

		// The "substring" method allows you to extract a string from a string.

		// Something you'll need to do often is to find the index of a character and
		// then "cut" the string from there.

		// While `String::split()` does exactly this - and with actual strings, even,
		// `String::substring()` can be better simply because it's much more "low-level"
		// and such.

		// *Specifically* speaking - it's because you can get a few advantages or figure
		// out some optimizations.
		// Best part is that it lets you handle the error condition of the
		// character/pattern not being present and lets you escape early. Faster!

		// Moreover, you're also not making arrays of text.
		// This kind of decision depends on your workload. If you're dealing with huge
		// amounts of text, it's a good idea to stick to arrays for batched processing.

		// Don't think so procedurally then! Most programs work with a lot of data and
		// thus *should be* able to batch their tasks into a pipeline-like structure.
		// This is very important for performance. Pruning hierarchies of callbacks
		// calling callbacks (think "functions calling functions" if you don't know what
		// a "callback" is) is a waste of time, to be honest. Using a `boolean` flag to
		// reduce them has a similar price on performance.

		// Learn to flatten your data structures;
		// it flatters you back with the performance numbers!

		// DB normalization has awesome uses like this.
		// This is the kind of stuff I've been learning in the name of data-oriented
		// design. Dynamic and "pipelined" (think "batched") systems are always faster,
		// and being simple or **flat** is what allows you to be like this *easily*.

		// Anyway! Here, I have an example for `String::substring()` keeping your code
		// fast and clean:

		// `String::indexOf()` / `String::lastIndexOf()` return `-1` when they can't
		// find the character/pattern needed.

		// `String::split()` doesn't do that. And it returns an ARRAY - certainly not a
		// small stack-allocatable integer! Also, the array is empty. Wasted memory!

		if (letterId == -1)
			System.exit(1); // So... we... just exit.

		// If that doesn't happen, we can continue to make a substring:
		final String subName = firstName.substring(letterId);
		System.out.println(subName);

		// There are two overloads for `String::substring()`.

		// One lets you include the character at the position you give it and everything
		// after it.

		// The other lets you include the character at the *first* position you give it,
		// but not the *last*. The character at the last position is eliminated -
		// because methods for indexing strings - such as `String::indexOf()` - use
		// one-based indices. Or, well, `String::length()`. Which you should absolutely
		// never pass in there. **Always** use the **other overload** instead!

		// Next method is `String::charAt()`. Quite obvious given the name, right?
		@SuppressWarnings("unused")
		final char characterA = firstName.charAt(letterId);

		// `String::concat` concatenates two strings and returns a new one.
		// The strings in question could be the `String` object you're calling the
		// method on, and another string you pass into the method itself:

		final String firstNameSpaced = firstName.concat(" "); // Can't pass `char`s.

		final String fullName = firstNameSpaced.concat("Bhaktvatsal");

		// You can compare strings in many ways.

		// The simplest manner is by using `String::equals()`:

		if (firstName.equals(fullName.substring(0, fullName.indexOf(' '))))
			System.out.println("`fullName` and `firstName` match pretty well.");

		// You can compare strings with their cases ignored:
		if (firstName.equalsIgnoreCase("brahvim"))
			System.out.println("`firstName` is in fact, \"Brahvim\" in different casing.");

		// A more interesting manner is to use `String::compareTo()`:

		if (firstName.compareTo(fullName) > 1)
			System.out.println("IMPOSSIBLE!");

		// This method converts a strings' characters into integers according to the
		// encoding method (ASCII, *EBCDIC*, UTF-8...) used for it, then adds all those
		// integers up.

		// Both the string passed in as well as the `String` object the method is called
		// on are converted to numbers like this, and then this sum is used to compare
		// both strings' "sums". "Sums", but in a different space altogether - the
		// "encoding space".

		// This is most used for dictionary-style comparison of strings. If you use
		// ASCII or UTF-8 - encoding formats that have arranged letters of supported
		// languages in alphabetical order - then you'll be able to tell which letter
		// comes before/after which.

		// In the case of ASCII and UTF-8 specifically, this also accounts for uppercase
		// and lowercase characters. LOOK OUT FOR THAT!

		// Most programmers will convert to lowercase for this reason. Programmers and
		// Mathematicians love their lowercase stuff...

		// Now, `String::compareTo()` returns an **`int`**. This is because it has three
		// statuses to report:
		// - The strings being equal (`0`),
		// - Passed string being "lesser" than callee (less than `0`; negative),
		// - Callee string being "lesser" than the one passed (more than `0`; positive).
	}

}
