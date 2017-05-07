package org.apache.maven.shared.project.deploy.internal;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployer;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployerException;
import org.apache.maven.shared.project.NoFileAssignedException;
import org.apache.maven.shared.project.deploy.ProjectDeployer;
import org.apache.maven.shared.project.deploy.ProjectDeployerRequest;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This will deploy a whole project into the appropriate remote repository.
 * 
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a> 
 * 
 * Most of the code is taken from maven-deploy-plugin.
 */
@Component( role = ProjectDeployer.class )
public class DefaultProjectDeployer
    implements ProjectDeployer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DefaultProjectDeployer.class );

    @Requirement
    private ArtifactDeployer deployer;

    /**
     * This will deploy a single project which may contain several artifacts into the appropriate remote repository.
     * 
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param request {@link ProjectDeployerRequest}
     * @param artifactRepository {@link ArtifactRepository}
     * @throws IllegalArgumentException in case of artifact is not correctly assigned.
     * @throws NoFileAssignedException In case no file has been assigned to main file.
     */
    public void deploy( ProjectBuildingRequest buildingRequest, ProjectDeployerRequest request,
                        ArtifactRepository artifactRepository )
        throws NoFileAssignedException, IllegalArgumentException
    {

        Artifact artifact = request.getProject().getArtifact();
        String packaging = request.getProject().getPackaging();
        File pomFile = request.getProject().getFile();

        List<Artifact> attachedArtifacts = request.getProject().getAttachedArtifacts();

        // Deploy the POM
        boolean isPomArtifact = "pom".equals( packaging );
        if ( !isPomArtifact )
        {
            ProjectArtifactMetadata metadata = new ProjectArtifactMetadata( artifact, pomFile );
            artifact.addMetadata( metadata );
        }
        else
        {
            artifact.setFile( pomFile );
        }

        if ( request.isUpdateReleaseInfo() )
        {
            artifact.setRelease( true );
        }

        artifact.setRepository( artifactRepository );

        int retryFailedDeploymentCount = request.getRetryFailedDeploymentCount();

        try
        {
            List<Artifact> deployableArtifacts = new ArrayList<Artifact>();
            if ( isPomArtifact )
            {
                deployableArtifacts.add( artifact );
            }
            else
            {
                File file = artifact.getFile();

                if ( file != null && file.isFile() )
                {
                    deployableArtifacts.add( artifact );
                }
                else if ( !attachedArtifacts.isEmpty() )
                {
                    // TODO: Reconsider this exception? Better Exception type?
                    throw new NoFileAssignedException( "The packaging plugin for this project did not assign "
                        + "a main file to the project but it has attachments. Change packaging to 'pom'." );
                }
                else
                {
                    // TODO: Reconsider this exception? Better Exception type?
                    throw new NoFileAssignedException( "The packaging for this project did not assign "
                        + "a file to the build artifact" );
                }
            }

            for ( Artifact attached : attachedArtifacts )
            {
                // This is here when AttachedArtifact is used, like m-sources-plugin:2.0.4
                try
                {
                    attached.setRepository( artifactRepository );
                }
                catch ( UnsupportedOperationException e )
                {
                    LOGGER.warn( attached.getId() + " has been attached with deprecated code, "
                        + "try to upgrade the responsible plugin" );
                }

                deployableArtifacts.add( attached );
            }

            deploy( buildingRequest, deployableArtifacts, artifactRepository, retryFailedDeploymentCount );
        }
        catch ( ArtifactDeployerException e )
        {
            throw new IllegalArgumentException( e.getMessage(), e );
        }
    }

    private void deploy( ProjectBuildingRequest request, Collection<Artifact> artifacts,
                         ArtifactRepository deploymentRepository, int retryFailedDeploymentCount )
        throws ArtifactDeployerException
    {

        // for now retry means redeploy the complete artifacts collection
        int retryFailedDeploymentCounter = Math.max( 1, Math.min( 10, retryFailedDeploymentCount ) );
        ArtifactDeployerException exception = null;
        for ( int count = 0; count < retryFailedDeploymentCounter; count++ )
        {
            try
            {
                if ( count > 0 )
                {
                    LOGGER.info( "Retrying deployment attempt " + ( count + 1 ) + " of "
                        + retryFailedDeploymentCounter );
                }

                deployer.deploy( request, deploymentRepository, artifacts );
                exception = null;
                break;
            }
            catch ( ArtifactDeployerException e )
            {
                if ( count + 1 < retryFailedDeploymentCounter )
                {
                    LOGGER.warn( "Encountered issue during deployment: " + e.getLocalizedMessage() );
                    LOGGER.debug( e.getMessage() );
                }
                if ( exception == null )
                {
                    exception = e;
                }
            }
        }
        if ( exception != null )
        {
            throw exception;
        }
    }

}
