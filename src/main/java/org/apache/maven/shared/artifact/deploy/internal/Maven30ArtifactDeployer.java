package org.apache.maven.shared.artifact.deploy.internal;

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

import java.util.Collection;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployer;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployerException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.SubArtifact;

/**
 * 
 */
@Component( role = ArtifactDeployer.class, hint = "maven3" )
public class Maven30ArtifactDeployer
    implements ArtifactDeployer
{

    @Requirement
    private RepositorySystem repositorySystem;

    @Override
    public void deploy( ProjectBuildingRequest buildingRequest,
                        Collection<org.apache.maven.artifact.Artifact> mavenArtifacts )
                            throws ArtifactDeployerException
    {
        deploy( buildingRequest, null, mavenArtifacts );
    }

    @Override
    public void deploy( ProjectBuildingRequest buildingRequest, ArtifactRepository remoteRepository,
                        Collection<org.apache.maven.artifact.Artifact> mavenArtifacts )
                            throws ArtifactDeployerException
    {
        // prepare request
        DeployRequest request = new DeployRequest();

        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        RemoteRepository defaultRepository = null;

        if ( remoteRepository != null )
        {
            defaultRepository = getRemoteRepository( session, remoteRepository );
        }

        // transform artifacts
        for ( org.apache.maven.artifact.Artifact mavenArtifact : mavenArtifacts )
        {
            Artifact aetherArtifact =
                (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact",
                                           org.apache.maven.artifact.Artifact.class, mavenArtifact );
            request.addArtifact( aetherArtifact );

            RemoteRepository aetherRepository;
            if ( remoteRepository == null )
            {
                aetherRepository = getRemoteRepository( session, mavenArtifact.getRepository() );
            }
            else
            {
                aetherRepository = defaultRepository;
            }

            request.setRepository( aetherRepository );

            for ( ArtifactMetadata metadata : mavenArtifact.getMetadataList() )
            {
                if ( metadata instanceof ProjectArtifactMetadata )
                {
                    Artifact pomArtifact = new SubArtifact( aetherArtifact, "", "pom" );
                    pomArtifact = pomArtifact.setFile( ( (ProjectArtifactMetadata) metadata ).getFile() );
                    request.addArtifact( pomArtifact );
                }
                else if ( // metadata instanceof SnapshotArtifactRepositoryMetadata ||
                metadata instanceof ArtifactRepositoryMetadata )
                {
                    // eaten, handled by repo system
                }
                else
                {
                    // request.addMetadata( new MetadataBridge( metadata ) );
                }
            }
        }

        // deploy
        try
        {
            repositorySystem.deploy( session, request );
        }
        catch ( DeploymentException e )
        {
            throw new ArtifactDeployerException( e.getMessage(), e );
        }
    }

    private RemoteRepository getRemoteRepository( RepositorySystemSession session, ArtifactRepository remoteRepository )
        throws ArtifactDeployerException
    {
        // CHECKSTYLE_OFF: LineLength
        RemoteRepository aetherRepo = (RemoteRepository) Invoker.invoke( RepositoryUtils.class, "toRepo",
                                                                         org.apache.maven.artifact.repository.ArtifactRepository.class,
                                                                         remoteRepository );
        // CHECKSTYLE_ON: LineLength

        if ( aetherRepo.getAuthentication() == null )
        {
            aetherRepo.setAuthentication( session.getAuthenticationSelector().getAuthentication( aetherRepo ) );
        }

        if ( aetherRepo.getProxy() == null )
        {
            aetherRepo.setProxy( session.getProxySelector().getProxy( aetherRepo ) );
        }

        return aetherRepo;
    }
}
