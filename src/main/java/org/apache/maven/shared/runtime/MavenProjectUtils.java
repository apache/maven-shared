package org.apache.maven.shared.runtime;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

/**
 * Utility methods for working with Maven projects.
 * 
 * @author <a href="mailto:markh@apache.org">Mark Hobson</a>
 * @version $Id$
 * @see MavenProject
 */
final class MavenProjectUtils
{
    // constructors -----------------------------------------------------------

    private MavenProjectUtils()
    {
        throw new AssertionError();
    }

    // public methods ---------------------------------------------------------

    /**
     * Aligns dependency versions to their corresponding project version for the specified projects.
     * 
     * @param projects the projects whose dependency versions to align
     */
    public static void mediateDependencyVersions( List<MavenProject> projects )
    {
        Map<String, String> versionsByProjectId = getVersionsByProjectId( projects );

        for ( MavenProject project : projects )
        {
            mediateProject( project, versionsByProjectId );
        }
    }

    // private methods --------------------------------------------------------

    private static void mediateProject( MavenProject project, Map<String, String> versionsByProjectId )
    {
        for ( Dependency dependency : project.getDependencies() )
        {
            mediateDependency( dependency, versionsByProjectId );
        }
    }

    private static void mediateDependency( Dependency dependency, Map<String, String> versionsByProjectId )
    {
        String projectId = versionlessKey( dependency );
        String version = versionsByProjectId.get( projectId );

        if ( version != null )
        {
            dependency.setVersion( version );
        }
    }

    private static Map<String, String> getVersionsByProjectId( List<MavenProject> projects )
    {
        Map<String, String> versionsByProjectId = new HashMap<String, String>();

        for ( MavenProject project : projects )
        {
            String projectId = versionlessKey( project );
            String version = project.getVersion();

            versionsByProjectId.put( projectId, version );
        }

        return versionsByProjectId;
    }

    private static String versionlessKey( MavenProject project )
    {
        return ArtifactUtils.versionlessKey( project.getGroupId(), project.getArtifactId() );
    }

    private static String versionlessKey( Dependency dependency )
    {
        return ArtifactUtils.versionlessKey( dependency.getGroupId(), dependency.getArtifactId() );
    }
}
