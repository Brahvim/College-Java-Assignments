package com.brahvim.college_assignments;

import java.applet.Applet;

@Deprecated
public class App extends Applet {

	public static void main(final String[] p_args) {
		final App app = new App();
		app.start();
	}

	@Override
	public void start() {
		System.out.println("Started!");
		System.out.printf("Applet info string: `%s`%n", super.getAppletInfo());

		// *STRAIGHT* to an NPE!:
		// System.out.printf("Applet context: `%s`%n", super.getAppletContext());
	}

	@Override
	public void destroy() {
		System.out.println("Applet destroyed...");
	}

}
