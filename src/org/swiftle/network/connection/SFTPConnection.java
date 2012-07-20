package org.swiftle.network.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.swiftle.network.DirectoryEntry;
import org.swiftle.network.Entry;
import org.swiftle.network.FileEntry;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPConnection extends AbstractConnection implements Connection {

	public static final int DEFAULT_PORT = 22;

	public static final String PATH_SEPARATOR = "/";

	private final JSch jsch;

	private Session session;

	private ChannelSftp channelSftp;

	private final Map<InputStream, ChannelSftp> inputStreamMap;

	private final Map<OutputStream, ChannelSftp> outputStreamMap;

	private String lastError;

	private String home;

	public SFTPConnection() {
		jsch = new JSch();

		inputStreamMap = new HashMap<InputStream, ChannelSftp>();
		outputStreamMap = new HashMap<OutputStream, ChannelSftp>();

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
			session = jsch.getSession(user, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("PreferredAuthentications", "password");
			session.setPassword(password);

			session.connect();

			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			home = channelSftp.getHome();

			inputStreamMap.clear();
			outputStreamMap.clear();

			return true;

		} catch (SftpException e) {
			logger.error("Error establishing SFTP connection to host: " + host, e);
		} catch (JSchException e) {
			logger.error("Error establishing SFTP connection to host: " + host, e);
		}

		return false;
	}

	public boolean disconnect() {
		if (! isConnected())
			return false;

		channelSftp.disconnect();
		session.disconnect();

		inputStreamMap.clear();
		outputStreamMap.clear();
		return true;
	}

	public boolean isConnected() {
		return session != null && session.isConnected() && channelSftp != null && channelSftp.isConnected();
	}

	public String pwd() {
		if (! isConnected())
			return null;

		try {
			return channelSftp.pwd();

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
		}

		return null;
	}

	public boolean cd(final String path) {
		if (! isConnected())
			return false;

		try {
			channelSftp.cd(path);
			return true;

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
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
			channelSftp.mkdir(path);
			return true;

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
		}

		return false;
	}

	public boolean delete(final String file) {
		if (! isConnected())
			return false;

		try {
			channelSftp.rm(file);
			return true;

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
		}

		return false;
	}

	public boolean rename(final String orig, final String dest) {
		if (! isConnected())
			return false;

		try {
			channelSftp.rename(orig, dest);
			return true;

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
		}

		return false;
	}

	public List<Entry> list() {
		final List<Entry> list = new LinkedList<Entry>();
		if (! isConnected())
			return list;

		final String dir = pwd();
		try {
			final Vector<LsEntry> vector = channelSftp.ls(dir);
			for (LsEntry file : vector) {
				if (file.getFilename().equals("."))
					continue;
				
				if (file.getAttrs().isDir())
					list.add(new DirectoryEntry(file.getFilename(), dir + PATH_SEPARATOR + file.getFilename()));
				else
					list.add(new FileEntry(file.getFilename(), dir + PATH_SEPARATOR + file.getFilename(), file.getAttrs().getSize()));
			}

		} catch (SftpException e) {
			logger.error("Error retrieving file list from SFTP server", e);
		}

		return list;
	}

	public InputStream getStream(final String remote) {
		if (! isConnected())
			return null;

		try {
			final ChannelSftp newChannel = (ChannelSftp) session.openChannel("sftp");
			newChannel.connect();
			final InputStream stream = newChannel.get(remote);

			inputStreamMap.put(stream, newChannel);

			return stream;

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
		} catch (JSchException e) {
			logger.error("Error performing operation on SFTP server", e);
		}

		return null;
	}

	public void closeGetStream(final InputStream stream) throws IOException {
		if (stream == null)
			return;

		final ChannelSftp channel = inputStreamMap.remove(stream);

		stream.close();
		channel.disconnect();
	}

	public OutputStream putStream(final String remote) {
		if (! isConnected())
			return null;

		try {
			final ChannelSftp newChannel = (ChannelSftp) session.openChannel("sftp");
			newChannel.connect();
			final OutputStream stream = newChannel.put(remote);

			outputStreamMap.put(stream, newChannel);

			return stream;

		} catch (SftpException e) {
			logger.error("Error performing operation on SFTP server", e);
		} catch (JSchException e) {
			logger.error("Error performing operation on SFTP server", e);
		}

		return null;
	}

	public void closePutStream(final OutputStream stream) throws IOException {
		if (stream == null)
			return;

		final ChannelSftp channel = outputStreamMap.remove(stream);

		stream.close();
		channel.disconnect();
	}

	public String getPathSeparator() {
		return PATH_SEPARATOR;
	}

	public String lastError() {
		return lastError;
	}

}
