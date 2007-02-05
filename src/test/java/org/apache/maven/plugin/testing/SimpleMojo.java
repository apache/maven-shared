package org.apache.maven.plugin.testing;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class SimpleMojo
    extends AbstractMojo
{
    private String keyOne;

    private String keyTwo;

    public String getKeyOne()
    {
        return keyOne;
    }

    public String getKeyTwo()
    {
        return keyTwo;
    }

    public void execute()
        throws MojoExecutionException
    {
    }
}
