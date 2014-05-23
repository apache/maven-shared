package org.apache.maven.its.deptree;

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

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        log.info( "" );
        log.info( "ResolveDependenciesLifecycleParticipant#afterProjectsRead" );

        final List<MavenProject> projects = session.getProjects();
        File basedir = new File( session.getExecutionRootDirectory() );

        for ( MavenProject project : projects )
        {
            log.info( "building dependency graph for project " + project.getArtifact() );

            File resolved = new File( basedir, "resolved-" + project.getArtifactId() + ".txt" );
            try
            {
                // No need to filter our search. We want to resolve all artifacts.
                dependencyGraphBuilder.buildDependencyGraph( project, null, projects );

                // proof that resolution has happened
                resolved.createNewFile();
            }
            catch ( DependencyGraphBuilderException e )
            {
                throw new MavenExecutionException( "Could not resolve dependencies for project: " + project, e );
            }
            catch ( IOException e )
            {
                throw new MavenExecutionException( "Could not create " + resolved, e );
            }
        }

        log.info( "" );
    }
}
  
