package org.apache.maven.shared.jar;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

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

/**
 * Tests for the JarAnalyzer class.
 */
public class JarAnalyzerTest
    extends AbstractJarAnalyzerTestCase
{
    private JarAnalyzer jarAnalyzer;


    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        if ( jarAnalyzer != null )
        {
            jarAnalyzer.closeQuietly();
        }
    }

    private JarData getJarData( String filename )
        throws Exception
    {
        jarAnalyzer = getJarAnalyzer( filename );
        return jarAnalyzer.getJarData();
    }

    private JarAnalyzer getJarAnalyzer( String filename )
        throws IOException
    {
        return new JarAnalyzer( getSampleJar( filename ) );
    }

    public void testSealed()
        throws Exception
    {
        JarData jarData = getJarData( "evil-sealed-regex-1.0.jar" );
        assertTrue( jarData.isSealed() );
    }

    public void testNotSealed()
        throws Exception
    {
        JarData jarData = getJarData( "codec.jar" );
        assertFalse( jarData.isSealed() );
    }

    public void testMissingFile()
        throws Exception
    {
        try
        {
            jarAnalyzer = new JarAnalyzer( new File( "foo-bar.jar" ) );
            fail( "Should not have succeeded to get the missing JAR" );
        }
        catch ( ZipException e )
        {
            assertTrue( true );
        }
    }

    public void testInvalidJarFile()
        throws Exception
    {
        try
        {
            getJarAnalyzer( "invalid.jar" );
            fail( "Should not have succeeded to get the invalid JAR" );
        }
        catch ( ZipException e )
        {
            assertTrue( true );
        }
    }

    public void testCloseTwice()
        throws Exception
    {
        JarAnalyzer jarAnalyzer = getJarAnalyzer( "codec.jar" );

        // no exception should be thrown
        jarAnalyzer.closeQuietly();
        jarAnalyzer.closeQuietly();
        assertTrue( true );
    }
}
