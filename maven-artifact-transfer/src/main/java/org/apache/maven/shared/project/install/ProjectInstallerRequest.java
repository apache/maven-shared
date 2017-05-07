package org.apache.maven.shared.project.install;

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
 * @author Robert Scholte
 */
public class ProjectInstallerRequest
{
    // From AbstractInstallMojo

    private boolean createChecksum;

    private boolean updateReleaseInfo;

    // From InstallMojo

    private MavenProject project;

    /**
     * @return the createChecksum
     */
    public boolean isCreateChecksum()
    {
        return createChecksum;
    }

    /**
     * @param theCreateChecksumToBeSet the createChecksum to set
     * @return {@link ProjectInstallerRequest} for chaining.
     */
    public ProjectInstallerRequest setCreateChecksum( boolean theCreateChecksumToBeSet )
    {
        this.createChecksum = theCreateChecksumToBeSet;
        return this;
    }

    /**
     * @return the updateReleaseInfo
     */
    public boolean isUpdateReleaseInfo()
    {
        return updateReleaseInfo;
    }

    /**
     * @param theUpdateReleaseInfoToBeSet the updateReleaseInfo to set
     * @return {@link ProjectInstallerRequest} for chaining.
     */
    public ProjectInstallerRequest setUpdateReleaseInfo( boolean theUpdateReleaseInfoToBeSet )
    {
        this.updateReleaseInfo = theUpdateReleaseInfoToBeSet;
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
     * @param theProjectToBeSet the project to set
     * @return {@link ProjectInstallerRequest} for chaining.
     */
    public ProjectInstallerRequest setProject( MavenProject theProjectToBeSet )
    {
        this.project = theProjectToBeSet;
        return this;
    }

}
