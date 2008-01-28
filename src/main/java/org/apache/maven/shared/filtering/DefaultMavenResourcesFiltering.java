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
package org.apache.maven.shared.filtering;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 28 janv. 08
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" 
 *                   role-hint="default"
 */
public class DefaultMavenResourcesFiltering
    implements MavenResourcesFiltering
{

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String[] DEFAULT_INCLUDES = {"**/**"};
    
    /**
     * @plexus.requirement
     *  role-hint="default"
     */
    private MavenFileFilter mavenFileFilter;
    
    public void filterResources( List resources, File outputDirectory, MavenProject mavenProject, String encoding,
                                 List fileFilters )
        throws MavenFilteringException
    {
        for ( Iterator i = resources.iterator(); i.hasNext(); )
        {
            Resource resource = (Resource) i.next();

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File( resource.getDirectory() );
            if ( !resourceDirectory.isAbsolute() )
            {
                resourceDirectory = new File( mavenProject.getBasedir(), resourceDirectory.getPath() );
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

            List filterWrappers = mavenFileFilter.getDefaultFilterWrappers( mavenProject, fileFilters, true );            
            
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
                mavenFileFilter.copyFile( source, destinationFile, resource.isFiltering(), filterWrappers, encoding );
            }
        }

    }

}
