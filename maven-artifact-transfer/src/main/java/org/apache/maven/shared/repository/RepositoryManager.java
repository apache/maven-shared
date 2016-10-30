package org.apache.maven.shared.repository;

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

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.ArtifactCoordinate;

/**
 * 
 */
public interface RepositoryManager
{

    /**
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param artifact {@link Artifact}
     * @return the path of the local artifact.
     */
    String getPathForLocalArtifact( ProjectBuildingRequest buildingRequest, Artifact artifact );

    /**
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param coordinate {@link ArtifactCoordinate}
     * @return the path for the local artifact.
     */
    String getPathForLocalArtifact( ProjectBuildingRequest buildingRequest, ArtifactCoordinate coordinate );
    
    /**
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param metadata {@link ArtifactMetadata}
     * @return the path of the local metadata.
     */
    String getPathForLocalMetadata( ProjectBuildingRequest buildingRequest, ArtifactMetadata metadata );

    /**
     * Create a new {@code ProjectBuildingRequest} with an adjusted repository session.
     * 
     * @param request the project building request
     * @param basedir the base directory of the local repository
     * @return a new project building request
     */
    ProjectBuildingRequest setLocalRepositoryBasedir( ProjectBuildingRequest request, File basedir );

    /**
     * Get the localRepositryBasedir as specified in the repository session of the request
     * 
     * @param request the build request
     * @return the local repository base directory
     */
    File getLocalRepositoryBasedir( ProjectBuildingRequest request );
}
