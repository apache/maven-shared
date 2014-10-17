package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.PathTool;
import org.apache.maven.shared.utils.ReaderFactory;
import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;
import org.apache.maven.shared.utils.io.IOUtil;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * @author Olivier Lamy
 * @plexus.component role="org.apache.maven.shared.filtering.MavenResourcesFiltering"
 *                   role-hint="default"
 */
/**
 * @author kama
 *
 */
public class DefaultMavenResourcesFiltering
    extends AbstractLogEnabled
    implements MavenResourcesFiltering, Initializable
{

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String[] DEFAULT_INCLUDES = { "**/**" };

    private List<String> defaultNonFilteredFileExtensions;

    /**
     * @plexus.requirement
     */
    private BuildContext buildContext;

    // ------------------------------------------------
    // Plexus lifecycle
    // ------------------------------------------------
    /* (non-Javadoc)
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void initialize()
        throws InitializationException
    {
        // jpg,jpeg,gif,bmp,png
        this.defaultNonFilteredFileExtensions = new ArrayList<String>( 5 );
        this.defaultNonFilteredFileExtensions.add( "jpg" );
        this.defaultNonFilteredFileExtensions.add( "jpeg" );
        this.defaultNonFilteredFileExtensions.add( "gif" );
        this.defaultNonFilteredFileExtensions.add( "bmp" );
        this.defaultNonFilteredFileExtensions.add( "png" );
    }

    /**
     * @plexus.requirement role-hint="default"
     */
    private MavenFileFilter mavenFileFilter;

    /* (non-Javadoc)
     * @see org.apache.maven.shared.filtering.MavenResourcesFiltering#filterResources(java.util.List, java.io.File, org.apache.maven.project.MavenProject, java.lang.String, java.util.List, java.util.List, org.apache.maven.execution.MavenSession)
     */
    public void filterResources( List<Resource> resources, File outputDirectory, MavenProject mavenProject,
                                 String encoding, List<String> fileFilters, List<String> nonFilteredFileExtensions,
                                 MavenSession mavenSession )
        throws MavenFilteringException
    {
        MavenResourcesExecution mavenResourcesExecution =
            new MavenResourcesExecution( resources, outputDirectory, mavenProject, encoding, fileFilters,
                                         nonFilteredFileExtensions, mavenSession );
        mavenResourcesExecution.setUseDefaultFilterWrappers( true );
        // mavenResourcesExecution.setEscapeWindowsPaths( false );

        filterResources( mavenResourcesExecution );
    }

    /* (non-Javadoc)
     * @see org.apache.maven.shared.filtering.MavenResourcesFiltering#filterResources(java.util.List, java.io.File, java.lang.String, java.util.List, java.io.File, java.util.List)
     */
    public void filterResources( List<Resource> resources, File outputDirectory, String encoding,
                                 List<FileUtils.FilterWrapper> filterWrappers, File resourcesBaseDirectory,
                                 List<String> nonFilteredFileExtensions )
        throws MavenFilteringException
    {
        MavenResourcesExecution mavenResourcesExecution =
            new MavenResourcesExecution( resources, outputDirectory, encoding, filterWrappers, resourcesBaseDirectory,
                                         nonFilteredFileExtensions );
        filterResources( mavenResourcesExecution );
    }

    /* (non-Javadoc)
     * @see org.apache.maven.shared.filtering.MavenResourcesFiltering#filteredFileExtension(java.lang.String, java.util.List)
     */
    public boolean filteredFileExtension( String fileName, List<String> userNonFilteredFileExtensions )
    {
        List<String> nonFilteredFileExtensions = new ArrayList<String>( getDefaultNonFilteredFileExtensions() );
        if ( userNonFilteredFileExtensions != null )
        {
            nonFilteredFileExtensions.addAll( userNonFilteredFileExtensions );
        }
        boolean filteredFileExtension =
            !nonFilteredFileExtensions.contains( StringUtils.lowerCase( FileUtils.extension( fileName ) ) );
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "file " + fileName + " has a" + ( filteredFileExtension ? " " : " non " )
                                   + "filtered file extension" );
        }
        return filteredFileExtension;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.shared.filtering.MavenResourcesFiltering#getDefaultNonFilteredFileExtensions()
     */
    public List<String> getDefaultNonFilteredFileExtensions()
    {
        if ( this.defaultNonFilteredFileExtensions == null )
        {
            this.defaultNonFilteredFileExtensions = new ArrayList<String>();
        }
        return this.defaultNonFilteredFileExtensions;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.shared.filtering.MavenResourcesFiltering#filterResources(org.apache.maven.shared.filtering.MavenResourcesExecution)
     */
    public void filterResources( MavenResourcesExecution mavenResourcesExecution )
        throws MavenFilteringException
    {
        if ( mavenResourcesExecution == null )
        {
            throw new MavenFilteringException( "mavenResourcesExecution cannot be null" );
        }

        if ( mavenResourcesExecution.getResources() == null )
        {
            getLogger().info( "No resources configured skip copying/filtering" );
            return;
        }

        if ( mavenResourcesExecution.getOutputDirectory() == null )
        {
            throw new MavenFilteringException( "outputDirectory cannot be null" );
        }

        if ( mavenResourcesExecution.isUseDefaultFilterWrappers() )
        {
            List<FileUtils.FilterWrapper> filterWrappers = new ArrayList<FileUtils.FilterWrapper>();
            if ( mavenResourcesExecution.getFilterWrappers() != null )
            {
                filterWrappers.addAll( mavenResourcesExecution.getFilterWrappers() );
            }
            filterWrappers.addAll( mavenFileFilter.getDefaultFilterWrappers( mavenResourcesExecution ) );
            mavenResourcesExecution.setFilterWrappers( filterWrappers );
        }

        if ( mavenResourcesExecution.getEncoding() == null || mavenResourcesExecution.getEncoding().length() < 1 )
        {
            getLogger().warn( "Using platform encoding (" + ReaderFactory.FILE_ENCODING
                                  + " actually) to copy filtered resources, i.e. build is platform dependent!" );
        }
        else
        {
            getLogger().info( "Using '" + mavenResourcesExecution.getEncoding()
                                  + "' encoding to copy filtered resources." );
        }

        for ( Resource resource : mavenResourcesExecution.getResources() )
        {

            if ( getLogger().isDebugEnabled() )
            {
                String ls = System.getProperty( "line.separator" );
                StringBuilder debugMessage =
                    new StringBuilder( "resource with targetPath " ).append( resource.getTargetPath() ).append( ls );
                debugMessage.append( "directory " ).append( resource.getDirectory() ).append( ls );
                debugMessage.append( "excludes " ).append( resource.getExcludes() == null ? " empty "
                                                                           : resource.getExcludes().toString() ).append( ls );
                debugMessage.append( "includes " ).append( resource.getIncludes() == null ? " empty "
                                                                           : resource.getIncludes().toString() );
                getLogger().debug( debugMessage.toString() );
            }

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File( resource.getDirectory() );

            if ( !resourceDirectory.isAbsolute() )
            {
                resourceDirectory =
                    new File( mavenResourcesExecution.getResourcesBaseDirectory(), resourceDirectory.getPath() );
            }

            if ( !resourceDirectory.exists() )
            {
                getLogger().info( "skip non existing resourceDirectory " + resourceDirectory.getPath() );
                continue;
            }

            // this part is required in case the user specified "../something" as destination
            // see MNG-1345
            File outputDirectory = mavenResourcesExecution.getOutputDirectory();
            boolean outputExists = outputDirectory.exists();
            if ( !outputExists && !outputDirectory.mkdirs() )
            {
                throw new MavenFilteringException( "Cannot create resource output directory: " + outputDirectory );
            }

            boolean ignoreDelta =
                !outputExists || buildContext.hasDelta( mavenResourcesExecution.getFileFilters() )
                    || buildContext.hasDelta( getRelativeOutputDirectory( mavenResourcesExecution ) );
            getLogger().debug( "ignoreDelta " + ignoreDelta );
            Scanner scanner = buildContext.newScanner( resourceDirectory, ignoreDelta );

            setupScanner( resource, scanner );

            scanner.scan();

            if ( mavenResourcesExecution.isIncludeEmptyDirs() )
            {
                try
                {
                    File targetDirectory =
                        targetPath == null ? outputDirectory : new File( outputDirectory, targetPath );
                    copyDirectoryLayout( resourceDirectory, targetDirectory, scanner );
                }
                catch ( IOException e )
                {
                    throw new MavenFilteringException( "Cannot copy directory structure from "
                        + resourceDirectory.getPath() + " to " + outputDirectory.getPath() );
                }
            }

            List<String> includedFiles = Arrays.asList( scanner.getIncludedFiles() );

            getLogger().info( "Copying " + includedFiles.size() + " resource" + ( includedFiles.size() > 1 ? "s" : "" )
                                  + ( targetPath == null ? "" : " to " + targetPath ) );

            for ( String name : includedFiles )
            {

                File source = new File( resourceDirectory, name );

                File destinationFile = getDestinationFile( outputDirectory, targetPath, name, mavenResourcesExecution );

                boolean filteredExt =
                    filteredFileExtension( source.getName(), mavenResourcesExecution.getNonFilteredFileExtensions() );

                mavenFileFilter.copyFile( source, destinationFile, resource.isFiltering() && filteredExt,
                                          mavenResourcesExecution.getFilterWrappers(),
                                          mavenResourcesExecution.getEncoding(), mavenResourcesExecution.isOverwrite() );
            }

            // deal with deleted source files

            scanner = buildContext.newDeleteScanner( resourceDirectory );

            setupScanner( resource, scanner );

            scanner.scan();

            List<String> deletedFiles = Arrays.asList( scanner.getIncludedFiles() );

            for ( String name : deletedFiles )
            {
                File destinationFile = getDestinationFile( outputDirectory, targetPath, name, mavenResourcesExecution );

                destinationFile.delete();

                buildContext.refresh( destinationFile );
            }

        }

    }

    private File getDestinationFile( File outputDirectory, String targetPath, String name,
                                     MavenResourcesExecution mavenResourcesExecution )
        throws MavenFilteringException
    {
        String destination = name;

        if ( mavenResourcesExecution.isFilterFilenames() && mavenResourcesExecution.getFilterWrappers().size() > 0 )
        {
            destination = filterFileName( destination, mavenResourcesExecution.getFilterWrappers() );
        }

        if ( targetPath != null )
        {
            destination = targetPath + "/" + destination;
        }

        File destinationFile = new File( destination );
        if ( !destinationFile.isAbsolute() )
        {
            destinationFile = new File( outputDirectory, destination );
        }

        if ( !destinationFile.getParentFile().exists() )
        {
            destinationFile.getParentFile().mkdirs();
        }
        return destinationFile;
    }

    private String[] setupScanner( Resource resource, Scanner scanner )
    {
        String[] includes = null;
        if ( resource.getIncludes() != null && !resource.getIncludes().isEmpty() )
        {
            includes = (String[]) resource.getIncludes().toArray( EMPTY_STRING_ARRAY );
        }
        else
        {
            includes = DEFAULT_INCLUDES;
        }
        scanner.setIncludes( includes );

        String[] excludes = null;
        if ( resource.getExcludes() != null && !resource.getExcludes().isEmpty() )
        {
            excludes = (String[]) resource.getExcludes().toArray( EMPTY_STRING_ARRAY );
            scanner.setExcludes( excludes );
        }

        scanner.addDefaultExcludes();
        return includes;
    }

    private void copyDirectoryLayout( File sourceDirectory, File destinationDirectory, Scanner scanner )
        throws IOException
    {
        if ( sourceDirectory == null )
        {
            throw new IOException( "source directory can't be null." );
        }

        if ( destinationDirectory == null )
        {
            throw new IOException( "destination directory can't be null." );
        }

        if ( sourceDirectory.equals( destinationDirectory ) )
        {
            throw new IOException( "source and destination are the same directory." );
        }

        if ( !sourceDirectory.exists() )
        {
            throw new IOException( "Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")." );
        }

        List<String> includedDirectories = Arrays.asList( scanner.getIncludedDirectories() );

        for ( String name : includedDirectories )
        {
            File source = new File( sourceDirectory, name );

            if ( source.equals( sourceDirectory ) )
            {
                continue;
            }

            File destination = new File( destinationDirectory, name );
            destination.mkdirs();
        }
    }

    private String getRelativeOutputDirectory( MavenResourcesExecution execution )
    {
        String relOutDir = execution.getOutputDirectory().getAbsolutePath();

        if ( execution.getMavenProject() != null && execution.getMavenProject().getBasedir() != null )
        {
            String basedir = execution.getMavenProject().getBasedir().getAbsolutePath();
            relOutDir = PathTool.getRelativeFilePath( basedir, relOutDir );
            if ( relOutDir == null )
            {
                relOutDir = execution.getOutputDirectory().getPath();
            }
            else
            {
                relOutDir = relOutDir.replace( '\\', '/' );
            }
        }

        return relOutDir;
    }

    /*
     * Filter the name of a file using the same mechanism for filtering the content of the file.
     */
    private String filterFileName( String name, List<FilterWrapper> wrappers )
        throws MavenFilteringException
    {

        Reader reader = new StringReader( name );
        for ( FilterWrapper wrapper : wrappers )
        {
            reader = wrapper.getReader( reader );
        }

        StringWriter writer = new StringWriter();

        try
        {
            IOUtil.copy( reader, writer );
        }
        catch ( IOException e )
        {
            throw new MavenFilteringException( "Failed filtering filename" + name, e );
        }

        String filteredFilename = writer.toString();

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "renaming filename " + name + " to " + filteredFilename );
        }
        return filteredFilename;
    }

}
