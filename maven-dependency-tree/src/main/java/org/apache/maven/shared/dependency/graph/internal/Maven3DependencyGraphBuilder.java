package org.apache.maven.shared.dependency.graph.internal;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.version.VersionConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper around Maven 3 dependency resolver.
 *
 * @see ProjectDependenciesResolver
 * @author Hervé Boutemy
 * @since 2.0
 */
@Component( role = DependencyGraphBuilder.class, hint = "maven3" )
public class Maven3DependencyGraphBuilder
    extends AbstractLogEnabled
    implements DependencyGraphBuilder
{
    @Requirement
    private ProjectDependenciesResolver resolver;

    @Requirement
    private ArtifactFactory factory;

    /**
     * Builds the dependency graph for Maven 3.
     *
     * @param project the project
     * @param filter artifact filter (can be <code>null</code>)
     * @return DependencyNode containing the dependency graph.
     * @throws DependencyGraphBuilderException if some of the dependencies could not be resolved.
     */
    @Override
    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter )
        throws DependencyGraphBuilderException
    {
        return buildDependencyGraph( project, filter, null );
    }

    /**
     * Builds the dependency graph for Maven 3, eventually hacking for collecting projects from
     * reactor not yet built.
     *
     * @param project the project
     * @param filter artifact filter (can be <code>null</code>)
     * @param reactorProjects Collection of those projects contained in the reactor (can be <code>null</code>).
     * @return DependencyNode containing the dependency graph.
     * @throws DependencyGraphBuilderException if some of the dependencies could not be resolved.
     */
    @Override
    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter,
                                                Collection<MavenProject> reactorProjects )
        throws DependencyGraphBuilderException
    {
        ProjectBuildingRequest projectBuildingRequest =
            (ProjectBuildingRequest) Invoker.invoke( project, "getProjectBuildingRequest" );

        DependencyResolutionRequest request =
            new DefaultDependencyResolutionRequest( project, projectBuildingRequest.getRepositorySession() );

        DependencyResolutionResult result = resolveDependencies( request, reactorProjects );

        return buildDependencyNode( null, result.getDependencyGraph(), project.getArtifact(), filter );
    }

    private DependencyResolutionResult resolveDependencies( DependencyResolutionRequest request,
                                                            Collection<MavenProject> reactorProjects )
        throws DependencyGraphBuilderException
    {
        try
        {
            return resolver.resolve( request );
        }
        catch ( DependencyResolutionException e )
        {
            if ( reactorProjects == null )
            {
                throw new DependencyGraphBuilderException( "Could not resolve following dependencies: "
                    + e.getResult().getUnresolvedDependencies(), e );
            }

            // try collecting from reactor
            return collectDependenciesFromReactor( e, reactorProjects );
        }
    }

    private DependencyResolutionResult collectDependenciesFromReactor( DependencyResolutionException e,
                                                                       Collection<MavenProject> reactorProjects )
        throws DependencyGraphBuilderException
    {
        DependencyResolutionResult result = e.getResult();

        List<Dependency> reactorDeps = getReactorDependencies( reactorProjects, result.getUnresolvedDependencies() );

        result.getUnresolvedDependencies().removeAll( reactorDeps );
        Invoker.invoke( result.getResolvedDependencies(), "addAll", Collection.class, reactorDeps );

        if ( !result.getUnresolvedDependencies().isEmpty() )
        {
            throw new DependencyGraphBuilderException( "Could not resolve nor collect following dependencies: "
                + result.getUnresolvedDependencies(), e );
        }

        return result;
    }

    private List<org.sonatype.aether.graph.Dependency> getReactorDependencies( Collection<MavenProject> reactorProjects,
                                                                               List<?> dependencies )
    {
        Set<ArtifactKey> reactorProjectsIds = new HashSet<ArtifactKey>();
        for ( MavenProject project : reactorProjects )
        {
            reactorProjectsIds.add( new ArtifactKey( project ) );
        }

        List<Dependency> reactorDeps = new ArrayList<Dependency>();
        for ( Object untypedDependency : dependencies )
        {
            Dependency dependency = (Dependency) untypedDependency;
            org.sonatype.aether.artifact.Artifact depArtifact = dependency.getArtifact();

            ArtifactKey key =
                new ArtifactKey( depArtifact.getGroupId(), depArtifact.getArtifactId(), depArtifact.getVersion() );

            if ( reactorProjectsIds.contains( key ) )
            {
                reactorDeps.add( dependency );
            }
        }

        return reactorDeps;
    }

    private Artifact getDependencyArtifact( Dependency dep )
    {
        org.sonatype.aether.artifact.Artifact artifact = dep.getArtifact();

        return factory.createDependencyArtifact( artifact.getGroupId(), artifact.getArtifactId(),
                                                 VersionRange.createFromVersion( artifact.getVersion() ),
                                                 artifact.getProperty( "type", artifact.getExtension() ),
                                                 artifact.getClassifier(), dep.getScope(), dep.isOptional() );
    }

    private DependencyNode buildDependencyNode( DependencyNode parent, org.sonatype.aether.graph.DependencyNode node,
                                                Artifact artifact, ArtifactFilter filter )
    {
        DefaultDependencyNode current =
            new DefaultDependencyNode( parent, artifact,
                                       null /* node.getPremanagedVersion() */,
                                       null /* node.getPremanagedScope() */,
                                       getVersionSelectedFromRange( node.getVersionConstraint() ) );

        List<DependencyNode> nodes = new ArrayList<DependencyNode>( node.getChildren().size() );
        for ( org.sonatype.aether.graph.DependencyNode child : node.getChildren() )
        {
            Artifact childArtifact = getDependencyArtifact( child.getDependency() );

            if ( ( filter == null ) || filter.include( childArtifact ) )
            {
                nodes.add( buildDependencyNode( current, child, childArtifact, filter ) );
            }
        }

        current.setChildren( Collections.unmodifiableList( nodes ) );

        return current;
    }

    private String getVersionSelectedFromRange( VersionConstraint constraint )
    {
        if ( ( constraint == null ) || ( constraint.getVersion() != null ) )
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for ( org.sonatype.aether.version.VersionRange range : constraint.getRanges() )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ',' );
            }
            sb.append( range );
        }

        return sb.toString();
    }
}
