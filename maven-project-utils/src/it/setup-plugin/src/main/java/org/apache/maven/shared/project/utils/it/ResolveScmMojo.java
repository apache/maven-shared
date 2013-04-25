package org.apache.maven.shared.project.utils.it;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.project.utils.ScmUtils;
import org.apache.maven.shared.project.utils.SiteUtils;

@Mojo( name="resolve-scm" )
public class ResolveScmMojo
    extends AbstractMojo
{
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Resolved scm connection for " + project.getId() + ": " + ScmUtils.resolveScmConnection( project ) );
        getLog().info( "Resolved scm developer connection for " + project.getId() + ": " + ScmUtils.resolveScmDeveloperConnection( project ) );
    }
}
