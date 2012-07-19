package org.swiftle.network.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.swiftle.network.Entry;

/** This interface contains method that implementing classes must define in order to send
 * and receive file over remote protocol. */
public interface Connection {

	/** Connects with remote server.
	 * 
	 * @param host the remote host.
	 * @param user the user.
	 * @param password the password.
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean connect(final String host, final String user, final String password);

	/** Connects with remote server.
	 * 
	 * @param host the remote host.
	 * @param port the remote port.
	 * @param user the user.
	 * @param password the password.
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean connect(final String host, final int port, final String user, final String password);

	/** Close the remote connection.
	 * 
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean disconnect();
	
	/** Return the current connection status.
	 * 
	 * @return <code>true</code> if currently connected, <code>false</code> otherwise.
	 */
	public boolean isConnected();

	/** Returns the current directory path.
	 * 
	 * @return the current directory, <code>null</code> if an error occurred.
	 */
	public String pwd();
	
	/** Change current directory.
	 * 
	 * @param path the new path.
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean cd(final String path);

	/** Change directory to user home.
	 * 
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean home();

	/** Create a new directory.
	 * 
	 * @param path the new directory path.
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean mkdir(final String path);

	/** Remove input file.
	 * 
	 * @param file the file to be removed.
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean delete(final String file);
	
	/** Rename the specified file.
	 * 
	 * @param orig the file to be renamed.
	 * @param dest the new file name.
	 * @return <code>true</code> if everything ok, <code>false</code> otherwise.
	 */
	public boolean rename(final String orig, final String dest);
	
	/** Returns the list of files in current directory.
	 * 
	 * @return the list of files contained into current directory.
	 */
	public List<Entry> list();
	
	/** Return an InputStream associated with a remote file.
	 * 
	 * @param remote the path to remote filename (source).
	 * @return an InputStream to read the remote file.
	 */
	public InputStream getStream(final String remote);

	/** Close the stream received and perform required operation on remote serve to complete the transfer operation.
	 * 
	 * @param stream the stream to close.
	 * @throws IOException if an error occurs.
	 */
	public void closeGetStream(final InputStream stream) throws IOException;
	
	/** Return an OuputStream associated with a remote file.
	 * 
	 * @param remote the path to remote filename (dest).
	 * @return an OutputStream to write the remote file.
	 */
	public OutputStream putStream(final String remote);
	
	/** Close the stream received and perform required operation on remote serve to complete the transfer operation.
	 * 
	 * @param stream the stream to close.
	 * @throws IOException if an error occurs.
	 */
	public void closePutStream(final OutputStream stream) throws IOException;

	/** Returns a String representation of the file path separator defined for this connection.
	 * 
	 * @return a string the path separator used for this connection.
	 */
	public String getPathSeparator();

	/** Returns the String representation of the last connection error.
	 * 
	 * @return a string that identifies the error received.
	 */
	public String lastError();

	/** Returns the String representation of the current connection implementation.
	 * 
	 * @return a string that identifies this connection.
	 */
	public String toString();
}
