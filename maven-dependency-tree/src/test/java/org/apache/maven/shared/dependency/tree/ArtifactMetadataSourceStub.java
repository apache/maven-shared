package org.apache.maven.shared.dependency.tree;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 * Provides a stub to simulate an artifact metadata source.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class ArtifactMetadataSourceStub
    implements ArtifactMetadataSource
{
    // TODO: move to maven-plugin-testing-harness?

    // fields -----------------------------------------------------------------

    /**
     * Map of resolution groups by artifact.
     */
    private final Map<Artifact, ResolutionGroup> resolutionGroupsByArtifact;

    /**
     * Map of available versions by artifact.
     */
    private final Map<Artifact, List<ArtifactVersion>> availableVersionsByArtifact;

    // constructors -----------------------------------------------------------

    /**
     * Creates a new artifact metadata source stub.
     */
    public ArtifactMetadataSourceStub()
    {
        resolutionGroupsByArtifact = new HashMap<Artifact, ResolutionGroup>();
        availableVersionsByArtifact = new HashMap<Artifact, List<ArtifactVersion>>();
    }

    // ArtifactMetadataSource methods -----------------------------------------

    /**
     * {@inheritDoc}
     */
    public ResolutionGroup retrieve( Artifact artifact, ArtifactRepository localRepository, List remoteRepositories )
        throws ArtifactMetadataRetrievalException
    {
        ResolutionGroup resolution = resolutionGroupsByArtifact.get( artifact );

        // if we return null then the artifact gets excluded in DefaultArtifactCollector
        if ( resolution == null )
        {
            resolution = new ResolutionGroup( artifact, Collections.EMPTY_SET, Collections.EMPTY_LIST );
        }

        return resolution;
    }

    /**
     * {@inheritDoc}
     */
    public List retrieveAvailableVersions( Artifact artifact, ArtifactRepository localRepository,
                                           List remoteRepositories )
        throws ArtifactMetadataRetrievalException
    {
        List<ArtifactVersion> availableVersions = availableVersionsByArtifact.get( artifact );

        return availableVersions != null ? availableVersions : Collections.EMPTY_LIST;
    }

    // public methods ---------------------------------------------------------

    /**
     * Adds the specified dependency artifacts for the specified artifact to this artifact metadata source stub.
     * 
     * @param artifact the artifact to add metadata to
     * @param dependencyArtifacts the set of artifacts to register as dependencies of the specified artifact
     */
    public void addArtifactMetadata( Artifact artifact, Set<Artifact> dependencyArtifacts )
    {
        ResolutionGroup resolution = new ResolutionGroup( artifact, dependencyArtifacts, Collections.EMPTY_LIST );

        resolutionGroupsByArtifact.put( artifact, resolution );
    }

    /**
     * Adds versions for the specified artifact to this artifact metadata source stub.
     * 
     * @param artifact the artifact to add metadata to
     * @param versions the list of versions to register as available for the specified artifact
     */
    public void addAvailableVersions( Artifact artifact, List<ArtifactVersion> versions )
    {
        availableVersionsByArtifact.put( artifact, versions );
    }

    public Artifact retrieveRelocatedArtifact( Artifact artifact, ArtifactRepository localRepository,
                                               List remoteRepositories )
        throws ArtifactMetadataRetrievalException
    {
        return artifact;
    }
}
