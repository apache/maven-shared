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
     * @param updateReleaseInfo the updateReleaseInfo to set
     */
    public ProjectDeployerRequest setUpdateReleaseInfo( boolean updateReleaseInfo )
    {
        this.updateReleaseInfo = updateReleaseInfo;
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
     * @param retryFailedDeploymentCount the retryFailedDeploymentCount to set
     */
    public ProjectDeployerRequest setRetryFailedDeploymentCount( int retryFailedDeploymentCount )
    {
        this.retryFailedDeploymentCount = retryFailedDeploymentCount;
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
     * @param project the project to set
     */
    public ProjectDeployerRequest setProject( MavenProject project )
    {
        this.project = project;
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
     * @param altDeploymentRepository the altDeploymentRepository to set
     */
    public ProjectDeployerRequest setAltDeploymentRepository( String altDeploymentRepository )
    {
        this.altDeploymentRepository = altDeploymentRepository;
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
     * @param altSnapshotDeploymentRepository the altSnapshotDeploymentRepository to set
     */
    public ProjectDeployerRequest setAltSnapshotDeploymentRepository( String altSnapshotDeploymentRepository )
    {
        this.altSnapshotDeploymentRepository = altSnapshotDeploymentRepository;
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
     * @param altReleaseDeploymentRepository the altReleaseDeploymentRepository to set
     */
    public ProjectDeployerRequest setAltReleaseDeploymentRepository( String altReleaseDeploymentRepository )
    {
        this.altReleaseDeploymentRepository = altReleaseDeploymentRepository;
        return this;
    }
}
