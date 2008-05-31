package org.apache.maven.shared.invoker;

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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Offers a logger that writes to a print stream like {@link System#out}.
 * 
 * @version $Id$
 * @since 2.0.9
 */
public class PrintStreamLogger
    implements InvokerLogger
{

    /**
     * The print stream to write to, never <code>null</code>.
     */
    private PrintStream out;

    /**
     * The threshold used to filter messages.
     */
    private int threshold;

    /**
     * Creates a new logger that writes to {@link System#out} and has a threshold of {@link #INFO}.
     */
    public PrintStreamLogger()
    {
        this( System.out, INFO );
    }

    /**
     * Creates a new logger that writes to the specified print stream.
     * 
     * @param out The print stream to write to, must not be <code>null</code>.
     * @param threshold The threshold for the logger.
     */
    public PrintStreamLogger( PrintStream out, int threshold )
    {
        if ( out == null )
        {
            throw new NullPointerException( "missing output stream" );
        }
        this.out = out;
        setThreshold( threshold );
    }

    /**
     * Writes the specified message and exception to the print stream.
     * 
     * @param level The priority level of the message.
     * @param message The message to log, may be <code>null</code>.
     * @param error The exception to log, may be <code>null</code>.
     */
    private void log( int level, String message, Throwable error )
    {
        if ( level > threshold )
        {
            // don't log when it doesn't match your threshold.
            return;
        }

        if ( message == null && error == null )
        {
            // don't log when there's nothing to log.
            return;
        }

        StringBuffer buffer = new StringBuffer();

        switch ( level )
        {
            case ( DEBUG ):
            {
                buffer.append( "[DEBUG]" );
                break;
            }
            case ( INFO ):
            {
                buffer.append( "[INFO]" );
                break;
            }
            case ( WARN ):
            {
                buffer.append( "[WARN]" );
                break;
            }
            case ( ERROR ):
            {
                buffer.append( "[ERROR]" );
                break;
            }
            case ( FATAL ):
            {
                buffer.append( "[FATAL]" );
                break;
            }
        }

        buffer.append( ' ' );

        if ( message != null )
        {
            buffer.append( message );
        }

        if ( error != null )
        {
            StringWriter writer = new StringWriter();
            PrintWriter pWriter = new PrintWriter( writer );

            error.printStackTrace( pWriter );

            if ( message != null )
            {
                buffer.append( '\n' );
            }

            buffer.append( "Error:\n" );
            buffer.append( writer.toString() );
        }

        out.println( buffer.toString() );
    }

    public void debug( String message )
    {
        log( DEBUG, message, null );
    }

    public void debug( String message, Throwable throwable )
    {
        log( DEBUG, message, throwable );
    }

    public void info( String message )
    {
        log( INFO, message, null );
    }

    public void info( String message, Throwable throwable )
    {
        log( INFO, message, throwable );
    }

    public void warn( String message )
    {
        log( WARN, message, null );
    }

    public void warn( String message, Throwable throwable )
    {
        log( WARN, message, throwable );
    }

    public void error( String message )
    {
        log( ERROR, message, null );
    }

    public void error( String message, Throwable throwable )
    {
        log( ERROR, message, throwable );
    }

    public void fatalError( String message )
    {
        log( FATAL, message, null );
    }

    public void fatalError( String message, Throwable throwable )
    {
        log( FATAL, message, throwable );
    }

    public boolean isDebugEnabled()
    {
        return threshold >= DEBUG;
    }

    public boolean isErrorEnabled()
    {
        return threshold >= ERROR;
    }

    public boolean isFatalErrorEnabled()
    {
        return threshold >= FATAL;
    }

    public boolean isInfoEnabled()
    {
        return threshold >= INFO;
    }

    public boolean isWarnEnabled()
    {
        return threshold >= WARN;
    }

    public int getThreshold()
    {
        return threshold;
    }

    public void setThreshold( int threshold )
    {
        this.threshold = threshold;
    }

}
