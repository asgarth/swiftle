package org.swiftle.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SingleLineFormatter extends Formatter {

	public static void configLogFormat() {
		for (Handler handler : Logger.getLogger("").getHandlers()) {
			handler.setFormatter(new SingleLineFormatter());
			if (handler instanceof ConsoleHandler)
				handler.setLevel(Level.INFO);				// TODO: change to WARNING
		}
	}

	public String format(final LogRecord record) {
		// use the buffer for optimal string construction
		final StringBuffer sb = new StringBuffer();

		// level
		sb.append(record.getLevel().toString()).append(" - ");

		// message
		sb.append(record.getMessage());

		// if there was an exception thrown, log it as well
		if (record.getThrown() != null)
			sb.append("\n").append(stackTrace2String(record.getThrown()));

		sb.append("\n");

		return sb.toString();
	}

	/** Transform a stack trace to a string.
	 * 
	 * @param throwable
	 *            an exception.
	 * @return a new string containing the stack trace of input exception. */
	public static String stackTrace2String(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		throwable.printStackTrace(new PrintWriter(sw));

		return sw.toString();
	}

}
