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
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Default dependency graph builder that detects current Maven version to delegate to either
 * Maven 2 or Maven 3 specific code.
 *
 * @see Maven2DependencyGraphBuilder
 * @see Maven3DependencyGraphBuilder
 * @author Hervé Boutemy
 * @since 2.0
 */
@Component( role = DependencyGraphBuilder.class )
public class DefaultDependencyGraphBuilder
    extends AbstractLogEnabled
    implements DependencyGraphBuilder, Contextualizable
{
    protected PlexusContainer container;

    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter )
        throws DependencyGraphBuilderException
    {
        try
        {
            String hint = isMaven31() ? "maven31" : isMaven2x() ? "maven2" : "maven3";
            getLogger().debug( "building " + hint + " dependency graph for " + project.getId() );

            DependencyGraphBuilder effectiveGraphBuilder =
                (DependencyGraphBuilder) container.lookup( DependencyGraphBuilder.class.getCanonicalName(), hint );

            return effectiveGraphBuilder.buildDependencyGraph( project, filter );
        }
        catch ( ComponentLookupException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
    }

    /**
     * Check the current Maven version to see if it's Maven 2.x.
     */
    protected static boolean isMaven2x()
    {
        try
        {
            Class.forName( "org.apache.maven.project.DependencyResolutionRequest" ); // Maven 3 specific

            return false;
        }
        catch ( ClassNotFoundException e )
        {
            return true;
        }
    }

    /**
     * Check the current Maven version to see if it's Maven 3.1.
     */
    protected static boolean isMaven31()
    {
        try
        {
            Class.forName( "org.eclipse.aether.artifact.Artifact" ); // Maven 3.1 specific

            return false;
        }
        catch ( ClassNotFoundException e )
        {
            return true;
        }
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
