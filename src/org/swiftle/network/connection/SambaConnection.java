package org.swiftle.network.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

import org.swiftle.network.Entry;

public class SambaConnection extends AbstractConnection implements Connection {

	public static final String PATH_SEPARATOR = "/";

	private String lastError;

	private String home;
	
	public boolean connect(String host, String user, String password) {
		UniAddress dc;
		try {
			dc = UniAddress.getByName( host );
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user.split("\\")[0], user.split("\\")[1], password);
			SmbSession.logon(dc, auth);
			
			return true;
		} catch (UnknownHostException e) {
			logger.error("cannot find specified remote host: " + host, e);
		} catch (SmbException e) {
			logger.error("Error establishing Samba connection to host: " + host, e);
		}
		
		return false;
	}

	public boolean connect(String host, int port, String user, String password) {
		return false;
	}

	public boolean disconnect() {
		
		return false;
	}

	public boolean isConnected() {
		return false;
	}

	public String pwd() {
		return null;
	}

	public boolean cd(String path) {
		return false;
	}

	public boolean home() {
		return false;
	}

	public boolean mkdir(String path) {
		return false;
	}

	public boolean delete(String file) {
		return false;
	}

	public boolean rename(String orig, String dest) {
		return false;
	}

	public List<Entry> list() {
		return null;
	}

	public InputStream getStream(String remote) {
		return null;
	}

	public void closeGetStream(InputStream stream) throws IOException {

	}

	public OutputStream putStream(String remote) {
		return null;
	}

	public void closePutStream(OutputStream stream) throws IOException {

	}

	public String getPathSeparator() {
		return PATH_SEPARATOR;
	}

	public String lastError() {
		return lastError;
	}

}
