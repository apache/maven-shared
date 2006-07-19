package org.apache.maven.shared.io.logging;

import org.apache.maven.plugin.logging.Log;


public class MojoLogSink
    implements MessageSink
{
    
    private final Log logger;

    public MojoLogSink( Log logger )
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
        logger.error( message );
    }

    public void warning( String message )
    {
        logger.warn( message );
    }

}
