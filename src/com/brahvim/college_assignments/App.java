package com.brahvim.college_assignments;

public class App {

	public static void main(final String[] p_args) {

		// I guess I should explain EVERYTHING about the Java threading API here.
		// ...Mostly the syntax I'll be using, haha.

		// The most naive way to make threads is to write a class that extends `Thread`:
		class Thread1 extends Thread {

			// And override the `Thread::run()` method:
			@Override
			public void run() {
				this.log("This thread is now running!");

				// Threads can be "interrupted" whilst they operate.
				// They can also "sleep" for a few milliseconds whilst they operate.

				// If a thread decides to sleep, it won't be able to detect interruptions.
				// For this reason, `Thread.sleep()` (yes, that's `static`!) *throws up:*

				try {
					Thread.sleep(50);
				} catch (final InterruptedException e) {
					super.interrupt(); // DON'T call `Throwable::printStackTrace()` here!

					// **If code you didn't write is running on the same thread as your code**,
					// you need to consider the fact that it might be relying on an interrupt.

					// ...And if an interrupt not from your own code is in fact generated, you MUST
					// pass it over! The other code won't have a HINT that this happened.

					// Forgetting to do this means breaking somebody's API and thus your app!
				}

				// (Also `Object::wait()` + `Object::notify()` / `Object::notifyAll()` is ALWAYS
				// a better way to wait than to use `Thread.sleep()`.)

				this.log("Literally everything is async and out of control!");
				this.log("Woooooo-hoo!");
			}

			// Okay, I'll put the rest of the class here:
			final String className = this.getClass().getSimpleName();

			private Thread1() {
				super.setName(this.className);
			}

			// Ignore my use of var-args here :>
			private void log(final Object... p_objects) {
				for (final Object o : p_objects) {
					System.out.print("[" + this.className + "]");
					System.out.print(' ');
					System.out.println(o.toString());
				}
			}

		}

		new Thread1().start();

		// And Java has "syntax sugar" for that:
		new Thread() {

			@Override
			public void run() {
				System.out.println("Hello from thread two!");
			}

		}.start();

		// There's an interface called `Runnable` that *basically* allows you to declare
		// functions without inputs and outputs. (More on this later!)

		// An instance of this can be passed to `Thread::Thread(Runnable)`:

		final Runnable thread3Runnable = new Runnable() {

			@Override
			public void run() {
				System.out.println("This message comes courtesy of thread three!");
			}

		};

		new Thread(thread3Runnable).start();

		// But you can make this code even shorter because of THE SPECIFIC CASE!
		// Java has this syntax:

		final Runnable thread4Runnable = () -> {
			System.out.println("\"Hi!\"m says thread four!");
		};

		// This syntax is used by many languages such as JavaScript, C++ and C#.

		// We call it "lambda syntax"; because *functional programming* (the alternative
		// OOP method to our *class-oriented* object-oriented programming that I and
		// many low-level performance code-liking people like along with their dosage of
		// C and data-oriented design), calls functions without a name, a "lambda".

		// Lambdas in Java are just objects with a single method. There's an annotation
		// for interfaces with a single method, called `@FunctionalInterface`.
		// Interfaces like `Runnable`, `Function`, `BiFunction`, `Consumer`,
		// `BiConsumer`, `Supplier`, `Predicate` and `BiPredicate` got declared with it.

		// The syntax I showed earlier can be used only with functional interfaces.
		// If you need more than one method, use the one shown before that - the
		// `new Object() { method1() { } method2() { } }` syntax!

		// Yes, yes, all objects made from interfaces *are* still instances of `Object`,
		// and thus **a `Runnable` too is just an object with a single method,
		// `Runnable::run()`**.

		// Now, knowing that they are actually objects, you can now start to learn that:
		// A `Runnable` essentially lets you make a function w/o parameters,
		// A `Consumer` function has one parameter,
		// A `Supplier` function returns one value, but has no parameter,
		// A `Function` has both one input and an output.
		// A `Predicate` is a function w/ one parameter *intended to be* `if`-statement.

		// The `Bi*` versions of course indicate a change from "one" to "two" in *all*
		// of the previous statements. *Don't change the "a"s or "an"s or others!*

		new Thread(thread4Runnable).start();

		// ...Aaand of course, here's the version I like to use. Prepare for brackets!:
		new Thread(() -> System.out.println("thread 5 chillin' in 1 line. yo.")).start();

		// A bit more on lambdas by the way - you can do this!:

		@FunctionalInterface // You can... define your own functional interface!
		interface Adder {

			int addThemNumbers(int a, int b);

		}

		// ...And make an object out of it:
		final Adder ohGodNobodyWillReadThis = (a, b) -> a + b; // **No brackets required for a single statement!**
		System.out.println(ohGodNobodyWillReadThis.addThemNumbers(0, 1));

		// There's a "fun" way to do this:

		final var funWayToDoDis = (Adder) (a, b) -> a + b; // ...Using a cast, yeah. Trust me, I've used this in Nerd.
		System.out.println(funWayToDoDis.addThemNumbers(1, 0)); // ...somewhere. Maybe I removed it later.
		System.out.println("Bye! (...said the `main` thread, named by all OpenJDK JVMs, the same!)");
		// (Actually, it's the Java standard. **It's not just OpenJDK! It's ALL JVMs!**)

		// Okay, *me is not covering threading factz, baaaiii!* :D
	}

}
