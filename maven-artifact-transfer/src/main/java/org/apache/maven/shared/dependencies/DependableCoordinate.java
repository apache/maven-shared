package org.apache.maven.shared.dependencies;

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
 * Represents any instance which may contain Maven Dependencies, both explicit or implicit to (transitively) resolve 
 * and calculate its path for either a local or remote Maven2 repository.
 * </p>
 * <p>
 * The version can be a version range. Based on the groupId and artifactId it will be resolved to the actual version.
 * </p>
 * <p>
 * The type will be translated to an extension based on the artifact descriptor ({@code pom.xml} matching the groupId,
 * artifactId and version.
 * </p>
 * A MavenProject is not considered a DependableCoordinate because is should never have a versionRange, and it has 
 * packaging instead of type.
 * 
 * @author Robert Scholte
 */
public interface DependableCoordinate
{
    /**
     * @return the groupId of the coordinate
     */
    String getGroupId();

    /**
     * 
     * @return the artifact of the coordinate
     */
    String getArtifactId();

    /**
     * A version or versionRange
     * 
     * @return the version
     */
    String getVersion();

    /**
     * 
     * @return the type of the coordinate
     */
    String getType();

    /**
     * 
     * @return the classifier or {@code null}
     */
    String getClassifier();
}