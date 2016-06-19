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

import static org.apache.maven.shared.project.utils.AnsiUtils.ansi;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.fusesource.jansi.Ansi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnsiUtilsTest
{
    private boolean savedAnsiEnable;

    @Before
    public void saveAnsiState()
    {
        savedAnsiEnable = Ansi.isEnabled();
    }

    @After
    public void restoreAnsiState()
    {
        Ansi.setEnabled( savedAnsiEnable );
    }

    @Test
    public void constructors()
    {
        // check that ANSI color disable is taken into account
        Ansi.setEnabled( false );
        assertEquals( "test", ansi().error().a( "test" ).reset().toString() );
        assertEquals( "test", ansi( 16 ).error().a( "test" ).reset().toString() );
        assertEquals( "test", ansi( new StringBuilder() ).error().a( "test" ).reset().toString() );

        Ansi.setEnabled( true );
        assertNotEquals( "test", ansi().error().a( "test" ).reset().toString() );
        assertNotEquals( "test", ansi( 16 ).error().a( "test" ).reset().toString() );
        assertNotEquals( "test", ansi( new StringBuilder() ).error().a( "test" ).reset().toString() );
    }

    @Test
    public void appendText()
    {
        // autoboxing of primitives to Object
        assertEquals( "12", ansi().a( (int) 12 ).toString() );
        assertEquals( "-1212", ansi().a( (long) -1212 ).toString() );
        assertEquals( "1.2", ansi().a( 1.2f ).toString() );
        assertEquals( "-1.212", ansi().a( -1.212d ).toString() );
        assertEquals( "true", ansi().a( true ).toString() );
        assertEquals( "c", ansi().a( 'c' ).toString() );
    }

    @Test
    public void messages()
    {
        Ansi.setEnabled( true );
        assertEquals( ansi().warning().a( "test" ).reset().toString(), ansi().warning( "test" ).toString() );
        assertEquals( ansi().success().a( "test" ).reset().toString(), ansi().success( "test" ).toString() );
        assertEquals( ansi().failure().a( "test" ).reset().toString(), ansi().failure( "test" ).toString() );
        assertEquals( ansi().strong().a( "test" ).reset().toString(), ansi().strong( "test" ).toString() );
        assertEquals( ansi().mojo().a( "test" ).reset().toString(), ansi().mojo( "test" ).toString() );
        assertEquals( ansi().project().a( "test" ).reset().toString(), ansi().project( "test" ).toString() );
    }
}
