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
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 22 janv. 08
 * @version $Id$
 */
public class TestReflectionProperties
    extends PlexusTestCase
{

    public void testSimpleFiltering()
        throws Exception
    {
        FileInputStream readFileInputStream = null;
        try
        {
            MavenProject mavenProject = new MavenProject();
            mavenProject.setVersion( "1.0" );
            mavenProject.setGroupId( "org.apache" );
            System.setProperty( "foo", "bar" );
            MavenFileFilter mavenFileFilter = (MavenFileFilter) lookup( MavenFileFilter.class.getName(), "default" );

            File from = new File( getBasedir() + "/src/test/units-files/reflection-test.properties" );
            File to = new File( getBasedir() + "/target/reflection-test.properties" );

            if (to.exists())
            {
                to.delete();
            }            
            
            mavenFileFilter.copyFile( from, to, true, mavenProject, null, false, null, new StubMavenSession() );

            Properties reading = new Properties();
            readFileInputStream = new FileInputStream( to );
            reading.load( readFileInputStream );
            assertEquals( "1.0", reading.get( "version" ) );
            assertEquals( "org.apache", reading.get( "groupId" ) );
            assertEquals( "bar", reading.get( "foo" ) );
            assertEquals( "none filtered", reading.get( "none" ) );
        }
        finally
        {
            if ( readFileInputStream != null )
            {
                readFileInputStream.close();
            }
        }

    }

    public void testSimpleNonFiltering()
        throws Exception
    {
        FileInputStream readFileInputStream = null;
        try
        {
            MavenProject mavenProject = new MavenProject();
            mavenProject.setVersion( "1.0" );
            mavenProject.setGroupId( "org.apache" );
            System.setProperty( "foo", "bar" );
            MavenFileFilter mavenFileFilter = (MavenFileFilter) lookup( MavenFileFilter.class.getName(), "default" );

            File from = new File( getBasedir() + "/src/test/units-files/reflection-test.properties" );
            File to = new File( getBasedir() + "/target/reflection-test.properties" );

            if (to.exists())
            {
                to.delete();
            }
            
            mavenFileFilter.copyFile( from, to, false, mavenProject, null, false, null, new StubMavenSession() );

            Properties reading = new Properties();
            readFileInputStream = new FileInputStream( to );
            reading.load( readFileInputStream );
            assertEquals( "${pom.version}", reading.get( "version" ) );
            assertEquals( "${pom.groupId}", reading.get( "groupId" ) );
            assertEquals( "${foo}", reading.get( "foo" ) );
            assertEquals( "none filtered", reading.get( "none" ) );
        }
        finally
        {
            if ( readFileInputStream != null )
            {
                readFileInputStream.close();
            }
        }

    }    
    
}
