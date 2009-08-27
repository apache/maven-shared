package org.apache.maven.shared.artifact.resolver;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Collects basic settings to access the repository system.
 * 
 * @author Benjamin Bentmann
 * @author jdcasey
 */
public class DefaultRepositoryRequest
    implements RepositoryRequest
{

    private boolean offline;

    private ArtifactRepository localRepository;

    private List<ArtifactRepository> remoteRepositories;

    /**
     * Creates an empty repository request.
     */
    public DefaultRepositoryRequest()
    {
        // enables no-arg constructor
    }

    /**
     * Creates a shallow copy of the specified repository request.
     * 
     * @param repositoryRequest The repository request to copy from, must not be {@code null}.
     */
    public DefaultRepositoryRequest( RepositoryRequest repositoryRequest )
    {
        setLocalRepository( repositoryRequest.getLocalRepository() );
        setRemoteRepositories( repositoryRequest.getRemoteRepositories() );
        setOffline( repositoryRequest.isOffline() );
    }
    
    /**
     * Create a new request using the remote repositories associated with the project, along with the
     * specified local repository. This constructor also initializes an instance of 
     * {@link DefaultRepositoryCache} for use during resolution.
     * 
     * @param project The {@link MavenProject} instance, from which to retrieve the remote repositories
     *                to be used.
     * @param localRepository The local {@link ArtifactRepository} instance to be used.
     */
    @SuppressWarnings( "unchecked" )
    public DefaultRepositoryRequest( MavenProject project, ArtifactRepository localRepository )
    {
        this( Collections.singleton( project ), localRepository );
    }
    
    /**
     * Create a new request using the remote repositories associated with the projects, along with the
     * specified local repository. This constructor also initializes an instance of 
     * {@link DefaultRepositoryCache} for use during resolution.
     * 
     * @param projects The {@link MavenProject} instances, from which to retrieve the remote repositories
     *                to be used.
     * @param localRepository The local {@link ArtifactRepository} instance to be used.
     */
    @SuppressWarnings( "unchecked" )
    public DefaultRepositoryRequest( Collection<MavenProject> projects, ArtifactRepository localRepository )
    {
        Set<ArtifactRepository> remoteRepositories = new LinkedHashSet<ArtifactRepository>();
        if ( projects != null && !projects.isEmpty() )
        {
            for ( MavenProject project : projects )
            {
                remoteRepositories.addAll( (List<ArtifactRepository>) project.getRemoteArtifactRepositories() );
            }
        }
        
        setRemoteRepositories( new ArrayList<ArtifactRepository>( remoteRepositories ) );
        setLocalRepository( localRepository );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOffline()
    {
        return offline;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultRepositoryRequest setOffline( boolean offline )
    {
        this.offline = offline;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultRepositoryRequest setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public List<ArtifactRepository> getRemoteRepositories()
    {
        if ( remoteRepositories == null )
        {
            remoteRepositories = new ArrayList<ArtifactRepository>();
        }

        return remoteRepositories;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultRepositoryRequest setRemoteRepositories( List<ArtifactRepository> remoteRepositories )
    {
        this.remoteRepositories = remoteRepositories;

        return this;
    }

}
