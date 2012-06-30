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
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.sonatype.aether.graph.Dependency;

/**
 * Wrapper for Maven 3's dependency node, which is provided by Aether.
 *
 * @see org.sonatype.aether.graph.DependencyNode
 * @author Hervé Boutemy
 * @since 2.0
 */
public class Maven3DependencyNode
    extends AbstractDependencyNode
{
    private final Artifact artifact;

    private final List<DependencyNode> children;

    private final DependencyNode parent;

    public Maven3DependencyNode( DependencyNode parent, ArtifactFactory factory,
                                 org.sonatype.aether.graph.DependencyNode node, final Artifact artifact,
                                 ArtifactFilter filter )
    {
        this.parent = parent;

        if ( artifact != null )
        {
            this.artifact = artifact;
        }
        else
        {
            Dependency dep = node.getDependency();
            org.sonatype.aether.artifact.Artifact art = dep.getArtifact();

            Artifact tmpArtifact =
                factory.createDependencyArtifact( art.getGroupId(), art.getArtifactId(),
                                                  VersionRange.createFromVersion( art.getVersion() ),
                                                  art.getExtension(), art.getClassifier(), dep.getScope(),
                                                  dep.isOptional() );

            if ( ( filter != null ) && !filter.include( tmpArtifact ) )
            {
                this.artifact = null;
                children = null;
                return;
            }

            this.artifact = tmpArtifact;
        }

        List<DependencyNode> nodes = new ArrayList<DependencyNode>( node.getChildren().size() );
        for ( org.sonatype.aether.graph.DependencyNode child : node.getChildren() )
        {
            DependencyNode tmpNode = new Maven3DependencyNode( this, factory, child, null, filter );

            if ( tmpNode.getArtifact() != null )
            {
                nodes.add( tmpNode );
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

    public DependencyNode getParent()
    {
        return parent;
    }
}
