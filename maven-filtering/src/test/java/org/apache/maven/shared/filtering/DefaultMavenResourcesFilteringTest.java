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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Resource;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * 
 * @since 28 janv. 08
 * 
 * @version $Id$
 */
public class DefaultMavenResourcesFilteringTest
    extends PlexusTestCase
{

    File outputDirectory = new File( getBasedir(), "target/DefaultMavenResourcesFilteringTest" );

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

    public void testSimpleFiltering()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        Properties projectProperties = new Properties();
        projectProperties.put( "foo", "bar" );
        projectProperties.put( "java.version", "zloug" );
        mavenProject.setProperties( projectProperties );
        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";
        File initialImageFile = new File( unitFilesDir, "happy_duke.gif" );

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        List nonFilteredFileExtensions = Collections.singletonList( "gif" );

        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, "UTF-8", filtersFile,
                                                 nonFilteredFileExtensions, new StubMavenSession() );

        assertFiltering( baseDir, initialImageFile, false );
    }

    public void testWithMavenResourcesExecution()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        Properties projectProperties = new Properties();
        projectProperties.put( "foo", "bar" );
        projectProperties.put( "java.version", "zloug" );
        mavenProject.setProperties( projectProperties );
        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";
        File initialImageFile = new File( unitFilesDir, "happy_duke.gif" );

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        List nonFilteredFileExtensions = Collections.singletonList( "gif" );
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", filtersFile,
                                                                                       nonFilteredFileExtensions,
                                                                                       new StubMavenSession() );
        mavenResourcesExecution.setEscapeString( "\\" );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        assertFiltering( baseDir, initialImageFile, true );
    }

    private void assertFiltering( File baseDir, File initialImageFile, boolean escapeTest )
        throws Exception
    {
        assertEquals( 7, outputDirectory.listFiles().length );
        Properties result = new Properties();
        FileInputStream in = null;
        try
        {
            in = new FileInputStream( new File( outputDirectory, "empty-maven-resources-filtering.txt" ) );
            result.load( in );
        }
        finally
        {
            IOUtil.close( in );
        }

        assertTrue( result.isEmpty() );

        result = new Properties();
        in = null;
        try
        {
            in = new FileInputStream( new File( outputDirectory, "maven-resources-filtering.txt" ) );
            result.load( in );
        }
        finally
        {
            IOUtil.close( in );
        }
        assertFalse( result.isEmpty() );

        assertEquals( "1.0", result.get( "version" ) );
        assertEquals( "org.apache", result.get( "groupId" ) );
        assertEquals( "bar", result.get( "foo" ) );
        assertEquals( "${foo.version}", result.get( "fooVersion" ) );

        assertEquals( "@@", result.getProperty( "emptyexpression" ) );
        assertEquals( "${}", result.getProperty( "emptyexpression2" ) );
        assertEquals( System.getProperty( "user.dir" ), result.getProperty( "userDir" ) );
        assertEquals( System.getProperty( "java.version" ), result.getProperty( "javaVersion" ) );

        if ( escapeTest )
        {
            assertEquals( "${java.version}", result.getProperty( "escapeJavaVersion" ) );
            assertEquals( "@user.dir@", result.getProperty( "escapeuserDir" ) );
        }
        assertEquals( baseDir.toString(), result.get( "base" ) );
        assertEquals( new File( baseDir.toString() ).getPath(), new File( result.getProperty( "base" ) ).getPath() );

        File imageFile = new File( outputDirectory, "happy_duke.gif" );
        assertTrue( imageFile.exists() );
        //assertEquals( initialImageFile.length(), imageFile.length() );
        assertTrue( filesAreIdentical( initialImageFile, imageFile ) );
    }

    public void testAddingTokens()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        final StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        Properties projectProperties = new Properties();
        projectProperties.put( "foo", "bar" );
        projectProperties.put( "java.version", "zloug" );
        mavenProject.setProperties( projectProperties );
        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";
        File initialImageFile = new File( unitFilesDir, "happy_duke.gif" );

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        List nonFilteredFileExtensions = Collections.singletonList( "gif" );

        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", null,
                                                                                       nonFilteredFileExtensions,
                                                                                       new StubMavenSession() );

        mavenResourcesExecution.addFilerWrapperWithEscaping( new MavenProjectValueSource( mavenProject, true ), "@",
                                                             "@", null );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        Properties result = PropertyUtils
            .loadPropertyFile( new File( outputDirectory, "maven-resources-filtering.txt" ), null );
        assertFalse( result.isEmpty() );
        assertEquals( mavenProject.getName(), result.get( "pomName" ) );
        assertFiltering( baseDir, initialImageFile, false );
    }

    public void testNoFiltering()
        throws Exception
    {
        StubMavenProject mavenProject = new StubMavenProject( new File( getBasedir() ) );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";
        File initialImageFile = new File( unitFilesDir, "happy_duke.gif" );

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );

        resource.setDirectory( unitFilesDir );
        resource.setFiltering( false );
        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, "UTF-8", null,
                                                 Collections.EMPTY_LIST, new StubMavenSession() );

        assertEquals( 7, outputDirectory.listFiles().length );
        Properties result = PropertyUtils.loadPropertyFile( new File( outputDirectory,
                                                                      "empty-maven-resources-filtering.txt" ), null );
        assertTrue( result.isEmpty() );

        result = PropertyUtils.loadPropertyFile( new File( outputDirectory, "maven-resources-filtering.txt" ), null );
        assertFalse( result.isEmpty() );

        assertEquals( "${pom.version}", result.get( "version" ) );
        assertEquals( "${pom.groupId}", result.get( "groupId" ) );
        assertEquals( "${foo}", result.get( "foo" ) );
        assertEquals( "@@", result.getProperty( "emptyexpression" ) );
        assertEquals( "${}", result.getProperty( "emptyexpression2" ) );
        File imageFile = new File( outputDirectory, "happy_duke.gif" );
        assertTrue( filesAreIdentical( initialImageFile, imageFile ) );
    }

    public static boolean filesAreIdentical( File expected, File current )
        throws IOException
    {
        if ( expected.length() != current.length() )
        {
            return false;
        }
        FileInputStream expectedIn = null;
        FileInputStream currentIn = null;
        try
        {
            expectedIn = new FileInputStream( expected );
            currentIn = new FileInputStream( current );

            byte[] expectedBuffer = IOUtil.toByteArray( expectedIn );

            byte[] currentBuffer = IOUtil.toByteArray( currentIn );
            if ( expectedBuffer.length != currentBuffer.length )
            {
                return false;
            }
            for ( int i = 0, size = expectedBuffer.length; i < size; i++ )
            {
                if ( expectedBuffer[i] != currentBuffer[i] )
                {
                    return false;
                }
            }
        }
        finally
        {
            IOUtil.close( expectedIn );
            IOUtil.close( currentIn );
        }
        return true;
    }

    public void testIncludeOneFile()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );
        resource.addInclude( "includ*" );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", filtersFile,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );

        File[] files = outputDirectory.listFiles();
        assertEquals( 1, files.length );
        assertEquals( "includefile.txt", files[0].getName() );

    }

    public void testIncludeOneFileAndDirectory()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );
        resource.addInclude( "includ*" );
        resource.addInclude( "**/includ*" );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", filtersFile,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );

        File[] files = outputDirectory.listFiles();
        assertEquals( 2, files.length );
        File includeFile = new File( outputDirectory, "includefile.txt" );
        assertTrue( includeFile.exists() );

        includeFile = new File( new File( outputDirectory, "includedir" ), "include.txt" );
        assertTrue( includeFile.exists() );

    }

    public void testExcludeOneFile()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );
        resource.addExclude( "*.gif" );
        resource.addExclude( "**/excludedir/**" );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", filtersFile,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );

        File[] files = outputDirectory.listFiles();
        assertEquals( 5, files.length );
        File includeFile = new File( outputDirectory, "includefile.txt" );
        assertTrue( includeFile.exists() );

        includeFile = new File( new File( outputDirectory, "includedir" ), "include.txt" );
        assertTrue( includeFile.exists() );

        File imageFile = new File( outputDirectory, "happy_duke.gif" );
        assertFalse( imageFile.exists() );

        File excludeDir = new File( outputDirectory, "excludedir" );
        assertFalse( excludeDir.exists() );
    }
    
    public void testTargetAbsolutePath()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );
        resource.addInclude( "includ*" );

        String targetPath = getBasedir() + "/target/testAbsolutePath/";
        File targetPathFile = new File( targetPath );
        resource.setTargetPath( targetPathFile.getAbsolutePath() );
        
        if ( !targetPathFile.exists() )
        {
            targetPathFile.mkdirs();
        }
        else
        {
            FileUtils.cleanDirectory( targetPathFile );
        }
        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", filtersFile,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        
        File[] files = targetPathFile.listFiles();
        assertEquals( 1, files.length );
        assertEquals( "includefile.txt", files[0].getName() );
    }       

    public void testTargetPath()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        String unitFilesDir = getBasedir() + "/src/test/units-files/maven-resources-filtering";

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );
        resource.addInclude( "includ*" );
        resource.setTargetPath( "testTargetPath" );
        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir()
            + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );

        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", filtersFile,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );

        File targetPathFile = new File( outputDirectory, "testTargetPath" );

        File[] files = targetPathFile.listFiles();
        assertEquals( 1, files.length );
        assertEquals( "includefile.txt", files[0].getName() );
    }    

    public void testEmptyDirectories()
        throws Exception
    {
        File baseDir = new File( "c:\\foo\\bar" );
        StubMavenProject mavenProject = new StubMavenProject( baseDir );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );
        mavenProject.setName( "test project" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );


        List resources = new ArrayList();
        resources.add( new Resource()
        {
            {
                setDirectory( getBasedir() + "/src/test/units-files/includeEmptyDirs" );
            }
        } );
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, "UTF-8", Collections.EMPTY_LIST,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesExecution.setIncludeEmptyDirs( true );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );
        
        File[] childs = outputDirectory.listFiles();
        assertEquals( 3, childs.length );
        
        for ( int i = 0, size = childs.length; i < size; i++ )
        {
            File file = childs[i];
            if ( file.getName().endsWith( "dir1" ) || file.getName().endsWith( "empty-directory" )
                || file.getName().endsWith( "empty-directory-child" ) )
            {
                if ( file.getName().endsWith( "dir1" ) )
                {
                    assertEquals( 1, file.list().length );
                    assertTrue( file.listFiles()[0].getName().endsWith( "foo.txt" ) );
                }
                if ( file.getName().endsWith( "empty-directory" ) )
                {
                    assertEquals( 0, file.list().length );
                }
                if ( file.getName().endsWith( "empty-directory-child" ) )
                {
                    assertEquals( 1, file.list().length );
                    assertTrue( file.listFiles()[0].isDirectory() );
                    assertEquals( 0, file.listFiles()[0].listFiles().length );
                }
            }
            else
            {
                fail( "unknow child file found " + file.getName() );
            }
        }
    }    
    
    /**
     * unit test for MSHARED-81 : http://jira.codehaus.org/browse/MSHARED-81
     */
    public void testMSHARED81()
        throws Exception
    {
        StubMavenProject mavenProject = new StubMavenProject( new File( "/foo/bar" ) );
        
        mavenProject.setVersion( "1.0" );

        mavenProject.addProperty( "escaped","this is escaped");
        mavenProject.addProperty( "escaped.at","this is escaped.at");
        mavenProject.addProperty( "foo","this is foo");
        mavenProject.addProperty( "bar","this is bar");
        
        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        List resources = new ArrayList();
        resources.add( new Resource()
        {
            {
                setDirectory( getBasedir() + "/src/test/units-files/MSHARED-81/resources" );
                setFiltering( false );
            }
        } );
        resources.add( new Resource()
        {
            {
                setDirectory( getBasedir() + "/src/test/units-files/MSHARED-81/filtered" );
                setFiltering( true );
            }
        } );
        File output = new File( outputDirectory, "MSHARED-81" );
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, output, mavenProject,
                                                                                       "UTF-8", Collections.EMPTY_LIST,
                                                                                       Collections.EMPTY_LIST,
                                                                                       new StubMavenSession() );
        mavenResourcesExecution.setIncludeEmptyDirs( true );
        mavenResourcesExecution.setEscapeString( "\\" );
        mavenResourcesFiltering.filterResources( mavenResourcesExecution );

        Properties filteredResult = PropertyUtils.loadPropertyFile( new File( output, "filtered.properties" ), null );

        Properties expectedFilteredResult = PropertyUtils.loadPropertyFile( new File( getBasedir()
            + "/src/test/units-files/MSHARED-81", "expected-filtered.properties" ), null );

        assertTrue( filteredResult.equals( expectedFilteredResult ) );

        Properties nonFilteredResult = PropertyUtils.loadPropertyFile( new File( output, "unfiltered.properties" ),
                                                                       null );

        Properties expectedNonFilteredResult = PropertyUtils.loadPropertyFile( new File( getBasedir()
            + "/src/test/units-files/MSHARED-81/resources", "unfiltered.properties" ), null );

        assertTrue( nonFilteredResult.equals( expectedNonFilteredResult ) );
    }        
    
}
