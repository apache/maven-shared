package org.apache.maven.shared.runtime;

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
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.test.plugin.BuildTool;
import org.apache.maven.shared.test.plugin.TestToolsException;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Tests <code>DefaultMavenRuntime</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see DefaultMavenRuntime
 */
public class DefaultMavenRuntimeTest extends PlexusTestCase
{
    // fields -----------------------------------------------------------------

    private BuildTool buildTool;

    private MavenRuntime mavenRuntime;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        buildTool = (BuildTool) lookup( BuildTool.ROLE );

        mavenRuntime = (MavenRuntime) lookup( MavenRuntime.ROLE );
    }

    // tests ------------------------------------------------------------------

    public void testGetSortedProjectsWithSingleJar()
        throws TestToolsException, MalformedURLException, MavenRuntimeException
    {
        packageProject( "testSingleJar/pom.xml" );

        File jar = getPackage( "testSingleJar/pom.xml" );

        ClassLoader classLoader = newClassLoader( jar );

        List projects = mavenRuntime.getSortedProjects( classLoader );

        assertMavenProjects( "org.apache.maven.shared.runtime.tests:testSingleJar:1.0", projects );
    }

    public void testGetSortedProjectsWithMultipleJars()
        throws TestToolsException, MalformedURLException, MavenRuntimeException
    {
        packageProject( "testMultipleJars/project1/pom.xml" );
        packageProject( "testMultipleJars/project2/pom.xml" );
        packageProject( "testMultipleJars/project3/pom.xml" );

        File jar1 = getPackage( "testMultipleJars/project1/pom.xml" );
        File jar2 = getPackage( "testMultipleJars/project2/pom.xml" );
        File jar3 = getPackage( "testMultipleJars/project3/pom.xml" );

        ClassLoader classLoader = newClassLoader( new File[] { jar1, jar2, jar3 } );

        List projects = mavenRuntime.getSortedProjects( classLoader );

        assertMavenProjects( new String[] {
            "org.apache.maven.shared.runtime.tests:testMultipleJars1:1.0",
            "org.apache.maven.shared.runtime.tests:testMultipleJars2:1.0",
            "org.apache.maven.shared.runtime.tests:testMultipleJars3:1.0"
        }, projects );
    }

    public void testGetSortedProjectsWithDependentJars()
        throws TestToolsException, MalformedURLException, MavenRuntimeException
    {
        packageProject( "testDependentJars/pom.xml" );

        File jar1 = getPackage( "testDependentJars/project1/pom.xml" );
        File jar2 = getPackage( "testDependentJars/project2/pom.xml" );
        File jar3 = getPackage( "testDependentJars/project3/pom.xml" );

        ClassLoader classLoader = newClassLoader( new File[] { jar1, jar2, jar3 } );

        List projects = mavenRuntime.getSortedProjects( classLoader );

        assertMavenProjects( new String[] {
            "org.apache.maven.shared.runtime.tests:testDependentJars3:1.0",
            "org.apache.maven.shared.runtime.tests:testDependentJars1:1.0",
            "org.apache.maven.shared.runtime.tests:testDependentJars2:1.0"
        }, projects );
    }

    // private methods --------------------------------------------------------

    private void packageProject( String pomPath ) throws TestToolsException
    {
        File pom = getTestFile( "target/test-classes/", pomPath );
        Properties properties = new Properties();
        List goals = Arrays.asList( new String[] { "clean", "package" } );

        InvocationResult result = buildTool.executeMaven( pom, properties, goals, null );
        assertNull( "Error building test project", result.getExecutionException() );
        assertEquals( "Error building test project", 0, result.getExitCode() );
    }

    private File getPackage( String pomPath )
    {
        File pom = getTestFile( "target/test-classes/", pomPath );

        File target = new File( pom.getParentFile(), "target" );
        File[] jars = target.listFiles( (FilenameFilter) new SuffixFileFilter( "jar" ) );
        assertEquals( "Cannot find jar", 1, jars.length );

        File jar = jars[0];
        assertTrue( "Cannot find jar", jar.exists() && jar.isFile() );

        return jar;
    }

    private ClassLoader newClassLoader( File file ) throws MalformedURLException
    {
        return newClassLoader( new File[] { file } );
    }

    private ClassLoader newClassLoader( File[] files ) throws MalformedURLException
    {
        URL[] urls = new URL[files.length];

        for ( int i = 0; i < files.length; i++ )
        {
            urls[i] = files[i].toURI().toURL();
        }

        return new URLClassLoader( urls, null );
    }

    private void assertMavenProjects( String id, List projects )
    {
        assertMavenProjects( new String[] { id }, projects );
    }

    private void assertMavenProjects( String[] ids, List projects )
    {
        assertEquals( "Number of projects", ids.length, projects.size() );

        for ( int i = 0; i < ids.length; i++ )
        {
            assertMavenProject( ids[i], (MavenProject) projects.get( i ) );
        }
    }

    private void assertMavenProject( String id, MavenProject project )
    {
        String[] tokens = id.split( ":" );

        assertMavenProject( tokens[0], tokens[1], tokens[2], project );
    }

    private void assertMavenProject( String groupId, String artifactId, String version, MavenProject project )
    {
        assertEquals( "Group id", groupId, project.getGroupId() );
        assertEquals( "Artifact id", artifactId, project.getArtifactId() );
        assertEquals( "Version", version, project.getVersion() );
    }
}
