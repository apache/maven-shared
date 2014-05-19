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
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.version.VersionConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper around Eclipse Aether dependency resolver, used in Maven 3.1.
 *
 * @see ProjectDependenciesResolver
 * @author Hervé Boutemy
 * @since 2.1
 */
@Component( role = DependencyGraphBuilder.class, hint = "maven31" )
public class Maven31DependencyGraphBuilder
    extends AbstractLogEnabled
    implements DependencyGraphBuilder
{
    @Requirement
    private ProjectDependenciesResolver resolver;

    @Requirement
    private ArtifactFactory factory;

    /**
     * Builds the dependency graph for Maven 3.1+.
     *
     * @param project the project
     * @param filter artifact filter (can be <code>null</code>)
     * @return DependencyNode containing the dependency graph.
     * @throws DependencyGraphBuilderException if some of the dependencies could not be resolved.
     */
    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter )
        throws DependencyGraphBuilderException
    {
        return buildDependencyGraph( project, filter, Collections.<MavenProject>emptyList() );
    }

    /**
     * Builds the dependency graph for Maven 3.1+ including any dependencies from any projects in the reactor.
     *
     * @param project the project
     * @param filter artifact filter (can be <code>null</code>)
     * @param reactorProjects Collection of those projects contained in the reactor.
     * @return DependencyNode containing the dependency graph.
     * @throws DependencyGraphBuilderException if some of the dependencies could not be resolved.
     */
    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter,
                                                Collection<MavenProject> reactorProjects )
        throws DependencyGraphBuilderException
    {
        ProjectBuildingRequest projectBuildingRequest =
            (ProjectBuildingRequest) Invoker.invoke( project, "getProjectBuildingRequest" );

        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( projectBuildingRequest, "getRepositorySession" );

        /*
         * if ( Boolean.TRUE != ( (Boolean) session.getConfigProperties().get(
         * DependencyManagerUtils.NODE_DATA_PREMANAGED_VERSION ) ) ) { DefaultRepositorySystemSession newSession = new
         * DefaultRepositorySystemSession( session ); newSession.setConfigProperty(
         * DependencyManagerUtils.NODE_DATA_PREMANAGED_VERSION, true ); session = newSession; }
         */

        final DependencyResolutionRequest request = new DefaultDependencyResolutionRequest();
        request.setMavenProject( project );
        Invoker.invoke( request, "setRepositorySession", RepositorySystemSession.class, session );

        final DependencyResolutionResult result = resolveDependencies( request, reactorProjects );
        org.eclipse.aether.graph.DependencyNode graph =
            (org.eclipse.aether.graph.DependencyNode) Invoker.invoke( DependencyResolutionResult.class, result,
                                                                      "getDependencyGraph" );

        return buildDependencyNode( null, graph, project.getArtifact(), filter );
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
            // Ignore any resolution failure for deps that are part of the reactor but have not yet been built.
            // NB Typing has been removed because DependencyResolutionResult returns Sonatype Aether in 3.0.4 and
            // Eclipse Aether in 3.1.1 and while dep-tree is a single module we can only compile against one of them.
            //
            // NB While applying this code to Maven3DependencyGraphBuilder is trivial it won't work because
            // in Maven 3, MavenProject.getProjectReferences isn't populated. So we would need to have the reactor
            // modules passed in separately which would change the API for DependencyGraphBuilder. If
            // MavenProject.getProjectReferences were populated (like it should be) then this would work for Maven3 too.
            //
            // NB There doesn't seem to be any way to apply this to Maven2DependencyGraphBuilder as there is no
            // concept of partial resolution like there is is 3 and 3.1
            final DependencyResolutionResult result = e.getResult();

            final List<Dependency> reactorDeps =
                getReactorDependencies( reactorProjects, result.getUnresolvedDependencies() );
            result.getUnresolvedDependencies().removeAll( reactorDeps );
            Invoker.invoke( result.getResolvedDependencies(), "addAll", Collection.class, reactorDeps );

            if ( !result.getUnresolvedDependencies().isEmpty() )
            {
                throw new DependencyGraphBuilderException( "Could not resolve the following dependencies : "
                    + result.getUnresolvedDependencies(), e );
            }

            getLogger().debug( "Resolved dependencies after ignoring reactor dependencies : " + reactorDeps );

            return result;
        }
    }

    private List<Dependency> getReactorDependencies( Collection<MavenProject> reactorProjects, List<?> dependencies )
    {
        final Set<ArtifactKey> reactorProjectsIds = new HashSet<ArtifactKey>();
        for ( final MavenProject project : reactorProjects )
        {
            reactorProjectsIds.add( new ArtifactKey( project ) );
        }

        final List<Dependency> reactorDeps = new ArrayList<Dependency>();
        for ( final Object untypedDependency : dependencies )
        {
            final Dependency dependency = (Dependency) untypedDependency;
            final org.eclipse.aether.artifact.Artifact depArtifact = dependency.getArtifact();

            final ArtifactKey key =
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
        org.eclipse.aether.artifact.Artifact artifact = dep.getArtifact();

        return factory.createDependencyArtifact( artifact.getGroupId(), artifact.getArtifactId(),
                                                 VersionRange.createFromVersion( artifact.getVersion() ),
                                                 artifact.getProperty( "type", artifact.getExtension() ),
                                                 artifact.getClassifier(), dep.getScope(), dep.isOptional() );
    }

    private DependencyNode buildDependencyNode( DependencyNode parent, org.eclipse.aether.graph.DependencyNode node,
                                                Artifact artifact, ArtifactFilter filter )
    {
        String premanagedVersion = null; // DependencyManagerUtils.getPremanagedVersion( node );
        String premanagedScope = null; // DependencyManagerUtils.getPremanagedScope( node );

        DefaultDependencyNode current =
            new DefaultDependencyNode( parent, artifact, premanagedVersion, premanagedScope,
                                       getVersionSelectedFromRange( node.getVersionConstraint() ) );

        List<DependencyNode> nodes = new ArrayList<DependencyNode>( node.getChildren().size() );
        for ( org.eclipse.aether.graph.DependencyNode child : node.getChildren() )
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

        return constraint.getRange().toString();
    }
}
