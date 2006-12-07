package org.apache.maven.shared.dependency.tree;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

/**
 * Default implementation of <code>DependencyTreeBuilder</code>.
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.shared.dependency.tree.DependencyTreeBuilder"
 * @see DependencyTreeBuilder
 */
public class DefaultDependencyTreeBuilder implements DependencyTreeBuilder
{
    // DependencyTreeAssembler methods ----------------------------------------

    /*
     * @see org.apache.maven.shared.dependency.tree.DependencyTreeBuilder#buildDependencyTree(org.apache.maven.project.MavenProject,
     *      org.apache.maven.artifact.repository.ArtifactRepository, org.apache.maven.artifact.factory.ArtifactFactory,
     *      org.apache.maven.artifact.metadata.ArtifactMetadataSource,
     *      org.apache.maven.artifact.resolver.ArtifactCollector)
     */
    public DependencyTree buildDependencyTree( MavenProject project, ArtifactRepository repository,
                                               ArtifactFactory factory, ArtifactMetadataSource metadataSource,
                                               ArtifactCollector collector ) throws DependencyTreeBuilderException
    {
        DependencyTreeResolutionListener listener = new DependencyTreeResolutionListener();

        try
        {
            Map managedVersions = getManagedVersionMap( project, factory );

            // TODO site:run Why do we need to resolve this...
            if ( project.getDependencyArtifacts() == null )
            {
                project.setDependencyArtifacts( project.createArtifacts( factory, null, null ) );
            }

            collector.collect( project.getDependencyArtifacts(), project.getArtifact(), managedVersions, repository,
                               project.getRemoteArtifactRepositories(), metadataSource, null,
                               Collections.singletonList( listener ) );

            return new DependencyTree( listener.getRootNode(), listener.getNodes() );
        }
        catch ( ProjectBuildingException exception )
        {
            throw new DependencyTreeBuilderException( "Cannot build project dependency tree", exception );
        }
        catch ( InvalidDependencyVersionException exception )
        {
            throw new DependencyTreeBuilderException( "Cannot build project dependency tree", exception );
        }
        catch ( ArtifactResolutionException exception )
        {
            throw new DependencyTreeBuilderException( "Cannot build project dependency tree", exception );
        }
    }

    // private methods --------------------------------------------------------

    private Map getManagedVersionMap( MavenProject project, ArtifactFactory factory ) throws ProjectBuildingException
    {
        DependencyManagement dependencyManagement = project.getDependencyManagement();
        Map managedVersionMap;

        if ( dependencyManagement != null && dependencyManagement.getDependencies() != null )
        {
            managedVersionMap = new HashMap();

            for ( Iterator iterator = dependencyManagement.getDependencies().iterator(); iterator.hasNext(); )
            {
                Dependency dependency = (Dependency) iterator.next();

                try
                {
                    VersionRange versionRange = VersionRange.createFromVersionSpec( dependency.getVersion() );

                    Artifact artifact =
                        factory.createDependencyArtifact( dependency.getGroupId(), dependency.getArtifactId(),
                                                          versionRange, dependency.getType(),
                                                          dependency.getClassifier(), dependency.getScope() );

                    managedVersionMap.put( dependency.getManagementKey(), artifact );
                }
                catch ( InvalidVersionSpecificationException exception )
                {
                    throw new ProjectBuildingException( project.getId(), "Unable to parse version '"
                                    + dependency.getVersion() + "' for dependency '" + dependency.getManagementKey()
                                    + "': " + exception.getMessage(), exception );
                }
            }
        }
        else
        {
            managedVersionMap = Collections.EMPTY_MAP;
        }

        return managedVersionMap;
    }
}
