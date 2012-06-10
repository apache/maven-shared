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

import java.lang.reflect.InvocationTargetException;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Wrapper around Maven 3 dependency resolver.
 *
 * @see ProjectDependenciesResolver
 * @author Hervé Boutemy
 * @since 2.0
 */
@Component( role = DependencyGraphBuilder.class, hint = "maven3" )
public class Maven3DependencyGraphBuilder
    implements DependencyGraphBuilder
{
    @Requirement
    private ProjectDependenciesResolver resolver;

    @Requirement
    private ArtifactFactory factory;

    public DependencyNode buildDependencyGraph( MavenProject project, ArtifactFilter filter )
        throws DependencyGraphBuilderException
    {
        try
        {
            ProjectBuildingRequest projectBuildingRequest =
                (ProjectBuildingRequest) invoke( project, "getProjectBuildingRequest" );

            DependencyResolutionRequest request =
                new DefaultDependencyResolutionRequest( project, projectBuildingRequest.getRepositorySession() );

            DependencyResolutionResult result = resolver.resolve( request );

            return new Maven3DependencyNode( factory, result.getDependencyGraph(), project.getArtifact(), filter );
        }
        catch ( DependencyResolutionException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
        catch ( IllegalAccessException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
        catch ( InvocationTargetException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new DependencyGraphBuilderException( e.getMessage(), e );
        }
    }

    private Object invoke( Object object, String method )
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return object.getClass().getMethod( method ).invoke( object );
    }
}
