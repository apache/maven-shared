package org.apache.maven.shared.filtering;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

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

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @version $Id$
 */
public class DefaultMavenFileFilterTest
    extends PlexusTestCase
{

    File to = new File( getBasedir(), "target/reflection-test.properties" );

    protected void setUp()
        throws Exception
    {
        super.setUp();
        if ( to.exists() )
        {
            FileUtils.forceDelete( to );
        }
    }

    public void testNotOverwriteFile()
        throws Exception
    {
        MavenFileFilter mavenFileFilter = (MavenFileFilter) lookup( MavenFileFilter.class.getName(), "default" );

        File from = new File( getBasedir(), "src/test/units-files/reflection-test.properties" );

        mavenFileFilter.copyFile( from, to, false, null, null );

        from = new File( getBasedir(), "src/test/units-files/reflection-test-older.properties" );

        // very old file :-)
        from.setLastModified( 1 );

        to.setLastModified( System.currentTimeMillis() );

        mavenFileFilter.copyFile( from, to, false, null, null );

        Properties properties = PropertyUtils.loadPropertyFile( to, null );
        assertEquals( "${pom.version}", properties.getProperty( "version" ) );

    }

    public void testOverwriteFile()
        throws Exception
    {
        MavenFileFilter mavenFileFilter = (MavenFileFilter) lookup( MavenFileFilter.class.getName(), "default" );

        File from = new File( getBasedir(), "src/test/units-files/reflection-test.properties" );

        mavenFileFilter.copyFile( from, to, false, null, null );

        from = new File( getBasedir(), "src/test/units-files/reflection-test-older.properties" );

        // very old file :-)
        from.setLastModified( 1 );

        to.setLastModified( System.currentTimeMillis() );

        mavenFileFilter.copyFile( from, to, false, null, null, true );

        Properties properties = PropertyUtils.loadPropertyFile( to, null );
        assertEquals( "older file", properties.getProperty( "version" ) );

    }

    public void testNullSafeDefaultFilterWrappers()
        throws Exception
    {
        MavenFileFilter mavenFileFilter = (MavenFileFilter) lookup( MavenFileFilter.class.getName(), "default" );

        mavenFileFilter.getDefaultFilterWrappers( null, null, false, null, null );

        // shouldn't fail
    }

    public void testMultiFilterFileInheritance()
        throws Exception
    {
        DefaultMavenFileFilter mavenFileFilter = new DefaultMavenFileFilter();

        File testDir = new File(getBasedir(), "src/test/units-files/MSHARED-177");

        List filters = new ArrayList();

        filters.add(new File(testDir, "first_filter_file.properties").getAbsolutePath());
        filters.add(new File(testDir, "second_filter_file.properties").getAbsolutePath());
        filters.add(new File(testDir, "third_filter_file.properties").getAbsolutePath());

        final Properties filterProperties = new Properties();

        mavenFileFilter.loadProperties(filterProperties, filters, new Properties() );

        assertTrue( filterProperties.getProperty( "third_filter_key" ).equals( "first and second" ) );
    }
}
