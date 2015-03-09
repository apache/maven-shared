package org.apache.maven.shared.artifact.install.internal;

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
import java.util.Collection;

import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.install.ArtifactInstaller;
import org.apache.maven.shared.artifact.install.ArtifactInstallerException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.installation.InstallRequest;
import org.sonatype.aether.installation.InstallationException;
import org.sonatype.aether.metadata.Metadata;
import org.sonatype.aether.metadata.Metadata.Nature;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.metadata.DefaultMetadata;

@Component( role = ArtifactInstaller.class, hint = "maven3" )
public class Maven30ArtifactInstaller
    implements ArtifactInstaller
{
    @Requirement
    private RepositorySystem repositorySystem;

    public void install( ProjectBuildingRequest buildingRequest,
                         Collection<org.apache.maven.artifact.Artifact> mavenArtifacts )
        throws ArtifactInstallerException
    {
        // prepare installRequest
        InstallRequest request = new InstallRequest();

        // transform artifacts
        for ( org.apache.maven.artifact.Artifact mavenArtifact : mavenArtifacts )
        {
            Artifact aetherArtifact =
                new DefaultArtifact( mavenArtifact.getGroupId(), mavenArtifact.getArtifactId(),
                                     mavenArtifact.getClassifier(), mavenArtifact.getArtifactHandler().getExtension(),
                                     mavenArtifact.getVersion(), null, mavenArtifact.getFile() );

            request.addArtifact( aetherArtifact );

            if ( mavenArtifact.getMetadataList() != null )
            {
                for ( org.apache.maven.artifact.metadata.ArtifactMetadata metadata : mavenArtifact.getMetadataList() )
                {
                    Metadata aetherMetadata =
                        new DefaultMetadata( metadata.getGroupId(), metadata.getArtifactId(), "maven-metadata.xml",
                                             Nature.RELEASE_OR_SNAPSHOT );

                    request.addMetadata( aetherMetadata );
                }
            }
        }

        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        // install
        try
        {
            repositorySystem.install( session, request );
        }
        catch ( InstallationException e )
        {
            throw new ArtifactInstallerException( e.getMessage(), e );
        }
    }
    
    public ProjectBuildingRequest setLocalRepositoryBasedir( ProjectBuildingRequest buildingRequest, File basedir )
        throws ArtifactInstallerException
    {
        ProjectBuildingRequest newRequest = new DefaultProjectBuildingRequest( buildingRequest );
        
        RepositorySystemSession session =
                        (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        // "clone" session and replace localRepository
        DefaultRepositorySystemSession newSession = new DefaultRepositorySystemSession( session );
        
        // keep same repositoryType 
        String repositoryType = session.getLocalRepository().getContentType();
        LocalRepositoryManager localRepositoryManager =
            repositorySystem.newLocalRepositoryManager( new LocalRepository( basedir, repositoryType ) );
        newSession.setLocalRepositoryManager( localRepositoryManager );
        
        Invoker.invoke( newRequest, "setRepositorySession", RepositorySystemSession.class, newSession );
        
        return newRequest;
    }
}
