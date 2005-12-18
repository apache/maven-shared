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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class BasicMonitor
    extends AbstractMonitor
{

    private PrintStream stream;

    private PrintWriter writer;

    public BasicMonitor( PrintStream stream )
    {
        this.stream = stream;
    }

    public BasicMonitor( PrintWriter writer )
    {
        this.writer = writer;
    }

    private void output( CharSequence message, Throwable error, int messageLevel )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        pWriter.print( "[" );
        pWriter.print( MESSAGE_LEVELS[messageLevel] );
        pWriter.print( "]  " );

        if ( isEnabled( messageLevel ) )
        {
            pWriter.println( message );

            if ( error != null )
            {
                error.printStackTrace( pWriter );
                pWriter.println();
            }
        }

        if ( stream != null )
        {
            stream.println( sWriter.toString() );
        }
        else if ( writer != null )
        {
            writer.println( sWriter.toString() );
        }
        else
        {
            throw new IllegalStateException(
                                             "You must either provide a PrintStream or PrinteWriter instance in the constructor of this class." );
        }
    }

    public void debug( CharSequence message )
    {
        output( message, null, DEBUG_LEVEL );
    }

    public void debug( CharSequence message, Throwable error )
    {
        output( message, error, DEBUG_LEVEL );
    }

    public void info( CharSequence message )
    {
        output( message, null, INFO_LEVEL );
    }

    public void info( CharSequence message, Throwable error )
    {
        output( message, error, INFO_LEVEL );
    }

    public void warn( CharSequence message )
    {
        output( message, null, WARN_LEVEL );
    }

    public void warn( CharSequence message, Throwable error )
    {
        output( message, error, WARN_LEVEL );
    }

    public void error( CharSequence message )
    {
        output( message, null, ERROR_LEVEL );
    }

    public void error( CharSequence message, Throwable error )
    {
        output( message, error, ERROR_LEVEL );
    }

    public void verbose( CharSequence message )
    {
        output( message, null, VERBOSE_LEVEL );
    }

    public void verbose( CharSequence message, Throwable error )
    {
        output( message, error, VERBOSE_LEVEL );
    }

}
