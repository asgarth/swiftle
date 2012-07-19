package org.swiftle;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snow.util.cache.ImageCache;
import org.swiftle.ui.SwiftleUI;
import org.swiftle.util.Constants;
import org.swiftle.util.logging.SingleLineFormatter;

public class Swiftle {

	private static final Logger logger = LoggerFactory.getLogger(Swiftle.class);

	private SwiftleUI ui;

	private void run() {
		/** create SWT display */
		final Display display = new Display();

		/** open main window */
		ui = new SwiftleUI(display, Constants.SWIFTLE, 800, 600);
		ui.open();
	}
	
	/** Release acquired OS resources. */
	private void clear() {
		ImageCache.getInstance().dispose();
	}

	public static void main(final String[] args) {
		SingleLineFormatter.configLogFormat();

		logger.info(Constants.SWIFTLE + " - version: " + Constants.getVersion() + " (java: " + System.getProperty("java.version") + ")");
		logger.info("System: " + System.getProperty("os.name") + ", " + System.getProperty("os.version") + " - " + System.getProperty("os.arch"));

		/** run main program */
		final Swiftle app = new Swiftle();
		app.run();

		/** clear os resources */
		app.clear();
	}

}
