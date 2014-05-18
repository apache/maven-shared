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
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.shared.dependency.tree.traversal.CollectingDependencyNodeVisitor;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * Default implementation of <code>DependencyTreeBuilder</code>.
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see DependencyTreeBuilder
 */
@Component( role = DependencyTreeBuilder.class )
public class DefaultDependencyTreeBuilder
    extends AbstractLogEnabled
    implements DependencyTreeBuilder
{
    @Requirement
    private ArtifactFactory factory;

    @Requirement
    private ArtifactMetadataSource metadataSource;

    /**
     * Artifact collector component.
     */
    @Requirement
    private ArtifactCollector collector;

    // fields -----------------------------------------------------------------

    private ArtifactResolutionResult result;

    // DependencyTreeBuilder methods ------------------------------------------

    /**
     * {@inheritDoc}
     * 
     * @deprecated
     */
    public DependencyTree buildDependencyTree( MavenProject project, ArtifactRepository repository,
                                               ArtifactFactory factory, ArtifactMetadataSource metadataSource,
                                               ArtifactCollector collector )
        throws DependencyTreeBuilderException
    {
        DependencyNode rootNode = buildDependencyTree( project, repository, factory, metadataSource, null, collector );

        CollectingDependencyNodeVisitor collectingVisitor = new CollectingDependencyNodeVisitor();
        rootNode.accept( collectingVisitor );

        return new DependencyTree( rootNode, collectingVisitor.getNodes() );
    }

    /**
     * {@inheritDoc}
     */
    public DependencyNode buildDependencyTree( MavenProject project, ArtifactRepository repository,
                                               ArtifactFactory factory, ArtifactMetadataSource metadataSource,
                                               ArtifactFilter filter, ArtifactCollector collector )
        throws DependencyTreeBuilderException
    {
        DependencyTreeResolutionListener listener = new DependencyTreeResolutionListener( getLogger() );

        try
        {
            @SuppressWarnings( "unchecked" )
            Map<String, Artifact> managedVersions = project.getManagedVersionMap();

            @SuppressWarnings( "unchecked" )
            Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();

            if ( dependencyArtifacts == null )
            {
                dependencyArtifacts = project.createArtifacts( factory, null, null );
            }

            getLogger().debug( "Dependency tree resolution listener events:" );

            // TODO: note that filter does not get applied due to MNG-3236

            result =
                collector.collect( dependencyArtifacts, project.getArtifact(), managedVersions, repository,
                                   project.getRemoteArtifactRepositories(), metadataSource, filter,
                                   Collections.singletonList( (ResolutionListener) listener ) );

            return listener.getRootNode();
        }
        catch ( ArtifactResolutionException exception )
        {
            throw new DependencyTreeBuilderException( "Cannot build project dependency tree", exception );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new DependencyTreeBuilderException( "Invalid dependency version for artifact "
                + project.getArtifact() );
        }
    }

    public DependencyNode buildDependencyTree( MavenProject project )
        throws DependencyTreeBuilderException
    {
        return buildDependencyTree( project, project.getProjectBuilderConfiguration().getLocalRepository(), factory,
                                    metadataSource, null, collector );
    }

    public DependencyNode buildDependencyTree( MavenProject project, ArtifactRepository repository,
                                               ArtifactFilter filter )
        throws DependencyTreeBuilderException
    {
        return buildDependencyTree( project, repository, factory, metadataSource, filter, collector );
    }

    // protected methods ------------------------------------------------------

    protected ArtifactResolutionResult getArtifactResolutionResult()
    {
        return result;
    }
}
