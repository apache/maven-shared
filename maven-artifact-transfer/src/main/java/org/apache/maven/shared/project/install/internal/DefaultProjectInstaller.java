package org.apache.maven.shared.project.install.internal;

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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.artifact.install.ArtifactInstaller;
import org.apache.maven.shared.artifact.install.ArtifactInstallerException;
import org.apache.maven.shared.project.NoFileAssignedException;
import org.apache.maven.shared.project.install.ProjectInstaller;
import org.apache.maven.shared.project.install.ProjectInstallerRequest;
import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This will install a whole project into the appropriate repository.
 * 
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
@Component( role = ProjectInstaller.class )
public class DefaultProjectInstaller
    implements ProjectInstaller
{

    private static final Logger LOGGER = LoggerFactory.getLogger( DefaultProjectInstaller.class );

    @Requirement
    private ArtifactInstaller installer;

    @Requirement
    private RepositoryManager repositoryManager;

    private final DualDigester digester = new DualDigester();

    /**
     * {@inheritDoc}
     */
    @Override
    public void install( ProjectBuildingRequest buildingRequest, ProjectInstallerRequest request )
        throws IOException, ArtifactInstallerException, NoFileAssignedException
    {

        MavenProject project = request.getProject();
        boolean createChecksum = request.isCreateChecksum();
        boolean updateReleaseInfo = request.isUpdateReleaseInfo();

        Artifact artifact = project.getArtifact();
        String packaging = project.getPackaging();
        File pomFile = project.getFile();

        List<Artifact> attachedArtifacts = project.getAttachedArtifacts();

        // TODO: push into transformation
        boolean isPomArtifact = "pom".equals( packaging );

        ProjectArtifactMetadata metadata;

        if ( updateReleaseInfo )
        {
            artifact.setRelease( true );
        }

        Collection<File> metadataFiles = new LinkedHashSet<File>();

        if ( isPomArtifact )
        {
            if ( pomFile != null )
            {
                installer.install( buildingRequest,
                                   Collections.<Artifact>singletonList( new ProjectArtifact( project ) ) );
                installChecksums( buildingRequest, artifact, createChecksum );
                addMetaDataFilesForArtifact( buildingRequest, artifact, metadataFiles, createChecksum );
            }
        }
        else
        {
            if ( pomFile != null )
            {
                metadata = new ProjectArtifactMetadata( artifact, pomFile );
                artifact.addMetadata( metadata );
            }

            File file = artifact.getFile();

            // Here, we have a temporary solution to MINSTALL-3 (isDirectory() is true if it went through compile
            // but not package). We are designing in a proper solution for Maven 2.1
            if ( file != null && file.isFile() )
            {
                installer.install( buildingRequest, Collections.<Artifact>singletonList( artifact ) );
                installChecksums( buildingRequest, artifact, createChecksum );
                addMetaDataFilesForArtifact( buildingRequest, artifact, metadataFiles, createChecksum );
            }
            else if ( !attachedArtifacts.isEmpty() )
            {
                throw new NoFileAssignedException( "The packaging plugin for this project did not assign "
                    + "a main file to the project but it has attachments. Change packaging to 'pom'." );
            }
            else
            {
                // CHECKSTYLE_OFF: LineLength
                throw new NoFileAssignedException( "The packaging for this project did not assign a file to the build artifact" );
                // CHECKSTYLE_ON: LineLength
            }
        }

        for ( Artifact attached : attachedArtifacts )
        {
            installer.install( buildingRequest, Collections.singletonList( attached ) );
            installChecksums( buildingRequest, attached, createChecksum );
            addMetaDataFilesForArtifact( buildingRequest, attached, metadataFiles, createChecksum );
        }

        installChecksums( metadataFiles );
    }

    /**
     * Installs the checksums for the specified artifact if this has been enabled in the plugin configuration. This
     * method creates checksums for files that have already been installed to the local repo to account for on-the-fly
     * generated/updated files. For example, in Maven 2.0.4- the <code>ProjectArtifactMetadata</code> did not install
     * the original POM file (cf. MNG-2820). While the plugin currently requires Maven 2.0.6, we continue to hash the
     * installed POM for robustness with regard to future changes like re-introducing some kind of POM filtering.
     *
     * @param buildingRequest The project building request, must not be <code>null</code>.
     * @param artifact The artifact for which to create checksums, must not be <code>null</code>.
     * @param createChecksum {@code true} if checksum should be created, otherwise {@code false}.
     * @throws IOException If the checksums could not be installed.
     */
    private void installChecksums( ProjectBuildingRequest buildingRequest, Artifact artifact, boolean createChecksum )
        throws IOException
    {
        if ( !createChecksum )
        {
            return;
        }

        File artifactFile = getLocalRepoFile( buildingRequest, artifact );
        installChecksums( artifactFile );
    }

    // CHECKSTYLE_OFF: LineLength
    private void addMetaDataFilesForArtifact( ProjectBuildingRequest buildingRequest, Artifact artifact,
                                              Collection<File> targetMetadataFiles, boolean createChecksum )
    // CHECKSTYLE_ON: LineLength
    {
        if ( !createChecksum )
        {
            return;
        }

        Collection<ArtifactMetadata> metadatas = artifact.getMetadataList();
        if ( metadatas != null )
        {
            for ( ArtifactMetadata metadata : metadatas )
            {
                File metadataFile = getLocalRepoFile( buildingRequest, metadata );
                targetMetadataFiles.add( metadataFile );
            }
        }
    }

    /**
     * Installs the checksums for the specified metadata files.
     *
     * @param metadataFiles The collection of metadata files to install checksums for, must not be <code>null</code>.
     * @throws IOException If the checksums could not be installed.
     */
    private void installChecksums( Collection<File> metadataFiles )
        throws IOException
    {
        for ( File metadataFile : metadataFiles )
        {
            installChecksums( metadataFile );
        }
    }

    /**
     * Installs the checksums for the specified file (if it exists).
     *
     * @param installedFile The path to the already installed file in the local repo for which to generate checksums,
     *            must not be <code>null</code>.
     * @throws IOException In case of errors. Could not install checksums.
     */
    private void installChecksums( File installedFile )
        throws IOException
    {
        boolean signatureFile = installedFile.getName().endsWith( ".asc" );
        if ( installedFile.isFile() && !signatureFile )
        {
            LOGGER.debug( "Calculating checksums for " + installedFile );
            digester.calculate( installedFile );
            installChecksum( installedFile, ".md5", digester.getMd5() );
            installChecksum( installedFile, ".sha1", digester.getSha1() );
        }
    }

    /**
     * Installs a checksum for the specified file.
     *
     * @param installedFile The base path from which the path to the checksum files is derived by appending the given
     *            file extension, must not be <code>null</code>.
     * @param ext The file extension (including the leading dot) to use for the checksum file, must not be
     *            <code>null</code>.
     * @param checksum the checksum to write
     * @throws IOException If the checksum could not be installed.
     */
    private void installChecksum( File installedFile, String ext, String checksum )
        throws IOException
    {
        File checksumFile = new File( installedFile.getAbsolutePath() + ext );
        LOGGER.debug( "Installing checksum to " + checksumFile );
        try
        {
            // noinspection ResultOfMethodCallIgnored
            checksumFile.getParentFile().mkdirs();
            FileUtils.fileWrite( checksumFile.getAbsolutePath(), "UTF-8", checksum );
        }
        catch ( IOException e )
        {
            throw new IOException( "Failed to install checksum to " + checksumFile, e );
        }
    }

    /**
     * Gets the path of the specified artifact within the local repository. Note that the returned path need not exist
     * (yet).
     *
     * @param buildingRequest The project building request, must not be <code>null</code>.
     * @param artifact The artifact whose local repo path should be determined, must not be <code>null</code>.
     * @return The absolute path to the artifact when installed, never <code>null</code>.
     */
    private File getLocalRepoFile( ProjectBuildingRequest buildingRequest, Artifact artifact )
    {
        String path = repositoryManager.getPathForLocalArtifact( buildingRequest, artifact );
        return new File( repositoryManager.getLocalRepositoryBasedir( buildingRequest ), path );
    }

    /**
     * Gets the path of the specified artifact metadata within the local repository. Note that the returned path need
     * not exist (yet).
     *
     * @param buildingRequest The project building request, must not be <code>null</code>.
     * @param metadata The artifact metadata whose local repo path should be determined, must not be <code>null</code>.
     * @return The absolute path to the artifact metadata when installed, never <code>null</code>.
     */
    private File getLocalRepoFile( ProjectBuildingRequest buildingRequest, ArtifactMetadata metadata )
    {
        String path = repositoryManager.getPathForLocalMetadata( buildingRequest, metadata );
        return new File( repositoryManager.getLocalRepositoryBasedir( buildingRequest ), path );
    }

}
