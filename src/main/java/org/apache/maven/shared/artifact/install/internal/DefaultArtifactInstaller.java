package org.apache.maven.shared.artifact.install.internal;

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
import org.apache.maven.shared.artifact.install.ArtifactInstaller;
import org.apache.maven.shared.artifact.install.ArtifactInstallerException;
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
@Component( role = ArtifactInstaller.class )
public class DefaultArtifactInstaller
    implements ArtifactInstaller, Contextualizable
{

    private PlexusContainer container;

    @Override
    public void install( ProjectBuildingRequest request, Collection<Artifact> mavenArtifacts )
        throws ArtifactInstallerException, IllegalArgumentException
    {
        validateParameters( request, mavenArtifacts );
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            ArtifactInstaller effectiveArtifactInstaller = container.lookup( ArtifactInstaller.class, hint );

            effectiveArtifactInstaller.install( request, mavenArtifacts );
        }
        catch ( ComponentLookupException e )
        {
            throw new ArtifactInstallerException( e.getMessage(), e );
        }
    }

    @Override
    public void install( ProjectBuildingRequest request, File localRepositry, Collection<Artifact> mavenArtifacts )
        throws ArtifactInstallerException
    {
        validateParameters( request, mavenArtifacts );
        if ( localRepositry == null )
        {
            throw new IllegalArgumentException( "The parameter localRepository is not allowed to be null." );
        }
        if ( !localRepositry.isDirectory() )
        {
            throw new IllegalArgumentException( "The parameter localRepository must be a directory." );
        }

        // TODO: Should we check for exists() ?

        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            ArtifactInstaller effectiveArtifactInstaller = container.lookup( ArtifactInstaller.class, hint );

            effectiveArtifactInstaller.install( request, localRepositry, mavenArtifacts );
        }
        catch ( ComponentLookupException e )
        {
            throw new ArtifactInstallerException( e.getMessage(), e );
        }
    }

    private void validateParameters( ProjectBuildingRequest request, Collection<Artifact> mavenArtifacts )
    {
        if ( request == null )
        {
            throw new IllegalArgumentException( "The parameter request is not allowed to be null." );
        }
        if ( mavenArtifacts == null )
        {
            throw new IllegalArgumentException( "The parameter mavenArtifacts is not allowed to be null." );
        }
        if ( mavenArtifacts.isEmpty() )
        {
            throw new IllegalArgumentException( "The collection mavenArtifacts is not allowed to be empty." );
        }
    }

    /**
     * @return true if the current Maven version is Maven 3.1.
     */
    protected static boolean isMaven31()
    {
        return canFindCoreClass( "org.eclipse.aether.artifact.Artifact" ); // Maven 3.1 specific
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
