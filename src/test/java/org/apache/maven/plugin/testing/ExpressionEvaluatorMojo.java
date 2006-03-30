package org.apache.maven.plugin.testing;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Edwin Punzalan
 */
public class ExpressionEvaluatorMojo
    extends AbstractMojo
{
    private String basedir;

    private ArtifactRepository localRepository;

    private String workdir;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( StringUtils.isEmpty( basedir ) )
        {
            throw new MojoExecutionException( "basedir was not injected." );
        }

        if ( localRepository == null )
        {
            throw new MojoExecutionException( "localRepository was not injected." );
        }

        if ( StringUtils.isEmpty( workdir ) )
        {
            throw new MojoExecutionException( "workdir was not injected." );
        }
        else if ( !workdir.startsWith( basedir ) )
        {
            throw new MojoExecutionException( "workdir does not start with basedir." );
        }
    }
}
