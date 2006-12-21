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
package org.apache.maven.shared.test.plugin;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.tools.easymock.TestFileManager;
import org.codehaus.plexus.PlexusTestCase;

public class RepositoryToolTest
    extends PlexusTestCase
{

    private TestFileManager fileManager;

    public void setUp()
        throws Exception
    {
        super.setUp();

        fileManager = new TestFileManager( "RepositoryToolTest.", "" );
    }

    public void tearDown()
        throws Exception
    {
        super.tearDown();

        fileManager.cleanUp();
    }

    public void testCreateLocalRepositoryFromPlugin_ShouldWriteJarAndPom()
        throws Exception
    {
        RepositoryTool repoTool = (RepositoryTool) lookup( RepositoryTool.ROLE, "default" );

        File tempDir = fileManager.createTempDir();

        String pomContent = "<project><modelVersion>4.0.0</modelVersion></project>";
        String jarContent = "This is a test";

        File pom = fileManager.createFile( tempDir, "pom.xml", pomContent );
        File jar = fileManager.createFile( tempDir, "artifact-test.jar", jarContent );

        MavenProject pluginProject = new MavenProject();

        ArtifactFactory artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );

        Artifact pluginArtifact = artifactFactory.createArtifact( "group", "artifact", "test", null, "jar" );
        pluginArtifact.setFile( jar );
        pluginArtifact.addMetadata( new ProjectArtifactMetadata( pluginArtifact, pom ) );

        pluginProject.setArtifact( pluginArtifact );
        pluginProject.setFile( pom );

        File targetLocalRepoBasedir = fileManager.createTempDir();

        repoTool.createLocalRepositoryFromPlugin( pluginProject, pom, targetLocalRepoBasedir );

        fileManager.assertFileExistence( targetLocalRepoBasedir, "group/artifact/test/artifact-test.pom", true );
        fileManager.assertFileContents( targetLocalRepoBasedir, "group/artifact/test/artifact-test.jar", jarContent );

    }

}
