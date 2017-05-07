package org.apache.maven.shared.project.deploy;

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

import org.apache.maven.project.MavenProject;

/**
 * 
 * @author Robert Scholte
 */
public class ProjectDeployerRequest
{

    // From AbstractDeployMojo

    private boolean updateReleaseInfo;

    private int retryFailedDeploymentCount;

    // From DeployMojo

    private MavenProject project;

    private String altDeploymentRepository;

    private String altSnapshotDeploymentRepository;

    private String altReleaseDeploymentRepository;

    /**
     * @return the updateReleaseInfo
     */
    public boolean isUpdateReleaseInfo()
    {
        return updateReleaseInfo;
    }

    /**
     * @param theUpdateReleaseInfoToBeSet the updateReleaseInfo to set
     * @return {@link ProjectDeployerRequest} for chaining.
     */
    public ProjectDeployerRequest setUpdateReleaseInfo( boolean theUpdateReleaseInfoToBeSet )
    {
        this.updateReleaseInfo = theUpdateReleaseInfoToBeSet;
        return this;
    }

    /**
     * @return the retryFailedDeploymentCount
     */
    public int getRetryFailedDeploymentCount()
    {
        return retryFailedDeploymentCount;
    }

    /**
     * @param theRetryFailedDeploymentCountToBeSet the retryFailedDeploymentCount to set
     * @return {@link ProjectDeployerRequest} for chaining.
     */
    public ProjectDeployerRequest setRetryFailedDeploymentCount( int theRetryFailedDeploymentCountToBeSet )
    {
        this.retryFailedDeploymentCount = theRetryFailedDeploymentCountToBeSet;
        return this;
    }

    /**
     * @return the project
     */
    public MavenProject getProject()
    {
        return project;
    }

    /**
     * @param theProjectToBeSet the {link {@link MavenProject project} to set
     * @return {@link ProjectDeployerRequest} for chaining.
     */
    public ProjectDeployerRequest setProject( MavenProject theProjectToBeSet )
    {
        this.project = theProjectToBeSet;
        return this;
    }

    /**
     * @return the altDeploymentRepository
     */
    public String getAltDeploymentRepository()
    {
        return altDeploymentRepository;
    }

    /**
     * @param theAltDeploymentRepositoryToBeSet the altDeploymentRepository to set
     * @return {@link ProjectDeployerRequest} for chaining.
     */
    public ProjectDeployerRequest setAltDeploymentRepository( String theAltDeploymentRepositoryToBeSet )
    {
        this.altDeploymentRepository = theAltDeploymentRepositoryToBeSet;
        return this;
    }

    /**
     * @return the altSnapshotDeploymentRepository
     */
    public String getAltSnapshotDeploymentRepository()
    {
        return altSnapshotDeploymentRepository;
    }

    /**
     * @param theAltSnapshotDeploymentRepositoryToBeSet the altSnapshotDeploymentRepository to set
     * @return {@link ProjectDeployerRequest} for chaining.
     */
    public ProjectDeployerRequest setAltSnapshotDeploymentRepository( String theAltSnapshotDeploymentRepositoryToBeSet )
    {
        this.altSnapshotDeploymentRepository = theAltSnapshotDeploymentRepositoryToBeSet;
        return this;
    }

    /**
     * @return the altReleaseDeploymentRepository
     */
    public String getAltReleaseDeploymentRepository()
    {
        return altReleaseDeploymentRepository;
    }

    /**
     * @param theAltReleaseDeploymentRepositoryToBeSet the altReleaseDeploymentRepository to set
     * @return {@link ProjectDeployerRequest} for chaining.
     */
    public ProjectDeployerRequest setAltReleaseDeploymentRepository( String theAltReleaseDeploymentRepositoryToBeSet )
    {
        this.altReleaseDeploymentRepository = theAltReleaseDeploymentRepositoryToBeSet;
        return this;
    }
}
