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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Resource;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 28 janv. 08
 * @version $Id$
 */
public class DefaultMavenResourcesFilteringTest
    extends PlexusTestCase
{

    File outputDirectory = new File(getBasedir(), "target/DefaultMavenResourcesFilteringTest");
    

    
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
        File initialImageFile = new File(unitFilesDir, "happy_duke.gif");
        
        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( true );

        List filtersFile = new ArrayList();
        filtersFile.add( getBasedir() + "/src/test/units-files/maven-resources-filtering/empty-maven-resources-filtering.txt" );
        
        List nonFilteredFileExtensions = Collections.singletonList( "gif" );
        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, null, filtersFile, nonFilteredFileExtensions );
       
        assertEquals( 3, outputDirectory.listFiles().length );
        Properties result = PropertyUtils.loadPropertyFile( new File(outputDirectory, "empty-maven-resources-filtering.txt"), null );
        assertTrue (result.isEmpty());
        
        result = PropertyUtils.loadPropertyFile( new File(outputDirectory, "maven-resources-filtering.txt"), null );
        assertFalse( result.isEmpty() );
        
        assertEquals("1.0", result.get( "version" ));
        assertEquals("org.apache", result.get( "groupId" ));
        assertEquals("bar", result.get( "foo" ));
        // FIXME this can fail with a windows path
        String base = result.getProperty( "base" );
        
        assertEquals( "@@", result.getProperty( "emptyexpression" ) );
        assertEquals( "${}", result.getProperty( "emptyexpression2" ) );
        assertEquals( System.getProperty( "java.version" ), result.getProperty( "javaVersion" ) );
        
        assertEquals( baseDir.toString(), result.get( "base" ) );
        
        File imageFile = new File(outputDirectory, "happy_duke.gif");
        assertTrue( imageFile.exists() );
        //assertEquals( initialImageFile.length(), imageFile.length() );
        assertTrue(filesAreIdentical( initialImageFile, imageFile ));
    }
    
    public void testaddingTokens()
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
        
        MavenFileFilter mavenFileFilter = (MavenFileFilter) lookup( MavenFileFilter.class.getName(), "default" );
        List defaultFilterWrappers = mavenFileFilter.getDefaultFilterWrappers( mavenProject, null, true );

        List filterWrappers = new ArrayList( );
        filterWrappers.addAll( defaultFilterWrappers );
        FileUtils.FilterWrapper filterWrapper = new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                ReflectionProperties reflectionProperties = new ReflectionProperties( mavenProject, true );
                return new InterpolationFilterReader( reader, reflectionProperties, "@", "@" );
            }
        };
        filterWrappers.add( filterWrapper );
        mavenResourcesFiltering.filterResources( resources, outputDirectory, null, filterWrappers,
                                                 new File( getBasedir() ), nonFilteredFileExtensions );
        
        Properties result = PropertyUtils.loadPropertyFile( new File(outputDirectory, "maven-resources-filtering.txt"), null );
        assertFalse( result.isEmpty() );
        assertEquals( mavenProject.getName(), result.get( "pomName" ) );
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
        File initialImageFile = new File(unitFilesDir, "happy_duke.gif");
        
        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        
        resource.setDirectory( unitFilesDir );
        resource.setFiltering( false );
        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, null, null,
                                                 Collections.EMPTY_LIST );

        assertEquals( 3, outputDirectory.listFiles().length );
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
        File imageFile = new File(outputDirectory, "happy_duke.gif");
        assertTrue(filesAreIdentical( initialImageFile, imageFile ));
    }  
    
    public static boolean filesAreIdentical( File expected, File current )
        throws IOException
    {
        if ( expected.length() != current.length() )
        {
            return false;
        }
        FileInputStream expectedIn = new FileInputStream( expected );
        FileInputStream currentIn = new FileInputStream( current );
        try
        {
            byte[] expectedBuffer = IOUtil.toByteArray( expectedIn );
            
            byte[] currentBuffer = IOUtil.toByteArray( currentIn );
            if (expectedBuffer.length != currentBuffer.length)
            {
                return false;
            }
            for (int i = 0,size = expectedBuffer.length;i<size;i++)
            {
                if(expectedBuffer[i]!= currentBuffer[i])
                {
                    return false;
                }
            }
        }
        finally
        {
            expectedIn.close();
            currentIn.close();
        }
        return true;
    }


}
