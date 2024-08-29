package com.brahvim.college_assignments;

import java.awt.Graphics;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class App extends JApplet {

	public static void main(final String[] p_args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final App app = new App();
		frame.add(app);
		app.init();

		frame.pack();
		frame.setSize(480, 360);
		frame.setVisible(true);
	}

	@Override
	public void paint(final Graphics p_context) {
		p_context.drawString("Hi!", super.getWidth() / 2, super.getHeight() / 2);
	}

	@Override
	public void init() {
		System.out.println("Applet initiated.");
		System.out.printf("Applet info string: `%s`%n", super.getAppletInfo());

		// *Still causes* an NPE! :/
		// System.out.printf("Applet context: `%s`%n", super.getAppletContext());
	}

	@Override
	public void destroy() {
		System.out.println("Applet destroyed...");
	}

}
