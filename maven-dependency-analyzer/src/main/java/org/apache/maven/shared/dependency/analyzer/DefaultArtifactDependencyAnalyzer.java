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
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * This analyzer will analyze specified artifact. The actual analyzing work would be done by ProjectDependencyAnalyzer.
 * 
 * @author <a href="mailto:wangyf2010@gmail.com">Simon Wang</a>
 * @version $Id$
 */
@Component( role = ArtifactDependencyAnalyzer.class )
public class DefaultArtifactDependencyAnalyzer
    implements ArtifactDependencyAnalyzer
{

    // fields -----------------------------------------------------------------

    @Requirement
    private ProjectDependencyAnalyzer projectDependencyAnalyzer;

    @Requirement
    private MavenProjectBuilder mavenProjectBuilder;

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private ArtifactFactory artifactFactory;

    // public methods ---------------------------------------------------------

    public ProjectDependencyAnalysis analyze( Artifact artifact, List remoteArtifactRepositories,
                                              ArtifactRepository localRepository )
        throws ProjectDependencyAnalyzerException
    {
        try
        {
            if ( !artifact.isResolved() )
            {
                artifactResolver.resolve( artifact, remoteArtifactRepositories, localRepository );
            }

            Artifact projectArtifact =
                artifactFactory.createProjectArtifact( artifact.getGroupId(), artifact.getArtifactId(),
                                                       artifact.getVersion() );

            File pomFile = new File( localRepository.getBasedir(), localRepository.pathOf( projectArtifact ) );

            MavenProject project = this.mavenProjectBuilder.buildWithDependencies( pomFile, localRepository, null );
            project.setDependencyArtifacts( project.createArtifacts( artifactFactory, null, null ) );

            return this.projectDependencyAnalyzer.analyze( project );
        }
        catch ( ProjectBuildingException e )
        {
            throw new ProjectDependencyAnalyzerException( "can't build maven project for artifact - "
                + artifact.toString(), e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ProjectDependencyAnalyzerException( "can't resolve artifact - " + artifact.toString(), e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new ProjectDependencyAnalyzerException( "can't find artifact - " + artifact.toString(), e );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new ProjectDependencyAnalyzerException( "Invalid dependency version for artifact - "
                + artifact.toString(), e );
        }
    }

    // ClassFileVisitor methods for testing -----------------------------------------------

    protected void setProjectDependencyAnalyzer( ProjectDependencyAnalyzer projectDependencyAnalyzer )
    {
        this.projectDependencyAnalyzer = projectDependencyAnalyzer;
    }

    protected void setMavenProjectBuilder( MavenProjectBuilder mavenProjectBuilder )
    {
        this.mavenProjectBuilder = mavenProjectBuilder;
    }

    protected void setArtifactResolver( ArtifactResolver artifactResolver )
    {
        this.artifactResolver = artifactResolver;
    }

    protected ArtifactFactory getFactory()
    {
        return artifactFactory;
    }

    protected void setArtifactFactory( ArtifactFactory factory )
    {
        this.artifactFactory = factory;
    }

}