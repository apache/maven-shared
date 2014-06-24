package org.apache.maven.shared.dependency.analyzer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.test.plugin.ProjectTool;
import org.apache.maven.shared.test.plugin.RepositoryTool;
import org.apache.maven.shared.test.plugin.TestToolsException;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Tests <code>DefaultProjectDependencyAnalyzer</code>.
 * 
 * @author <a href="mailto:wangyf2010@gmail.com">Simon Wang</a>
 * @version $Id$
 * @see DefaultArtifactDependencyAnalyzer
 */
public class DefaultArtifactDependencyAnalyzerTest
    extends PlexusTestCase
{
    // fields -----------------------------------------------------------------

    private ProjectTool projectTool;

    private DefaultArtifactDependencyAnalyzer analyzer;

    private static ArtifactRepository localRepo;

    private ProjectDependencyAnalyzer projectDependencyAnalyzer;

    private MavenProjectBuilder mavenProjectBuilder;

    private ArtifactResolver artifactResolver;

    private ArtifactFactory artifactFactory;

    // TestCase methods -------------------------------------------------------

    /*
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        projectTool = (ProjectTool) lookup( ProjectTool.ROLE );

        if ( localRepo == null )
        {
            RepositoryTool repositoryTool = (RepositoryTool) lookup( RepositoryTool.ROLE );

            localRepo = repositoryTool.createLocalArtifactRepositoryInstance();
        }

        analyzer = (DefaultArtifactDependencyAnalyzer) lookup( ArtifactDependencyAnalyzer.ROLE );

        projectDependencyAnalyzer = (ProjectDependencyAnalyzer) lookup( ProjectDependencyAnalyzer.ROLE );

        mavenProjectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.ROLE );

        artifactResolver = (ArtifactResolver) lookup( ArtifactResolver.ROLE );

        artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );

    }

    private MavenProject getProject( String pomPath )
        throws TestToolsException
    {
        File pom = getTestFile( "target/test-classes/", pomPath );

        return projectTool.readProjectWithDependencies( pom );
    }

    private Artifact createArtifact( String groupId, String artifactId, String type, String version, String scope )
    {
        return artifactFactory.createArtifact( groupId, artifactId, version, scope, type );
    }

    // tests ------------------------------------------------------------------
    public void testJarWithXmlTransitiveDependency()
        throws TestToolsException, ProjectDependencyAnalyzerException, ProjectBuildingException,
        ArtifactResolutionException, ArtifactNotFoundException
    {
        System.setProperty( "maven.home", "C:/apache-maven-3.0.5" );

        MavenProject project = getProject( "jarWithXmlTransitiveDependency/pom.xml" );

        Artifact jdom = createArtifact( "xalan", "xalan", "jar", "2.7.1", "compile" );

        analyzer.setArtifactResolver( artifactResolver );
        analyzer.setMavenProjectBuilder( mavenProjectBuilder );
        analyzer.setProjectDependencyAnalyzer( projectDependencyAnalyzer );
        analyzer.setArtifactFactory( artifactFactory );
        ProjectDependencyAnalysis actualAnalysis =
            analyzer.analyze( jdom, project.getRemoteArtifactRepositories(), localRepo );

        System.out.println( actualAnalysis.getUnusedDeclaredArtifacts().toString() );
        System.out.println( actualAnalysis.getUsedDeclaredArtifacts().toString() );
        System.out.println( actualAnalysis.getUsedUndeclaredArtifacts().toString() );
    }

}
