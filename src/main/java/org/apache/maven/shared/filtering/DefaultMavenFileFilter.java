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

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;

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
                          boolean escapedBackslashesInFilePath, String encoding )
        throws MavenFilteringException
    {
        List filterWrappers = getDefaultFilterWrappers( mavenProject, filters, escapedBackslashesInFilePath );
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
                                          final boolean escapedBackslashesInFilePath )
        throws MavenFilteringException
    {

        final Properties filterProperties = new Properties();

        // System properties
        filterProperties.putAll( System.getProperties() );

        // Project properties
        filterProperties.putAll( mavenProject.getProperties() == null ? Collections.EMPTY_MAP : mavenProject.getProperties() );

        // Take a copy of filterProperties to ensure that evaluated filterTokens are not propagated
        // to subsequent filter files. NB this replicates current behaviour and seems to make sense.
        final Properties baseProps = new Properties();
        baseProps.putAll( filterProperties );

        if ( filters != null )
        {
            for ( Iterator i = filters.iterator(); i.hasNext(); )
            {
                String filterfile = (String) i.next();
                try
                {
                    Properties properties = PropertyUtils.loadPropertyFile( new File( filterfile ), baseProps );
                    filterProperties.putAll( properties );
                }
                catch ( IOException e )
                {
                    throw new MavenFilteringException( "Error loading property file '" + filterfile + "'", e );
                }
            }
        }
        
        List buildFilters = mavenProject.getFilters();
        if ( buildFilters != null )
        {
            for ( Iterator iterator = buildFilters.iterator(); iterator.hasNext(); )
            {
                String filterFile = (String) iterator.next();
                try
                {

                    Properties properties = PropertyUtils.loadPropertyFile( new File( filterFile ), baseProps );
                    filterProperties.putAll( properties );
                }
                catch ( IOException e )
                {
                    throw new MavenFilteringException( "Error loading property file '" + filterFile + "'", e );
                }
            }
        }

        buildFilters = mavenProject.getBuild().getFilters();
        if ( buildFilters != null )
        {
            for ( Iterator iterator = buildFilters.iterator(); iterator.hasNext(); )
            {
                String filterFile = (String) iterator.next();
                try
                {

                    Properties properties = PropertyUtils.loadPropertyFile( new File( filterFile ), baseProps );
                    filterProperties.putAll( properties );
                }
                catch ( IOException e )
                {
                    throw new MavenFilteringException( "Error loading property file '" + filterFile + "'", e );
                }
            }
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

}
