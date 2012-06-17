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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * Wrapper for Maven 2's dependency node.
 *
 * @see org.apache.maven.shared.dependency.tree.DependencyNode
 * @author Hervé Boutemy
 * @since 2.0
 */
public class Maven2DependencyNode
    extends AbstractDependencyNode
{
    private final Artifact artifact;

    private final List<DependencyNode> children;

    public Maven2DependencyNode( org.apache.maven.shared.dependency.tree.DependencyNode node, ArtifactFilter filter )
    {
        this.artifact = node.getArtifact();

        List<DependencyNode> nodes = new ArrayList<DependencyNode>( node.getChildren().size() );
        for ( org.apache.maven.shared.dependency.tree.DependencyNode child : node.getChildren() )
        {
            if ( ( filter == null ) || filter.include( child.getArtifact() ) )
            {
                nodes.add( new Maven2DependencyNode( child, filter ) );
            }
        }

        children = Collections.unmodifiableList( nodes );
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public List<DependencyNode> getChildren()
    {
        return children;
    }

}
