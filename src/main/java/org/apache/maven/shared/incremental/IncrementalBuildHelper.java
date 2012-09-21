package org.apache.maven.shared.incremental;

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


import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.DirectoryScanResult;
import org.apache.maven.shared.utils.io.DirectoryScanner;
import org.apache.maven.shared.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Various helper methods to support incremental builds
 *
 */
public class IncrementalBuildHelper
{
    /**
     * the root directory to store status information about maven executions in.
     */
    private static final String MAVEN_STATUS_ROOT = "maven-status";
    public static final String CREATED_FILES_LST_FILENAME = "createdFiles.lst";

    /**
     * Needed for storing the status for the incremental build support.
     */
    private MojoExecution mojoExecution;

    /**
     * Needed for storing the status for the incremental build support.
     */
    private MavenProject mavenProject;

    /**
     * Used for detecting changes between the Mojo execution.
     * @see #getDirectoryScanner();
     */
    private DirectoryScanner directoryScanner;

    private String[] filesBeforeAction;

    public IncrementalBuildHelper( MojoExecution mojoExecution, MavenSession mavenSession )
    {
        this( mojoExecution, mavenSession.getCurrentProject() );
    }


    public IncrementalBuildHelper( MojoExecution mojoExecution, MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
        this.mojoExecution = mojoExecution;
    }

    public DirectoryScanner getDirectoryScanner()
    {
        if ( directoryScanner == null )
        {
            directoryScanner = new DirectoryScanner();
        }

        return directoryScanner;
    }

    /**
     * @return the directory for storing status information of the current mojo execution.
     */
    public File getMojoStatusDirectory() throws MojoExecutionException
    {
        if ( mojoExecution == null )
        {
            throw new MojoExecutionException( "MojoExecution could not get resolved" );
        }

        File buildOutputDirectory = new File( mavenProject.getBuild().getDirectory() );

        String mojoStatusPath = MAVEN_STATUS_ROOT + File.separator
                                + mojoExecution.getMojoDescriptor().getPluginDescriptor().getArtifactId() + File.separator
                                + mojoExecution.getMojoDescriptor().getGoal() + File.separator
                                + mojoExecution.getExecutionId();

        File mojoStatusDir = new File( buildOutputDirectory, mojoStatusPath );

        if ( !mojoStatusDir.exists() )
        {
            mojoStatusDir.mkdirs();
        }

        return mojoStatusDir;
    }

    /**
     * <p>This method shall get invoked before the actual mojo task gets triggered,
     * e.g. the actual compile in maven-compiler-plugin.</p>
     *
     * <p><b>Attention:</b> This method shall only get invoked if the plugin re-creates <b>all</b> the output.</p>
     *
     * <p>It first picks up the list of files created in the previous build and delete them.
     * This step is necessary to prevent left-overs. After that we take a 'directory snapshot'
     * (list of all files which exist in the outputDirectory after the clean). </p>
     *
     * <p>After the actual mojo task got executed you should invoke the method
     * {@link #afterRebuildExecution()} to collect the list of files which got changed
     * by this task.</p>
     *
     *
     * @param outputDirectory
     * @return all files which got created in the previous build and have been deleted now.
     * @throws MojoExecutionException
     */
    public String[] beforeRebuildExecution( File outputDirectory ) throws MojoExecutionException
    {
        File mojoConfigBase = getMojoStatusDirectory();
        File mojoConfigFile = new File( mojoConfigBase, "createdFiles.lst" );

        String[] oldFiles;

        try
        {
            oldFiles = FileUtils.fileReadArray( mojoConfigFile );
            for ( String oldFileName : oldFiles )
            {
                File oldFile = new File( oldFileName );
                oldFile.delete();
            }
        }
        catch( IOException e )
        {
            throw new MojoExecutionException( "Error reading old mojo status", e );
        }

        // we remember all files which currently exist in the output directory
        DirectoryScanner diffScanner = getDirectoryScanner();
        diffScanner.setBasedir( outputDirectory );
        if ( outputDirectory.exists() )
        {
            diffScanner.scan();
        }
        filesBeforeAction = diffScanner.getIncludedFiles();

        return oldFiles;
    }

    /**
     * <p>This method collects and stores all information about files changed since
     * the call to {@link #beforeRebuildExecution(java.io.File)}.</p>
     *
     * <p><b>Attention:</b> This method shall only get invoked if the plugin re-creates <b>all</b> the output.</p>
     *
     * @throws MojoExecutionException
     */
    public void afterRebuildExecution() throws MojoExecutionException
    {
        DirectoryScanner diffScanner = getDirectoryScanner();
        // now scan the same directory again and create a diff
        diffScanner.scan();
        DirectoryScanResult scanResult = diffScanner.diffIncludedFiles( filesBeforeAction );

        File mojoConfigBase = getMojoStatusDirectory();
        File mojoConfigFile = new File( mojoConfigBase, CREATED_FILES_LST_FILENAME );

        try
        {
            FileUtils.fileWriteArray( mojoConfigFile, scanResult.getFilesAdded() );
        }
        catch( IOException e )
        {
            throw new MojoExecutionException( "Error while storing the mojo status", e );
        }

    }
}
