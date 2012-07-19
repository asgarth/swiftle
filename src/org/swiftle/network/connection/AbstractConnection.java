package org.swiftle.network.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnection implements Connection {

	protected final Logger logger = LoggerFactory.getLogger( this.getClass().getName() );

}
