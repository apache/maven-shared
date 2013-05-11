package org.apache.maven.shared.project.utils.it;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.project.utils.ProjectUtils;

@Mojo( name="resolve-project" )
public class ResolveProjectMojo
    extends AbstractMojo
{
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;

    public void execute()
    {
        getLog().info( project.getId() + " is root project: " + ProjectUtils.isRootProject( project ) );
        getLog().info( project.getId() + " is aggregator: " + ProjectUtils.isAggregator( project ) );
    }
}
