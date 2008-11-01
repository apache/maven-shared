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

import org.apache.maven.shared.io.TestUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class FileLocationTest
    extends TestCase
{

    public void testShouldConstructWithFileThenRetrieveSameFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        assertSame( file, location.getFile() );
        assertEquals( file.getAbsolutePath(), location.getSpecification() );
    }

    public void testShouldReadFileContentsUsingByteBuffer() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeFileWithEncoding( file, testStr, "US-ASCII" );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        location.open();

        ByteBuffer buffer = ByteBuffer.allocate( testStr.length() );
        location.read( buffer );

        assertEquals( testStr, new String( buffer.array(), "US-ASCII" ) );
    }

    public void testShouldReadFileContentsUsingStream() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeFileWithEncoding( file, testStr, "US-ASCII" );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        location.open();

        InputStream stream = location.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtil.copy( stream, out );

        assertEquals( testStr, new String(out.toByteArray(), "US-ASCII" ) );
    }

    public void testShouldReadFileContentsUsingByteArray() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeFileWithEncoding( file, testStr, "US-ASCII" );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        location.open();

        byte[] buffer = new byte[ testStr.length() ];
        location.read( buffer );

        assertEquals( testStr, new String( buffer, "US-ASCII" ) );
    }

    public void testShouldReadThenClose() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeFileWithEncoding( file, testStr, "US-ASCII" );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        location.open();

        byte[] buffer = new byte[ testStr.length() ];
        location.read( buffer );

        assertEquals( testStr, new String( buffer, "US-ASCII" ) );

        location.close();
    }

    public void testShouldOpenThenFailToSetFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        TestFileLocation location = new TestFileLocation( file.getAbsolutePath() );

        location.open();

        try
        {
            location.setFile( file );

            fail( "should not succeed." );
        }
        catch( IllegalStateException e )
        {
        }
    }

    public void testShouldConstructWithoutFileThenSetFileThenOpen() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        TestFileLocation location = new TestFileLocation( file.getAbsolutePath() );

        location.setFile( file );
        location.open();
    }

    public void testShouldConstructWithLocationThenRetrieveEquivalentFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        Location location = new TestFileLocation( file.getAbsolutePath() );

        assertEquals( file, location.getFile() );
        assertEquals( file.getAbsolutePath(), location.getSpecification() );
    }

    private static final class TestFileLocation extends FileLocation
    {

        TestFileLocation( String specification )
        {
            super( specification );
        }

    }

}
