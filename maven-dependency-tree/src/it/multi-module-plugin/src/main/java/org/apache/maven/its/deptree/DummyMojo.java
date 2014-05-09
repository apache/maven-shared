package org.apache.maven.its.deptree;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Does absolutely nothing.
 *
 * @goal dummy
 * @phase package
 * @requiresDependencyResolution compile
 */
public class DummyMojo extends AbstractMojo
{
    /**
     * The maven project.
     *
     * @parameter property="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Running DummyMojo : " + project.getArtifactId() );
    }
}
