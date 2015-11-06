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
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;
import org.apache.maven.shared.utils.io.IOUtil;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author Olivier Lamy
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
        MavenFileFilter mavenFileFilter = lookup( MavenFileFilter.class );

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
        MavenFileFilter mavenFileFilter = lookup( MavenFileFilter.class );

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
        MavenFileFilter mavenFileFilter = lookup( MavenFileFilter.class );

        mavenFileFilter.getDefaultFilterWrappers( null, null, false, null, null );

        // shouldn't fail
    }

    public void testMultiFilterFileInheritance()
        throws Exception
    {
        DefaultMavenFileFilter mavenFileFilter = new DefaultMavenFileFilter();

        File testDir = new File( getBasedir(), "src/test/units-files/MSHARED-177" );

        List<String> filters = new ArrayList<String>();

        filters.add( new File( testDir, "first_filter_file.properties" ).getAbsolutePath() );
        filters.add( new File( testDir, "second_filter_file.properties" ).getAbsolutePath() );
        filters.add( new File( testDir, "third_filter_file.properties" ).getAbsolutePath() );

        final Properties filterProperties = new Properties();

        mavenFileFilter.loadProperties( filterProperties, new File( getBasedir() ), filters, new Properties() );

        assertTrue( filterProperties.getProperty( "third_filter_key" ).equals( "first and second" ) );
    }

    // MSHARED-161: DefaultMavenFileFilter.getDefaultFilterWrappers loads
    // filters from the current directory instead of using basedir
    public void testMavenBasedir()
        throws Exception
    {
        MavenFileFilter mavenFileFilter = lookup( MavenFileFilter.class );

        AbstractMavenFilteringRequest req = new AbstractMavenFilteringRequest();
        req.setFileFilters( Collections.singletonList( "src/main/filters/filefilter.properties" ) );

        MavenProject mavenProject = new StubMavenProject( new File( "src/test/units-files/MSHARED-161" ) );
        mavenProject.getBuild().setFilters( Collections.singletonList( "src/main/filters/buildfilter.properties" ) );
        req.setMavenProject( mavenProject );
        req.setInjectProjectBuildFilters( true );

        List<FilterWrapper> wrappers = mavenFileFilter.getDefaultFilterWrappers( req );

        Reader reader = wrappers.get( 0 ).getReader( new StringReader( "${filefilter} ${buildfilter}" ) );

        assertEquals( "true true", IOUtil.toString( reader ) );
    }

    // MSHARED-198: custom delimiters doesn't work as expected
    public void testCustomDelimiters()
        throws Exception
    {
        MavenFileFilter mavenFileFilter = lookup( MavenFileFilter.class );

        AbstractMavenFilteringRequest req = new AbstractMavenFilteringRequest();
        Properties additionalProperties = new Properties();
        additionalProperties.setProperty( "FILTER.a.ME", "DONE" );
        req.setAdditionalProperties( additionalProperties );
        req.setDelimiters( new LinkedHashSet<String>( Arrays.asList( "aaa*aaa", "abc*abc" ) ) );

        List<FilterWrapper> wrappers = mavenFileFilter.getDefaultFilterWrappers( req );

        Reader reader = wrappers.get( 0 ).getReader( new StringReader( "aaaFILTER.a.MEaaa" ) );
        assertEquals( "DONE", IOUtil.toString( reader ) );

        reader = wrappers.get( 0 ).getReader( new StringReader( "abcFILTER.a.MEabc" ) );
        assertEquals( "DONE", IOUtil.toString( reader ) );
    }

    // MSHARED-199: Filtering doesn't work if 2 delimiters are used on the same line, the first one being left open
    public void testLineWithSingleAtAndExpression()
        throws Exception
    {
        MavenFileFilter mavenFileFilter = lookup( MavenFileFilter.class );

        AbstractMavenFilteringRequest req = new AbstractMavenFilteringRequest();
        Properties additionalProperties = new Properties();
        additionalProperties.setProperty( "foo", "bar" );
        req.setAdditionalProperties( additionalProperties );

        List<FilterWrapper> wrappers = mavenFileFilter.getDefaultFilterWrappers( req );

        Reader reader = wrappers.get( 0 ).getReader( new StringReader( "toto@titi.com ${foo}" ) );
        assertEquals( "toto@titi.com bar", IOUtil.toString( reader ) );
    }
}
