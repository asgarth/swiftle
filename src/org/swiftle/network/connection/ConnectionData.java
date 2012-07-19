package org.swiftle.network.connection;


public class ConnectionData {

	private final Protocol protocol;

	private final String host;

	private final int port;

	private final String user;

	private final String password;

	public static ConnectionData getLocalInstance() {
		return new ConnectionData( Protocol.LOCAL );
	}
	
	public static ConnectionData getFTPInstance( final String host, final int port, final String user, final String password ) {
		return new ConnectionData( Protocol.FTP, host, port, user, password );
	}

	private ConnectionData( final Protocol protocol ) {
		this( protocol, null, 0, null, null );
	}

	private ConnectionData( final Protocol protocol, final String host, final int port, final String user, final String password ) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
