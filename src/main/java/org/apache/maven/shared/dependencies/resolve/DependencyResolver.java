package org.apache.maven.shared.dependencies.resolve;

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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.dependencies.DependableCoordinate;

/**
 * @author Robert Scholte
 */
public interface DependencyResolver
{
    /**
     * This will resolve the dependencies of the coordinate, not resolving the the artifact of the coordinate itself. If
     * the coordinate needs to be resolved too, use
     * {@link #resolveDependencies(ProjectBuildingRequest, Collection, Collection, TransformableFilter)} passing
     * {@code Collections.singletonList(coordinate)}
     * 
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param coordinate {@link DependableCoordinate}
     * @param filter {@link TransformableFilter}
     * @return the resolved dependencies.
     * @throws DependencyResolverException in case of an error.
     */
    Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                  DependableCoordinate coordinate, TransformableFilter filter )
        throws DependencyResolverException;

    /**
     * This will resolve the dependencies of the coordinate, not resolving the the artifact of the coordinate itself. If
     * the coordinate needs to be resolved too, use
     * {@link #resolveDependencies(ProjectBuildingRequest, Collection, Collection, TransformableFilter)} passing
     * {@code Collections.singletonList(coordinate)}
     * 
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param model {@link Model}
     * @param filter {@link TransformableFilter}
     * @return the resolved dependencies.
     * @throws DependencyResolverException in case of an error.
     */
    Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest, Model model,
                                                  TransformableFilter filter )
        throws DependencyResolverException;

    /**
     * @param buildingRequest the project building request, never {@code null}
     * @param dependencies the dependencies to resolve, can be {@code null}
     * @param managedDependencies managed dependencies, can be {@code null}
     * @param filter a filter, can be {@code null}
     * @return the resolved dependencies.
     * @throws DependencyResolverException in case of an error.
     */
    Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                  Collection<Dependency> dependencies,
                                                  Collection<Dependency> managedDependencies,
                                                  TransformableFilter filter )
        throws DependencyResolverException;
}
