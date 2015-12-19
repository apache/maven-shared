package org.apache.maven.shared.artifact.deploy;

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
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.ProjectBuildingRequest;

/**
 * 
 */
public interface ArtifactDeployer
{

    /**
     * @param request {@link ProjectBuildingRequest}
     * @param mavenArtifacts {@link Artifact}
     * @throws ArtifactDeployerException in case of an error.
     */
    void deploy( ProjectBuildingRequest request, Collection<Artifact> mavenArtifacts )
        throws ArtifactDeployerException;

    /**
     * @param request the building request
     * @param remoteRepository the repository to deploy to. If {@code null} the {@code mavenArtifact.getRepository()} is
     *            used.
     * @param mavenArtifacts the artifacts to deploy
     * @throws ArtifactDeployerException in case of an error.
     */
    void deploy( ProjectBuildingRequest request, ArtifactRepository remoteRepository,
                 Collection<Artifact> mavenArtifacts )
                     throws ArtifactDeployerException;

}
