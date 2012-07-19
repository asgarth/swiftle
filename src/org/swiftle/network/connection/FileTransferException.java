package org.swiftle.network.connection;

import java.io.IOException;

public class FileTransferException extends IOException {

	private static final long serialVersionUID = -8540431414624795287L;

	public FileTransferException(String message) {
		super(message);
	}

}
