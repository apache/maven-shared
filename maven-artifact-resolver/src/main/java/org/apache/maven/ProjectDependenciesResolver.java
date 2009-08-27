package org.apache.maven;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collection;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

/**
 * Common interface for plugins and other third-party components running inside a Maven runtime to
 * resolve the transitive dependency closure for various {@link MavenProject} instances.
 * 
 * @author jdcasey
 */
// NOTE: The package specified for this interface is REQUIRED, since it must match Maven 3.x!!
public interface ProjectDependenciesResolver
{
    /**
     * Resolve the dependencies for a collection of {@link MavenProject} instances, using a common
     * set of remote repositories and a common set of scopes.
     * 
     * @param projects The projects whose dependencies should be resolved.
     * @param scopes The list of scopes to resolve. These scopes may imply other scopes.
     * @param repositoryRequest The request containing a {@link RepositoryCache}, along with 
     *          zero or more {@link ArtifactRepository} remote repositories, a local {@link ArtifactRepository},
     *          and a flag determining whether to run in offline mode.
     * @return The set of resolved artifacts. If the projects contain no dependencies, this will return an empty set.
     * @throws ArtifactResolutionException In case {@link Artifact} instances cannot be created from 
     *          project {@link Dependency} instances, or artifact resolution fails.
     * @throws ArtifactNotFoundException In cases where one or more dependency artifacts cannot be found in the
     *          various repositories.
     */
    public Set<Artifact> resolve( Collection<MavenProject> projects, Collection<String> scopes, RepositoryRequest repositoryRequest )
        throws ArtifactResolutionException, ArtifactNotFoundException;

    /**
     * Resolve the dependencies for a single {@link MavenProject} instance, using the supplied
     * set of remote repositories and scopes.
     * 
     * @param project The project whose dependencies should be resolved.
     * @param scopes The list of scopes to resolve. These scopes may imply other scopes.
     * @param repositoryRequest The request containing a {@link RepositoryCache}, along with 
     *          zero or more {@link ArtifactRepository} remote repositories, a local {@link ArtifactRepository},
     *          and a flag determining whether to run in offline mode.
     * @return The set of resolved artifacts. If the project contains no dependencies, this will return an empty set.
     * @throws ArtifactResolutionException In case {@link Artifact} instances cannot be created from the
     *          project {@link Dependency} instance, or artifact resolution fails.
     * @throws ArtifactNotFoundException In cases where one or more dependency artifacts cannot be found in the
     *          various repositories.
     */
    public Set<Artifact> resolve( MavenProject project, Collection<String> scopes, RepositoryRequest repositoryRequest )
        throws ArtifactResolutionException, ArtifactNotFoundException;
}
