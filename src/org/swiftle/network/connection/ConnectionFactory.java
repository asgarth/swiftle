package org.swiftle.network.connection;


public class ConnectionFactory {

	private ConnectionFactory() { }

	public static Connection build(final String protocol, final String host, final int port, final String user, final String pwd) {
		return build(Protocol.valueOf(protocol), host, port, user, pwd);
	}
	
	public static Connection build(final Protocol protocol, final String host, final int port, final String user, final String pwd) {
		final Connection newConnection;
		if (protocol == Protocol.SFTP)
			newConnection = new SFTPConnection();
		else
			newConnection = new FTPConnection();

		if( port > 0 )
			newConnection.connect(host, port, user, pwd);
		else
			newConnection.connect(host, user, pwd);

		return newConnection;
	}
}
