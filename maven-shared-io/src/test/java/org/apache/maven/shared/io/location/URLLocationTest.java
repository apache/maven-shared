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
import java.net.URL;

import org.apache.maven.shared.io.Utils;

import junit.framework.TestCase;

public class URLLocationTest
    extends TestCase
{

    public void testShouldConstructFromUrlAndTempFileSpecifications() throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );
        f.deleteOnExit();

        URL url = f.toURL();

        new URLLocation( url, f.getAbsolutePath(), "prefix.", ".suffix", true );
    }

    public void testShouldTransferFromTempFile() throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );
        f.deleteOnExit();

        URL url = f.toURL();

        URLLocation location = new URLLocation( url, f.getAbsolutePath(), "prefix.", ".suffix", true );

        assertNotNull( location.getFile() );
        assertFalse( f.equals( location.getFile() ) );
    }

    public void testShouldTransferFromTempFileThenRead() throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );
        f.deleteOnExit();

        String testStr = "This is a test";

        Utils.writeFileWithEncoding( f, testStr, "US-ASCII" );

        URL url = f.toURL();

        URLLocation location = new URLLocation( url, f.getAbsolutePath(), "prefix.", ".suffix", true );

        location.open();

        byte[] buffer = new byte[ testStr.length() ];

        int read = location.read( buffer );

        assertEquals( testStr.length(), read );

        assertEquals( testStr, new String( buffer, "US-ASCII" ) );
    }

}
