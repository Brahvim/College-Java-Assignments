package com.brahvim.college_assignments.arrays;

public class ArraysExercise1 {

	// Empty, `private`, "null" constructor so nobody constructs from this class:
	private ArraysExercise1() {
		throw new IllegalAccessError(); // `Error`s in Java cannot be caught or *somehow* recovered from!
	}

	public static void main() {
		// Arrays can be declared in both Java's more data-type reliant and C++ styles:

		@SuppressWarnings("unused") // (This is a Java "annotation". **Feel free to ignore!**)
		final int[] javaStyle;

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
