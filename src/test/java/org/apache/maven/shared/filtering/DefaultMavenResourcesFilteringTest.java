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
package org.apache.maven.shared.filtering;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Resource;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

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
        StubMavenProject mavenProject = new StubMavenProject( new File( getBasedir() ) );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );

        Properties projectProperties = new Properties();
        projectProperties.put( "foo", "bar" );     
        mavenProject.setProperties( projectProperties );
        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( getBasedir() + "/src/test/units-files/maven-resources-filtering" );
        resource.setFiltering( true );
        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, null, null );
       
        assertEquals( 2, outputDirectory.listFiles().length );
        Properties result = PropertyUtils.loadPropertyFile( new File(outputDirectory, "empty-maven-resources-filtering.txt"), null );
        assertTrue (result.isEmpty());
        
        result = PropertyUtils.loadPropertyFile( new File(outputDirectory, "maven-resources-filtering.txt"), null );
        assertFalse( result.isEmpty() );
        
        assertEquals("1.0", result.get( "version" ));
        assertEquals("org.apache", result.get( "groupId" ));
        assertEquals("bar", result.get( "foo" ));
        // FIXME this can fail with a windows path
        String base = result.getProperty( "base" );
        assertEquals(getBasedir(), base);
    }
    
    public void testNoFiltering()
        throws Exception
    {
        StubMavenProject mavenProject = new StubMavenProject( new File( getBasedir() ) );
        mavenProject.setVersion( "1.0" );
        mavenProject.setGroupId( "org.apache" );

        MavenResourcesFiltering mavenResourcesFiltering = (MavenResourcesFiltering) lookup( MavenResourcesFiltering.class
            .getName() );

        Resource resource = new Resource();
        List resources = new ArrayList();
        resources.add( resource );
        resource.setDirectory( getBasedir() + "/src/test/units-files/maven-resources-filtering" );
        resource.setFiltering( false );
        mavenResourcesFiltering.filterResources( resources, outputDirectory, mavenProject, null, null );

        assertEquals( 2, outputDirectory.listFiles().length );
        Properties result = PropertyUtils.loadPropertyFile( new File( outputDirectory,
                                                                      "empty-maven-resources-filtering.txt" ), null );
        assertTrue( result.isEmpty() );

        result = PropertyUtils.loadPropertyFile( new File( outputDirectory, "maven-resources-filtering.txt" ), null );
        assertFalse( result.isEmpty() );

        assertEquals( "${pom.version}", result.get( "version" ) );
        assertEquals( "${pom.groupId}", result.get( "groupId" ) );
        assertEquals( "${foo}", result.get( "foo" ) );
    }    
}
