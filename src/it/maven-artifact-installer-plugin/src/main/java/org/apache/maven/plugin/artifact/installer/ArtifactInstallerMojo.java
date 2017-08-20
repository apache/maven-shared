package org.apache.maven.plugin.artifact.installer;

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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

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
import org.apache.maven.shared.artifact.install.ArtifactInstaller;
import org.apache.maven.shared.artifact.install.ArtifactInstallerException;
import org.apache.maven.shared.repository.RepositoryManager;

/**
 */
@Mojo( name = "artifact-installer", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true )
public class ArtifactInstallerMojo
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
    private ArtifactInstaller installer;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info( "Hello from artifact-installer plugin" );
        installProject( session.getProjectBuildingRequest() );
        getLog().info( "Bye bye from artifact-installer plugin" );
    }

    private void installProject( ProjectBuildingRequest pbr )
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

            File tmpFile = File.createTempFile( "test-install", ".jar", artifactsDirectory );

            DefaultArtifact artifact = new DefaultArtifact( "GROUPID-" + mvnVersion, "ARTIFACTID", "VERSION", "compile",
                                                            "jar", null, artifactHandler );
            artifact.setFile( tmpFile );
            DefaultArtifact artifactWithClassifier =
                new DefaultArtifact( "GROUPID-" + mvnVersion, "ARTIFACTID", "VERSION", "compile", "jar", "CLASSIFIER",
                                     artifactHandler );
            artifactWithClassifier.setFile( tmpFile );

            Collection<Artifact> mavenArtifacts = Arrays.<Artifact>asList( artifact, artifactWithClassifier );

            installer.install( session.getProjectBuildingRequest(), mavenArtifacts );
        }
        catch ( ArtifactInstallerException e )
        {
            throw new MojoExecutionException( "ArtifactInstallerException", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "IOException", e );
        }

    }

}
