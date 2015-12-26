package org.apache.maven.shared.dependency.resolve.internal;

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
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.apache.maven.shared.artifact.filter.resolve.transform.SonatypeAetherFilterTransformer;
import org.apache.maven.shared.dependency.DependencyCoordinate;
import org.apache.maven.shared.dependency.resolve.DependencyResolver;
import org.apache.maven.shared.dependency.resolve.DependencyResolverException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;

/**
 * 
 */
@Component( role = DependencyResolver.class, hint = "maven3" )
public class Maven30DependencyResolver
    implements DependencyResolver
{
    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement
    private ArtifactHandlerManager artifactHandlerManager;

    @Override
    // CHECKSTYLE_OFF: LineLength
    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                  DependencyCoordinate coordinate,
                                                                                                  TransformableFilter dependencyFilter )
                                                                                                      // CHECKSTYLE_ON:
                                                                                                      // LineLength
                                                                                                      throws DependencyResolverException
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
    // CHECKSTYLE_OFF: LineLength
    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                  Collection<org.apache.maven.model.Dependency> mavenDependencies,
                                                                                                  Collection<org.apache.maven.model.Dependency> managedMavenDependencies,
                                                                                                  TransformableFilter filter )
                                                                                                      // CHECKSTYLE_ON:
                                                                                                      // LineLength
                                                                                                      throws DependencyResolverException
    {
        ArtifactTypeRegistry typeRegistry =
            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                   ArtifactHandlerManager.class, artifactHandlerManager );

        List<Dependency> aetherDependencies = new ArrayList<Dependency>( mavenDependencies.size() );

        final Class<?>[] argClasses =
            new Class<?>[] { org.apache.maven.model.Dependency.class, ArtifactTypeRegistry.class };

        for ( org.apache.maven.model.Dependency mavenDependency : mavenDependencies )
        {
            Object[] args = new Object[] { mavenDependency, typeRegistry };

            Dependency aetherDependency =
                (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

            aetherDependencies.add( aetherDependency );
        }

        List<Dependency> aetherManagedDependencies = new ArrayList<Dependency>( managedMavenDependencies.size() );

        for ( org.apache.maven.model.Dependency mavenDependency : managedMavenDependencies )
        {
            Object[] args = new Object[] { mavenDependency, typeRegistry };

            Dependency aetherDependency =
                (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

            aetherManagedDependencies.add( aetherDependency );
        }

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );

        CollectRequest request =
            new CollectRequest( aetherDependencies, aetherManagedDependencies, aetherRepositories );

        return resolveDependencies( buildingRequest, aetherRepositories, filter, request );
    }

    // CHECKSTYLE_OFF: LineLength
    private Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                   List<RemoteRepository> aetherRepositories,
                                                                                                   TransformableFilter dependencyFilter,
                                                                                                   CollectRequest request )
                                                                                                       throws DependencyResolverException
    // CHECKSTYLE_ON :LineLength
    {
        try
        {
            DependencyFilter depFilter = null;
            if ( dependencyFilter != null )
            {
                depFilter = dependencyFilter.transform( new SonatypeAetherFilterTransformer() );
            }

            RepositorySystemSession session =
                (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

            List<ArtifactResult> dependencyResults =
                repositorySystem.resolveDependencies( session, request, depFilter );

            Collection<ArtifactRequest> artifactRequests = new ArrayList<ArtifactRequest>( dependencyResults.size() );

            for ( ArtifactResult artifactResult : dependencyResults )
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
                        artResults.add( new Maven30ArtifactResult( artifactResult ) );
                    }

                    return artResults.iterator();
                }
            };
        }
        catch ( ArtifactResolutionException e )
        {
            throw new DependencyResolverException( e.getMessage(), e );
        }
        catch ( DependencyCollectionException e )
        {
            throw new DependencyResolverException( e.getMessage(), e );
        }
    }

    /**
     * Based on RepositoryUtils#toDependency(org.apache.maven.model.Dependency, ArtifactTypeRegistry)
     * 
     * @param coordinate
     * @param stereotypes
     * @return as Aether Dependency
     */
    private static Dependency toDependency( DependencyCoordinate coordinate, ArtifactTypeRegistry stereotypes )
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
