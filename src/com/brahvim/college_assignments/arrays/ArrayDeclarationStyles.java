package com.brahvim.college_assignments.arrays;

public class ArrayDeclarationStyles {

	// Empty, `private`, "null" constructor so nobody constructs from this class:
	private ArrayDeclarationStyles() {
		throw new IllegalAccessError(); // `Error`s in Java cannot be caught or *somehow* recovered from!
	}

	public static void main(final String[] p_args) {
		// Arrays can be declared in both Java's more data-type reliant and C++ styles:

		@SuppressWarnings("unused") // (This is a Java "annotation". **Feel free to ignore!**)
		final int[] javaStyle;
		// This is the preferred style for Java arrays.

		// When using the *generics* feature of Java,
		// you'll see that arrays are in fact, also a data type.

		// ...Just like how the preference for the `*` of a pointer is always on the
		// side of the variable in all languages that have them, the "Java style"
		// should serve as a good reminder.

		// Yes, generics were introduced in the language *much later*, but the purpose
		// of this style was to *look cleaner*, which the C/C++ one doesn't exactly do.

		// The C/C++ style was included in Java so it was easier for programmers to
		// switch from C++. Of course, it's not as easy for beginners to interpret.
		// ...Though, even actual programmers can sometimes miss the square brackets!

		@SuppressWarnings("unused")
		final int cAndCppStyle[]; // NOSONAR <- Starting a comment with this shuts my "linter".
		// They do not require any kind of immediate initialization like in C/C++
		// (where array length needs to be known at compile-time;
		// arrays in said languages are always stack-allocated).
		// **This is because Java treats arrays as heap-allocated objects.**
		// (Most data in Java is heap-allocated.)

		// Like C/C++, Java arrays can be initialized with curly brackets:
		final int[] initializedArray = {
				0, 5, 5, 5,
				6, 1, 6, 6,
				9, 9, 0, 9,
				4, 4, 4, 1, // Yes, "trailing commas" are allowed.
		};

		// Unlike in C++, you can actually iterate over Java arrays in a for-each loop:
		for (final int i : initializedArray)
			System.out.println(i);

		// Set-notation may be used instead, but not exactly like in other languages:
		for (final int i : new int[] { 0, 1 }) // Note how array initialization is needed.
			System.out.println(i);

		// Of course iteration is allowed in the usual manner.
		// Do note the presence of the `length` property:
		for (int i = 0; i < initializedArray.length; i++)
			System.out.println(i);
	}

}
