package org.apache.maven.shared.artifact;

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

/**
 * <p>
 * Contains all required elements of a Maven Artifact to resolve and calculate its path for either a local or
 * remote Maven2 repository.
 * </p>
 * 
 * @author Robert Scholte
 */
public interface ArtifactCoordinate
{
    /**
     * @return The groupId of the artifact.
     */
    String getGroupId();

    /**
     * @return The artifactId of the artifact.
     */
    String getArtifactId();

    /**
     * A version, never a versionRange
     * 
     * @return The version.
     */
    String getVersion();

    /**
     * The file-extension of the artifact.
     * 
     * @return The extension.
     */
    String getExtension();

    /**
     * @return The classifier of the artifact.
     */
    String getClassifier();
}