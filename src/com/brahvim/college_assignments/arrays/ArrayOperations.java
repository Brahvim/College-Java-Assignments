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
		// If we're still thinking in terms of copying, here we go:

		// We can do better than using an ordinary Java loop: asking the JVM
		// to do this task for us:
		System.arraycopy(firstArray, 0, anotherArray, 0, firstArray.length);
		// `System.arraycopy()` will copy `firstArray.length` elements from `firstArray`
		// to `anotherArray`.

		// It'll copy `firstArray[0]` to `anotherArray[0]`, and keep going.
		// The copying performed is a "shallow copy"; this means that only a reference
		// to the object currently in `firstArray[0]` will be copied.

		// Primitive data-types are copied by value, even if boxed (I think).

		// For objects, this means:
		// - If the object at `firstArray[0]` changes, the copy will show those changes.
		//
		// - If `firstArray[0]` suddenly holds a different object or `null`,
		// `anotherArray[0]`'s held object won't change - no matter what it is.
		//
		// - If the object hasn't been removed from both arrays, the garbage
		// collector ("GC") *won't* delete it.

		// ...And over here, we do a second copy:
		System.arraycopy(secondArray, firstArray.length, anotherArray, 0, secondArray.length);

		// Let's talk about the JVM more, actually.

		// When we asked the JVM to do a copy for us, we called `System.arraycopy()`,
		// which is a "`native` method".

		// This means that the actual code that gets run when you call the method was
		// written in C or C++.

		// This is because the JVM provides an API called the "Java Native Interface",
		// *a.k.a.* "JNI" to allow *platform-native binaries* to get loaded and run.

		// By the term "platform-native binaries", I am referring to programs that
		// contain assembly-style *"routines"* - assembly *labels* that contain
		// instructions instead of data, really.

		// "Labels" that provide a list of instructions to execute, like a `case` in a
		// `switch`, the code in which will be run, and finally, if a `break;` is not
		// encountered, will allow the CPU to run code from the next address in RAM,
		// which is generally either the next label, or a part of the program that
		// doesn't actually contain instructions which an operating system or some
		// built-in safeguard in the CPU will then ask the CPU to stop reading into.

		// If you didn't know, C/C++ functions ***are*** converted to labels with some
		// "prologue" and "epilogue" code, which may sometimes be in other labels.
		// This is because the registers of the CPU and the RAM attached to the CPU can
		// both be used for storing and sharing data between routines.
		// All routines need to grab data from these, and save data to them.

		// If each routine shared data in its own way, compilation of programs could
		// become difficult and time-consuming, since the compiler would have to spend
		// time figuring out how exactly to call the routine.

		// There is however, significant advantage in changing how this is done.
		// Thus, all CPU architectures as well as operating systems provide their own
		// sets of "routine calling conventions", which C/C++ compilers as well as
		// assemblers then provide intrinsic keywords or macros for, always along with a
		// default. On *nix OSs, this is generally the Unix System V calling convention
		// for "i386" / "x86" (32-bit) as well as "amd64" / "x86-64" chips, while on
		// Microsoft Windows, this is the "`cdecl`" calling convention (I think).

		// Anyway - as long as you're using calling conventions supported by the exact
		// architecture and operating system, and package your assembly routines, or
		// C/C++ compiled code into a *dynamic library*, the JVM will be able to call
		// it like any other method, `static` or not. But yes, such as method has to be
		// marked as `native`.

		// An interesting fact about `native` methods is that the JVM successfully
		// begins executing their code within very little time - generally less than a
		// nanosecond (I THINK!). This is not the case with ordinary Java methods. Those
		// take a bit longer, about a nanosecond or so for the JVM to get into their
		// code, even though both Java bytecode as well as native code are in RAM.

		// So, I mentioned "dynamic libraries" in the second-last paragraph before this
		// one.
		// What are they?

		// Well, when you compile a program, you expect to get a file that you can ask
		// your OS to run. On Windows, that's double-clicking a file with a name that
		// ends in `.exe`.

		// Now, a dynamic library is a file that your compiler can produce, that
		// contains your code, compiled, but not ready for execution like I talked
		// about.

		// These libraries are made for programs to load and call functions from.
		// And... YEP! That's it! That's how the JVM calls your assembly routines, or
		// your C/C++ functions.

		// When you compile a C/C++ program / *assemble* an assembly program to either
		// the dynamic library or executable format, the file produced contains the
		// functions, their calling conventions, parameters, names and everything the OS
		// needs to know. C/C++ functions' names, though, are "mangled" before this -
		// which makes them appear less readable, but allows storing type information
		// about parameters and what the function returns within just the function's
		// *mangled* name.

		// Also, dynamic libraries are stored:
		// - On Windows as `.dll` - "Dynamically Linked Library" files,
		// - On macOS as `.dylib` - "Dynamic Library" files,
		// - On all other *nix systems as `.so` - "Shared Object" files.

		// These are exactly what the JVM will ask you for.
		// Check out `System.load()` and `System.loadLibrary()`.

		// Anyway. Back to the JVM.
		// With a bit of a JNI mention in it:

		// This prints a string that shows how the JVM sees the array.
		System.out.println(anotherArray.toString());
		// The JVM, *and* C/C++ code that's given to the JVM via the JNI, are what
		// benefit most from this representation of Java objects.

		// When using C/C++ i.e. JNI code to talk to the JVM, you have to
		// represent data types as strings in this new syntax that we saw after calling
		// the method, `toString()`.

		// In the case of this array, which is an `int[]`, it's the part before the `@`.
		// The part AFTER the `@` is the object's *hash-code* in **hexadecimal**.

		// You can obtain the decimal version in these ways:
		System.out.println(anotherArray.hashCode());
		System.out.println(Objects.hashCode(anotherArray));

		// If the object you're trying to get the hash-code for is `null`, the first one
		// will cause a crash. The second one returns `0` if it sees `null`.
		// ...At least when running with the OpenJDK JRE 8 and above.
		// As I write these programs, I use OpenJDK 17 LTS and its JRE.

		// Yes, it is a "hash-code" generated after "hashing".
		// This "hashing" serves the same purpose as MD5, SHA1, et cetera.
		// The exact hashing method of course, is most appropriate for use with Java.

		// Hash-codes can change with the properties of the object.
		// Do not rely on them outside of your own Java program or any JNI code attached
		// to it. They might not behave as you expect in every case.

		// To be precise, I mean to mention cases where you "save" your hashes,
		// exit your Java program, and loads them back for use, some other time it runs.

		// ("Serialization" is a Java feature you can learn about later.)

		// (It's basically like writing all bytes of a C `struct` into a file,
		// but suited for Java classes, which of course, can have a change in the
		// methods they have between different runs of your program, causing integrity
		// issues that then become too tedious to solve using ***just*** Java code.
		// ...Hence, the serialization feature exists in Java.)

		// (Java's own method of serialization is sometimes left-out in favor of
		// libraries like Google's "Protobuf", which was made to bring this
		// serialization feature to C and C++. However, Protobuf supports more
		// standardized formats like JSON. Java serialization as of the JDK 21 LTS era,
		// does not.)
	}

}
