package org.apache.maven.plugin.testing.stubs;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.metadata.ArtifactMetadata;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Stub class for {@link Artifact} testing.
 * 
 * @author jesse
 * @version $Id$
 */
public class ArtifactStub
    implements Artifact
{

    private String groupId;

    private String artifactId;

    private String version;

    private String scope;

    private String type;

    private String classifier;

    private File file;

    public int compareTo( Object object )
    {
        return 0;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getScope()
    {
        return scope;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getClassifier()
    {
        return classifier;
    }

    public boolean hasClassifier()
    {
        return classifier != null;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile( File file )
    {
        this.file = file;
    }

    public String getBaseVersion()
    {
        return null;
    }

    public void setBaseVersion( String string )
    {
    }

    public String getId()
    {
        return null;
    }

    public String getDependencyConflictId()
    {
        return null;
    }

    public void addMetadata( ArtifactMetadata artifactMetadata )
    {
    }

    public Collection getMetadataList()
    {
        return null;
    }

    public void setRepository( ArtifactRepository artifactRepository )
    {
    }

    public ArtifactRepository getRepository()
    {
        return null;
    }

    public void updateVersion( String string, ArtifactRepository artifactRepository )
    {
    }

    public String getDownloadUrl()
    {
        return null;
    }

    public void setDownloadUrl( String string )
    {
    }

    public ArtifactFilter getDependencyFilter()
    {
        return null;
    }

    public void setDependencyFilter( ArtifactFilter artifactFilter )
    {
    }

    public ArtifactHandler getArtifactHandler()
    {
        return null;
    }

    public List getDependencyTrail()
    {
        return null;
    }

    public void setDependencyTrail( List list )
    {
    }

    public void setScope( String scope )
    {
        this.scope = scope;
    }

    public VersionRange getVersionRange()
    {
        return null;
    }

    public void setVersionRange( VersionRange versionRange )
    {
    }

    public void selectVersion( String string )
    {
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public boolean isSnapshot()
    {
        return false;
    }

    public void setResolved( boolean b )
    {
    }

    public boolean isResolved()
    {
        return false;
    }

    public void setResolvedVersion( String string )
    {
    }

    public void setArtifactHandler( ArtifactHandler artifactHandler )
    {
    }

    public boolean isRelease()
    {
        return false;
    }

    public void setRelease( boolean b )
    {
    }

    public List getAvailableVersions()
    {
        return null;
    }

    public void setAvailableVersions( List list )
    {
    }

    public boolean isOptional()
    {
        return false;
    }

    public void setOptional( boolean b )
    {
    }

    public ArtifactVersion getSelectedVersion()
        throws OverConstrainedVersionException
    {
        return null;
    }

    public boolean isSelectedVersionKnown()
        throws OverConstrainedVersionException
    {
        return false;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if ( getGroupId() != null )
        {
            sb.append( getGroupId() );
            sb.append( ":" );
        }
        appendArtifactTypeClassifierString( sb );
        if ( version != null )
        {
            sb.append( ":" );
            sb.append( getVersion() );
        }
        if ( scope != null )
        {
            sb.append( ":" );
            sb.append( scope );
        }
        return sb.toString();
    }

    private void appendArtifactTypeClassifierString( StringBuffer sb )
    {
        sb.append( getArtifactId() );
        sb.append( ":" );
        sb.append( getType() );
        if ( hasClassifier() )
        {
            sb.append( ":" );
            sb.append( getClassifier() );
        }
    }

}
