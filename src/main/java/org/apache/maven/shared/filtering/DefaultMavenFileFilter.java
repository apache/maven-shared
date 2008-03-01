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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 22 janv. 08
 * @version $Id$
 * 
 * @plexus.component role="org.apache.maven.shared.filtering.MavenFileFilter" 
 *                   role-hint="default"
 */
public class DefaultMavenFileFilter
    implements MavenFileFilter
{

    public void copyFile( File from, File to, boolean filtering, MavenProject mavenProject, List filters,
                          boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession )
        throws MavenFilteringException
    {
        List filterWrappers = getDefaultFilterWrappers( mavenProject, filters, escapedBackslashesInFilePath, mavenSession );
        copyFile( from, to, filtering, filterWrappers, encoding );
    }

    public void copyFile( File from, File to, boolean filtering, List filterWrappers, String encoding )
        throws MavenFilteringException
    {

        try
        {
            if ( filtering )
            {
                FileUtils.FilterWrapper[] wrappers = (FileUtils.FilterWrapper[]) filterWrappers
                    .toArray( new FileUtils.FilterWrapper[filterWrappers.size()] );
                FileUtils.copyFile( from, to, encoding, wrappers );
            }
            else
            {
                FileUtils.copyFile( from, to, encoding, new FileUtils.FilterWrapper[0] );
            }
        }
        catch ( IOException e )
        {
            throw new MavenFilteringException( e.getMessage(), e );
        }

    }

    public List getDefaultFilterWrappers( final MavenProject mavenProject, List filters,
                                          final boolean escapedBackslashesInFilePath, MavenSession mavenSession )
        throws MavenFilteringException
    {
        
        // here we build some properties which will be used to read some properties files
        // to interpolate the expression ${ }  in this properties file

        // Take a copy of filterProperties to ensure that evaluated filterTokens are not propagated
        // to subsequent filter files. NB this replicates current behaviour and seems to make sense.
        
        final Properties baseProps = new Properties();

        // Project properties
        baseProps.putAll( mavenProject.getProperties() == null ? Collections.EMPTY_MAP : mavenProject
            .getProperties() );    
        // TODO this is NPE free but do we consider this as normal
        // or do we have to throw an MavenFilteringException with mavenSession cannot be null
        if ( mavenSession != null )
        {
            // execution properties wins
            baseProps.putAll( mavenSession.getExecutionProperties() );
        }
        
        // now we build properties to use for resources interpolation
        
        final Properties filterProperties = new Properties();
        
        loadProperties( filterProperties, filters, baseProps );

        loadProperties( filterProperties, mavenProject.getFilters(), baseProps );

        loadProperties( filterProperties, mavenProject.getBuild().getFilters(), baseProps );

        // Project properties
        filterProperties.putAll( mavenProject.getProperties() == null ? Collections.EMPTY_MAP : mavenProject
            .getProperties() );     
        if ( mavenSession != null )
        {
            // execution properties wins
            filterProperties.putAll( mavenSession.getExecutionProperties() );
        }
        
        List defaultFilterWrappers = new ArrayList( 3 );

        // support ${token}
        FileUtils.FilterWrapper one = new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                return new InterpolationFilterReader( reader, filterProperties, "${", "}" );
            }
        };
        defaultFilterWrappers.add( one );

        // support @token@
        FileUtils.FilterWrapper second = new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                return new InterpolationFilterReader( reader, filterProperties, "@", "@" );
            }
        };
        defaultFilterWrappers.add( second );
        // support ${token} with mavenProject reflection
        FileUtils.FilterWrapper third = new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                ReflectionProperties reflectionProperties = new ReflectionProperties( mavenProject,
                                                                                      escapedBackslashesInFilePath );
                return new InterpolationFilterReader( reader, reflectionProperties, "${", "}" );
            }
        };

        defaultFilterWrappers.add( third );

        return defaultFilterWrappers;
    }

    private void loadProperties( Properties filterProperties, List /*String*/propertiesFilePaths, Properties baseProps )
        throws MavenFilteringException
    {
        if ( propertiesFilePaths != null )
        {
            for ( Iterator iterator = propertiesFilePaths.iterator(); iterator.hasNext(); )
            {
                String filterFile = (String) iterator.next();
                if (StringUtils.isEmpty( filterFile ))
                {
                    // skip empty file name
                    continue;
                }
                try
                {
                    // TODO new File should be new File(mavenProject.getBasedir(), filterfile ) ? 
                    Properties properties = PropertyUtils.loadPropertyFile( new File( filterFile ), baseProps );
                    filterProperties.putAll( properties );
                }
                catch ( IOException e )
                {
                    throw new MavenFilteringException( "Error loading property file '" + filterFile + "'", e );
                }
            }
        }
    }

}
