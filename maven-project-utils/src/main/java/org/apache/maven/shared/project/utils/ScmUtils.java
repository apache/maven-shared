package org.apache.maven.shared.project.utils;

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

    public static String resolveScmConnection( MavenProject project )
    {
        String scmConnection = getScmConnection( project.getModel() );

        if ( scmConnection == null )
        {
            // prevent null-value
            scmConnection = "" + getScmConnection( project );
            
            if ( !ProjectUtils.isRootProject( project ) )
            {
                // assuming that folder matches the moduleName
                scmConnection += "/" + project.getFile().getParentFile().getName();
            }
        }
        return scmConnection;
    }

    public static String resolveScmDeveloperConnection( MavenProject project )
    {
        String siteUrl = getScmDeveloperConnection( project.getModel() );

        if ( siteUrl == null )
        {
            // prevent null-value
            siteUrl = "" + getScmDeveloperConnection( project );
            
            if ( !ProjectUtils.isRootProject( project ) )
            {
                // assuming that folder matches the moduleName
                siteUrl += "/" + project.getFile().getParentFile().getName();
            }
        }
        return siteUrl;
    }

    protected static String getScmConnection( Model model )
    {
        if ( model.getScm() != null )
        {
            return model.getScm().getConnection();
        }
        else
        {
            return null;
        }
    }

    protected static String getScmConnection( MavenProject project )
    {
        if ( project.getScm() != null )
        {
            return project.getScm().getConnection();
        }
        else
        {
            return null;
        }
    }

    protected static String getScmDeveloperConnection( Model model )
    {
        if ( model.getScm() != null )
        {
            return model.getScm().getDeveloperConnection();
        }
        else
        {
            return null;
        }
    }

    protected static String getScmDeveloperConnection( MavenProject project )
    {
        if ( project.getScm() != null )
        {
            return project.getScm().getDeveloperConnection();
        }
        else
        {
            return null;
        }
    }

}
