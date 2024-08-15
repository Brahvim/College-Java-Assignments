package com.brahvim.college_assignments.arrays;

import java.util.Objects;

public class ArrayOperations {

	private ArrayOperations() {
		throw new IllegalAccessError();
	}

	public static void main(final String[] p_args) {
		// This program should show how an array can be concatenated.
		// This should show how dynamic Java arrays are.

		// I'll declare a statically-sized array first
		// (the size of which is known at compile-time):

		final int[] firstArray = { 1, 5, 4, 4, 1, 5, 1, 3, 1, 1, 3 };

		// PS You can prefix the curly brackets with the type if wanted:
		final int[] secondArray = new int[] { 0, 5, 2, 4, 3, 5, 1, 5, 2 };
		// Generally speaking, it's best to *ignore* it.
		// Saves you typing when you suddenly need to change array types.

		// Finally, I'll make a *new array* called literally that:
		// int[] newArray; // Actually, no. Have another nice rule:

		// DON'T name your variables with keywords!
		// Your IDE's autocomplete could annoy you if you choose to do that.

		// I'll call it something else:
		int[] anotherArray;

		// I'll initialize it *now*:
		anotherArray = new int[firstArray.length + secondArray.length];
		// Could that expression be solved for at compile time? Totally.
		// But Java arrays ARE in fact, fully dynamic.
		// This `length` property is not tracked [much].

		for (int i = 0; i < firstArray.length; i++)
			anotherArray[i] = firstArray[i];

		for (int i = firstArray.length; i < anotherArray.length; i++)
			anotherArray[i] = secondArray[i - firstArray.length];

		// **There actually are way faster ways to copy arrays within Java itself.**

		// But before we talk about that, it is better for you to know that **allocating
		// an entirely new array of a given size is faster** than setting all elements
		// of an array to `0`/`null`. About 2 orders of magnitude (100x) faster.

		// Therefore, please look towards this option if possible.

		// However, we can do better than using an ordinary Java loop: asking the JVM
		// to do this task for us.

		// There are two ways to do this. I'll cover the *least convenient* one first:
		// System.arraycopy();

		// This prints a string that shows how the JVM sees the array.
		System.out.println(anotherArray.toString());
		// The "Java Native Interface" *a.k.a.* "JNI", benefits from these
		// representations.

		// When using C/C++ code to talk to the JVM (generally, this is used to make
		// "`native`" methods in Java, which are actual C/C++ functions), you have to
		// represent data types as strings in this syntax.

		// In the case of this array, which is an `int[]`, it's the part before the `@`.
		// The part AFTER the `@` is the object's *hash-code* in hexadecimal.

		// You can obtain the decimal version in these ways:
		System.out.println(anotherArray.hashCode());
		System.out.println(Objects.hashCode(anotherArray));

		// If the object you're trying to get the hash-code for is `null`, the first one
		// will cause a crash. The second one has a check.
		// At least when using the OpenJDK JRE.

		// Yes, it is a "hash-code" generated after "hashing".
		// This "hashing" serves the same purpose as MD5, SHA1, et cetera.
		// The exact code of course, is most appropriate for use with Java.

		// Hash-codes can change with the properties of the object.
		// Do not rely on them outside of your own Java program or any JNI code attached
		// to it. They might not behave as you expect in every case.

		// To be precise, I mean to mention cases where you "save" your hashes,
		// exit your Java program, and loads them back for use, some other time it runs.

		// ("Serialization" is a Java feature you can learn about later.)
		// (It's basically like writing all bytes of a C `struct` into a file,
		// but suited for Java classes, which of course, can have a change in the
		// methods they have between different runs of your program, causing integrity
		// issues that then become too tedious to solve using **just** Java code.)

	}

}
