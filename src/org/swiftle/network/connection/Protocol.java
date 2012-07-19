package org.swiftle.network.connection;

import java.util.ArrayList;
import java.util.List;

public enum Protocol {

	LOCAL( "./" ),
	FTP( "ftp://" ),
	SFTP( "sftp://" ),
	SMB( "\\" );

	private final String textForm;

	private Protocol( final String textForm ) {
		this.textForm = textForm;
	}

	public String getConnectionString() {
		return textForm;
	}

	public static String[] stringValues() {
		final List<String> values = new ArrayList<String>();
		for( Protocol p : values() )
			values.add( p.name() );

		return values.toArray( new String[values.size()] );
	}

}
