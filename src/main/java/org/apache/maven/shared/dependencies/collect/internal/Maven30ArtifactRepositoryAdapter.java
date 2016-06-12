package org.apache.maven.shared.dependencies.collect.internal;

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

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.Proxy;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;

/**
 * ArtifactRepository wrapper around {@link RemoteRepository}
 * 
 * @author Robert Scholte
 *
 */
public class Maven30ArtifactRepositoryAdapter implements ArtifactRepository
{
    
    private RemoteRepository remoteRepository;

    /**
     * @param remoteRepository {@link RemoteRepository}
     */
    public Maven30ArtifactRepositoryAdapter( RemoteRepository remoteRepository )
    {
        this.remoteRepository = remoteRepository;
    }

    @Override
    public String pathOf( Artifact artifact )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String pathOfRemoteRepositoryMetadata( ArtifactMetadata artifactMetadata )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String pathOfLocalRepositoryMetadata( ArtifactMetadata metadata, ArtifactRepository repository )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUrl()
    {
        return remoteRepository.getUrl();
    }

    @Override
    public void setUrl( String url )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBasedir()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId()
    {
        return remoteRepository.getId();
    }

    @Override
    public void setId( String id )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArtifactRepositoryPolicy getSnapshots()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSnapshotUpdatePolicy( ArtifactRepositoryPolicy policy )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArtifactRepositoryPolicy getReleases()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReleaseUpdatePolicy( ArtifactRepositoryPolicy policy )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ArtifactRepositoryLayout getLayout()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLayout( ArtifactRepositoryLayout layout )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getKey()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUniqueVersion()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBlacklisted()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlacklisted( boolean blackListed )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Artifact find( Artifact artifact )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> findVersions( Artifact artifact )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isProjectAware()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthentication( Authentication authentication )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Authentication getAuthentication()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProxy( Proxy proxy )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Proxy getProxy()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "       id: " ).append( getId() ).append( "\n" );
        sb.append( "      url: " ).append( getUrl() ).append( "\n" );
        sb.append( "   layout: " ).append( "default" ).append( "\n" );

        RepositoryPolicy snapshotPolicy = remoteRepository.getPolicy( true ); 
        sb.append( "snapshots: [enabled => " ).append( snapshotPolicy.isEnabled() );
        sb.append( ", update => " ).append( snapshotPolicy.getUpdatePolicy() ).append( "]\n" );

        RepositoryPolicy releasePolicy = remoteRepository.getPolicy( false ); 
        sb.append( " releases: [enabled => " ).append( releasePolicy.isEnabled() );
        sb.append( ", update => " ).append( releasePolicy.getUpdatePolicy() ).append( "]\n" );

        return sb.toString();
    }
    
    
    @Override
    public int hashCode()
    {
        return remoteRepository.hashCode();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        
        Maven30ArtifactRepositoryAdapter other = (Maven30ArtifactRepositoryAdapter) obj;
        if ( remoteRepository == null )
        {
            if ( other.remoteRepository != null )
            {
                return false;
            }
        }
        else if ( !remoteRepository.equals( other.remoteRepository ) )
        {
            return false;
        }
        return true;
    }
}
