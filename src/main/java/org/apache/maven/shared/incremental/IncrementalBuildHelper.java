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
import java.util.Set;

/**
 * Various helper methods to support incremental builds
 */
public class IncrementalBuildHelper
{
    /**
     * the root directory to store status information about Maven executions in.
     */
    private static final String MAVEN_STATUS_ROOT = "maven-status";
    public static final String CREATED_FILES_LST_FILENAME = "createdFiles.lst";
    private static final String INPUT_FILES_LST_FILENAME = "inputFiles.lst";

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

    /**
     * Once the {@link #beforeRebuildExecution(java.io.File)} got called,
     * this will contain the list of files in the build directory.
     */
    private String[] filesBeforeAction = new String[0];

    public IncrementalBuildHelper( MojoExecution mojoExecution, MavenSession mavenSession )
    {
        this( mojoExecution, getMavenProject( mavenSession ) );
    }

    public IncrementalBuildHelper( MojoExecution mojoExecution, MavenProject mavenProject )
    {
        if ( mavenProject == null )
        {
            throw new IllegalArgumentException( "MavenProject must not be null!" );
        }
        if ( mojoExecution == null )
        {
            throw new IllegalArgumentException( "MojoExecution must not be null!" );
        }

        this.mavenProject = mavenProject;
        this.mojoExecution = mojoExecution;
    }

    /**
     * small helper method to allow for the nullcheck in the ct invocation
     */
    private static MavenProject getMavenProject( MavenSession mavenSession )
    {
        if ( mavenSession == null )
        {
            throw new IllegalArgumentException( "MavenSession must not be null!" );
        }

        return mavenSession.getCurrentProject();
    }

    /**
     * Get the existing DirectoryScanner used by this helper,
     * or create new a DirectoryScanner if none is yet set.
     * The DirectoryScanner is used for detecting changes in a directory
     */
    public DirectoryScanner getDirectoryScanner()
    {
        if ( directoryScanner == null )
        {
            directoryScanner = new DirectoryScanner();
        }

        return directoryScanner;
    }

    /**
     * Set the DirectoryScanner which shall get used by this build helper.
     * @param directoryScanner
     */
    public void setDirectoryScanner( DirectoryScanner directoryScanner )
    {
        this.directoryScanner = directoryScanner;
    }

    /**
     * We use a specific status directory for each Mojo execution to store state
     * which is needed during the next build invocation run.
     * @return the directory for storing status information of the current Mojo execution.
     */
    public File getMojoStatusDirectory()
        throws MojoExecutionException
    {
        if ( mojoExecution == null )
        {
            throw new MojoExecutionException( "MojoExecution could not get resolved" );
        }

        File buildOutputDirectory = new File( mavenProject.getBuild().getDirectory() );

        //X TODO the executionId contains -cli and -mojoname
        //X we should remove those postfixes as it should not make
        //X any difference whether being run on the cli or via build
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
     * Detect whether the list of detected files has changed since the last build.
     * We simply load the list of files for the previous build from a status file
     * and compare it with the new list. Afterwards we store the new list in the status file.
     *
     * @param inputFiles
     * @return <code>true</code> if the set of inputFiles got changed since the last build.
     * @throws MojoExecutionException
     */
    public boolean inputFileTreeChanged( Set<File> inputFiles )
        throws MojoExecutionException
    {
        File mojoConfigBase = getMojoStatusDirectory();
        File mojoConfigFile = new File( mojoConfigBase, INPUT_FILES_LST_FILENAME );

        String[] oldInputFiles = new String[0];

        if ( mojoConfigFile.exists() )
        {
            try
            {
                oldInputFiles = FileUtils.fileReadArray( mojoConfigFile );
            }
            catch( IOException e )
            {
                throw new MojoExecutionException( "Error reading old mojo status " + mojoConfigFile, e );
            }
        }

        String[] inputFileNames = new String[ inputFiles.size() ];
        int i = 0;
        for ( File inputFile : inputFiles )
        {
            inputFileNames[ i++ ] = inputFile.getAbsolutePath();
        }

        DirectoryScanResult dsr = DirectoryScanner.diffFiles( oldInputFiles, inputFileNames );

        try
        {
            FileUtils.fileWriteArray( mojoConfigFile, inputFileNames );
        }
        catch( IOException e )
        {
            throw new MojoExecutionException( "Error while storing the mojo status", e );
        }

        return ( dsr.getFilesAdded().length > 0 || dsr.getFilesRemoved().length > 0 );
    }

    /**
     * Detect whether the list of detected files picked up by the DirectoryScanner
     * has changed since the last build.
     * We simply load the list of files for the previous build from a status file
     * and compare it with the result of the new DirectoryScanner#scan().
     * Afterwards we store the new list in the status file.
     *
     * @param dirScanner
     * @return <code>true</code> if the set of inputFiles got changed since the last build.
     * @throws MojoExecutionException
     */
    public boolean inputFileTreeChanged( DirectoryScanner dirScanner )
        throws MojoExecutionException
    {
        File mojoConfigBase = getMojoStatusDirectory();
        File mojoConfigFile = new File( mojoConfigBase, INPUT_FILES_LST_FILENAME );

        String[] oldInputFiles = new String[0];

        if ( mojoConfigFile.exists() )
        {
            try
            {
                oldInputFiles = FileUtils.fileReadArray( mojoConfigFile );
            }
            catch( IOException e )
            {
                throw new MojoExecutionException( "Error reading old mojo status " + mojoConfigFile, e );
            }
        }

        dirScanner.scan();

        try
        {
            // store away the list of input files
            FileUtils.fileWriteArray( mojoConfigFile, dirScanner.getIncludedFiles() );
        }
        catch( IOException e )
        {
            throw new MojoExecutionException( "Error while storing new mojo status" + mojoConfigFile, e );
        }

        DirectoryScanResult dsr = dirScanner.diffIncludedFiles( oldInputFiles );

        return ( dsr.getFilesAdded().length > 0 || dsr.getFilesRemoved().length > 0 );
    }

    /**
     * <p>This method shall get invoked before the actual Mojo task gets triggered,
     * e.g. the actual compile in maven-compiler-plugin.</p>
     *
     * <p><b>Attention:</b> This method shall only get invoked if the plugin re-creates <b>all</b> the output.</p>
     *
     * <p>It first picks up the list of files created in the previous build and delete them.
     * This step is necessary to prevent left-overs. After that we take a 'directory snapshot'
     * (list of all files which exist in the outputDirectory after the clean). </p>
     *
     * <p>After the actual Mojo task got executed you should invoke the method
     * {@link #afterRebuildExecution()} to collect the list of files which got changed
     * by this task.</p>
     *
     *
     * @param outputDirectory
     * @return all files which got created in the previous build and have been deleted now.
     * @throws MojoExecutionException
     */
    public String[] beforeRebuildExecution( File outputDirectory )
        throws MojoExecutionException
    {
        File mojoConfigBase = getMojoStatusDirectory();
        File mojoConfigFile = new File( mojoConfigBase, CREATED_FILES_LST_FILENAME );

        String[] oldFiles;

        try
        {
            oldFiles = FileUtils.fileReadArray( mojoConfigFile );
            for ( String oldFileName : oldFiles )
            {
                File oldFile = new File( outputDirectory, oldFileName );
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
            filesBeforeAction = diffScanner.getIncludedFiles();
        }

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
    public void afterRebuildExecution()
        throws MojoExecutionException
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
