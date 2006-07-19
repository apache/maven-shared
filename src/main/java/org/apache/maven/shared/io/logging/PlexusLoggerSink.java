package org.apache.maven.shared.io.logging;

import org.codehaus.plexus.logging.Logger;


public class PlexusLoggerSink
    implements MessageSink
{
    
    private final Logger logger;

    public PlexusLoggerSink( Logger logger )
    {
        this.logger = logger;
    }

    public void debug( String message )
    {
        logger.debug( message );
    }

    public void error( String message )
    {
        logger.error( message );
    }

    public void info( String message )
    {
        logger.info( message );
    }

    public void severe( String message )
    {
        logger.fatalError( message );
    }

    public void warning( String message )
    {
        logger.warn( message );
    }

}
