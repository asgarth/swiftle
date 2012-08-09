package org.swiftle.network.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;

import org.swiftle.network.DirectoryEntry;
import org.swiftle.network.Entry;
import org.swiftle.network.FileEntry;

public class SambaConnection extends AbstractConnection implements Connection {

	public static final String PATH_SEPARATOR = "/";

	private UniAddress dc;

	private NtlmPasswordAuthentication auth;

	private String currentPath;

	private String lastError;

	private String home;

	public boolean connect(final String host, final int port, final String domainUser, final String password) {
		return connect(host, domainUser, password);
	}

	public boolean connect(final String host, final String domainUser, final String password) {
		try {
			dc = UniAddress.getByName(host);

			String domain = "";
			String user = domainUser;
			if (domainUser.contains("\\")) {
				domain = domainUser.split("\\\\")[0];
				user = domainUser.split("\\\\")[1];
			}

			auth = new NtlmPasswordAuthentication(domain, user, password);
			SmbSession.logon(dc, auth);

			//Config.setProperty("jcifs.smb.lmCompatibility", "0");
			//Config.setProperty("jcifs.smb.client.useExtendedSecurity", "false");

			currentPath = "smb://" + host;

			home = pwd();

			return true;

		} catch (UnknownHostException e) {
			logger.error("cannot find specified remote host: " + host, e);
		} catch (SmbException e) {
			logger.error("Error establishing Samba connection to host: " + host, e);
		}

		return false;
	}

	public boolean disconnect() {
		dc = null;
		auth = null;

		return true;
	}

	public boolean isConnected() {
		return dc != null && auth != null;
	}

	public String pwd() {
		return currentPath;
	}

	public boolean cd(final String path) {
		if (path.equals("..")) {
			if (currentPath.equals(home))
				return false;

			try {
				currentPath = new SmbFile(currentPath, auth).getParent();
			} catch (MalformedURLException e) {
				return false;
			}
			return true;
		}

		currentPath = normalizePath(path);

		return true;
	}

	public boolean home() {
		currentPath = home;

		return true;
	}

	public boolean mkdir(final String path) {
		final String s = normalizePath(path);

		try {
			final SmbFile d = new SmbFile(s, auth);
			d.mkdir();

			return true;

		} catch (Exception e) {
			logger.error("Error creating directory: " + s, e);
		}

		return false;
	}

	public boolean delete(final String file) {
		final String s = normalizePath(file);

		try {
			final SmbFile f = new SmbFile(s, auth);
			f.delete();

			return true;

		} catch (Exception e) {
			logger.error("Error deleting: " + s, e);
		}

		return false;
	}

	public boolean rename(final String orig, final String dest) {
		final String s = normalizePath(orig);
		final String d = normalizePath(dest);

		try {
			final SmbFile f1 = new SmbFile(s, auth);
			final SmbFile f2 = new SmbFile(d, auth);
			f1.renameTo(f2);

			return true;

		} catch (Exception e) {
			logger.error("Error renaming " + s + " to " + d, e);
		}

		return false;
	}

	public List<Entry> list() {
		final List<Entry> list = new LinkedList<Entry>();
		if (! isConnected())
			return list;

		if (! currentPath.equals(home))
			list.add(new DirectoryEntry("..", "."));

		try {
			final SmbFile dir = new SmbFile(currentPath, auth);
			final SmbFile[] smbList = dir.listFiles();

			for (SmbFile file : smbList) {
				if (file.isDirectory())
					list.add(new DirectoryEntry(file.getName(), dir + getPathSeparator() + file.getName()));
				else
					list.add(new FileEntry(file.getName(), dir + getPathSeparator() + file.getName(), file.length()));
			}

		} catch (Exception e) {
			logger.error("Error retrieving file list from: " + currentPath, e);
		}

		return list;
	}

	public InputStream getStream(final String remote) {
		final String path = normalizePath(remote);

		try {
			final SmbFile f = new SmbFile(path, auth);
			return f.getInputStream();

		} catch (Exception e) {
			logger.error("Error performing operation on remote server", e);
		}

		return null;
	}

	public void closeGetStream(final InputStream stream) throws IOException {
		if (stream == null)
			return;

		stream.close();
	}

	public OutputStream putStream(final String remote) {
		final String path = normalizePath(remote);

		try {
			final SmbFile f = new SmbFile(path, auth);
			return f.getOutputStream();

		} catch (Exception e) {
			logger.error("Error performing operation on remote server", e);
		}

		return null;
	}

	public void closePutStream(final OutputStream stream) throws IOException {
		if (stream == null)
			return;

		stream.close();
	}

	private String normalizePath(final String path) {
		if (isAbsolute(path))
			return path;

		if (currentPath.endsWith("/"))
			return currentPath + path;

		return currentPath + getPathSeparator() + path;
	}

	private boolean isAbsolute(final String path) {
		return path.startsWith("smb://");
	}

	public String getPathSeparator() {
		return PATH_SEPARATOR;
	}

	public String lastError() {
		return lastError;
	}

}
