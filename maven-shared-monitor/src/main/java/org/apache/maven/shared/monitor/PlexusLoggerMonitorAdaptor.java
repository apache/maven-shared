package org.apache.maven.shared.monitor;

import org.codehaus.plexus.logging.Logger;

/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Adapter class to make a mojo Log instance look like an shared monitor.
 * This adaptor class is tied to the optional dependency on maven-plugin-api.
 */
public class PlexusLoggerMonitorAdaptor
    extends AbstractMonitor
{

    private final Logger logger;

    public PlexusLoggerMonitorAdaptor( Logger logger )
    {
        this.logger = logger;
    }

    public void debug( CharSequence message, Throwable error )
    {
        logger.debug( message.toString(), error );
    }

    public void debug( CharSequence message )
    {
        logger.debug( message.toString() );
    }

    public void error( CharSequence message, Throwable error )
    {
        logger.error( message.toString(), error );
    }

    public void error( CharSequence message )
    {
        logger.error( message.toString() );
    }

    public void info( CharSequence message, Throwable error )
    {
        logger.info( message.toString(), error );
    }

    public void info( CharSequence message )
    {
        logger.info( message.toString() );
    }

    public void warn( CharSequence message, Throwable error )
    {
        logger.warn( message.toString(), error );
    }

    public void warn( CharSequence message )
    {
        logger.warn( message.toString() );
    }

    public void verbose( CharSequence message )
    {
        logger.debug( message.toString() );
    }

    public void verbose( CharSequence message, Throwable error )
    {
        logger.debug( message.toString(), error );
    }

}
