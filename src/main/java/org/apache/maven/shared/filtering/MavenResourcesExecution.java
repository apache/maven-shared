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
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils.FilterWrapper;

/**
 * A bean to configure a resources filtering execution
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 27 févr. 2008
 * @version $Id$
 */
public class MavenResourcesExecution
{

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

    public List getResources()
    {
        return resources;
    }

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

    public List getFileFilters()
    {
        return fileFilters;
    }

    public void setFileFilters( List fileFilters )
    {
        this.fileFilters = fileFilters;
    }

    public List getNonFilteredFileExtensions()
    {
        return nonFilteredFileExtensions;
    }

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

    public List getFilterWrappers()
    {
        return filterWrappers;
    }

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
