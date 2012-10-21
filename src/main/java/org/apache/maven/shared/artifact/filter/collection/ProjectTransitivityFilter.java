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

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id$
 */
public class ProjectTransitivityFilter
    extends AbstractArtifactsFilter
{

    private boolean excludeTransitive;

    private Set<Artifact> directDependencies;

    public ProjectTransitivityFilter( Set<Artifact> directDependencies, boolean excludeTransitive )
    {
        this.excludeTransitive = excludeTransitive;
        this.directDependencies = directDependencies;
    }

    public Set<Artifact> filter( Set<Artifact> artifacts )
    {
        // why not just take the directDependencies here?
        // because if this filter is run after some other process, the
        // set of artifacts may not be the same as the directDependencies.
        Set<Artifact> result = artifacts;

        if ( excludeTransitive )
        {
            result = new HashSet<Artifact>();
            for ( Artifact artifact : artifacts )
            {
                if ( artifactIsADirectDependency( artifact ) )
                {
                    result.add( artifact );
                }
            }
        }
        return result;
    }

    /**
     * Compares the artifact to the list of dependencies to see if it is directly included by this project
     * 
     * @param artifact representing the item to compare.
     * @return true if artifact is a direct dependency
     */
    public boolean artifactIsADirectDependency( Artifact artifact )
    {
        for ( Artifact dependency : this.directDependencies )
        {
            if ( dependency.equals( artifact ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the excludeTransitive.
     */
    public boolean isExcludeTransitive()
    {
        return this.excludeTransitive;
    }

    /**
     * @param excludeTransitive The excludeTransitive to set.
     */
    public void setExcludeTransitive( boolean excludeTransitive )
    {
        this.excludeTransitive = excludeTransitive;
    }
}
