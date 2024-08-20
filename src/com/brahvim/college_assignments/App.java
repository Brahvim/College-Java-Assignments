package com.brahvim.college_assignments;

public class App {

	public static void main(final String[] p_args) {

		// I guess I should explain EVERYTHING about the Java threading API here.

		// The most naive way to make threads is to write a class that extends `Thread`:
		class MyThread extends Thread {

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

				// (Also `Object::wait()` is ALWAYS a better to wait than

				this.log("Literally everything is async and out of control!");
			}

			final String name = this.getClass().getSimpleName();

			private MyThread() {
				super.setName(this.name);
			}

			// Ignore my use of var-args here :>
			private void log(final Object... p_objects) {
				for (final Object o : p_objects) {
					System.out.println(

							"[" + this.name + "]"
									+ o.toString()

					);
				}
			}

		}

	}

}
