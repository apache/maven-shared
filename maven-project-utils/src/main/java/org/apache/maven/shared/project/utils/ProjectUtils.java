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
        if( project.hasParent() )
        {
            MavenProject parent = project.getParent();

            // Are collectedProject the resolved modules?
            @SuppressWarnings( "unchecked" )
            List<MavenProject> collectedProjects = (List<MavenProject>) parent.getCollectedProjects();
            
            for( MavenProject collectedProject :  collectedProjects )
            {
                if( project.getId().equals( collectedProject.getId() ) )
                {
                    return true;
                }
            }
            return false;
        }
        else 
        {
            return true;
        }
    }
}
