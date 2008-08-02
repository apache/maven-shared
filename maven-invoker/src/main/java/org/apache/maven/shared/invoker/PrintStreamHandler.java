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

/**
 * Offers an output handler that writes to a print stream like {@link System#out}.
 * 
 * @version $Id$
 * @since 2.0.9
 */
public class PrintStreamHandler
    implements InvocationOutputHandler
{

    /**
     * The print stream to write to, never <code>null</code>.
     */
    private PrintStream out;

    /**
     * A flag whether the print stream should be flushed after each line.
     */
    private boolean alwaysFlush;

    /**
     * Creates a new output handler that writes to {@link System#out}.
     */
    public PrintStreamHandler()
    {
        this( System.out, false );
    }

    /**
     * Creates a new output handler that writes to the specified print stream.
     * 
     * @param out The print stream to write to, must not be <code>null</code>.
     * @param alwaysFlush A flag whether the print stream should be flushed after each line.
     */
    public PrintStreamHandler( PrintStream out, boolean alwaysFlush )
    {
        if ( out == null )
        {
            throw new NullPointerException( "missing output stream" );
        }
        this.out = out;
        this.alwaysFlush = alwaysFlush;
    }

    public void consumeLine( String line )
    {
        if ( line == null )
        {
            out.println();
        }
        else
        {
            out.println( line );
        }

        if ( alwaysFlush )
        {
            out.flush();
        }
    }

}
