package org.apache.maven.shared.filtering;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

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

/**
 * @author <a href="mailto:olamy@apache">olamy</a>
 *
 * @since 1.0-beta-3
 */
public class MavenFileFilterRequest
{
    
    private File from;

    private File to;

    private boolean filtering;

    private MavenProject mavenProject;

    private List filters;

    private boolean escapedBackslashesInFilePath;

    private String encoding;

    private MavenSession mavenSession;

    private Properties additionnalProperties;

    public MavenFileFilterRequest()
    {
        // nothing
    }

    public MavenFileFilterRequest( File from, File to, boolean filtering, MavenProject mavenProject, List filters,
                                   boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession,
                                   Properties additionnalProperties )
    {
        this.from = from;
        this.to = to;
        this.filtering = filtering;
        this.mavenProject = mavenProject;
        this.filters = filters;
        this.escapedBackslashesInFilePath = escapedBackslashesInFilePath;
        this.encoding = encoding;
        this.mavenSession = mavenSession;
        this.additionnalProperties = additionnalProperties;
    }


    public File getFrom()
    {
        return from;
    }

    public void setFrom( File from )
    {
        this.from = from;
    }

    public File getTo()
    {
        return to;
    }

    public void setTo( File to )
    {
        this.to = to;
    }

    public boolean isFiltering()
    {
        return filtering;
    }

    public void setFiltering( boolean filtering )
    {
        this.filtering = filtering;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    public List getFilters()
    {
        return filters;
    }

    public void setFilters( List filters )
    {
        this.filters = filters;
    }

    public boolean isEscapedBackslashesInFilePath()
    {
        return escapedBackslashesInFilePath;
    }

    public void setEscapedBackslashesInFilePath( boolean escapedBackslashesInFilePath )
    {
        this.escapedBackslashesInFilePath = escapedBackslashesInFilePath;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public void setMavenSession( MavenSession mavenSession )
    {
        this.mavenSession = mavenSession;
    }

    public Properties getAdditionnalProperties()
    {
        return additionnalProperties;
    }

    public void setAdditionnalProperties( Properties additionnalProperties )
    {
        this.additionnalProperties = additionnalProperties;
    }    

}
