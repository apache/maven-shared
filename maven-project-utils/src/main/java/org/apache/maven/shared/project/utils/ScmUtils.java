package org.apache.maven.shared.project.utils;

import java.io.File;
import java.util.Map;

import org.apache.maven.model.Model;
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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public final class ScmUtils
{
    private ScmUtils()
    {
    }

    /**
     * Resolve the scm connection, based on the type of project and inheritance.
     * This method assumes that the connection ends with the path and can be extended.
     * 
     * @param project the Maven project
     * @return the resolved SCM connection, otherwise an empty String
     * @since 1.0
     */
    public static String resolveScmConnection( MavenProject project )
    {
        String scmConnection = getScmConnection( project.getModel() );

        if ( scmConnection == null )
        {
            // prevent null-value
            scmConnection = defaultString( getScmConnection( project ) );

            if ( !ProjectUtils.isRootProject( project ) )
            {
                Map<String, String> modules = ProjectUtils.getAllModules( project.getParent() );

                for ( String module : modules.keySet() )
                {
                    if ( new File( project.getParent().getBasedir(), module ).equals( project.getFile() ) )
                    {
                        return scmConnection + '/' + module;
                    }
                }
                // project is not a module of its parent, so use project's directoryname
                scmConnection += '/' + project.getFile().getParentFile().getName();
            }
        }
        return scmConnection;
    }

    /**
     * Resolve the scm developer connection, based on the type of project and inheritance.
     * This method assumes that the developer connection ends with the path and can be extended.
     * 
     * @param project the Maven Project
     * @return the resolved SCM developer connection, otherwise an empty String
     * @since 1.0
     */
    public static String resolveScmDeveloperConnection( MavenProject project )
    {
        String scmDeveloperConnection = getScmDeveloperConnection( project.getModel() );

        if ( scmDeveloperConnection == null )
        {
            // prevent null-value
            scmDeveloperConnection = defaultString( getScmDeveloperConnection( project ) );

            if ( !ProjectUtils.isRootProject( project ) )
            {
                Map<String, String> modules = ProjectUtils.getAllModules( project.getParent() );

                for ( String module : modules.keySet() )
                {
                    if ( new File( project.getParent().getBasedir(), module ).equals( project.getFile() ) )
                    {
                        return scmDeveloperConnection + '/' + module;
                    }
                }
                // project is not a module of its parent, so use project's directoryname
                scmDeveloperConnection += '/' + project.getFile().getParentFile().getName();
            }
        }
        return scmDeveloperConnection;
    }

    protected static String getScmConnection( Model model )
    {
        if ( model.getScm() == null )
        {
            return null;
        }
        return model.getScm().getConnection();
    }

    protected static String getScmConnection( MavenProject project )
    {
        if ( project.getScm() == null )
        {
            return null;
        }
        return project.getScm().getConnection();
    }

    protected static String getScmDeveloperConnection( Model model )
    {
        if ( model.getScm() == null )
        {
            return null;
        }
        return model.getScm().getDeveloperConnection();
    }

    protected static String getScmDeveloperConnection( MavenProject project )
    {
        if ( project.getScm() == null )
        {
            return null;
        }
        return project.getScm().getDeveloperConnection();
    }

    public static String defaultString( String value )
    {
        return ( value == null ? "" : value );
    }

}
