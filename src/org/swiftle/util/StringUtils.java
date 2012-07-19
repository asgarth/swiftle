package org.swiftle.util;

/** Utility class for common operation on string. */
public class StringUtils {

	/** Utility class, prevent instantiation */
	private StringUtils() { }

	public static boolean isEmpty( final String arg ) {
		return arg == null || arg.equals( "" );
	}

}
