package org.apache.maven.shared.artifact.collect.internal;

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
import java.util.List;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.collect.CollectorResult;
import org.apache.maven.shared.artifact.collect.DependencyCollector;
import org.apache.maven.shared.artifact.collect.DependencyCollectorException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * Maven 3.0 implementation of the {@link DependencyCollector}
 * 
 * @author Robert Scholte
 *
 */
@Component( role = DependencyCollector.class, hint = "maven3" )
public class Maven30DependencyCollector
    implements DependencyCollector
{
    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement
    private ArtifactHandlerManager artifactHandlerManager;

    @Override
    public CollectorResult collectDependencies( final ProjectBuildingRequest buildingRequest, Artifact root )
        throws DependencyCollectorException
    {
        Class<?>[] argClasses = new Class<?>[] { Artifact.class, Collection.class };
        Object[] args = new Object[] { root, null };
        Dependency aetherRoot = (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

        return collectDependencies( buildingRequest, aetherRoot );
    }

    @Override
    public CollectorResult collectDependencies( final ProjectBuildingRequest buildingRequest,
                                                org.apache.maven.model.Dependency root )
        throws DependencyCollectorException
    {
        ArtifactTypeRegistry typeRegistry =
            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                   ArtifactHandlerManager.class, artifactHandlerManager );

        Class<?>[] argClasses = new Class<?>[] { Dependency.class, ArtifactTypeRegistry.class };
        Object[] args = new Object[] { root, typeRegistry };
        Dependency aetherRoot = (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

        return collectDependencies( buildingRequest, aetherRoot );
    }

    private CollectorResult collectDependencies( final ProjectBuildingRequest buildingRequest, Dependency aetherRoot )
        throws DependencyCollectorException
    {
        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        CollectRequest request = new CollectRequest();
        request.setRoot( aetherRoot );

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );
        request.setRepositories( aetherRepositories );

        try
        {
            return new Maven30CollectorResult( repositorySystem.collectDependencies( session, request ) );
        }
        catch ( DependencyCollectionException e )
        {
            throw new DependencyCollectorException( e.getMessage(), e );
        }
    }

}
