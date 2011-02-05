package org.apache.maven.shared.repository;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.shared.repository.model.DefaultRepositoryInfo;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;

public class DefaultRepositoryAssemblerTest
    extends PlexusTestCase
{

    private MavenProjectBuilder projectBuilder;

    private ArtifactRepositoryLayout defaultLayout;

    private ArtifactRepositoryFactory repoFactory;

    private ArtifactFactory artifactFactory;

    private ArtifactRepository localRepository;

    private ArtifactResolver artifactResolver;

    private ArtifactMetadataSource metadataSource;

    private DefaultProfileManager profileManager;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.class.getName() );

        defaultLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.class.getName(), "default" );
        repoFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.class.getName() );
        artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.class.getName() );
        artifactResolver = (ArtifactResolver) lookup( ArtifactResolver.class.getName() );
        metadataSource = (ArtifactMetadataSource) lookup( ArtifactMetadataSource.class.getName() );

        File localRepo = new File( getBasedir(), "target/local-repository" );

        localRepository = repoFactory.createArtifactRepository( "local", localRepo.getAbsolutePath(), defaultLayout,
                                                                null, null );

    }

    private MavenProject getProject( String projectResource, String parentGroupId, String parentArtifactId,
                                     String parentVersion, boolean preCacheParent )
        throws ProjectBuildingException, IOException, InvalidDependencyVersionException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL res = cloader.getResource( "projects/" + projectResource );

        File projectFile = new File( URLDecoder.decode( res.getPath(), "UTF-8" ) ).getAbsoluteFile();

        if ( preCacheParent )
        {
            // pre-load the parent model...this is a hack!
            Artifact parentArtifact = artifactFactory.createParentArtifact( parentGroupId, parentArtifactId,
                                                                            parentVersion );
            projectBuilder.buildFromRepository( parentArtifact, Collections.EMPTY_LIST, localRepository );
        }

        MavenProject project = projectBuilder.build( projectFile, localRepository, getProfileManager() );

        project.setDependencyArtifacts( project.createArtifacts( artifactFactory, null, null ) );

        return project;
    }

    private ProfileManager getProfileManager()
        throws IOException
    {
        if ( profileManager == null )
        {
            File repoDir = getTestRemoteRepositoryBasedir();

            Profile profile = new Profile();
            Repository repo = new Repository();
            repo.setId( "test.repo" );
            repo.setUrl( repoDir.toURL().toExternalForm() );

            repo.setReleases( new RepositoryPolicy() );
            repo.setSnapshots( new RepositoryPolicy() );

            profile.addRepository( repo );

            profileManager = new DefaultProfileManager( getContainer() );
            profileManager.addProfile( profile );
            profileManager.explicitlyActivate( "test.repo" );
        }

        return profileManager;
    }

    private File getTestRemoteRepositoryBasedir()
        throws IOException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL res = cloader.getResource( "marker.txt" );

        File markerFile = new File( URLDecoder.decode( res.getPath(), "UTF-8" ) );
        markerFile = markerFile.getCanonicalFile();

        File repoDir = new File( markerFile.getParentFile(), "remote-repository" );

        return repoDir;
    }

    public void test_MASSEMBLY_210_projectParentIsIncludedInRepository()
        throws ProjectBuildingException, RepositoryAssemblyException, IOException, InvalidDependencyVersionException
    {
        File repoDir = getTestRemoteRepositoryBasedir();

        ArtifactRepository localRepository = repoFactory.createArtifactRepository( "local", repoDir.getAbsoluteFile()
                                                                                                   .toURL()
                                                                                                   .toExternalForm(),
                                                                                   defaultLayout, null, null );

        MavenProject project = getProject( "massembly-210-direct-parent/pom.xml", "massembly.210", "parent",
                                           "1.0-SNAPSHOT", true );

        TestRepositoryBuilderConfigSource cs = new TestRepositoryBuilderConfigSource();
        cs.setProject( project );
        cs.setLocalRepository( localRepository );

        DefaultRepositoryAssembler assembler = new DefaultRepositoryAssembler( artifactFactory, artifactResolver,
                                                                               defaultLayout, repoFactory,
                                                                               metadataSource, projectBuilder );

        File repositoryDirectory = new File( getBasedir(), "target/test-repositories/massembly-210-direct-parent" );

        DefaultRepositoryInfo repoInfo = new DefaultRepositoryInfo();

        assembler.buildRemoteRepository( repositoryDirectory, repoInfo, cs );

        File parentFile = new File( repositoryDirectory, "massembly/210/parent/1.0-SNAPSHOT/parent-1.0-SNAPSHOT.pom" );

        assertTrue( parentFile.exists() );
    }

    public void test_MASSEMBLY_210_projectParentIsNotInRepository()
        throws ProjectBuildingException, RepositoryAssemblyException, IOException, InvalidDependencyVersionException
    {
        File repoDir = getTestRemoteRepositoryBasedir();

        ArtifactRepository localRepository = repoFactory.createArtifactRepository( "local", repoDir.getAbsoluteFile()
                                                                                                   .toURL()
                                                                                                   .toExternalForm(),
                                                                                   defaultLayout, null, null );

        MavenProject project = getProject( "massembly-210-direct-parent-on-fs/project/pom.xml", null, null, null, false );

        TestRepositoryBuilderConfigSource cs = new TestRepositoryBuilderConfigSource();
        cs.setProject( project );
        cs.setLocalRepository( localRepository );

        DefaultRepositoryAssembler assembler = new DefaultRepositoryAssembler( artifactFactory, artifactResolver,
                                                                               defaultLayout, repoFactory,
                                                                               metadataSource, projectBuilder );

        File repositoryDirectory = new File( getBasedir(), "target/test-repositories/massembly-210-direct-parent-on-fs" );

        DefaultRepositoryInfo repoInfo = new DefaultRepositoryInfo();

        assembler.buildRemoteRepository( repositoryDirectory, repoInfo, cs );

        File parentFile = new File( repositoryDirectory,
                                    "massembly/210/parent-on-fs/1.0-SNAPSHOT/parent-on-fs-1.0-SNAPSHOT.pom" );

        assertTrue( parentFile.exists() );
    }

    public void test_MASSEMBLY_218_projectDependencyWithClassifier()
        throws ProjectBuildingException, RepositoryAssemblyException, IOException, InvalidDependencyVersionException
    {
        File repoDir = getTestRemoteRepositoryBasedir();

        ArtifactRepository localRepository = repoFactory.createArtifactRepository( "local", repoDir.getAbsoluteFile()
                                                                                                   .toURL()
                                                                                                   .toExternalForm(),
                                                                                   defaultLayout, null, null );

        MavenProject project = getProject( "massembly-210-direct-parent-on-fs/project/pom.xml", null, null, null, false );

        TestRepositoryBuilderConfigSource cs = new TestRepositoryBuilderConfigSource();
        cs.setProject( project );
        cs.setLocalRepository( localRepository );

        DefaultRepositoryAssembler assembler = new DefaultRepositoryAssembler( artifactFactory, artifactResolver,
                                                                               defaultLayout, repoFactory,
                                                                               metadataSource, projectBuilder );

        File repositoryDirectory = new File( getBasedir(), "target/test-repositories/massembly-210-direct-parent-on-fs" );

        DefaultRepositoryInfo repoInfo = new DefaultRepositoryInfo();

        assembler.buildRemoteRepository( repositoryDirectory, repoInfo, cs );

        File parentFile = new File( repositoryDirectory,
                                    "massembly/210/parent-on-fs/1.0-SNAPSHOT/parent-on-fs-1.0-SNAPSHOT.pom" );

        assertTrue( parentFile.exists() );
    }
}
