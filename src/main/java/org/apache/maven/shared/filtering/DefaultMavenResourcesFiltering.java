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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 28 janv. 08
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" 
 *                   role-hint="default"
 */
public class DefaultMavenResourcesFiltering
    implements MavenResourcesFiltering, Initializable
{

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String[] DEFAULT_INCLUDES = {"**/**"};
    
    private List defaultNonFilteredFileExtensions;
    
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
                                 List fileFilters, List nonFilteredFileExtensions )
        throws MavenFilteringException
    {
        List filterWrappers = mavenFileFilter.getDefaultFilterWrappers( mavenProject, fileFilters, true );

        filterResources( resources, outputDirectory, encoding, filterWrappers, mavenProject.getBasedir(),
                         nonFilteredFileExtensions );
    }

    public void filterResources( List resources, File outputDirectory, String encoding, List filterWrappers,
                                 File resourcesBaseDirectory, List nonFilteredFileExtensions )
        throws MavenFilteringException
    {
        for ( Iterator i = resources.iterator(); i.hasNext(); )
        {
            Resource resource = (Resource) i.next();

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File( resource.getDirectory() );

            if ( !resourceDirectory.isAbsolute() )
            {
                resourceDirectory = new File( resourcesBaseDirectory, resourceDirectory.getPath() );
            }

            if ( !resourceDirectory.exists() )
            {
                // TODO how to log here ?
                continue;
            }

            // this part is required in case the user specified "../something" as destination
            // see MNG-1345
            if ( !outputDirectory.exists() )
            {
                if ( !outputDirectory.mkdirs() )
                {
                    throw new MavenFilteringException( "Cannot create resource output directory: " + outputDirectory );
                }
            }

            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir( resourceDirectory );
            if ( resource.getIncludes() != null && !resource.getIncludes().isEmpty() )
            {
                scanner.setIncludes( (String[]) resource.getIncludes().toArray( EMPTY_STRING_ARRAY ) );
            }
            else
            {
                scanner.setIncludes( DEFAULT_INCLUDES );
            }

            if ( resource.getExcludes() != null && !resource.getExcludes().isEmpty() )
            {
                scanner.setExcludes( (String[]) resource.getExcludes().toArray( EMPTY_STRING_ARRAY ) );
            }

            scanner.addDefaultExcludes();
            scanner.scan();

            List includedFiles = Arrays.asList( scanner.getIncludedFiles() );

            for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
            {
                String name = (String) j.next();

                String destination = name;

                if ( targetPath != null )
                {
                    destination = targetPath + "/" + name;
                }

                File source = new File( resourceDirectory, name );

                File destinationFile = new File( outputDirectory, destination );

                if ( !destinationFile.getParentFile().exists() )
                {
                    destinationFile.getParentFile().mkdirs();
                }
                boolean filteredExt = filteredFileExtension( source.getName(), nonFilteredFileExtensions );
                mavenFileFilter.copyFile( source, destinationFile, resource.isFiltering() && filteredExt,
                                          filterWrappers, encoding );
            }
        }

    }

    
    public boolean filteredFileExtension( String fileName, List userNonFilteredFileExtensions )
    {
        List nonFilteredFileExtensions = new ArrayList( getDefaultNonFilteredFileExtensions() );
        if ( userNonFilteredFileExtensions != null )
        {
            nonFilteredFileExtensions.addAll( userNonFilteredFileExtensions );
        }
        return !nonFilteredFileExtensions.contains( StringUtils.lowerCase( FileUtils.extension( fileName ) ) );
    }

    public List getDefaultNonFilteredFileExtensions()
    {
        return this.defaultNonFilteredFileExtensions;
    }
    
    
}
