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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * @since 1.0-beta-3
 */
public class AbstractMavenFilteringRequest
{

    private MavenProject mavenProject;

    private List<String> filters;

    private boolean escapeWindowsPaths = true;

    private MavenSession mavenSession;

    /**
     * List of Strings considered as expressions which contains values in the project/pom: pom project Default value
     * will be pom and project.
     *
     * @since 1.0-beta-2
     */
    private List<String> projectStartExpressions = new ArrayList<String>();

    /**
     * String which will escape interpolation mechanism: foo \${foo.bar} -> foo ${foo.bar}
     *
     * @since 1.0-beta-2
     */
    private String escapeString;

    /**
     * @since 1.0-beta-3
     */
    private Properties additionalProperties;

    /**
     * @since 1.0-beta-3
     */
    private boolean injectProjectBuildFilters = false;

    /**
     * Set of expression delimiter specifications to use during filtering. Delimiter specifications are given in the
     * form 'BEGIN*END' or, for symmetrical delimiters, simply 'TOKEN'. The default values are '${*}' and '@'.
     * 
     * @since 1.0-beta-3
     */
    private LinkedHashSet<String> delimiters = new LinkedHashSet<String>();

    /**
     * Do not stop trying to filter tokens when reaching EOL.
     *
     * @since 1.0
     */
    private boolean supportMultiLineFiltering;

    /**
     * Create instance.
     */
    protected AbstractMavenFilteringRequest()
    {
        initDefaults();
    }

    /**
     * Create instance with given parameters
     * 
     * @param mavenProject The instance of MavenProject.
     * @param filters The list of filters.
     * @param mavenSession The MavenSession.
     */
    protected AbstractMavenFilteringRequest( MavenProject mavenProject, List<String> filters,
                                             MavenSession mavenSession )
    {
        initDefaults();
        this.mavenProject = mavenProject;
        this.filters = filters;
        this.mavenSession = mavenSession;
    }

    private void initDefaults()
    {
        projectStartExpressions.add( "pom" );
        projectStartExpressions.add( "project" );

        delimiters.add( "${*}" );
        delimiters.add( "@" );
    }

    /**
     * @return The MavenProject
     */
    public MavenProject getMavenProject()
    {
        return mavenProject;
    }

    /**
     * Set the MavenProject.
     * 
     * @param mavenProject The MavenProject to be set.
     */
    public void setMavenProject( MavenProject mavenProject )
    {
        this.mavenProject = mavenProject;
    }

    /**
     * The list of filters.
     * 
     * @return The list of currently set filters.
     */
    public List<String> getFilters()
    {
        return filters;
    }

    /**
     * Set the filters.
     * 
     * @param filters Set the list of filters
     */
    public void setFilters( List<String> filters )
    {
        this.filters = filters;
    }

    /**
     * Alias for {@link #getFilters()}.
     * 
     * @return The list of filters.
     */
    public List<String> getFileFilters()
    {
        return getFilters();
    }

    /**
     * Alias for {@link #setFilters(List)}
     * 
     * @param paramfilters The list of filters to be set.
     */
    public void setFileFilters( List<String> paramfilters )
    {
        setFilters( paramfilters );
    }

    /**
     * @since 1.0-beta-3
     * @return true if escape is activated false otherwise.
     */
    public boolean isEscapeWindowsPaths()
    {
        return escapeWindowsPaths;
    }

    /**
     * @since 1.0-beta-3
     * @param escapedBackslashesInFilePath true or false.
     */
    public void setEscapeWindowsPaths( boolean escapedBackslashesInFilePath )
    {
        this.escapeWindowsPaths = escapedBackslashesInFilePath;
    }

    /**
     * Alias for {@link #isEscapeWindowsPaths()}
     * 
     * @return The current value of {@link #isEscapeWindowsPaths()}
     */
    public boolean isEscapedBackslashesInFilePath()
    {
        return isEscapeWindowsPaths();
    }

    /**
     * Alias for {@link #setEscapeWindowsPaths(boolean)}
     * 
     * @param escape activate or deactivate escaping.
     */
    public void setEscapedBackslashesInFilePath( boolean escape )
    {
        setEscapeWindowsPaths( escape );
    }

    /**
     * @return Current value of mavenSession
     */
    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    /**
     * @param mavenSession Set new value for the MavenSession of the instance.
     */
    public void setMavenSession( MavenSession mavenSession )
    {
        this.mavenSession = mavenSession;
    }

    /**
     * @return the additional properties.
     * @since 1.0-beta-3
     */
    public Properties getAdditionalProperties()
    {
        return additionalProperties;
    }

    /**
     * @param additionalProperties The additional properties to be set.
     * @since 1.0-beta-3
     */
    public void setAdditionalProperties( Properties additionalProperties )
    {
        this.additionalProperties = additionalProperties;
    }

    /**
     * @return the current value of injectProjectBuildFilters.
     * @since 1.0-beta-3
     */
    public boolean isInjectProjectBuildFilters()
    {
        return injectProjectBuildFilters;
    }

    /**
     * @param injectProjectBuildFilters true or false.
     * @since 1.0-beta-3
     */
    public void setInjectProjectBuildFilters( boolean injectProjectBuildFilters )
    {
        this.injectProjectBuildFilters = injectProjectBuildFilters;
    }

    /**
     * @return Current value of escapeString.
     * @since 1.0-beta-2
     */
    public String getEscapeString()
    {
        return escapeString;
    }

    /**
     * @param escapeString The escape string to use
     * @since 1.0-beta-2
     */
    public void setEscapeString( String escapeString )
    {
        this.escapeString = escapeString;
    }

    /**
     * @return The list of project start expressions.
     * @since 1.0-beta-2
     */
    public List<String> getProjectStartExpressions()
    {
        return projectStartExpressions;
    }

    /**
     * @param projectStartExpressions The start expressions
     * @since 1.0-beta-2
     */
    public void setProjectStartExpressions( List<String> projectStartExpressions )
    {
        this.projectStartExpressions = projectStartExpressions;
    }

    /**
     * See {@link AbstractMavenFilteringRequest#delimiters} for more information and default values.
     *
     * @return Not allowed to be <code>null</code> or empty.
     * @since 1.0-beta-3
     */
    public LinkedHashSet<String> getDelimiters()
    {
        return delimiters;
    }

    /**
     * Set the delimiter specifications to use during filtering. Specifications should be of the form: 'BEGIN*END' for
     * asymmetrical delimiters, or 'TOKEN' for symmetrical delimiters. See
     * {@link AbstractMavenFilteringRequest#delimiters} for more information and default values.
     * 
     * @param delimiters If <code>null</code>, reset delimiters to '${*}' only. Otherwise, use the provided value.
     * @since 1.0-beta-3
     */
    public void setDelimiters( LinkedHashSet<String> delimiters )
    {
        if ( delimiters == null || delimiters.isEmpty() )
        {
            this.delimiters.clear();
            this.delimiters.add( "${*}" );
        }
        else
        {
            this.delimiters = delimiters;
        }
    }

    /**
     * @param delimiters If {@code null} than nothing will happen. If not {@code null} the delimiters will be set
     *            according to the contents. If delimiter entries are {@code null} those entries will be set to '${*}'.
     * @param useDefaultDelimiters true if the default delimiters will be used false otherwise.
     */
    public void setDelimiters( LinkedHashSet<String> delimiters, boolean useDefaultDelimiters )
    {
        if ( delimiters != null && !delimiters.isEmpty() )
        {
            LinkedHashSet<String> delims = new LinkedHashSet<String>();
            if ( useDefaultDelimiters )
            {
                delims.addAll( this.getDelimiters() );
            }

            for ( String delim : delimiters )
            {
                if ( delim == null )
                {
                    // FIXME: ${filter:*} could also trigger this condition. Need a better long-term solution.
                    delims.add( "${*}" );
                }
                else
                {
                    delims.add( delim );
                }
            }

            this.setDelimiters( delims );
        }

    }

    /**
     * @return If support multiple line filtering is active or not.
     */
    public boolean isSupportMultiLineFiltering()
    {
        return supportMultiLineFiltering;
    }

    /**
     * @param supportMultiLineFiltering activate or deactivate multiple line filtering support.
     */
    public void setSupportMultiLineFiltering( boolean supportMultiLineFiltering )
    {
        this.supportMultiLineFiltering = supportMultiLineFiltering;
    }

}
