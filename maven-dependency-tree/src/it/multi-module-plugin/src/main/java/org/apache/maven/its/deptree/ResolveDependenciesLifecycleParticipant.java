/*******************************************************************************
 * Copyright (c) 2008, 2011 Sonatype Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype Inc. - initial API and implementation
 *******************************************************************************/
package org.apache.maven.its.deptree;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.ProjectReferenceKeyGenerator;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves all the dependencies in the project immediately after the Project has been read.
 *
 * It needs to resolve dependencies from the reactor because in a multi-module build
 * sibling modules may never have been built yet, so will not exist in any repo.
 *
 * It is crucial that reactor dependencies can be found at this point in the build because
 * this is the only time at which we can modify the classpath. And for modules that produce
 * archives (eg Android AAR) which contain the actual Java Jar dependency, we need to know
 * that they exist so that we can add a placeholder for them onto the classpath,
 * which we can replace with the real classes once they are built.
 */
@Component( role = AbstractMavenLifecycleParticipant.class, hint = "default" )
public final class ResolveDependenciesLifecycleParticipant extends AbstractMavenLifecycleParticipant
{
    @Requirement( hint = "default" )
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Requirement
    private Logger log;

    @Override
    public void afterProjectsRead( MavenSession session ) throws MavenExecutionException
    {
        log.debug( "" );
        log.info( "ResolveDependenciesLifecycleParticipant#afterProjectsRead" );
        log.debug( "" );

        final List<MavenProject> projects = session.getProjects();

        // NB We could get this from session.getProjectMap() but it doesn't exist in Maven-2.2.1 or 3.0.4
        final Map<String, MavenProject> reactorProjects = new HashMap<String, MavenProject>();
        final ProjectReferenceKeyGenerator keyGenerator = new ProjectReferenceKeyGenerator();

        log.debug( "Reactor projects:" );
        for ( MavenProject project : projects )
        {
            log.debug( " - " + project );
            reactorProjects.put( keyGenerator.getProjectReferenceKey( project ), project );
        }
        log.debug( "" );

        for ( MavenProject project : projects )
        {
            log.debug( "" );
            log.debug( "project=" + project.getArtifact() );

            try
            {
                // No need to filter our search. We want to resolve all artifacts.
                dependencyGraphBuilder.buildDependencyGraph( project, null, reactorProjects );
            }
            catch ( DependencyGraphBuilderException e )
            {
                throw new MavenExecutionException( "Could not resolve dependencies for project : " + project, e );
            }
        }
    }
}
  
