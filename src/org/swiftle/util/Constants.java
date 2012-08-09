package org.swiftle.util;

import static org.snow.util.Platform.getPathSeparator;

import java.io.File;

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
		// get correct dir for current platform
		final String localDir = Platform.getUserApp(SWIFTLE.toLowerCase());
		
		// create directory if required
		final File fileLocalDir = new File(localDir);
		if (! fileLocalDir.exists())
			if (! fileLocalDir.mkdir())
				return null;
			
		return localDir;
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
