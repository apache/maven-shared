package org.apache.maven.shared.dependency.graph;

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
