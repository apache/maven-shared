package org.apache.maven.shared.project.utils;

import java.util.List;

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

public final class ProjectUtils
{

    private ProjectUtils()
    {
    }

    /**
     * Returns {@code true} if this project has no parent, or it has a parent but isn't one of its modules.
     * 
     * @param project the project to verify
     * @return {@code true} if this is a root project, otherwise {@code false}
     */
    public static boolean isRootProject( MavenProject project )
    {
        if ( !project.hasParent() )
        {
            return true;
        }

        MavenProject parent = project.getParent();

        @SuppressWarnings( "unchecked" )
        List<MavenProject> collectedProjects = (List<MavenProject>) parent.getCollectedProjects();

        if ( collectedProjects != null )
        {
            for ( MavenProject collectedProject : collectedProjects )
            {
                if ( project.getId().equals( collectedProject.getId() ) )
                {
                    // project is a module of its parent
                    return false;
                }
            }
        }
        else
        {
            if ( parent.getModules().size() > 0 )
            {
                // problem: parent has modules, but they aren't collected (ie not in the reactor)
                // can't really tell if current project is root or not
            }
        }

        // project isn't a module of its parent
        return true;
    }

    public static MavenProject getRootProject( MavenProject project )
    {
        if ( project == null )
        {
            return null;
        }
        
        MavenProject current = project;

        while ( !isRootProject( current ) )
        {
            current = current.getParent();
        }

        return current;
    }

    /**
     * Return {@code true} if this project has modules, but is <strong>never</strong> the parent of one of them.<br/>
     * 
     * Return {@code false} if this project has no modules, or if 1 or more modules have this project as its parent.
     * 
     * @param project
     * @return {@code true} if project is an aggregator, {@code false} if project is standalone or hybrid 
     */
    public static boolean isAggregator( MavenProject project )
    {
        @SuppressWarnings( "unchecked" )
        List<MavenProject> collectedProjects = (List<MavenProject>) project.getCollectedProjects();

        if ( collectedProjects.isEmpty() )
        {
            return false;
        }

        for ( MavenProject collectedProject : collectedProjects )
        {
            if ( project.getId().equals( collectedProject.getId() ) )
            {
                return false;
            }
        }
        return true;
    }
}
