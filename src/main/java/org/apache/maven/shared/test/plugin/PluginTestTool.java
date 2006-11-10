package org.apache.maven.shared.test.plugin;

import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * @plexus.component role="org.apache.maven.shared.test.plugin.PluginTestTool" role-hint="default"
 * @author jdcasey
 *
 */
public class PluginTestTool
{
    public static final String ROLE = PluginTestTool.class.getName();

    /**
     * @plexus.requirement role-hint="default"
     */
    private ProjectTool projectTool;

    /**
     * @plexus.requirement role-hint="default"
     */
    private RepositoryTool repositoryTool;

    public File preparePluginForIntegrationTesting( String testVersion )
        throws TestToolsException
    {
        return prepareForTesting( testVersion, false, null );
    }

    public File preparePluginForUnitTestingWithMavenBuilds( String testVersion )
        throws TestToolsException
    {
        return prepareForTesting( testVersion, true, null );
    }

    public File preparePluginForIntegrationTesting( String testVersion, File localRepositoryDir )
        throws TestToolsException
    {
        return prepareForTesting( testVersion, false, localRepositoryDir );
    }

    public File preparePluginForUnitTestingWithMavenBuilds( String testVersion, File localRepositoryDir )
        throws TestToolsException
    {
        return prepareForTesting( testVersion, true, localRepositoryDir );
    }

    private File prepareForTesting( String testVersion, boolean skipUnitTests, File localRepositoryDir )
        throws TestToolsException
    {
        File pomFile = new File( "pom.xml" );
        File buildLog = new File( "target/test-build-logs/setup.build.log" );
        File localRepoDir = localRepositoryDir;
        
        if ( localRepoDir == null )
        {
            localRepoDir = new File( "target/test-local-repository" );
        }

        MavenProject project = projectTool.packageProjectArtifact( pomFile, testVersion, skipUnitTests, buildLog );
        repositoryTool.createLocalRepositoryFromPlugin( project, localRepoDir );

        return localRepoDir;
    }

}
