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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.Scanner;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" 
 *                   role-hint="default"
 */
public class DefaultMavenResourcesFiltering
    extends AbstractLogEnabled
    implements MavenResourcesFiltering, Initializable
{

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String[] DEFAULT_INCLUDES = {"**/**"};
    
    private List defaultNonFilteredFileExtensions;

    /** @plexus.requirement */
    private BuildContext buildContext;

    // ------------------------------------------------
    //  Plexus lifecycle
    // ------------------------------------------------
    public void initialize()
        throws InitializationException
    {
        // jpg,jpeg,gif,bmp,png
        this.defaultNonFilteredFileExtensions = new ArrayList( 5 );
        this.defaultNonFilteredFileExtensions.add( "jpg" );
        this.defaultNonFilteredFileExtensions.add( "jpeg" );
        this.defaultNonFilteredFileExtensions.add( "gif" );
        this.defaultNonFilteredFileExtensions.add( "bmp" );
        this.defaultNonFilteredFileExtensions.add( "png" );
    }    
    
    
    
    /**
     * @plexus.requirement
     *  role-hint="default"
     */
    private MavenFileFilter mavenFileFilter;
    
    public void filterResources( List resources, File outputDirectory, MavenProject mavenProject, String encoding,
                                 List fileFilters, List nonFilteredFileExtensions, MavenSession mavenSession )
        throws MavenFilteringException
    {
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       mavenProject, encoding,
                                                                                       fileFilters,
                                                                                       nonFilteredFileExtensions,
                                                                                       mavenSession );
        mavenResourcesExecution.setUseDefaultFilterWrappers( true );
//        mavenResourcesExecution.setEscapeWindowsPaths( false );
        
        filterResources( mavenResourcesExecution );
    }

    public void filterResources( List resources, File outputDirectory, String encoding, List filterWrappers,
                                 File resourcesBaseDirectory, List nonFilteredFileExtensions )
        throws MavenFilteringException
    {
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution( resources, outputDirectory,
                                                                                       encoding, filterWrappers,
                                                                                       resourcesBaseDirectory,
                                                                                       nonFilteredFileExtensions );
        filterResources( mavenResourcesExecution );
    }

    
    public boolean filteredFileExtension( String fileName, List userNonFilteredFileExtensions )
    {
        List nonFilteredFileExtensions = new ArrayList( getDefaultNonFilteredFileExtensions() );
        if ( userNonFilteredFileExtensions != null )
        {
            nonFilteredFileExtensions.addAll( userNonFilteredFileExtensions );
        }
        boolean filteredFileExtension = !nonFilteredFileExtensions.contains( StringUtils.lowerCase( FileUtils
            .extension( fileName ) ) );
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug(
                               "file " + fileName + " has a" + ( filteredFileExtension ? " " : " non " )
                                   + "filtered file extension" );
        }
        return filteredFileExtension;
    }

    public List getDefaultNonFilteredFileExtensions()
    {
        return this.defaultNonFilteredFileExtensions;
    }

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
            List filterWrappers = new ArrayList();
            if ( mavenResourcesExecution.getFilterWrappers() != null )
            {
                filterWrappers.addAll( mavenResourcesExecution.getFilterWrappers() );
            }
            filterWrappers.addAll( mavenFileFilter.getDefaultFilterWrappers( mavenResourcesExecution ) );
            mavenResourcesExecution.setFilterWrappers( filterWrappers );
        }

        if ( mavenResourcesExecution.getEncoding() == null || mavenResourcesExecution.getEncoding().length() < 1 )
        {
            getLogger().warn(
                              "Using platform encoding (" + ReaderFactory.FILE_ENCODING
                                  + " actually) to copy filtered resources, i.e. build is platform dependent!" );
        }
        else
        {
            getLogger().info(
                              "Using '" + mavenResourcesExecution.getEncoding()
                                  + "' encoding to copy filtered resources." );
        }
        
        for ( Iterator i = mavenResourcesExecution.getResources().iterator(); i.hasNext(); )
        {
            Resource resource = (Resource) i.next();
            
            if ( getLogger().isDebugEnabled() )
            {
                String ls = System.getProperty( "line.separator" );
                StringBuffer debugMessage = new StringBuffer( "resource with targetPath " + resource.getTargetPath() )
                    .append( ls );
                debugMessage.append( "directory " + resource.getDirectory() ).append( ls );
                debugMessage.append(
                                     "excludes "
                                         + ( resource.getExcludes() == null ? " empty " : resource.getExcludes()
                                             .toString() ) ).append( ls );
                debugMessage.append( "includes "
                    + ( resource.getIncludes() == null ? " empty " : resource.getIncludes().toString() ) );
                getLogger().debug( debugMessage.toString() );
            }

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File( resource.getDirectory() );

            if ( !resourceDirectory.isAbsolute() )
            {
                resourceDirectory = new File( mavenResourcesExecution.getResourcesBaseDirectory(), resourceDirectory
                    .getPath() );
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

            setupScanner(resource, scanner);

            scanner.scan();

            if ( mavenResourcesExecution.isIncludeEmptyDirs() )
            {
                try
                {
                    File targetDirectory = targetPath == null ? outputDirectory
                                                             : new File( outputDirectory, targetPath );
                    copyDirectoryLayout( resourceDirectory, targetDirectory, scanner );
                }
                catch ( IOException e )
                {
                    throw new MavenFilteringException( "Cannot copy directory structure from "
                        + resourceDirectory.getPath() + " to " + outputDirectory.getPath() );
                }
            }

            List includedFiles = Arrays.asList( scanner.getIncludedFiles() );

            getLogger().info(
                              "Copying " + includedFiles.size() + " resource" + ( includedFiles.size() > 1 ? "s" : "" )
                                  + ( targetPath == null ? "" : " to " + targetPath ) );

            for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
            {
                String name = (String) j.next();

                File source = new File( resourceDirectory, name );

                //File destinationFile = new File( outputDirectory, destination );

                File destinationFile = getDestinationFile(outputDirectory, targetPath, name);
                
                boolean filteredExt = filteredFileExtension( source.getName(), mavenResourcesExecution
                    .getNonFilteredFileExtensions() );
                
                mavenFileFilter.copyFile( source, destinationFile, resource.isFiltering() && filteredExt,
                                          mavenResourcesExecution.getFilterWrappers(), mavenResourcesExecution
                                              .getEncoding(), mavenResourcesExecution.isOverwrite() );
            }

            // deal with deleted source files

            scanner = buildContext.newDeleteScanner( resourceDirectory );
            
            setupScanner(resource, scanner);
            
            scanner.scan();

            List deletedFiles = Arrays.asList( scanner.getIncludedFiles() );

            for ( Iterator j = deletedFiles.iterator(); j.hasNext(); )
            {
                String name = (String) j.next();

                File destinationFile = getDestinationFile(outputDirectory, targetPath, name);
                
                destinationFile.delete();
                
                buildContext.refresh( destinationFile );
            }

        }

    }

    private File getDestinationFile(File outputDirectory, String targetPath, String name) {
      String destination = name;

      if ( targetPath != null )
      {
          destination = targetPath + "/" + name;
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

    private String[] setupScanner(Resource resource, Scanner scanner) {
      String[] includes = null;
      if ( resource.getIncludes() != null && !resource.getIncludes().isEmpty() )
      {
          includes = (String[]) resource.getIncludes().toArray( EMPTY_STRING_ARRAY ) ;
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

        List includedDirectories = Arrays.asList( scanner.getIncludedDirectories() );

        for ( Iterator i = includedDirectories.iterator(); i.hasNext(); )
        {
            String name = (String) i.next();

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

}
