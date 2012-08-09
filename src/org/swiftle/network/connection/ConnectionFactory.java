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
		else if (protocol == Protocol.FTP)
			newConnection = new FTPConnection();
		else if (protocol == Protocol.SAMBA)
			newConnection = new SambaConnection();
		else
			throw new UnsupportedOperationException("Protocol not available: + " + protocol.name());

		if( port > 0 )
			newConnection.connect(host, port, user, pwd);
		else
			newConnection.connect(host, user, pwd);

		return newConnection;
	}
}
