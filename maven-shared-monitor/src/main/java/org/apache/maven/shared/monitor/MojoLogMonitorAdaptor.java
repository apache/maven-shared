package org.apache.maven.shared.monitor;

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

import org.apache.maven.plugin.logging.Log;

/**
 * Adapter class to make a mojo Log instance look like an shared monitor.
 * This adaptor class is tied to the optional dependency on maven-plugin-api.
 */
public class MojoLogMonitorAdaptor
    extends AbstractMonitor
{

    private final Log mojoLog;

    public MojoLogMonitorAdaptor( Log mojoLog )
    {
        this.mojoLog = mojoLog;
    }

    public void debug( CharSequence message, Throwable error )
    {
        mojoLog.debug( message, error );
    }

    public void debug( CharSequence message )
    {
        mojoLog.debug( message );
    }

    public void error( CharSequence message, Throwable error )
    {
        mojoLog.error( message, error );
    }

    public void error( CharSequence message )
    {
        mojoLog.error( message );
    }

    public void info( CharSequence message, Throwable error )
    {
        mojoLog.info( message, error );
    }

    public void info( CharSequence message )
    {
        mojoLog.info( message );
    }

    public void warn( CharSequence message, Throwable error )
    {
        mojoLog.warn( message, error );
    }

    public void warn( CharSequence message )
    {
        mojoLog.warn( message );
    }

    public void verbose( CharSequence message )
    {
        mojoLog.debug( message );
    }

    public void verbose( CharSequence message, Throwable error )
    {
        mojoLog.debug( message, error );
    }

}
