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

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper around Maven 2 dependency tree builder.
 *
 * @see DependencyTreeBuilder
 * @author Hervé Boutemy
 * @since 2.0
 */
@Component( role = DependencyGraphBuilder.class, hint = "maven2" )
public class Maven2DependencyGraphBuilder
    extends AbstractLogEnabled
    implements DependencyGraphBuilder
{
    @Requirement
    private DependencyTreeBuilder treeBuilder;

    /**
     * Builds the dependency graph for Maven 2.
     *
     * @param project the project
     * @param filter artifact filter (can be <code>null</code>)
     * @return DependencyNode containing the dependency graph.
     * @throws DependencyGraphBuilderException if some of the dependencies could not be resolved.
     */
    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter )
        throws DependencyGraphBuilderException
    {
        try
        {
            return buildDependencyNode( null, treeBuilder.buildDependencyTree( project ), filter );
        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
    }

    /**
     * Builds the dependency graph for Maven 2.
     *
     * NB the reactor projects are ignored as Maven 2 is not able to resolve projects from the reactor.
     *
     * @param project           the project
     * @param filter            artifact filter (can be <code>null</code>)
     * @param reactorProjects   Ignored.
     * @return DependencyNode containing the dependency graph.
     * @throws DependencyGraphBuilderException if some of the dependencies could not be resolved.
     */
    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter,
                                                Collection<MavenProject> reactorProjects )
        throws DependencyGraphBuilderException
    {
        getLogger().warn( "Reactor projects ignored - reactor dependencies cannot be resolved in Maven2" );
        return buildDependencyGraph( project, filter );
    }

    private DependencyNode buildDependencyNode( DependencyNode parent,
                                                org.apache.maven.shared.dependency.tree.DependencyNode node,
                                                ArtifactFilter filter )
    {
        String versionSelectedFromRange = null;
        if ( node.getVersionSelectedFromRange() != null )
        {
            versionSelectedFromRange = node.getVersionSelectedFromRange().toString();
        }

        DefaultDependencyNode current =
            new DefaultDependencyNode( parent, node.getArtifact(), node.getPremanagedVersion(),
                                       node.getPremanagedScope(), versionSelectedFromRange );

        List<DependencyNode> nodes = new ArrayList<DependencyNode>( node.getChildren().size() );
        for ( org.apache.maven.shared.dependency.tree.DependencyNode child : node.getChildren() )
        {
            if ( child.getState() != org.apache.maven.shared.dependency.tree.DependencyNode.INCLUDED )
            {
                // only included nodes are supported in the graph API
                continue;
            }

            if ( ( filter == null ) || filter.include( child.getArtifact() ) )
            {
                nodes.add( buildDependencyNode( current, child, filter ) );
            }
        }

        current.setChildren( Collections.unmodifiableList( nodes ) );

        return current;
    }
}
