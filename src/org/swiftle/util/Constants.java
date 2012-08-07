package org.swiftle.util;

import static org.snow.util.Platform.getPathSeparator;

import org.snow.util.Platform;

public class Constants {

	public static final String SWIFTLE = "Swiftle";

	public static String getJarPath() {
		final String dir = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (dir.endsWith(getPathSeparator()))
			return dir.substring(0, dir.lastIndexOf(getPathSeparator()));

		return dir;
	}

	public static String getAppDir() {
		return Platform.getUserApp(SWIFTLE.toLowerCase());
	}

	/** Return the application version. */
	public static String getVersion() {
		// try to get version from manifest file
		String version = getManifestVersion();
		if (version != null && !version.equals(""))
			return version;

		return "DEV";
	}

	private static String getManifestVersion() {
		final Package p = Constants.class.getPackage();
		return p.getSpecificationVersion();
	}

}
