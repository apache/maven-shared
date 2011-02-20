package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Resource;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.plexus.build.incremental.ThreadBuildContext;
import org.sonatype.plexus.build.incremental.test.TestIncrementalBuildContext;

public class IncrementalResourceFilteringTest
    extends PlexusTestCase
{

    File outputDirectory = new File( getBasedir(), "target/IncrementalResourceFilteringTest" );

    File unitDirectory = new File( getBasedir(), "src/test/units-files/incremental" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
        if ( outputDirectory.exists() )
        {
            FileUtils.forceDelete( outputDirectory );
        }
        outputDirectory.mkdirs();
    }

    public void testSimpleIncrementalFiltering()
        throws Exception
    {
        // run full build first
        filter( "time" );

        assertTime( "time", "file01.txt" );
        assertTime( "time", "file02.txt" );

        // only one file is expected to change
        HashSet changedFiles = new HashSet();
        changedFiles.add( "file01.txt" );

        TestIncrementalBuildContext ctx = new TestIncrementalBuildContext( unitDirectory, changedFiles, new HashMap() );
        ThreadBuildContext.setThreadBuildContext( ctx );

        filter( "notime" );
        assertTime( "notime", "file01.txt" );
        assertTime( "time", "file02.txt" ); // this one is unchanged

        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file01.txt" ) ) );

        // one file is expected to be deleted
        HashSet deletedFiles = new HashSet();
        deletedFiles.add( "file01.txt" );

        ctx = new TestIncrementalBuildContext( unitDirectory, new HashSet(), changedFiles, new HashMap() );
        ThreadBuildContext.setThreadBuildContext( ctx );

        filter( "moretime" );
        assertFalse( new File( outputDirectory, "file01.txt" ).exists() );
        assertTime( "time", "file02.txt" ); // this one is unchanged

        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file01.txt" ) ) );

    }

    public void testOutputChange()
        throws Exception
    {
        // run full build first
        filter( "time" );

        // all files are reprocessed after contents of output directory changed (e.g. was deleted)
        HashSet changedFiles = new HashSet();
        changedFiles.add( "target/IncrementalResourceFilteringTest" );
        TestIncrementalBuildContext ctx = new TestIncrementalBuildContext( unitDirectory, changedFiles, new HashMap() );
        ThreadBuildContext.setThreadBuildContext( ctx );

        filter( "notime" );
        assertTime( "notime", "file01.txt" );
        assertTime( "notime", "file02.txt" );

        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file01.txt" ) ) );
        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file02.txt" ) ) );

    }

    public void testFilterChange()
        throws Exception
    {
        // run full build first
        filter( "time" );

        // all files are reprocessed after content of filters changes
        HashSet changedFiles = new HashSet();
        changedFiles.add( "filters.txt" );
        TestIncrementalBuildContext ctx = new TestIncrementalBuildContext( unitDirectory, changedFiles, new HashMap() );
        ThreadBuildContext.setThreadBuildContext( ctx );

        filter( "notime" );
        assertTime( "notime", "file01.txt" );
        assertTime( "notime", "file02.txt" );

        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file01.txt" ) ) );
        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file02.txt" ) ) );

    }

    public void testFilterDeleted()
        throws Exception
    {
        // run full build first
        filter( "time" );

        // all files are reprocessed after content of filters changes
        HashSet deletedFiles = new HashSet();
        deletedFiles.add( "filters.txt" );
        TestIncrementalBuildContext ctx =
            new TestIncrementalBuildContext( unitDirectory, new HashSet(), deletedFiles, new HashMap() );
        ThreadBuildContext.setThreadBuildContext( ctx );

        filter( "notime" );
        assertTime( "notime", "file01.txt" );
        assertTime( "notime", "file02.txt" );

        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file01.txt" ) ) );
        assertTrue( ctx.getRefreshFiles().contains( new File( outputDirectory, "file02.txt" ) ) );
    }

    private void assertTime( String time, String relpath )
        throws IOException
    {
        Properties properties = new Properties();

        InputStream is = new FileInputStream( new File( outputDirectory, relpath ) );
        try
        {
            properties.load( is );
        }
        finally
        {
            IOUtil.close( is );
        }

        assertEquals( time, properties.getProperty( "time" ) );
    }

    private void filter( String time )
        throws Exception, MavenFilteringException
    {
        File baseDir = new File( getBasedir() );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        Properties projectProperties = new Properties();
        projectProperties.put( "time", time );
        projectProperties.put( "java.version", "zloug" );
        mavenProject.setProperties( projectProperties );
        MavenResourcesFiltering mavenResourcesFiltering =
            (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class.getName() );

        String unitFilesDir = new File( unitDirectory, "files" ).getPath();

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );

        List filtersFile = new ArrayList();
        filtersFile.add( new File( unitDirectory, "filters.txt" ).getPath() );

        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, "UTF-8", filtersFile,
                                                 new ArrayList(), new StubMavenSession() );
    }

}
