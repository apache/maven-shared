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

import java.util.List;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;

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
    // CHECKSTYLE_OFF: LineLength
    public org.apache.maven.shared.artifact.resolve.ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest,
                                                                                    org.apache.maven.artifact.Artifact mavenArtifact )
                                                                                        throws ArtifactResolverException
    // CHECKSTYLE_ON: LineLength
    {
        Artifact aetherArtifact = (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact",
                                                             org.apache.maven.artifact.Artifact.class, mavenArtifact );

        return resolveArtifact( buildingRequest, aetherArtifact );
    }

    @Override
    // CHECKSTYLE_OFF: LineLength
    public org.apache.maven.shared.artifact.resolve.ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest,
                                                                                    ArtifactCoordinate coordinate )
                                                                                        throws ArtifactResolverException
    // CHECKSTYLE_ON: LineLength
    {
        Artifact aetherArtifact =
            new DefaultArtifact( coordinate.getGroupId(), coordinate.getArtifactId(), coordinate.getClassifier(),
                                 coordinate.getExtension(), coordinate.getVersion() );

        return resolveArtifact( buildingRequest, aetherArtifact );
    }

    // CHECKSTYLE_OFF: LineLength
    private org.apache.maven.shared.artifact.resolve.ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest,
                                                                                     Artifact aetherArtifact )
                                                                                         throws ArtifactResolverException
    // CHECKSTYLE_ON: LineLength
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

//    @Override
//    // CHECKSTYLE_OFF: LineLength
//    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
//                                                                                                  ArtifactCoordinate coordinate,
//                                                                                                  TransformableFilter dependencyFilter )
//                                                                                                      throws ArtifactResolverException
//    // CHECKSTYLE_ON: LineLength
//    {
//        ArtifactTypeRegistry typeRegistry =
//            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
//                                                   ArtifactHandlerManager.class, artifactHandlerManager );
//
//        Dependency aetherRoot = toDependency( coordinate, typeRegistry );
//
//        @SuppressWarnings( "unchecked" )
//        List<RemoteRepository> aetherRepositories =
//            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
//                                                     buildingRequest.getRemoteRepositories() );
//
//        CollectRequest request = new CollectRequest( aetherRoot, aetherRepositories );
//
//        return resolveDependencies( buildingRequest, aetherRepositories, dependencyFilter, request );
//    }
//
//    @Override
//    // CHECKSTYLE_OFF: LineLength
//    public Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
//                                                                                                  Collection<org.apache.maven.model.Dependency> mavenDependencies,
//                                                                                                  Collection<org.apache.maven.model.Dependency> managedMavenDependencies,
//                                                                                                  TransformableFilter filter )
//                                                                                                      throws ArtifactResolverException
//    // CHECKSTYLE_ON: LineLength
//    {
//        ArtifactTypeRegistry typeRegistry =
//            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
//                                                   ArtifactHandlerManager.class, artifactHandlerManager );
//
//        List<Dependency> aetherDeps = new ArrayList<Dependency>( mavenDependencies.size() );
//
//        final Class<?>[] argClasses =
//            new Class<?>[] { org.apache.maven.model.Dependency.class, ArtifactTypeRegistry.class };
//
//        for ( org.apache.maven.model.Dependency mavenDependency : mavenDependencies )
//        {
//            Object[] args = new Object[] { mavenDependency, typeRegistry };
//
//            Dependency aetherDependency =
//                (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );
//
//            aetherDeps.add( aetherDependency );
//        }
//
//        List<Dependency> aetherManagedDeps = new ArrayList<Dependency>( managedMavenDependencies.size() );
//
//        for ( org.apache.maven.model.Dependency mavenDependency : managedMavenDependencies )
//        {
//            Object[] args = new Object[] { mavenDependency, typeRegistry };
//
//            Dependency aetherDependency =
//                (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );
//
//            aetherManagedDeps.add( aetherDependency );
//        }
//
//        @SuppressWarnings( "unchecked" )
//        List<RemoteRepository> aetherRepos =
//            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
//                                                     buildingRequest.getRemoteRepositories() );
//
//        CollectRequest request = new CollectRequest( aetherDeps, aetherManagedDeps, aetherRepos );
//
//        return resolveDependencies( buildingRequest, aetherRepos, filter, request );
//    }

//    // CHECKSTYLE_OFF: LineLength
//    private Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
//                                                                                                   List<RemoteRepository> aetherRepositories,
//                                                                                                   TransformableFilter dependencyFilter,
//                                                                                                   CollectRequest request )
//                                                                                                       throws ArtifactResolverException
//    // CHECKSTYLE_ON: LineLength
//    {
//        try
//        {
//            DependencyFilter depFilter = null;
//            if ( dependencyFilter != null )
//            {
//                depFilter = dependencyFilter.transform( new EclipseAetherFilterTransformer() );
//            }
//
//            DependencyRequest depRequest = new DependencyRequest( request, depFilter );
//
//            RepositorySystemSession session =
//                (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );
//
//            DependencyResult dependencyResults = repositorySystem.resolveDependencies( session, depRequest );
//
//            Collection<ArtifactRequest> artifactRequests =
//                new ArrayList<ArtifactRequest>( dependencyResults.getArtifactResults().size() );
//
//            for ( ArtifactResult artifactResult : dependencyResults.getArtifactResults() )
//            {
//                artifactRequests.add( new ArtifactRequest( artifactResult.getArtifact(), aetherRepositories, null ) );
//            }
//
//         final List<ArtifactResult> artifactResults = repositorySystem.resolveArtifacts( session, artifactRequests );
//
//            // Keep it lazy! Often artifactsResults aren't used, so transforming up front is too expensive
//            return new Iterable<org.apache.maven.shared.artifact.resolve.ArtifactResult>()
//            {
//                @Override
//                public Iterator<org.apache.maven.shared.artifact.resolve.ArtifactResult> iterator()
//                {
//                    // CHECKSTYLE_OFF: LineLength
//                    Collection<org.apache.maven.shared.artifact.resolve.ArtifactResult> artResults =
//                    new ArrayList<org.apache.maven.shared.artifact.resolve.ArtifactResult>( artifactResults.size() );
//                    // CHECKSTYLE_ON: LineLength
//
//                    for ( ArtifactResult artifactResult : artifactResults )
//                    {
//                        artResults.add( new Maven31ArtifactResult( artifactResult ) );
//                    }
//
//                    return artResults.iterator();
//                }
//            };
//        }
//        catch ( ArtifactResolutionException e )
//        {
//            throw new ArtifactResolverException( e.getMessage(), e );
//        }
//        catch ( DependencyResolutionException e )
//        {
//            throw new ArtifactResolverException( e.getMessage(), e );
//        }
//    }

//    /**
//     * Based on RepositoryUtils#toDependency(org.apache.maven.model.Dependency, ArtifactTypeRegistry)
//     * 
//     * @param coordinate {@link ArtifactCoordinate}
//     * @param stereotypes {@link ArtifactTypeRegistry
//     * @return as Aether Dependency
//     */
//    private static Dependency toDependency( ArtifactCoordinate coordinate, ArtifactTypeRegistry stereotypes )
//    {
//        ArtifactType stereotype = stereotypes.get( coordinate.getExtension() );
//        if ( stereotype == null )
//        {
//            stereotype = new DefaultArtifactType( coordinate.getExtension() );
//        }
//
//        Artifact artifact =
//          new DefaultArtifact( coordinate.getGroupId(), coordinate.getArtifactId(), coordinate.getClassifier(), null,
//                                 coordinate.getVersion(), null, stereotype );
//
//        return new Dependency( artifact, null );
//    }
}
