package org.apache.maven.shared.repository.internal;

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
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.DefaultArtifactCoordinate;
import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * 
 */
@Component( role = RepositoryManager.class )
public class DefaultRepositoryManager
    implements RepositoryManager, Contextualizable
{
    private PlexusContainer container;

    @Override
    public String getPathForLocalArtifact( ProjectBuildingRequest buildingRequest, Artifact artifact )
    {
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            RepositoryManager effectiveRepositoryManager = container.lookup( RepositoryManager.class, hint );

            return effectiveRepositoryManager.getPathForLocalArtifact( buildingRequest, artifact );
        }
        catch ( ComponentLookupException e )
        {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    @Override
    public String getPathForLocalArtifact( ProjectBuildingRequest buildingRequest, ArtifactCoordinate coor )
    {
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            RepositoryManager effectiveRepositoryManager = container.lookup( RepositoryManager.class, hint );

            return effectiveRepositoryManager.getPathForLocalArtifact( buildingRequest, coor );
        }
        catch ( ComponentLookupException e )
        {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    @Override
    public String getPathForLocalMetadata( ProjectBuildingRequest buildingRequest, ArtifactMetadata metadata )
    {
        if ( metadata instanceof ProjectArtifactMetadata )
        {
            DefaultArtifactCoordinate pomCoordinate = new DefaultArtifactCoordinate();
            pomCoordinate.setGroupId( metadata.getGroupId() );
            pomCoordinate.setArtifactId( metadata.getArtifactId() );
            pomCoordinate.setVersion( metadata.getBaseVersion() );
            pomCoordinate.setExtension( "pom" );
            return getPathForLocalArtifact( buildingRequest, pomCoordinate );
        }
        
        try
        {
            
            String hint = isMaven31() ? "maven31" : "maven3";
            
            RepositoryManager effectiveRepositoryManager = container.lookup( RepositoryManager.class, hint );
            
            return effectiveRepositoryManager.getPathForLocalMetadata( buildingRequest, metadata );
        }
        catch ( ComponentLookupException e )
        {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    @Override
    public ProjectBuildingRequest setLocalRepositoryBasedir( ProjectBuildingRequest request, File basedir )
    {
        try
        {
            String hint = isMaven31() ? "maven31" : isMaven302() ? "maven302" : "maven3";

            RepositoryManager effectiveRepositoryManager = container.lookup( RepositoryManager.class, hint );

            return effectiveRepositoryManager.setLocalRepositoryBasedir( request, basedir );
        }
        catch ( ComponentLookupException e )
        {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    @Override
    public File getLocalRepositoryBasedir( ProjectBuildingRequest request )
    {
        try
        {
            String hint = isMaven31() ? "maven31" : isMaven302() ? "maven302" : "maven3";

            RepositoryManager effectiveRepositoryManager = container.lookup( RepositoryManager.class, hint );

            return effectiveRepositoryManager.getLocalRepositoryBasedir( request );
        }
        catch ( ComponentLookupException e )
        {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    /**
     * @return true if the current Maven version is Maven 3.1.
     */
    protected static boolean isMaven31()
    {
        return canFindCoreClass( "org.eclipse.aether.artifact.Artifact" ); // Maven 3.1 specific
    }

    /**
     * @return true if the current Maven version is Maven 3.0.2
     */
    protected static boolean isMaven302()
    {
        return canFindCoreClass( "org.sonatype.aether.spi.localrepo.LocalRepositoryManagerFactory" );
    }

    private static boolean canFindCoreClass( String className )
    {
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( className );

            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    /**
     * Injects the Plexus content.
     *
     * @param context Plexus context to inject.
     * @throws ContextException if the PlexusContainer could not be located.
     */
    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
