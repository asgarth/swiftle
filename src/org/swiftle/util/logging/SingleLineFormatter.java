package org.swiftle.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SingleLineFormatter extends Formatter {

	public static void configLogFormat() {
		for( Handler handler : Logger.getLogger( "" ).getHandlers() )
			handler.setFormatter( new SingleLineFormatter() );
	}

	public String format( final LogRecord record ) {
		// use the buffer for optimal string construction
		final StringBuffer sb = new StringBuffer();

		// date
		final String time = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss,SSS" ).format(  new Date() );
		sb.append( time );

		// level
		sb.append( " [" ).append( record.getLevel().toString() ).append( "] " );

		// message
		sb.append( record.getLoggerName() ).append(": ").append( record.getMessage() );

		// if there was an exception thrown, log it as well
		if( record.getThrown() != null )
			sb.append( "\n" ).append( stackTrace2String( record.getThrown() ) );

		sb.append( "\n" );

		return sb.toString();
	}

	/** Transform a stack trace to a string.
	 * 
	 * @param throwable
	 *            an exception.
	 * @return a new string containing the stack trace of input exception. */
	public static String stackTrace2String( final Throwable throwable ) {
		final StringWriter sw = new StringWriter();
		throwable.printStackTrace( new PrintWriter( sw ) );

		return sw.toString();
	}

}
