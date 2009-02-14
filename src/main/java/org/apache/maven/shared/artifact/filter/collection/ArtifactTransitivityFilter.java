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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

/**
 * This filter will exclude everything that is not a dependency of the selected artifact.
 * 
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id$
 */
public class ArtifactTransitivityFilter
    extends AbstractArtifactsFilter
{

    Collection transitiveArtifacts;

    ArtifactFactory factory;

    ArtifactRepository local;

    List remote;

    public ArtifactTransitivityFilter( Artifact artifact, ArtifactFactory factory, ArtifactRepository local,
                                       List remote, MavenProjectBuilder builder )
        throws ProjectBuildingException, InvalidDependencyVersionException
    {
        this.factory = factory;
        this.local = local;
        this.remote = remote;

        Artifact rootArtifactPom =
            factory.createArtifact( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), "", "pom" );

        MavenProject rootArtifactProject = builder.buildFromRepository( rootArtifactPom, remote, local );

        // load all the artifacts.
        transitiveArtifacts =
            rootArtifactProject.createArtifacts( this.factory, Artifact.SCOPE_TEST,
                                                 new ScopeArtifactFilter( Artifact.SCOPE_TEST ) );

    }

    public ArtifactTransitivityFilter( Dependency dependency, ArtifactFactory factory, ArtifactRepository local,
                                       List remote, MavenProjectBuilder builder )
        throws ProjectBuildingException, InvalidDependencyVersionException
    {

        this.factory = factory;
        this.local = local;
        this.remote = remote;

        Artifact rootArtifactPom =
            factory.createArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "",
                                    "pom" );

        MavenProject rootArtifactProject = builder.buildFromRepository( rootArtifactPom, remote, local );

        // load all the artifacts.
        transitiveArtifacts =
            rootArtifactProject.createArtifacts( this.factory, Artifact.SCOPE_TEST,
                                                 new ScopeArtifactFilter( Artifact.SCOPE_TEST ) );

    }

    public Set filter( Set artifacts )
    {

        Set result = new HashSet();
        Iterator iterator = artifacts.iterator();
        while ( iterator.hasNext() )
        {
            Artifact artifact = (Artifact) iterator.next();
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
        boolean result = false;
        Iterator iterator = transitiveArtifacts.iterator();
        while ( iterator.hasNext() )
        {
            Artifact trans = (Artifact) iterator.next();
            if ( trans.getGroupId().equals( artifact.getGroupId() ) &&
                trans.getArtifactId().equals( artifact.getArtifactId() ) )
            {
                result = true;
                break;
            }
        }
        return result;
    }
}
