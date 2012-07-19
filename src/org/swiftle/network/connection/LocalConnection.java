package org.swiftle.network.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.swiftle.network.DirectoryEntry;
import org.swiftle.network.Entry;
import org.swiftle.network.FileEntry;
import org.swiftle.util.Constants;

public class LocalConnection extends AbstractConnection implements Connection {

	private static final String SEPARATOR = Constants.getPathSeparator();

	private static final String HOME = System.getProperty("user.home");

	private String path;

	private String lastError;

	public LocalConnection() {
		path = HOME;
		lastError = "";
	}

	public boolean connect(final String host, final String user, final String password) {
		logger.info("Connecting to local filesystem");

		path = HOME;
		lastError = "";

		return true;
	}

	public boolean connect(final String host, final int port, final String user, final String password) {
		return connect(host, user, password);
	}

	public boolean disconnect() {
		return true;
	}
	
	public boolean isConnected() {
		return false;
	}

	public String pwd() {
		return path;
	}

	public boolean cd(final String dest) {
		// check if parent direcotry
		if (dest.equals("..")) {
			path = parentDirectory(path);
			return true;
		}

		// new path
		final String newPath = getAbsolutePath(dest);

		// check valid directory
		final File file = new File(newPath);
		if (file.exists() && file.isDirectory()) {
			path = newPath;
			return true;
		}

		lastError = "Specified directory not found";
		return false;
	}

	public boolean home() {
		return cd(HOME);
	}

	public boolean mkdir(final String path) {
		final File dirname = new File(getAbsolutePath(path));
		if (dirname.exists()) {
			lastError = "Specified directory already exists";
			return false;
		}

		final boolean res = dirname.mkdir();
		if (!res)
			lastError = "Error creating directory: " + path;

		return res;
	}

	public boolean delete(final String file) {
		final boolean success = new File(getAbsolutePath(file)).delete();
		if (!success)
			lastError = "File not found";

		return success;
	}

	public boolean rename(final String orig, final String dest) {
		final File a = new File(getAbsolutePath(orig));
		final File b = new File(getAbsolutePath(dest));
		if (! a.exists() || b.exists()) {
			lastError = "Cannot rename " + a + " to " + b;
			return false;
		}

		final boolean success = a.renameTo(b);
		if (!success)
			lastError = "Cannot rename " + a + " to " + b;

		return success;
	}

	public List<Entry> list() {
		return list(path);
	}

	private List<Entry> list(final String path) {
		// get file list
		final File dest = new File(getAbsolutePath(path));
		if (!dest.exists() || !dest.isDirectory())
			throw new IllegalArgumentException("Invalid path received");

		final File[] files = dest.listFiles();

		// build result list
		final List<Entry> list = new ArrayList<Entry>(files.length);
		for (int i = 0; i < files.length; i++) {
			final Entry entry;

			if (files[i].isDirectory())
				entry = new DirectoryEntry(files[i].getName(), files[i].getPath());
			else
				entry = new FileEntry(files[i].getName(), files[i].getPath(), files[i].length());

			list.add(entry);
		}

		return list;
	}

	public InputStream getStream(final String file) {
		try {
			return new FileInputStream(file);

		} catch (FileNotFoundException e) {
			logger.error("Error retrieving file stream", e);
		}

		lastError = "File not found";
		return null;
	}

	public void closeGetStream(final InputStream stream) throws IOException {
		stream.close();
	}

	public OutputStream putStream(final String file) {
		try {
			return new FileOutputStream(file);

		} catch (FileNotFoundException e) {
			logger.error("Error creating file stream", e);
		}

		lastError = "File not found";
		return null;
	}

	public void closePutStream(final OutputStream stream) throws IOException {
		stream.close();
	}

	public String getPathSeparator() {
		return SEPARATOR;
	}

	public String lastError() {
		return lastError;
	}

	public String toString() {
		return "localhost";
	}

	private String getAbsolutePath(final String filename) {
		if (new File(filename).isAbsolute())
			return filename;

		return path + (path.endsWith("/") ? "" : SEPARATOR) + filename;
	}

	private static String parentDirectory(final String path) {
		if (isRootDir(path))
			return path;

		return path.substring(0, path.lastIndexOf(SEPARATOR) == 0 ? 1 : path.lastIndexOf(SEPARATOR));
	}

	private static boolean isRootDir(final String path) {
		final File[] roots = File.listRoots();
		for (int i = roots.length; --i >= 0;)
			if (roots[i].getAbsolutePath().equals(path))
				return true;

		return false;
	}

}
