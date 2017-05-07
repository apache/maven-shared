package org.apache.maven.shared.artifact.install;

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
import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.ProjectBuildingRequest;

/**
 * @author Robert Scholte
 */
public interface ArtifactInstaller
{

    /**
     * @param request {@link ProjectBuildingRequest}
     * @param mavenArtifacts {@link Artifact}
     * @throws ArtifactInstallerException in case of an error.
     */
    void install( ProjectBuildingRequest request, Collection<Artifact> mavenArtifacts )
        throws ArtifactInstallerException;

    /**
     * @param request {@link ProjectBuildingRequest}.
     * @param localRepository The location for the local repository.
     * @param mavenArtifacts Collection of {@link Artifact MavenArtifacts}
     * @throws ArtifactInstallerException In case of an error which can be the a given artifact can not be found or the
     *             installation has failed.
     */
    void install( ProjectBuildingRequest request, File localRepository, Collection<Artifact> mavenArtifacts )
        throws ArtifactInstallerException;

}
