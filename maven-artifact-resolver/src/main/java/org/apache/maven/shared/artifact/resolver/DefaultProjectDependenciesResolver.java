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

import static org.apache.maven.project.MavenProject.getProjectReferenceId;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of {@link ProjectDependenciesResolver}.
 * 
 * @author jdcasey
 * 
 * @see ProjectDependenciesResolver
 * @plexus.component role="org.apache.maven.shared.artifact.resolver.ProjectDependenciesResolver" role-hint="default"
 */
public final class DefaultProjectDependenciesResolver
    implements ProjectDependenciesResolver
{

    /**
     * @plexus.requirement
     */
    private ArtifactResolver resolver;

    /**
     * @plexus.requirement
     */
    private ArtifactFactory artifactFactory;

    /**
     * @plexus.requirement role-hint="maven"
     */
    private ArtifactMetadataSource metadataSource;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Set<Artifact> resolve( final Collection<MavenProject> projects, final Collection<String> scopes, final MavenSession session )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        if ( projects == null || projects.isEmpty() )
        {
            return Collections.emptySet();
        }
        
        Set<Artifact> resolved = new LinkedHashSet<Artifact>();
        CumulativeScopeArtifactFilter scopeFilter = new CumulativeScopeArtifactFilter();
        if ( scopes == null )
        {
            scopeFilter.addScope( Artifact.SCOPE_COMPILE );
        }
        else
        {
            for ( String scope : scopes )
            {
                scopeFilter.addScope( scope );
            }
        }
        
        for ( MavenProject project : projects )
        {
            Set<Artifact> depArtifacts = (Set<Artifact>) project.getDependencyArtifacts();
            if ( depArtifacts == null )
            {
                try
                {
                    depArtifacts = project.createArtifacts( artifactFactory, null, scopeFilter );
                }
                catch ( InvalidDependencyVersionException e )
                {
                    throw new ArtifactResolutionException( "Failed to create Artifact instances for project dependencies: "
                        + e.getMessage(), null, e );
                }
            }
            
            if ( depArtifacts == null || depArtifacts.isEmpty() )
            {
                continue;
            }
            
            for ( Iterator<Artifact> it = depArtifacts.iterator(); it.hasNext(); )
            {
                Artifact artifact = it.next();
                if ( resolved.contains( artifact ) )
                {
                    // already resolved, don't do it again.
                    it.remove();
                }
            }
            
            Artifact projectArtifact = project.getArtifact();
            if ( projectArtifact == null )
            {
                projectArtifact = artifactFactory.createProjectArtifact( project.getGroupId(), project.getArtifactId(),
                                                                     project.getVersion() );
            }
            
            try
            {
                ArtifactResolutionResult result = resolver.resolveTransitively( depArtifacts, projectArtifact,
                                                                                project.getManagedVersionMap(),
                                                                                session.getLocalRepository(),
                                                                                project.getRemoteArtifactRepositories(),
                                                                                metadataSource, scopeFilter );

                if ( result.getArtifacts() != null && !result.getArtifacts().isEmpty() )
                {
                    resolved.addAll( result.getArtifacts() );
                }
            }
            catch ( MultipleArtifactsNotFoundException me )
            {
                Set<String> projectIds = getProjectIds( projects );
                Collection<Artifact> missing = new HashSet<Artifact>( me.getMissingArtifacts() );
                for ( Iterator<Artifact> it = missing.iterator(); it.hasNext(); )
                {
                    Artifact artifact = it.next();
                    if ( projectIds.contains( getProjectReferenceId( artifact.getGroupId(), artifact.getArtifactId(),
                                                                     artifact.getVersion() ) ) )
                    {
                        it.remove();
                    }
                }

                if ( missing.isEmpty() )
                {
                    resolved.addAll( me.getResolvedArtifacts() );
                }
                else
                {
                    throw me;
                }
            }
        }
        
        return resolved;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Artifact> resolve( final MavenProject project, final Collection<String> scopes, final MavenSession session )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        Collection<MavenProject> projects = Collections.singleton( project );

        return resolve( projects, scopes, session );
    }

    private Set<String> getProjectIds( final Collection<MavenProject> projects )
    {
        Set<String> ids = new HashSet<String>();
        if ( projects != null && !projects.isEmpty() )
        {
            for ( MavenProject project : projects )
            {
                ids.add( getProjectReferenceId( project.getGroupId(), project.getArtifactId(), project.getVersion() ) );
            }
        }

        return ids;
    }

}
