package org.apache.maven.shared.dependency.graph;

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

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

/**
 * Represents an artifact node within a Maven project's dependency graph.
 *
 * @author Hervé Boutemy
 * @since 2.0
 */
public interface DependencyNode
{
    public Artifact getArtifact();

    public List<DependencyNode> getChildren();

    /**
     * Applies the specified dependency node visitor to this dependency node and its children.
     * 
     * @param visitor
     *            the dependency node visitor to use
     * @return the visitor result of ending the visit to this node
     * @since 1.1
     */
    public boolean accept( DependencyNodeVisitor visitor );

    /**
     * Gets the parent dependency node of this dependency node.
     * 
     * @return the parent dependency node
     */
    public DependencyNode getParent();
}
