package org.apache.maven.shared.project.utils.it;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.project.utils.SiteUtils;

@Mojo( name="resolve-site" )
public class ResolveSiteMojo
    extends AbstractMojo
{
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;

    public void execute()
    {
        getLog().info( "Resolved distributionManagement site URL for " + project.getId() + ": " + SiteUtils.resolveDistributionManagementSiteUrl( project ) );
    }
}
