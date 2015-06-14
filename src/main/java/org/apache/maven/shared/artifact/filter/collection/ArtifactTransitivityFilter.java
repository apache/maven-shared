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

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This filter will exclude everything that is not a dependency of the selected artifact.
 *
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id$
 */
public class ArtifactTransitivityFilter
    extends AbstractArtifactsFilter
{
    /**
     * List of dependencyConflictIds of transitiveArtifacts
     */
    private Set<String> transitiveArtifacts;

    /**
     * Use {@link org.apache.maven.execution.MavenSession#getProjectBuildingRequest()} to get the buildingRequest.
     * The projectBuilder should be resolved with CDI.
     * <p/>
     * <pre>
     *   // For Mojo
     *   &#64;Component
     *   private ProjectBuilder projectBuilder;
     *
     *   // For Components
     *   &#64;Requirement // or &#64;Inject
     *   private ProjectBuilder projectBuilder;
     * </pre>
     *
     * @param artifact        the artifact to resolve the dependencies from
     * @param buildingRequest the buildingRequest
     * @param projectBuilder  the projectBuilder
     * @throws ProjectBuildingException if the project descriptor could not be successfully built
     */
    public ArtifactTransitivityFilter( Artifact artifact, ProjectBuildingRequest buildingRequest,
                                       ProjectBuilder projectBuilder )
        throws ProjectBuildingException
    {
        ProjectBuildingRequest request = new DefaultProjectBuildingRequest( buildingRequest );

        request.setResolveDependencies( true );

        ProjectBuildingResult buildingResult = projectBuilder.build( artifact, request );

        DependencyResolutionResult resolutionResult = buildingResult.getDependencyResolutionResult();
        if ( resolutionResult != null )
        {
            if ( isMaven31() )
            {
                try
                {
                    @SuppressWarnings( "unchecked" ) List<org.eclipse.aether.graph.Dependency> dependencies =
                        (List<org.eclipse.aether.graph.Dependency>) Invoker.invoke( resolutionResult,
                                                                                    "getDependencies" );

                    for ( org.eclipse.aether.graph.Dependency dependency : dependencies )
                    {
                        Artifact mavenArtifact = (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact",
                                                                            org.eclipse.aether.artifact.Artifact.class,
                                                                            dependency.getArtifact() );

                        transitiveArtifacts.add( mavenArtifact.getDependencyConflictId() );
                    }
                }
                catch ( IllegalAccessException e )
                {
                    // don't want to pollute method signature with ReflectionExceptions
                    throw new RuntimeException( e.getMessage(), e );
                }
                catch ( InvocationTargetException e )
                {
                    // don't want to pollute method signature with ReflectionExceptions
                    throw new RuntimeException( e.getMessage(), e );
                }
                catch ( NoSuchMethodException e )
                {
                    // don't want to pollute method signature with ReflectionExceptions
                    throw new RuntimeException( e.getMessage(), e );
                }
            }
            else
            {
                try
                {
                    @SuppressWarnings( "unchecked" ) List<org.sonatype.aether.graph.Dependency> dependencies =
                        (List<org.sonatype.aether.graph.Dependency>) Invoker.invoke( resolutionResult,
                                                                                     "getDependencies" );

                    for ( org.sonatype.aether.graph.Dependency dependency : dependencies )
                    {
                        Artifact mavenArtifact = (Artifact) Invoker.invoke( RepositoryUtils.class, "toArtifact",
                                                                            org.sonatype.aether.artifact.Artifact.class,
                                                                            dependency.getArtifact() );

                        transitiveArtifacts.add( mavenArtifact.getDependencyConflictId() );
                    }
                }
                catch ( IllegalAccessException e )
                {
                    // don't want to pollute method signature with ReflectionExceptions
                    throw new RuntimeException( e.getMessage(), e );
                }
                catch ( InvocationTargetException e )
                {
                    // don't want to pollute method signature with ReflectionExceptions
                    throw new RuntimeException( e.getMessage(), e );
                }
                catch ( NoSuchMethodException e )
                {
                    // don't want to pollute method signature with ReflectionExceptions
                    throw new RuntimeException( e.getMessage(), e );
                }
            }
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

    public Set<Artifact> filter( Set<Artifact> artifacts )
    {

        Set<Artifact> result = new HashSet<Artifact>();
        for ( Artifact artifact : artifacts )
        {
            if ( artifactIsATransitiveDependency( artifact ) )
            {
                result.add( artifact );
            }
        }
        return result;
    }

    /**
     * Compares the artifact to the list of dependencies to see if it is directly included by this project
     *
     * @param artifact representing the item to compare.
     * @return true if artifact is a transitive dependency
     */
    public boolean artifactIsATransitiveDependency( Artifact artifact )
    {
        return transitiveArtifacts.contains( artifact.getDependencyConflictId() );
    }
}
