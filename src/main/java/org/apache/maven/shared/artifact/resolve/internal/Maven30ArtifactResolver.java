package org.apache.maven.shared.artifact.resolve.internal;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.artifact.resolve.filter.TransformableFilter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

@Component( role = ArtifactResolver.class, hint = "maven3" )
public class Maven30ArtifactResolver
    implements ArtifactResolver
{
    @Requirement
    private RepositorySystem repositorySystem;

    public org.apache.maven.artifact.Artifact resolveArtifact( ProjectBuildingRequest buildingRequest,
                                                               org.apache.maven.artifact.Artifact mavenArtifact,
                                                               List<ArtifactRepository> remoteRepositories )
        throws ArtifactResolverException
    {
        Artifact aetherArtifact =
            (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact", org.apache.maven.artifact.Artifact.class,
                                       mavenArtifact );

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class, remoteRepositories );

        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        try
        {
            // use descriptor to respect relocation
            ArtifactDescriptorRequest descriptorRequest =
                new ArtifactDescriptorRequest( aetherArtifact, aetherRepositories, null );

            ArtifactDescriptorResult descriptorResult =
                repositorySystem.readArtifactDescriptor( session, descriptorRequest );

            ArtifactRequest request = new ArtifactRequest( descriptorResult.getArtifact(), aetherRepositories, null );

            Artifact resolvedArtifact = repositorySystem.resolveArtifact( session, request ).getArtifact();

            return (org.apache.maven.artifact.Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact",
                                                                        Artifact.class, resolvedArtifact );
        }
        catch ( ArtifactDescriptorException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
    }

    public void resolveTransitively( ProjectBuildingRequest buildingRequest,
                                  org.apache.maven.artifact.Artifact mavenArtifact,
                                  List<ArtifactRepository> remoteRepositories )
        throws ArtifactResolverException
    {
        resolveTransitively( buildingRequest, mavenArtifact, remoteRepositories, null );
    }

    public void resolveTransitively( ProjectBuildingRequest buildingRequest,
                                  org.apache.maven.artifact.Artifact mavenArtifact,
                                  List<ArtifactRepository> remoteRepositories,
                                  TransformableFilter dependencyFilter )
        throws ArtifactResolverException
    {
        Artifact aetherArtifact =
            (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact", org.apache.maven.artifact.Artifact.class,
                                       mavenArtifact );

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class, remoteRepositories );

        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        try
        {
            // use descriptor to respect relocation
            ArtifactDescriptorRequest descriptorRequest =
                new ArtifactDescriptorRequest( aetherArtifact, aetherRepositories, null );

            ArtifactDescriptorResult descriptorResult =
                repositorySystem.readArtifactDescriptor( session, descriptorRequest );

            CollectRequest request =
                new CollectRequest( descriptorResult.getDependencies(), descriptorResult.getManagedDependencies(),
                                    aetherRepositories );

            DependencyFilter depFilter  = null;
            if( dependencyFilter != null )
            {
                depFilter = dependencyFilter.transform( new SonatypeAetherFilterTransformer() );
            }

            List<ArtifactResult> artifactResults = repositorySystem.resolveDependencies( session, request, depFilter );

            Collection<ArtifactRequest> artifactRequests = new ArrayList<ArtifactRequest>( 1 + artifactResults.size() );

            artifactRequests.add( new ArtifactRequest( descriptorResult.getArtifact(), aetherRepositories, null ) );

            for ( ArtifactResult artifactResult : artifactResults )
            {
                artifactRequests.add( new ArtifactRequest( artifactResult.getArtifact(), aetherRepositories, null ) );
            }

            repositorySystem.resolveArtifacts( session, artifactRequests );
        }
        catch ( ArtifactDescriptorException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
        catch ( DependencyCollectionException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
    }
}
