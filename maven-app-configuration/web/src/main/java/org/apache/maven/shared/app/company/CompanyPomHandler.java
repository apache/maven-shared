package org.apache.maven.shared.app.company;

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

import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.app.configuration.CompanyPom;

import java.io.IOException;
import java.util.List;

/**
 * Holds a company POM to avoid re-reading it.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public interface CompanyPomHandler
{
    String ROLE = CompanyPomHandler.class.getName();

    String ORGANIZATION_LOGO_PROPERTY = "organization.logo";

    /**
     * Retrieve the company model (may be cached).
     *
     * @param companyPom      the configuration holding the required group and artifact ID
     * @param localRepository the local repository to use while resolving the POM
     * @return the model
     * @throws org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException
     *          if there is a problem locating the existing POM from the repository
     * @throws org.apache.maven.project.ProjectBuildingException
     *          if the existing POM in the repository is invalid
     */
    Model getCompanyPomModel( CompanyPom companyPom, ArtifactRepository localRepository )
        throws ProjectBuildingException, ArtifactMetadataRetrievalException;

    /**
     * Retrieve the company model (may be cached).
     *
     * @param companyPom         the configuration holding the required group and artifact ID
     * @param localRepository    the local repository to use while resolving the POM
     * @param remoteRepositories the repositories to search for the POM. The default repositories from the super POM will also be used if necessary.
     * @return the model
     * @throws org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException
     *          if there is a problem locating the existing POM from the repository
     * @throws org.apache.maven.project.ProjectBuildingException
     *          if the existing POM in the repository is invalid
     */
    Model getCompanyPomModel( CompanyPom companyPom, ArtifactRepository localRepository,
                              List/*<ArtifactRepository>*/ remoteRepositories )
        throws ProjectBuildingException, ArtifactMetadataRetrievalException;

    /**
     * Save a company POM in the repository. At present, it does not deploy it to any remote repositories.
     * The version in the model will be incremented to the next sequential single digit.
     *
     * @param companyModel    the company model to save. This is likely to be the same instance already cached, but will replace the cached version regardless
     * @param localRepository the local repository to use while deploying the POM.
     * @throws java.io.IOException if there is a problem saving the model to the local repository
     * @throws org.apache.maven.artifact.installer.ArtifactInstallationException
     *                             if there is a problem saving to the local repository
     */
    void save( Model companyModel, ArtifactRepository localRepository )
        throws IOException, ArtifactInstallationException;

    /**
     * Save a company POM in the repository. At present, it does not deploy it to any remote repositories.
     * The version in the model will be incremented to the next sequential single digit.
     *
     * @param companyModel         the company model to save. This is likely to be the same instance already cached, but will replace the cached version regardless
     * @param localRepository      the local repository to use while deploying the POM.
     * @param deploymentRepository the repository to deploy the final POM to. If <code>null</code>, the POM is not deployed.
     * @throws java.io.IOException if there is a problem saving the model to the local repository
     * @throws org.apache.maven.artifact.installer.ArtifactInstallationException
     *                             if there is a problem saving to the local repository
     * @throws org.apache.maven.artifact.deployer.ArtifactDeploymentException
     *                             if there is a problem deploying to the remote repository
     */
    void save( Model companyModel, ArtifactRepository localRepository, ArtifactRepository deploymentRepository )
        throws IOException, ArtifactInstallationException, ArtifactDeploymentException;
}
