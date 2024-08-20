package com.brahvim.college_assignments;

public class App {

	public static void main(final String[] p_args) {

		// I guess I should explain EVERYTHING about the Java threading API here.
		// ...Mostly the syntax I'll be using, haha.

		// The most naive way to make threads is to write a class that extends `Thread`:
		class ThreadingMethod1 extends Thread {

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

			private ThreadingMethod1() {
				super.setName(this.className);
			}

			// Ignore my use of var-args here :>
			private void log(final Object... p_objects) {
				for (final Object o : p_objects) {
					System.out.println(

							"[" + this.className + "]"
									+ o.toString()

					);
				}
			}

		}

		new ThreadingMethod1()
				.start();

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

		// But you can make this even shorter!
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

		// A `Runnable` essentially lets you a function w/o parameters.
		// A `Consumer` function has one

		new Thread(thread4Runnable).start();
	}

}
