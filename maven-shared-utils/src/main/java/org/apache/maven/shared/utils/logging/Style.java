package org.apache.maven.shared.utils.logging;

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

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

import java.lang.reflect.Method;

import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;

/**
 *
 */
enum Style
{

    DEBUG(   "bold,fgCyan"   ),
    INFO(    "bold,fgBlue"   ),
    WARNING( "bold,fgYellow" ),
    ERROR(   "bold,fgRed"    ),
    SUCCESS( "bold,fgGreen"  ),
    FAILURE( "bold,fgRed"    ),
    STRONG(  "bold"        ),
    MOJO(        "fgGreen"   ),
    PROJECT(     "fgCyan"    );

    private final Attribute attribute;

    private final String fgColor;
    private final String bgColor;

    Style( String defaultConfiguration )
    {
        Attribute currentAttribute = null;
        String currentFgColor = null;
        String currentBgColor = null;

        for ( String token : System.getProperty( "style." + name().toLowerCase(), defaultConfiguration ).split( "," ) )
        {
            if ( token.startsWith( "fg" ) )
            {
                currentFgColor = token;
            }
            if ( token.startsWith( "bg" ) )
            {
                currentBgColor = token;
            }

            if ( "bold".equalsIgnoreCase( token ) )
            {
                currentAttribute = INTENSITY_BOLD;
            }
        }

        this.attribute = currentAttribute;
        this.fgColor = currentFgColor;
        this.bgColor = currentBgColor;
    }

    void apply( Ansi ansi )
    {
        if ( attribute != null )
        {
            ansi.a( attribute );
        }

        if ( fgColor != null )
        {
            applyColor( ansi, fgColor );
        }
        if ( bgColor != null )
        {
            applyColor( ansi, bgColor );
        }
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder( name() );
        if ( attribute != null || fgColor != null || bgColor != null )
        {
            s.append( "=" );
            String prefix = "";
            if ( attribute == null )
            {
                prefix = ",";
                s.append( attribute.toString() );
            }
            if ( fgColor == null )
            {
                s.append( prefix );
                prefix = ",";
                s.append( fgColor );
            }
            if ( bgColor == null )
            {
                s.append( prefix );
                s.append( bgColor );
            }
        }
        return s.toString();
    }

    private void applyColor( Ansi ansi, String c )
    {
        try
        {
            Method m = ansi.getClass().getMethod( c );
            m.invoke( ansi );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

}
