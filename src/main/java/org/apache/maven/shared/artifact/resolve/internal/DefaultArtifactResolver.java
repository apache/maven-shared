package org.apache.maven.shared.artifact.resolve.internal;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.ArtifactCoordinate;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * 
 */
@Component( role = ArtifactResolver.class, hint = "default" )
public class DefaultArtifactResolver
    implements ArtifactResolver, Contextualizable
{
    private PlexusContainer container;

    @Override
    public ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest, Artifact mavenArtifact )
        throws ArtifactResolverException
    {
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            ArtifactResolver effectiveArtifactResolver = container.lookup( ArtifactResolver.class, hint );

            return effectiveArtifactResolver.resolveArtifact( buildingRequest, mavenArtifact );
        }
        catch ( ComponentLookupException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
    }

    @Override
    public ArtifactResult resolveArtifact( ProjectBuildingRequest buildingRequest, ArtifactCoordinate coordinate )
        throws ArtifactResolverException
    {
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            ArtifactResolver effectiveArtifactResolver = container.lookup( ArtifactResolver.class, hint );

            return effectiveArtifactResolver.resolveArtifact( buildingRequest, coordinate );
        }
        catch ( ComponentLookupException e )
        {
            throw new ArtifactResolverException( e.getMessage(), e );
        }
    }

//    @Override
//    public Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
//                                                         Collection<Dependency> coordinates,
//                                                         Collection<Dependency> managedDependencies,
//                                                         TransformableFilter filter )
//                                                             throws ArtifactResolverException
//    {
//        try
//        {
//            String hint = isMaven31() ? "maven31" : "maven3";
//
//            ArtifactResolver effectiveArtifactResolver = container.lookup( ArtifactResolver.class, hint );
//
//            return effectiveArtifactResolver.resolveDependencies( buildingRequest, coordinates, null, filter );
//        }
//        catch ( ComponentLookupException e )
//        {
//            throw new ArtifactResolverException( e.getMessage(), e );
//        }
//    }
//
//    @Override
//    public Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
//                                                         ArtifactCoordinate coordinate, TransformableFilter filter )
//                                                             throws ArtifactResolverException
//    {
//        try
//        {
//            String hint = isMaven31() ? "maven31" : "maven3";
//
//            ArtifactResolver effectiveArtifactResolver = container.lookup( ArtifactResolver.class, hint );
//
//            return effectiveArtifactResolver.resolveDependencies( buildingRequest, coordinate, filter );
//        }
//        catch ( ComponentLookupException e )
//        {
//            throw new ArtifactResolverException( e.getMessage(), e );
//        }
//    }

    /**
     * @return true if the current Maven version is Maven 3.1.
     */
    protected static boolean isMaven31()
    {
        return canFindCoreClass( "org.eclipse.aether.artifact.Artifact" ); // Maven 3.1 specific
    }

    private static boolean canFindCoreClass( String className )
    {
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( className );

            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    /**
     * Injects the Plexus content.
     *
     * @param context Plexus context to inject.
     * @throws ContextException if the PlexusContainer could not be located.
     */
    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
