package org.apache.maven.shared.artifact.resolve;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;

/**
 * 
 */
public interface ArtifactResolver
{

    ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest , Artifact mavenArtifact )
        throws ArtifactResolverException;

    ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest , ArtifactCoordinate coordinate )
                    throws ArtifactResolverException;

    /**
     * This will resolve the dependencies of the coordinate, not resolving the the artifact of the coordinate itself.
     * If the coordinate needs to be resolved too, use 
     * {@link #resolveDependencies(ProjectBuildingRequest, Collection, Collection, TransformableFilter)} passing 
     * {@code Collections.singletonList(coordinate)}
     * 
     * @param buildingRequest
     * @param coordinate
     * @param filter
     * @return
     * @throws ArtifactResolverException
     */
    Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                  ArtifactCoordinate coordinate, TransformableFilter filter )
        throws ArtifactResolverException;

    /**
     * 
     * @param buildingRequest the project building request, never {@code null}
     * @param dependencies the dependencies to resolve, never {@code null}
     * @param managedDependencies managed dependencies, can be {@code null}
     * @param filter a filter, can be {@code null}
     * @return
     * @throws ArtifactResolverException
     */
    Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                  Collection<Dependency> dependencies,
                                                  Collection<Dependency> managedDependencies,
                                                  TransformableFilter filter )
                    throws ArtifactResolverException;
}
