package org.apache.maven.doxia.tools;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.logging.Log;

/**
 * Wrap a Mojo logger into a Doxia logger.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.1
 * @see org.apache.maven.plugin.logging.Log
 */
public class MojoLogWrapper
    implements Log
{
    private final org.apache.maven.plugin.logging.Log mojoLog;

    /**
     * @param log a Mojo log
     */
    public MojoLogWrapper( org.apache.maven.plugin.logging.Log log )
    {
        this.mojoLog = log;
    }

    /** {@inheritDoc} */
    public void setLogLevel( int level )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void debug( CharSequence content )
    {
        mojoLog.debug( toString( content ) );
    }

    /** {@inheritDoc} */
    public void debug( CharSequence content, Throwable error )
    {
        mojoLog.debug( toString( content ), error );
    }

    /** {@inheritDoc} */
    public void debug( Throwable error )
    {
        mojoLog.debug( "", error );
    }

    /** {@inheritDoc} */
    public void info( CharSequence content )
    {
        mojoLog.info( toString( content ) );
    }

    /** {@inheritDoc} */
    public void info( CharSequence content, Throwable error )
    {
        mojoLog.info( toString( content ), error );
    }

    /** {@inheritDoc} */
    public void info( Throwable error )
    {
        mojoLog.info( "", error );
    }

    /** {@inheritDoc} */
    public void warn( CharSequence content )
    {
        mojoLog.warn( toString( content ) );
    }

    /** {@inheritDoc} */
    public void warn( CharSequence content, Throwable error )
    {
        mojoLog.warn( toString( content ), error );
    }

    /** {@inheritDoc} */
    public void warn( Throwable error )
    {
        mojoLog.warn( "", error );
    }

    /** {@inheritDoc} */
    public void error( CharSequence content )
    {
        mojoLog.error( toString( content ) );
    }

    /** {@inheritDoc} */
    public void error( CharSequence content, Throwable error )
    {
        mojoLog.error( toString( content ), error );
    }

    /** {@inheritDoc} */
    public void error( Throwable error )
    {
        mojoLog.error( "", error );
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled()
    {
        return mojoLog.isDebugEnabled();
    }

    /** {@inheritDoc} */
    public boolean isInfoEnabled()
    {
        return mojoLog.isInfoEnabled();
    }

    /** {@inheritDoc} */
    public boolean isWarnEnabled()
    {
        return mojoLog.isWarnEnabled();
    }

    /** {@inheritDoc} */
    public boolean isErrorEnabled()
    {
        return mojoLog.isErrorEnabled();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private String toString( CharSequence content )
    {
        if ( content == null )
        {
            return "";
        }

        return content.toString();
    }
}
