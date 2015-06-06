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

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.filter.ArtifactDependencyNodeFilter;
import org.apache.maven.shared.dependency.graph.filter.DependencyNodeFilter;
import org.apache.maven.shared.dependency.graph.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.FilteringDependencyNodeVisitor;

/**
 * This filter will exclude everything that is not a dependency of the selected dependencyNode.
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
     * @TODO describe for to get a DependencyNode based on Artifact or Dependency
     * 
     */
    public ArtifactTransitivityFilter( DependencyNode node )
        throws ProjectBuildingException, InvalidDependencyVersionException
    {
        CollectingDependencyNodeVisitor collectingVisitor = new CollectingDependencyNodeVisitor();
     
        DependencyNodeFilter dependencyFilter = new ArtifactDependencyNodeFilter( new ScopeArtifactFilter( Artifact.SCOPE_TEST ) );
        
        FilteringDependencyNodeVisitor filteringVisitor = new FilteringDependencyNodeVisitor( collectingVisitor, dependencyFilter );
        
        BuildingDependencyNodeVisitor buildingVisitor = new BuildingDependencyNodeVisitor( filteringVisitor );

        buildingVisitor.visit( node );
        
        for( DependencyNode collectedNode : collectingVisitor.getNodes() )
        {
            transitiveArtifacts.add( collectedNode.getArtifact().getDependencyConflictId() );
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
