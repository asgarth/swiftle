package org.swiftle.network.connection;

import static org.swiftle.util.StringUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.swiftle.network.DirectoryEntry;
import org.swiftle.network.Entry;
import org.swiftle.network.FileEntry;

public class FTPConnection extends AbstractConnection implements Connection {

	public static final int DEFAULT_PORT = FTP.DEFAULT_PORT;
	
	public static final String PATH_SEPARATOR = "/";
	
	private final FTPClient client;

	private String lastError;

	private String home;

	public FTPConnection() {
		client = new FTPClient();

		lastError = "";
		home = "";
	}

	public boolean connect(final String host) {
		return connect(host, DEFAULT_PORT);
	}

	public boolean connect(final String host, final int port) {
		return connect(host, port, null, null);
	}

	public boolean connect(final String host, final String user, final String password) {
		return connect(host, DEFAULT_PORT, user, password);
	}

	public boolean connect(final String host, int port, final String user, final String password) {
		try {
			client.connect(host, port);

			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				logger.error("Error connecting to FTP server");
				client.disconnect();
				return false;
			}

			return login(user, password);

		} catch (IOException e) {
			logger.error("Error establishing FTP connection to host: " + host, e);
		}

		return false;
	}

	private boolean login(final String user, final String passwd) {
		if (! isConnected())
			return false;

		final String username = isEmpty(user) ? "anonymous" : user;
		final String password = isEmpty(passwd) ? "anonymous" : passwd;

		try {
			final boolean res = client.login(username, password);

			if (!res) {
				logger.error("Login error on FTP server for user: " + user);
				client.logout();

				return false;
			}

			home = client.printWorkingDirectory();
			client.setFileType(FTP.BINARY_FILE_TYPE);

		} catch (IOException e) {
			logger.error("Login error on FTP server for user: " + user, e.getMessage());
		}

		return true;
	}

	public boolean disconnect() {
		if (! isConnected())
			return false;

		try {
			client.logout();
			client.disconnect();

		} catch (IOException e) {
			logger.error("Error disconnecting from FTP server", e);
			return false;
		}

		return true;
	}

	public boolean isConnected() {
		return client != null && client.isConnected();
	}

	public String pwd() {
		if (! isConnected())
			return null;

		try {
			return client.printWorkingDirectory();

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return null;
	}

	public boolean cd(final String path) {
		if (! isConnected())
			return false;

		try {
			if (path.equals(".."))
				return client.changeToParentDirectory();

			return client.changeWorkingDirectory(path);

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return false;
	}

	public boolean home() {
		return cd(home);
	}

	public boolean mkdir(final String path) {
		if (! isConnected())
			return false;

		try {
			return client.makeDirectory(path);

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return false;
	}

	public boolean delete(final String file) {
		if (! isConnected())
			return false;

		try {
			return client.deleteFile(file);

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return false;
	}

	public boolean rename(final String orig, final String dest) {
		if (! isConnected())
			return false;

		try {
			return client.rename(orig, dest);

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return false;
	}

	public List<Entry> list() {
		final List<Entry> list = new LinkedList<Entry>();
		if (! client.isConnected())
			return list;

		list.add(new DirectoryEntry("..", "."));
		final String dir = pwd();
		try {
			final FTPFile[] fileList = client.listFiles();
			for (FTPFile file : fileList) {
				if (file.isDirectory())
					list.add(new DirectoryEntry(file.getName(), dir + getPathSeparator() + file.getName()));
				else
					list.add(new FileEntry(file.getName(), dir + getPathSeparator() + file.getName(), file.getSize()));
			}

		} catch (IOException e) {
			logger.error("Error retrieving file list from FTP server", e);
		}

		return list;
	}

	public InputStream getStream(final String remote) {
		if (! isConnected())
			return null;

		try {
			final InputStream stream = client.retrieveFileStream(remote);
			final int reply = client.getReplyCode();
			if (! FTPReply.isPositivePreliminary(reply) && ! FTPReply.isPositiveIntermediate(reply)) {
				logger.error("Error retrieving input stream from FTP server (code " + client.getReplyCode() + ", " +  client.getReplyString() + ")");
				stream.close();
				return null;
			}

			return stream;

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return null;
	}

	public void closeGetStream(final InputStream stream) throws IOException {
		if (stream == null)
			return;

		stream.close();

		if (! client.completePendingCommand())
			throw new FileTransferException("Error closing output stream after file trasfer");
	}

	public OutputStream putStream(final String remote) {
		if (!client.isConnected())
			return null;

		try {
			final OutputStream stream = client.storeFileStream(remote);
			final int reply = client.getReplyCode();
			if (! FTPReply.isPositivePreliminary(reply) && ! FTPReply.isPositiveIntermediate(reply)) {
				logger.error("Error creating output stream on FTP server (code " + client.getReplyCode() + ", " +  client.getReplyString() + ")");
				stream.close();
				return null;
			}

			return stream;

		} catch (IOException e) {
			logger.error("Error performing operation on FTP server", e);
		}

		return null;
	}

	public void closePutStream(final OutputStream stream) throws IOException {
		if (stream == null)
			return;

		stream.close();

		if (! client.completePendingCommand())
			throw new FileTransferException("Error closing output stream after file trasfer");
	}

	public String getPathSeparator() {
		return PATH_SEPARATOR;
	}

	public String lastError() {
		return lastError;
	}

}
