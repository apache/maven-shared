package org.apache.maven.shared.project.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

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

        // (not) being a rootProject must never depend on reactor projects or active profiles
        for ( String module : getAllModules( parent ).keySet() )
        {
            File moduleFile = new File( parent.getBasedir(), module );
            if ( moduleFile.isDirectory() )
            {
                moduleFile = new File( moduleFile, "pom.xml" );
            }

            if ( moduleFile.equals( project.getFile() ) )
            {
                // project is a module of its parent
                return false;
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
     // (not) being an aggregator must never depend on reactor projects or active profiles
        Set<String> modules = getAllModules( project ).keySet();

        if ( modules.isEmpty() )
        {
            return false;
        }

        MavenXpp3Reader reader = new MavenXpp3Reader();
        
        for ( String module : modules )
        {
            File moduleFile = new File( project.getBasedir(), module );
            
            if ( moduleFile.isDirectory() )
            {
                moduleFile = new File( moduleFile, "pom.xml" );
            }

            FileReader moduleReader = null; 
            
            try
            {
                moduleReader = new FileReader( moduleFile );
                
                Model model = reader.read( moduleReader );
                
                if ( model.getParent() != null && model.getParent().getId().equals( project.getId() ) )
                {
                    return false;
                }
            }
            catch ( FileNotFoundException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch ( XmlPullParserException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                IOUtil.close( moduleReader );
            }
            
        }
        return true;
    }
    
    /**
     * Returns all modules of a project, including does specified in profiles, both active and inactive.
     * The key of the returned Map is the name of the module, the value refers to the source of the module (the project or a specific profile). 
     * 
     * @param project the project
     * @return all modules, never {@code null}
     */
    public static Map<String, String> getAllModules( MavenProject project )
    {
        Map<String, String> modules = new LinkedHashMap<String, String>();

        for ( String module : project.getModel().getModules() )
        {
            modules.put( module, "project" ); // id?
        }

        for ( Profile profile : project.getModel().getProfiles() )
        {
            for ( String module : profile.getModules() )
            {
                modules.put( module, "profile(id:" + profile.getId() + ")" );
            }
        }
        return Collections.unmodifiableMap( modules );
    }
}
