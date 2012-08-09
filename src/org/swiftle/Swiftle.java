package org.swiftle;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snow.util.cache.ImageCache;
import org.swiftle.config.Configuration;
import org.swiftle.ui.SwiftleUI;
import org.swiftle.util.Constants;
import org.swiftle.util.logging.SingleLineFormatter;

public class Swiftle {

	private static final Logger logger = LoggerFactory.getLogger(Swiftle.class.getName());

	private SwiftleUI ui;
	
	private final Configuration config = Configuration.getInstance();

	private void run() {
		/** create SWT display */
		final Display display = new Display();

		/** open main window */
		ui = new SwiftleUI(display, Constants.SWIFTLE, config.getLayout().getInt("width"), config.getLayout().getInt("height"));
		ui.open();
	}

	/** Release acquired OS resources. */
	private void clear() {
		ImageCache.getInstance().dispose();
	}

	public static void main(final String[] args) {
		Configuration.load();
		
		SingleLineFormatter.configLogFormat();

		logger.info(Constants.SWIFTLE + " - version: " + Constants.getVersion() + ", running on "
				+ System.getProperty("os.name") + " " + System.getProperty("os.version") 
				+ " (" + System.getProperty("os.arch") + ") - java: " + System.getProperty("java.version"));

		logger.info("Application local dir: " + Constants.getAppDir());

		/** run main program */
		final Swiftle app = new Swiftle();
		app.run();

		/** clear os resources */
		app.clear();
		
		Configuration.save();
	}

}
