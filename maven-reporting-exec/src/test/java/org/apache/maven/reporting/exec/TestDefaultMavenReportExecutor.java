package org.apache.maven.reporting.exec;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
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
        ReportSet reportSet = new ReportSet();
        reportSet.getReports().add( "test-javadoc" );
        reportSet.getReports().add( "javadoc" );

        MavenProject mavenProject = getMavenProject();
        List<MavenReportExecution> mavenReportExecutions = buildReports( mavenProject, reportSet );

        assertNotNull( mavenReportExecutions );
        assertEquals( 2, mavenReportExecutions.size() );
        assertEquals( "testapidocs/index", mavenReportExecutions.get( 0 ).getMavenReport().getOutputName() );
        assertEquals( "apidocs/index", mavenReportExecutions.get( 1 ).getMavenReport().getOutputName() );
    }

    public void testMultipleReportSets()
        throws Exception
    {
        ReportSet reportSet = new ReportSet();
        reportSet.getReports().add( "javadoc" );
        ReportSet reportSet2 = new ReportSet();
        reportSet2.getReports().add( "test-javadoc" );
        reportSet2.getReports().add( "javadoc" );

        MavenProject mavenProject = getMavenProject();
        List<MavenReportExecution> mavenReportExecutions = buildReports( mavenProject, reportSet, reportSet2 );

        assertNotNull( mavenReportExecutions );
        assertEquals( 3, mavenReportExecutions.size() );
        assertEquals( "apidocs/index", mavenReportExecutions.get( 0 ).getMavenReport().getOutputName() );
        assertEquals( "testapidocs/index", mavenReportExecutions.get( 1 ).getMavenReport().getOutputName() );
        assertEquals( "apidocs/index", mavenReportExecutions.get( 2 ).getMavenReport().getOutputName() );
    }

    public void testReportingPluginWithDependenciesInPluginManagement()
        throws Exception
    {
        ReportSet reportSet = new ReportSet();
        reportSet.getReports().add( "javadoc" );

        MavenProject mavenProject = getMavenProject();
        Plugin plugin = new Plugin();
        plugin.setGroupId( "org.apache.maven.plugins" );
        plugin.setArtifactId( "maven-javadoc-plugin" );
        plugin.setVersion( "3.0.0-M1" );
        Dependency dependency = new Dependency();
        dependency.setGroupId( "commons-lang" );
        dependency.setArtifactId( "commons-lang" );
        dependency.setVersion( "2.6" );
        plugin.getDependencies().add( dependency );
        mavenProject.getBuild().setPluginManagement( new PluginManagement() );
        mavenProject.getBuild().getPluginManagement().addPlugin( plugin );
        List<MavenReportExecution> mavenReportExecutions = buildReports( mavenProject, reportSet );

        assertNotNull( mavenReportExecutions );
        assertEquals( 1, mavenReportExecutions.size() );
        List<Dependency> dependencies = mavenReportExecutions.get( 0 ).getPlugin().getDependencies();
        assertEquals( 1, dependencies.size() );
        assertEquals( "commons-lang", dependencies.get( 0 ).getGroupId() );
        assertEquals( "2.6", dependencies.get( 0 ).getVersion() );
    }

    private List<MavenReportExecution> buildReports( MavenProject mavenProject, ReportSet... javadocReportSets )
        throws Exception
    {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        ClassRealm realm = getContainer().getContainerRealm();

        Thread.currentThread().setContextClassLoader( realm );
        try
        {
            MavenReportExecutorRequest mavenReportExecutorRequest = new MavenReportExecutorRequest();

            mavenReportExecutorRequest.setLocalRepository( getLocalArtifactRepository() );

            mavenReportExecutorRequest.setProject( mavenProject );

            MavenSession mavenSession = getMavenSession( getLocalArtifactRepository(), mavenProject );
            mavenSession.setCurrentProject( mavenProject );
            mavenSession.setProjects( Lists.<MavenProject>newArrayList( mavenProject ) );
            mavenReportExecutorRequest.setMavenSession( mavenSession );

            ReportPlugin reportPlugin = new ReportPlugin();
            reportPlugin.setGroupId( "org.apache.maven.plugins" );
            reportPlugin.setArtifactId( "maven-javadoc-plugin" );
            reportPlugin.setVersion( "3.0.0-M1" );

            for ( ReportSet reportSet : javadocReportSets )
            {
                reportPlugin.getReportSets().add( reportSet );
            }

            List<ReportPlugin> reportPlugins = Lists.newArrayList( reportPlugin );

            mavenReportExecutorRequest.setReportPlugins( reportPlugins.toArray( new ReportPlugin[1] ) );

            MavenReportExecutor mavenReportExecutor = lookup( MavenReportExecutor.class );

            return mavenReportExecutor.buildMavenReports( mavenReportExecutorRequest );
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

        request.setLoggingLevel( MavenExecutionRequest.LOGGING_LEVEL_INFO );
        getContainer().lookup( Logger.class ).setThreshold( 1 );

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
            public List<ArtifactRepository> getRemoteArtifactRepositories()
            {
                if ( super.getRemotePluginRepositories() == null )
                {
                    return request.getRemoteRepositories();
                }
                return super.getRemoteArtifactRepositories();
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
