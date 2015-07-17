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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.apache.maven.shared.artifact.filter.resolve.transform.EclipseAetherFilterTransformer;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;

/**
 * 
 */
@Component( role = ArtifactResolver.class, hint = "maven31" )
public class Maven31ArtifactResolver
    implements ArtifactResolver
{
    @Requirement
    private RepositorySystem repositorySystem;
    
    @Requirement
    private ArtifactHandlerManager artifactHandlerManager;

    @Override
    public org.apache.maven.shared.artifact.resolve.ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest ,
                                                               org.apache.maven.artifact.Artifact mavenArtifact  )
        throws ArtifactResolverException
    {
        Artifact aetherArtifact =
            (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact", org.apache.maven.artifact.Artifact.class,
                                       mavenArtifact );

        return resolveArtifact( buildingRequest, aetherArtifact );
    }
    
    @Override
    public org.apache.maven.shared.artifact.resolve.ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest,
                                                                                    ArtifactCoordinate coordinate )
        throws ArtifactResolverException
    {
        ArtifactTypeRegistry typeRegistry =
                        (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                               ArtifactHandlerManager.class, artifactHandlerManager );

        Dependency aetherDependency = toDependency( coordinate, typeRegistry );
        
        return resolveArtifact( buildingRequest, aetherDependency.getArtifact() );
    }
    
    private org.apache.maven.shared.artifact.resolve.ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest,
                                                                                     Artifact aetherArtifact )
        throws ArtifactResolverException
    {
        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );

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

            return new Maven31ArtifactResult( repositorySystem.resolveArtifact( session, request ) );
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

    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest ,
                                                                                                  ArtifactCoordinate coordinate ,
                                     TransformableFilter dependencyFilter  )
        throws ArtifactResolverException
    {
        ArtifactTypeRegistry typeRegistry =
                        (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                               ArtifactHandlerManager.class, artifactHandlerManager );

        Dependency aetherRoot = toDependency( coordinate, typeRegistry );
        
        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );

        CollectRequest request = new CollectRequest( aetherRoot, aetherRepositories );

        return resolveDependencies( buildingRequest, aetherRepositories, dependencyFilter, request );
    }

    
    
    @Override
    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                  Collection<ArtifactCoordinate> coordinates,
                                                                                                  TransformableFilter filter )
        throws ArtifactResolverException
    {
        ArtifactTypeRegistry typeRegistry =
            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                   ArtifactHandlerManager.class, artifactHandlerManager );

        List<Dependency> dependencies = new ArrayList<Dependency>( coordinates.size() );

        for ( ArtifactCoordinate coordinate : coordinates )
        {
            dependencies.add( toDependency( coordinate, typeRegistry ) );
        }

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );

        CollectRequest request = new CollectRequest( (Dependency) null, dependencies, aetherRepositories );

        return resolveDependencies( buildingRequest, aetherRepositories, filter, request );
    }
    
    private Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                   List<RemoteRepository> aetherRepositories,
                                                                                                   TransformableFilter dependencyFilter,
                                                                                                   CollectRequest request )
        throws ArtifactResolverException
    {
        try
        {
            DependencyFilter depFilter  = null;
            if ( dependencyFilter != null )
            {
                depFilter = dependencyFilter.transform( new EclipseAetherFilterTransformer() );
            }

            DependencyRequest depRequest = new DependencyRequest( request, depFilter );
            
            RepositorySystemSession session =
                            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

            DependencyResult dependencyResults = repositorySystem.resolveDependencies( session, depRequest );

            Collection<ArtifactRequest> artifactRequests = new ArrayList<ArtifactRequest>( dependencyResults.getArtifactResults().size() );

            for ( ArtifactResult artifactResult : dependencyResults.getArtifactResults() )
            {
                artifactRequests.add( new ArtifactRequest( artifactResult.getArtifact(), aetherRepositories, null ) );
            }

            final List<ArtifactResult> artifactResults = repositorySystem.resolveArtifacts( session, artifactRequests );

            // Keep it lazy! Often artifactsResults aren't used, so transforming up front is too expensive
            return new Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult>()
            {
                @Override
                public Iterator<org.apache.maven.shared.artifact.resolve.ArtifactResult> iterator()
                {
                    Collection<org.apache.maven.shared.artifact.resolve.ArtifactResult> artResults =
                        new ArrayList<org.apache.maven.shared.artifact.resolve.ArtifactResult>( artifactResults.size() );

                    for ( ArtifactResult artifactResult : artifactResults )
                    {
                        artResults.add( new Maven31ArtifactResult( artifactResult ) );
                    }

                    return artResults.iterator();
                }
            };
        }
        catch ( ArtifactResolutionException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
        catch ( DependencyResolutionException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
    }
    
    /**
     * Based on RepositoryUtils#toDependency(org.apache.maven.model.Dependency, ArtifactTypeRegistry) 
     * 
     * @param coordinate
     * @param stereotypes
     * @return as Aether Dependency
     * 
     */
    private static Dependency toDependency( ArtifactCoordinate coordinate, ArtifactTypeRegistry stereotypes )
    {
        ArtifactType stereotype = stereotypes.get( coordinate.getType() );
        if ( stereotype == null )
        {
            stereotype = new DefaultArtifactType( coordinate.getType() );
        }

        Artifact artifact =
            new DefaultArtifact( coordinate.getGroupId(), coordinate.getArtifactId(), coordinate.getClassifier(), null,
                                 coordinate.getVersion(), null, stereotype );

        return new Dependency( artifact, null );
    }
}
