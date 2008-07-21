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
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.InterpolatorFilterReader;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;

/**
 * A bean to configure a resources filtering execution
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 27 févr. 2008
 * @version $Id$
 */
public class MavenResourcesExecution
{

    /** @see Resource  */
    private List resources;

    private File outputDirectory;

    private MavenProject mavenProject;

    private String encoding;

    private List fileFilters;

    private List nonFilteredFileExtensions;

    private MavenSession mavenSession;

    private List filterWrappers;

    private File resourcesBaseDirectory;

    private boolean useDefaultFilterWrappers = false;
    
    
    public MavenResourcesExecution()
    {
        // nothing here just add an empty constructor for java bean convention
    }
    
    /**
     * <b>As we use a maven project useDefaultFilterWrappers will set to true</b>
     * @param resources
     * @param outputDirectory
     * @param mavenProject
     * @param encoding
     * @param fileFilters
     * @param nonFilteredFileExtensions
     * @param mavenSession
     */
    public MavenResourcesExecution( List resources, File outputDirectory, MavenProject mavenProject, String encoding,
                                    List fileFilters, List nonFilteredFileExtensions, MavenSession mavenSession )
    {
        this.resources = resources;
        this.outputDirectory = outputDirectory;
        this.mavenProject = mavenProject;
        this.encoding = encoding;
        this.fileFilters = fileFilters;
        this.nonFilteredFileExtensions = nonFilteredFileExtensions;
        this.mavenSession = mavenSession;
        this.useDefaultFilterWrappers = true;
        this.resourcesBaseDirectory = mavenProject.getBasedir();
    }

    public MavenResourcesExecution( List resources, File outputDirectory, String encoding, List filterWrappers,
                                    File resourcesBaseDirectory, List nonFilteredFileExtensions )
    {
        this.resources = resources;
        this.outputDirectory = outputDirectory;
        this.encoding = encoding;
        this.filterWrappers = filterWrappers;
        this.nonFilteredFileExtensions = nonFilteredFileExtensions;
        this.resourcesBaseDirectory = resourcesBaseDirectory;
        this.useDefaultFilterWrappers = false;
    }

    
    /**
     * @return List of {@link Resource}
     */
    public List getResources()
    {
        return resources;
    }

    /**
     * @param resources List of {@link Resource}
     */
    public void setResources( List resources )
    {
        this.resources = resources;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * @return can be null
     */
    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * @return List of {@link String} which are properties file
     */
    public List getFileFilters()
    {
        return fileFilters;
    }

    /**
     * @param fileFilters List of {@link String} which are properties file
     */
    public void setFileFilters( List fileFilters )
    {
        this.fileFilters = fileFilters;
    }

    /**
     * @return List of {@link String} file extensions to not filtering
     */
    public List getNonFilteredFileExtensions()
    {
        return nonFilteredFileExtensions;
    }

    /**
     * @param nonFilteredFileExtensions List of {@link String} file extensions to not filtering
     */
    public void setNonFilteredFileExtensions( List nonFilteredFileExtensions )
    {
        this.nonFilteredFileExtensions = nonFilteredFileExtensions;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public void setMavenSession( MavenSession mavenSession )
    {
        this.mavenSession = mavenSession;
    }

    /**
     * @return List of {@link FilterWrapper}
     */
    public List getFilterWrappers()
    {
        return filterWrappers;
    }

    /**
     * @param interpolators List of {@link FilterWrapper}
     */
    public void setFilterWrappers( List filterWrappers )
    {
        this.filterWrappers = filterWrappers;
    }
    
    public void addFilterWrapper( FilterWrapper filterWrapper )
    {
        if ( this.filterWrappers == null )
        {
            this.filterWrappers = new ArrayList();
        }
        this.filterWrappers.add( filterWrapper );
    }
    
    /**
     * Helper to add {@link FilterWrapper}, will {@link RegexBasedInterpolator} with default regex Exp ${ } 
     * and InterpolatorFilterReader with defaultTokens ${ }
     * @param valueSource 
     */
    public void addFilerWrapper( final ValueSource valueSource )
    {
        addFilterWrapper( new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                Interpolator propertiesInterpolator = new RegexBasedInterpolator();
                propertiesInterpolator.addValueSource( valueSource );
                return new InterpolatorFilterReader( reader, propertiesInterpolator );
            }
        } );
    }

    public void addFilerWrapper( final ValueSource valueSource, final String startRegExp, final String endRegExp,
                                 final String startToken, final String endToken )
    {
        addFilterWrapper( new FileUtils.FilterWrapper()
        {
            public Reader getReader( Reader reader )
            {
                Interpolator propertiesInterpolator = new RegexBasedInterpolator( startRegExp, endRegExp );
                propertiesInterpolator.addValueSource( valueSource );
                return new InterpolatorFilterReader( reader, propertiesInterpolator, startToken, endToken );
            }
        } );
    }    
    
    
    public File getResourcesBaseDirectory()
    {
        return resourcesBaseDirectory;
    }

    public void setResourcesBaseDirectory( File resourcesBaseDirectory )
    {
        this.resourcesBaseDirectory = resourcesBaseDirectory;
    }

    public boolean isUseDefaultFilterWrappers()
    {
        return useDefaultFilterWrappers;
    }

    public void setUseDefaultFilterWrappers( boolean useDefaultFilterWrappers )
    {
        this.useDefaultFilterWrappers = useDefaultFilterWrappers;
    }
   
}
