package org.apache.maven.shared.model.fileset.util;

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
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Test the FileSet
 *
 * @version $Id$
 */
public class FileSetUtilsTest
    extends TestCase
{
    private Set testDirectories = new HashSet();

    private Set linkFiles = new HashSet();

    /** {@inheritDoc} */
    public void tearDown()
        throws IOException
    {
        for ( Iterator it = linkFiles.iterator(); it.hasNext(); )
        {
            File linkFile = (File) it.next();

            linkFile.delete();
        }

        for ( Iterator it = testDirectories.iterator(); it.hasNext(); )
        {
            File dir = (File) it.next();

            FileUtils.deleteDirectory( dir );
        }
    }

    /**
     * @throws IOException if any
     */
    public void testGetIncludedFiles()
        throws IOException
    {
        File directory = setupTestDirectory( "testGetIncludedFiles" );

        FileSet set = new FileSet();
        set.setDirectory( directory.getPath() );
        set.addInclude( "**/included.txt" );

        FileSetManager fileSetManager = new FileSetManager();

        String[] included = fileSetManager.getIncludedFiles( set );

        Assert.assertEquals( 1, included.length );
    }

    /**
     * @throws IOException if any
     * @throws InterruptedException if any
     * @throws CommandLineException if any
     */
    public void testIncludesDontFollowSymlinks()
        throws IOException, InterruptedException, CommandLineException
    {
        File directory = setupTestDirectory( "testIncludesDontFollowSymlinks" );
        File subdir = new File( directory, "linked-to-self" );

        if ( !createSymlink( directory, subdir ) )
        {
            // assume failure to create a sym link is because the system does not support them
            // and not because the sym link creation failed.
            return;
        }

        FileSet set = new FileSet();
        set.setDirectory( directory.getPath() );
        set.addInclude( "**/included.txt" );
        set.setFollowSymlinks( false );

        FileSetManager fileSetManager = new FileSetManager();

        String[] included = fileSetManager.getIncludedFiles( set );

        Assert.assertEquals( 1, included.length );
    }

    /**
     * @throws IOException if any
     * @throws InterruptedException if any
     * @throws CommandLineException if any
     */
    public void testDeleteDontFollowSymlinks()
        throws IOException, InterruptedException, CommandLineException
    {
        File directory = setupTestDirectory( "testDeleteDontFollowSymlinks" );
        File subdir = new File( directory, "linked-to-self" );

        if ( !createSymlink( directory, subdir ) )
        {
            // assume failure to create a sym link is because the system does not support them
            // and not because the sym link creation failed.
            return;
        }

        FileSet set = new FileSet();
        set.setDirectory( directory.getPath() );
        set.addInclude( "**/included.txt" );
        set.addInclude( "**/linked-to-self" );
        set.setFollowSymlinks( false );

        FileSetManager fileSetManager = new FileSetManager();

        fileSetManager.delete( set );

        Assert.assertFalse( subdir.exists() );
    }

    /**
     * @throws IOException if any
     */
    public void testDelete()
        throws IOException
    {
        File directory = setupTestDirectory( "testDelete" );
        File subdirFile = new File( directory, "subdir/excluded.txt" );

        FileSet set = new FileSet();
        set.setDirectory( directory.getPath() );
        set.addInclude( "**/included.txt" );
        set.addInclude( "**/subdir" );

        FileSetManager fileSetManager = new FileSetManager();

        fileSetManager.delete( set );

        Assert.assertFalse( "file in marked subdirectory still exists.", subdirFile.exists() );
    }

    /**
     * @param from
     * @param to
     * @return
     * @throws InterruptedException
     * @throws CommandLineException
     */
    private boolean createSymlink( File from, File to )
        throws InterruptedException, CommandLineException
    {
        if ( to.exists() )
        {
            to.delete();
        }

        Commandline cli = new Commandline();
        cli.setExecutable( "ln" );
        cli.createArgument().setLine( "-s" );
        cli.createArgument().setLine( from.getPath() );
        cli.createArgument().setLine( to.getPath() );

        int result = cli.execute().waitFor();

        linkFiles.add( to );

        return result == 0;
    }

    /**
     * @param directoryName
     * @return
     * @throws IOException
     */
    private File setupTestDirectory( String directoryName )
        throws IOException
    {
        System.out.println( "Setting up directory for test: " + directoryName );

        URL sourceResource = getClass().getClassLoader().getResource( directoryName );

        if ( sourceResource == null )
        {
            Assert.fail( "Source directory for test: " + directoryName + " cannot be found." );
        }

        File sourceDir = new File( URLDecoder.decode( sourceResource.getPath(), "UTF-8" ) );

        String basedir = System.getProperty( "basedir", System.getProperty( "user.dir" ) );
        String testBase = System.getProperty( "testBase", "target/test-directories" );

        File testDir = new File( basedir, testBase + "/" + directoryName );
        testDir.mkdirs();

        FileUtils.copyDirectoryStructure( sourceDir, testDir );

        testDirectories.add( testDir );

        return testDir;
    }
}
