package org.apache.maven.shared.dependencies.collect;

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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependencies.DependableCoordinate;

/**
 * Will only download the pom files when not available, never the artifact. 
 * 
 * @author Robert Scholte
 *
 */
public interface DependencyCollector
{

    /**
     * A dependency may have excludes 
     * 
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param root {@link Dependency}
     * @return {@link CollectorResult}
     * @throws DependencyCollectorException in case of an error.
     */
    CollectorResult collectDependencies( ProjectBuildingRequest buildingRequest, Dependency root )
        throws DependencyCollectorException;

    /**
     * @param buildingRequest {@link ProjectBuildingRequest}.
     * @param root {@link DependableCoordinate}
     * @return {@link CollectorResult}
     * @throws DependencyCollectorException in case of an error which can be a component lookup error or
     *  an error while trying to look up the dependencies.
     */
    CollectorResult collectDependencies( ProjectBuildingRequest buildingRequest, DependableCoordinate root )
                    throws DependencyCollectorException;

    /**
     * @param buildingRequest {@link ProjectBuildingRequest}.
     * @param root {@link Model}
     * @return {@link CollectorResult}
     * @throws DependencyCollectorException in case of an error which can be a component lookup error or
     *  an error while trying to look up the dependencies.
     */
    CollectorResult collectDependencies( ProjectBuildingRequest buildingRequest, Model root )
                    throws DependencyCollectorException;

}
