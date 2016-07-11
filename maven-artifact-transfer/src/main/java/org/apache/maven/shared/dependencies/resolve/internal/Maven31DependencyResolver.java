package org.apache.maven.shared.dependencies.resolve.internal;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.apache.maven.shared.artifact.filter.resolve.transform.EclipseAetherFilterTransformer;
import org.apache.maven.shared.dependencies.DependableCoordinate;
import org.apache.maven.shared.dependencies.resolve.DependencyResolver;
import org.apache.maven.shared.dependencies.resolve.DependencyResolverException;
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
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;

/**
 * 
 */
@Component( role = DependencyResolver.class, hint = "maven31" )
public class Maven31DependencyResolver
    implements DependencyResolver
{
    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement
    private ArtifactHandlerManager artifactHandlerManager;

    @Override
    // CHECKSTYLE_OFF: LineLength
    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                  DependableCoordinate coordinate,
                                                                                                  TransformableFilter dependencyFilter )
                                                                                                      throws DependencyResolverException
    // CHECKSTYLE_ON: LineLength
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
                                                                                                  Model model,
                                                                                                  TransformableFilter dependencyFilter )
    // CHECKSTYLE_ON: LineLength
        throws DependencyResolverException
    {
     // Are there examples where packaging and type are NOT in sync
        ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler( model.getPackaging() );
        
        String extension = artifactHandler != null ? artifactHandler.getExtension() : null;
        
        Artifact aetherArtifact =
            new DefaultArtifact( model.getGroupId(), model.getArtifactId(), extension, model.getVersion() );
        
        Dependency aetherRoot = new Dependency( aetherArtifact, null );
        
        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );

        CollectRequest request = new CollectRequest( aetherRoot, aetherRepositories );
        
        ArtifactTypeRegistry typeRegistry =
                        (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                               ArtifactHandlerManager.class, artifactHandlerManager );
        
        List<Dependency> aetherDependencies = new ArrayList<Dependency>( model.getDependencies().size() );
        for ( org.apache.maven.model.Dependency mavenDependency : model.getDependencies() )
        {
            aetherDependencies.add( toDependency( mavenDependency, typeRegistry ) );
        }
        request.setDependencies( aetherDependencies );

        DependencyManagement mavenDependencyManagement = model.getDependencyManagement();
        if ( mavenDependencyManagement != null )
        {
            List<Dependency> aetherManagerDependencies =
                new ArrayList<Dependency>( mavenDependencyManagement.getDependencies().size() );
            
            for ( org.apache.maven.model.Dependency mavenDependency : mavenDependencyManagement.getDependencies() )
            {
                aetherManagerDependencies.add( toDependency( mavenDependency, typeRegistry ) );
            }
            
            request.setManagedDependencies( aetherManagerDependencies );
        }

        return resolveDependencies( buildingRequest, aetherRepositories, dependencyFilter, request );
    }

    @Override
    // CHECKSTYLE_OFF: LineLength
    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                  Collection<org.apache.maven.model.Dependency> mavenDependencies,
                                                                                                  Collection<org.apache.maven.model.Dependency> managedMavenDependencies,
                                                                                                  TransformableFilter filter )
                                                                                                      throws DependencyResolverException
    // CHECKSTYLE_ON: LineLength
    {
        ArtifactTypeRegistry typeRegistry =
            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                   ArtifactHandlerManager.class, artifactHandlerManager );

        final Class<?>[] argClasses =
            new Class<?>[] { org.apache.maven.model.Dependency.class, ArtifactTypeRegistry.class };

        List<Dependency> aetherDeps = null;

        if ( mavenDependencies != null )
        {
            aetherDeps = new ArrayList<Dependency>( mavenDependencies.size() );

            for ( org.apache.maven.model.Dependency mavenDependency : mavenDependencies )
            {
                Object[] args = new Object[] { mavenDependency, typeRegistry };

                Dependency aetherDependency =
                    (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

                aetherDeps.add( aetherDependency );
            }
        }

        List<Dependency> aetherManagedDependencies = null;
        
        if ( managedMavenDependencies != null )
        {
            aetherManagedDependencies = new ArrayList<Dependency>( managedMavenDependencies.size() );

            for ( org.apache.maven.model.Dependency mavenDependency : managedMavenDependencies )
            {
                Object[] args = new Object[] { mavenDependency, typeRegistry };

                Dependency aetherDependency =
                    (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

                aetherManagedDependencies.add( aetherDependency );
            }
        }

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepos =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );

        CollectRequest request = new CollectRequest( aetherDeps, aetherManagedDependencies, aetherRepos );

        return resolveDependencies( buildingRequest, aetherRepos, filter, request );
    }

    // CHECKSTYLE_OFF: LineLength
    private Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                                                                   List<RemoteRepository> aetherRepositories,
                                                                                                   TransformableFilter dependencyFilter,
                                                                                                   CollectRequest request )
                                                                                                       throws DependencyResolverException
    // CHECKSTYLE_ON: LineLength
    {
        try
        {
            DependencyFilter depFilter = null;
            if ( dependencyFilter != null )
            {
                depFilter = dependencyFilter.transform( new EclipseAetherFilterTransformer() );
            }

            DependencyRequest depRequest = new DependencyRequest( request, depFilter );

            RepositorySystemSession session =
                (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

            final DependencyResult dependencyResults = repositorySystem.resolveDependencies( session, depRequest );

            // Keep it lazy! Often artifactsResults aren't used, so transforming up front is too expensive
            return new Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult>()
            {
                @Override
                public Iterator<org.apache.maven.shared.artifact.resolve.ArtifactResult> iterator()
                {
                    // CHECKSTYLE_OFF: LineLength
                    Collection<org.apache.maven.shared.artifact.resolve.ArtifactResult> artResults =
                        new ArrayList<org.apache.maven.shared.artifact.resolve.ArtifactResult>( dependencyResults.getArtifactResults().size() );
                    // CHECKSTYLE_ON: LineLength

                    for ( ArtifactResult artifactResult : dependencyResults.getArtifactResults() )
                    {
                        artResults.add( new Maven31ArtifactResult( artifactResult ) );
                    }

                    return artResults.iterator();
                }
            };
        }
        catch ( DependencyResolutionException e )
        {
            throw new Maven31DependencyResolverException( e );
        }
    }

    /**
     * Based on RepositoryUtils#toDependency(org.apache.maven.model.Dependency, ArtifactTypeRegistry)
     * 
     * @param coordinate {@link DependableCoordinate}
     * @param stereotypes {@link ArtifactTypeRegistry
     * @return as Aether Dependency
     */
    private static Dependency toDependency( DependableCoordinate coordinate, ArtifactTypeRegistry stereotypes )
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

    private static Dependency toDependency( org.apache.maven.model.Dependency root, ArtifactTypeRegistry typeRegistry )
                    throws DependencyResolverException
    {
        Class<?>[] argClasses = new Class<?>[] { org.apache.maven.model.Dependency.class, ArtifactTypeRegistry.class };

        Object[] args = new Object[] { root, typeRegistry };

        return (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );
    }    
}
