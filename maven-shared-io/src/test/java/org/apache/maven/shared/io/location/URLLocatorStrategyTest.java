package org.apache.maven.shared.io.location;

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

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.maven.shared.io.Utils;
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

public class URLLocatorStrategyTest
    extends TestCase
{

    public void testShouldConstructWithNoParams()
    {
        new URLLocatorStrategy();
    }

    public void testShouldConstructWithTempFileOptions()
    {
        new URLLocatorStrategy( "prefix.", ".suffix", true );
    }

    public void testShouldFailToResolveWithMalformedUrl()
    {
        MessageHolder mh = new DefaultMessageHolder();

        Location location = new URLLocatorStrategy().resolve( "://www.google.com", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );
    }

    public void testShouldResolveUrlForTempFile() throws IOException
    {
        File tempFile = File.createTempFile( "prefix.", ".suffix" );
        tempFile.deleteOnExit();

        String testStr = "This is a test.";

        Utils.writeFileWithEncoding( tempFile, testStr, "US-ASCII" );

        MessageHolder mh = new DefaultMessageHolder();

        Location location = new URLLocatorStrategy().resolve( tempFile.toURL().toExternalForm(), mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        location.open();

        byte[] buffer = new byte[testStr.length()];
        location.read( buffer );

        assertEquals( testStr, new String( buffer, "US-ASCII" ) );
    }

}
