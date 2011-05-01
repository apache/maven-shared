package org.apache.maven.reporting.exec;

import com.google.common.collect.Lists;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olivier Lamy
 */
public class TestDefaultMavenReportExecutor
    extends PlexusTestCase
{

    MavenExecutionRequest request = null;

    ArtifactRepository localArtifactRepository;

    public void testSimpleLookup()
        throws Exception
    {
        MavenReportExecutor mavenReportExecutor = lookup( MavenReportExecutor.class );
        assertNotNull( mavenReportExecutor );
    }

    public void testSimpleBuildReports()
        throws Exception
    {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        ClassRealm realm = getContainer().getContainerRealm();
        System.out.println( "realm " + Arrays.asList( realm.getURLs() ) );
        Thread.currentThread().setContextClassLoader( realm );
        try
        {
            MavenReportExecutorRequest mavenReportExecutorRequest = new MavenReportExecutorRequest();

            mavenReportExecutorRequest.setLocalRepository( getLocalArtifactRepository() );

            MavenProject mavenProject = getMavenProject();

            mavenReportExecutorRequest.setProject( mavenProject );

            MavenSession mavenSession = getMavenSession( getLocalArtifactRepository(), mavenProject );
            mavenSession.setCurrentProject( mavenProject );
            mavenSession.setProjects( Lists.<MavenProject>newArrayList( mavenProject ) );
            mavenReportExecutorRequest.setMavenSession( mavenSession );

            ReportPlugin reportPlugin = new ReportPlugin();
            reportPlugin.setGroupId( "org.apache.maven.plugins" );
            reportPlugin.setArtifactId( "maven-javadoc-plugin" );
            reportPlugin.setVersion( "2.7" );

            ReportSet reportSet = new ReportSet();
            reportSet.getReports().add( "javadoc" );
            reportSet.getReports().add( "test-javadoc" );
            reportPlugin.getReportSets().add( reportSet );

            List<ReportPlugin> reportPlugins = Lists.newArrayList( reportPlugin );

            mavenReportExecutorRequest.setReportPlugins( reportPlugins.toArray( new ReportPlugin[1] ) );

            MavenReportExecutor mavenReportExecutor = lookup( MavenReportExecutor.class );

            List<MavenReportExecution> mavenReportExecutions =
                mavenReportExecutor.buildMavenReports( mavenReportExecutorRequest );

            assertNotNull( mavenReportExecutions );
            assertEquals( 2, mavenReportExecutions.size() );
            assertEquals( "apidocs/index", mavenReportExecutions.get( 0 ).getMavenReport().getOutputName() );
            assertEquals( "testapidocs/index", mavenReportExecutions.get( 1 ).getMavenReport().getOutputName() );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( orig );
        }
    }

    protected MavenSession getMavenSession( ArtifactRepository localRepository, final MavenProject mavenProject )
        throws Exception
    {
        request = new DefaultMavenExecutionRequest();
        request.setLocalRepository( localRepository );

        request.setWorkspaceReader( new WorkspaceReader()
        {
            public WorkspaceRepository getRepository()
            {
                return new WorkspaceRepository();
            }

            public File findArtifact( Artifact artifact )
            {
                return null;
            }

            public List<String> findVersions( Artifact artifact )
            {
                return Collections.emptyList();
            }
        } );
        final Settings settings = getSettings();

        getContainer().lookup( MavenExecutionRequestPopulator.class ).populateFromSettings( request, settings );

        getContainer().lookup( MavenExecutionRequestPopulator.class ).populateDefaults( request );

        request.setLocalRepository( getLocalArtifactRepository() );
        request.setLocalRepositoryPath( getLocalArtifactRepository().getBasedir() );
        request.setCacheNotFound( false );

        request.setLoggingLevel( MavenExecutionRequest.LOGGING_LEVEL_DEBUG );
        getContainer().lookup( Logger.class ).setThreshold( 0 );

        request.setSystemProperties( System.getProperties() );

        MavenExecutionResult result = new DefaultMavenExecutionResult();

        RepositorySystemSession repositorySystemSession = buildRepositorySystemSession( request );

        MavenSession mavenSession = new MavenSession( getContainer(), repositorySystemSession, request, result )
        {
            @Override
            public MavenProject getTopLevelProject()
            {
                return mavenProject;
            }

            @Override
            public Settings getSettings()
            {
                return settings;
            }

            @Override
            public List<MavenProject> getProjects()
            {
                return Lists.newArrayList( mavenProject );
            }

            @Override
            public MavenProject getCurrentProject()
            {
                return mavenProject;
            }

        };
        return mavenSession;
    }

    private ArtifactRepository getLocalArtifactRepository()
        throws Exception
    {
        if ( localArtifactRepository != null )
        {
            return localArtifactRepository;
        }
        String localRepoPath =
            System.getProperty( "localRepository", MavenCli.userMavenConfigurationHome.getPath() + "/repository" );

        localArtifactRepository = lookup( RepositorySystem.class ).createLocalRepository( new File( localRepoPath ) );
        return localArtifactRepository;
    }

    public Settings getSettings()
        throws ComponentLookupException, SettingsBuildingException
    {

        SettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest();

        settingsBuildingRequest.setGlobalSettingsFile( MavenCli.DEFAULT_GLOBAL_SETTINGS_FILE );

        settingsBuildingRequest.setUserSettingsFile( MavenCli.DEFAULT_USER_SETTINGS_FILE );

        settingsBuildingRequest.getSystemProperties().putAll( System.getProperties() );

        Settings settings =
            getContainer().lookup( SettingsBuilder.class ).build( settingsBuildingRequest ).getEffectiveSettings();

        return settings;

    }

    protected MavenProject getMavenProject()
    {
        MavenProjectStub mavenProjectStub = new MavenProjectStub()
        {
            @Override
            public List<RemoteRepository> getRemotePluginRepositories()
            {
                if ( super.getRemotePluginRepositories() == null )
                {
                    return RepositoryUtils.toRepos( request.getRemoteRepositories() );
                }
                return super.getRemotePluginRepositories();
            }

            @Override
            public List<RemoteRepository> getRemoteArtifactRepositories()
            {
                if ( super.getRemotePluginRepositories() == null )
                {
                    return RepositoryUtils.toRepos( request.getRemoteRepositories() );
                }
                return super.getRemotePluginRepositories();
            }

            @Override
            public String getName()
            {
                return "foo";
            }

            @Override
            public String getVersion()
            {
                return "1.0-SNAPSHOT";
            }

            @Override
            public boolean isExecutionRoot()
            {
                return true;
            }

            @Override
            public List<String> getCompileSourceRoots()
            {
                return Lists.newArrayList( "src/main/java" );
            }

            @Override
            public List<String> getTestCompileSourceRoots()
            {
                return Lists.newArrayList( "src/test/java" );
            }
        };

        mavenProjectStub.setPackaging( "jar" );

        Build build = new Build();

        build.setOutputDirectory( "target" );

        build.setSourceDirectory( "src/main/java" );

        build.setTestSourceDirectory( "src/test/java" );

        mavenProjectStub.setBuild( build );

        return mavenProjectStub;
    }

    private RepositorySystemSession buildRepositorySystemSession( MavenExecutionRequest request )
        throws ComponentLookupException
    {
        DefaultMaven defaultMaven = (DefaultMaven) getContainer().lookup( Maven.class );

        return defaultMaven.newRepositorySession( request );
    }

}
