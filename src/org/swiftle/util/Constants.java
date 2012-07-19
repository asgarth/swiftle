package org.swiftle.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {

	public static final String SWIFTLE = "Swiftle";

	public static String getJarPath() {
		final String dir = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (dir.endsWith(getPathSeparator()))
			return dir.substring(0, dir.lastIndexOf(getPathSeparator()));

		return dir;
	}

	public static String getUserDir() {
		return System.getProperty("user.home") + getPathSeparator() + ".swiftle";
	}

	public static String getPathSeparator() {
		return System.getProperty("file.separator");
	}

	/** Return the application version. */
	public static String getVersion() {
		// try to get version from manifest file
		String version = getManifestVersion();
		if (version != null && !version.equals(""))
			return version;

		// try to get version from build.version file
		version = getAntVersion();
		if (version != null && !version.equals(""))
			return version;

		return "Unknown";
	}

	private static String getManifestVersion() {
		final Package p = Constants.class.getPackage();
		return p.getSpecificationVersion();
	}

	private static String getAntVersion() {
		final InputStream stream = Constants.class.getClassLoader().getResourceAsStream("./build.version");
		if (stream == null)
			return null;

		String version = null;
		try {
			final Properties properties = new Properties();
			properties.load(stream);

			version = properties.getProperty("version.number");
		} catch (IOException ignore) {
		} finally {
			try {
				stream.close();
			} catch (Throwable ignore) {
			}
		}

		return version;
	}

}
