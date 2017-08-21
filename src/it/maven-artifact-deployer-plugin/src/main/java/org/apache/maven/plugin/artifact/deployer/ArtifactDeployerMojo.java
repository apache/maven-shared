package org.apache.maven.plugin.artifact.deployer;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployer;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployerException;
import org.apache.maven.shared.repository.RepositoryManager;

/**
 * This mojo is implemented to test the ArtifactDeployer part of the maven-artifact-transfer shared component.
 */
@Mojo( name = "artifact-deployer", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true )
public class ArtifactDeployerMojo
    extends AbstractMojo
{

    /**
     * Parameter to have different locations for each Maven version we are testing with.
     */
    @Parameter
    private String mvnVersion;

    @Component
    protected RepositoryManager repositoryManager;

    @Parameter( defaultValue = "${session}", required = true, readonly = true )
    protected MavenSession session;

    @Component
    private ArtifactDeployer deployer;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Hello from artifact-deployer plugin" );
        deployerProject( session.getProjectBuildingRequest() );
        getLog().info( "Bye bye from artifact-deployer plugin" );
    }

    private void createFileContent( File outputFile )
        throws IOException
    {
        Path file = outputFile.toPath();
        List<String> asList = Arrays.asList( "Line 1", "Line 2" );
        Files.write( file, asList, Charset.forName( "UTF-8" ) );
    }

    private void deployerProject( ProjectBuildingRequest pbr )
        throws MojoFailureException, MojoExecutionException
    {
        try
        {
            DefaultArtifactHandler artifactHandler = new DefaultArtifactHandler();
            artifactHandler.setExtension( "EXTENSION" );

            File artifactsDirectory =
                new File( session.getCurrentProject().getBuild().getDirectory(), "tests/artifacts" );
            getLog().info( "Directory: '" + artifactsDirectory.getAbsolutePath() + "'" );
            artifactsDirectory.mkdirs();

            File tmpFile = File.createTempFile( "test-deploy", ".jar", artifactsDirectory );
            createFileContent( tmpFile );

            DefaultArtifact artifact = new DefaultArtifact( "DEPLOYER-GROUPID-" + mvnVersion, "ARTIFACTID", "VERSION",
                                                            "compile", "jar", null, artifactHandler );
            artifact.setFile( tmpFile );
            artifact.setRepository( session.getProjectBuildingRequest().getLocalRepository() );

            DefaultArtifact artifactWithClassifier =
                new DefaultArtifact( "DEPLOYER-GROUPID-" + mvnVersion, "ARTIFACTID", "VERSION", "compile", "jar",
                                     "CLASSIFIER", artifactHandler );
            File tmpFileClassifier = File.createTempFile( "test-deploy-classifier", ".jar", artifactsDirectory );
            createFileContent( tmpFileClassifier );
            artifactWithClassifier.setFile( tmpFileClassifier );
            artifactWithClassifier.setRepository( session.getProjectBuildingRequest().getLocalRepository() );

            Collection<Artifact> mavenArtifacts = Arrays.<Artifact>asList( artifact, artifactWithClassifier );

            deployer.deploy( session.getProjectBuildingRequest(), mavenArtifacts );
        }
        catch ( ArtifactDeployerException e )
        {
            throw new MojoExecutionException( "ArtifactDeployerException", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "IOException", e );
        }
    }

}
