package org.apache.maven.shared.artifact.install.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.shared.artifact.install.ArtifactInstaller;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;

public class Maven30ArtifactInstallerTest extends PlexusTestCase
{
    private final File localRepo = new File( "target/tests/local-repo" );
    
    private Maven30ArtifactInstaller installer;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        installer = (Maven30ArtifactInstaller) super.lookup( ArtifactInstaller.class, "maven3" );
    }

    public void testInstall() throws Exception
    {
        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
        repositorySession.setLocalRepositoryManager( new SimpleLocalRepositoryManager( localRepo ) );
        buildingRequest.setRepositorySession( repositorySession );
        
        DefaultArtifactHandler artifactHandler = new DefaultArtifactHandler();
        artifactHandler.setExtension( "EXTENSION" );

        File artifactsDirectory = new File( "target/tests/artifacts" );
        artifactsDirectory.mkdirs();
        File tmpFile = File.createTempFile( "test-install", ".jar", artifactsDirectory );
        
        DefaultArtifact artifact = new DefaultArtifact( "GROUPID", "ARTIFACTID", "VERSION", "compile", "jar", null, artifactHandler );
        artifact.setFile( tmpFile );
        DefaultArtifact artifactWithClassifier = new DefaultArtifact( "GROUPID", "ARTIFACTID", "VERSION", "compile", "jar", "CLASSIFIER", artifactHandler );
        artifactWithClassifier.setFile( tmpFile );
        
        Collection<Artifact> mavenArtifacts = Arrays.<Artifact>asList( artifact, artifactWithClassifier );
        
        installer.install( buildingRequest, mavenArtifacts );
        
        assertTrue( new File( localRepo, "GROUPID/ARTIFACTID/VERSION/ARTIFACTID-VERSION.EXTENSION" ).exists() );
        assertTrue( new File( localRepo, "GROUPID/ARTIFACTID/VERSION/ARTIFACTID-VERSION-CLASSIFIER.EXTENSION" ).exists() );
        assertTrue( new File( localRepo, "GROUPID/ARTIFACTID/maven-metadata-local.xml" ).exists() ); //??
    }
}
