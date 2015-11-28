package org.apache.maven.shared.artifact.filter.collection;

/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

/**
 * 
 */

import java.util.Set;

import org.apache.maven.artifact.Artifact;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id$
 */
public interface ArtifactsFilter
{
    /**
     * @param artifacts {@link Artifact}
     * @return Set of artifacts.
     * @throws ArtifactFilterException in case of a failure.
     */
    Set<Artifact> filter( Set<Artifact> artifacts )
        throws ArtifactFilterException;

    /**
     * @param artifact {@link Artifact}
     * @return {@code true} if artifact is included {@code false} otherwise.
     * @throws ArtifactFilterException in case of a failure.
     */
    boolean isArtifactIncluded( Artifact artifact )
        throws ArtifactFilterException;
}
