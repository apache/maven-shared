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
 
import org.apache.maven.project.MavenProject;

/**
 * Generates the key used by {@link org.apache.maven.project.MavenProject#getProjectReferences()}
 * , {@link org.apache.maven.execution.MavenSession#getProjectMap()} and the Map passed into
 * {@link org.apache.maven.shared.dependency.graph.DependencyGraphBuilder#buildDependencyGraph
 * (org.apache.maven.project.MavenProject, org.apache.maven.artifact.resolver.filter.ArtifactFilter, java.util.Map)}.
 */
public final class ProjectReferenceKeyGenerator
{
    // NB Copied here from MavenProject - because MavenProject#getProjectReferenceId is private
    public String getProjectReferenceKey( String groupId, String artifactId, String version )
    {
        final StringBuilder buffer = new StringBuilder( 128 );
        buffer.append( groupId ).append( ':' ).append( artifactId ).append( ':' ).append( version );
        return buffer.toString();
    }

    public String getProjectReferenceKey( MavenProject project )
    {
        return getProjectReferenceKey( project.getGroupId(), project.getArtifactId(), project.getVersion() );
    }
}
