package org.apache.maven.shared.project.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.shared.project.runtime.MavenUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Ansi color utils, to manage colors colors consistently across plugins (only if Maven version is at least 3.4).
 */
public class AnsiUtils
{
    private static final String MINIMUM_MAVEN_VERSION = "3.4.0"; // color added in Maven 3.4.0: see MNG-3507

    private Ansi ansi;

    private AnsiUtils()
    {
        ansi = Ansi.ansi();
    }

    private AnsiUtils( StringBuilder builder )
    {
        ansi = Ansi.ansi( builder );
    }

    private AnsiUtils( int size )
    {
        ansi = Ansi.ansi( size );
    }

    public static void systemInstall()
    {
        AnsiConsole.systemInstall();
        if ( MavenUtils.compareToVersion( MINIMUM_MAVEN_VERSION ) >= 0 )
        {
            // ANSI color support was added in Maven 3.4.0: don't enable color if executing older Maven versions
            Ansi.setEnabled( false );
        }
    }

    public static void systemUninstall()
    {
        AnsiConsole.systemUninstall();
    }

    public static AnsiUtils ansi()
    {
        return new AnsiUtils();
    }

    public static AnsiUtils ansi( StringBuilder builder )
    {
        return new AnsiUtils( builder );
    }

    public static AnsiUtils ansi( int size )
    {
        return new AnsiUtils( size );
    }

    //
    // consistent color management
    // TODO make configurable
    // settings.xml? during systemInstall(Settings)?
    // or project properties (that can be injected by settings)?
    //
    /**
     * Insert color for DEBUG level display.
     * By default, bold cyan
     */
    public AnsiUtils debug()
    {
        ansi.bold().fgCyan();
        return this;
    }
    
    /**
     * Insert color for INFO level display.
     * By default, bold blue
     */
    public AnsiUtils info()
    {
        ansi.bold().fgBlue();
        return this;
    }
    
    /**
     * Insert color for WARNING level or warning message display.
     * By default, bold yellow
     */
    public AnsiUtils warning()
    {
        ansi.bold().fgYellow();
        return this;
    }
    
    /**
     * Insert color for ERROR level display.
     * By default, bold red
     */
    public AnsiUtils error()
    {
        ansi.bold().fgRed();
        return this;
    }
    
    /**
     * Insert color for success message display.
     * By default, bold green
     */
    public AnsiUtils success()
    {
        ansi.bold().fgGreen();
        return this;
    }
    
    /**
     * Append success message: equivalent to appending success color, then message, then reset.
     */
    public AnsiUtils success( Object message )
    {
        return success().a( message ).reset();
    }
    
    /**
     * Insert color for failure message display.
     * By default, bold red
     */
    public AnsiUtils failure()
    {
        ansi.bold().fgRed();
        return this;
    }

    /**
     * Append failure message: equivalent to appending failure color, then message, then reset.
     */
    public AnsiUtils failure( Object message )
    {
        return failure().a( message ).reset();
    }
    
    /**
     * Insert color for strong message display.
     * By default, bold
     */
    public AnsiUtils strong()
    {
        ansi.bold();
        return this;
    }

    /**
     * Append strong message: equivalent to appending strong color, then message, then reset.
     */
    public AnsiUtils strong( Object message )
    {
        return strong().a( message ).reset();
    }
    
    /**
     * Insert color for mojo message display.
     * By default, green
     */
    public AnsiUtils mojo()
    {
        ansi.fgGreen();
        return this;
    }

    /**
     * Append mojo message: equivalent to appending mojo color, then message, then reset.
     */
    public AnsiUtils mojo( Object message )
    {
        return mojo().a( message ).reset();
    }
    
    /**
     * Insert color for project message display.
     * By default, cyan
     */
    public AnsiUtils project()
    {
        ansi.fgCyan();
        return this;
    }

    /**
     * Append project message: equivalent to appending project color, then message, then reset.
     */
    public AnsiUtils project( Object message )
    {
        return project().a( message ).reset();
    }
    
    //
    // message building methods (modelled after Ansi methods)
    //
    public AnsiUtils reset()
    {
        ansi.reset();
        return this;
    }

    public AnsiUtils a( char[] value, int offset, int len )
    {
        ansi.a( value, offset, len );
        return this;
    }

    public AnsiUtils a( char[] value )
    {
        ansi.a( value );
        return this;
    }

    public AnsiUtils a( CharSequence value, int start, int end )
    {
        ansi.a( value, start, end );
        return this;
    }

    public AnsiUtils a( CharSequence value )
    {
        ansi.a( value );
        return this;
    }

    public AnsiUtils a( Object value )
    {
        ansi.a( value );
        return this;
    }

    public AnsiUtils newline()
    {
        ansi.newline();
        return this;
    }

    public AnsiUtils format( String pattern, Object... args )
    {
        ansi.format( pattern, args );
        return this;
    }

    @Override
    public String toString()
    {
        return ansi.toString();
    }
}
