package org.apache.maven.shared.project.utils;

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * 
 */
public final class ProjectUtils
{
    // This instance is often used, including in recursive methods, so initiate it for general usage
    private static final MavenXpp3Reader POM_READER = new MavenXpp3Reader();

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
            File moduleFile = getModuleFile( parent, module );

            if ( moduleFile.equals( project.getFile() ) )
            {
                // project is a module of its parent
                return false;
            }
        }

        // project isn't a module of its parent
        return true;
    }

    /**
     * Go through the ancestors to find the rootProject of this project.
     * 
     * @param project the project
     * @return the root project
     * @see ProjectUtils#isRootProject(MavenProject)
     */
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

        for ( String module : modules )
        {
            File moduleFile = getModuleFile( project, module );

            Model model = null;
            try
            {
                model = readModel( moduleFile );
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

            if ( model.getParent() != null && model.getParent().getId().equals( project.getId() ) )
            {
                return false;
            }

        }
        return true;
    }

    private static Model readModel( File moduleFile )
        throws IOException, XmlPullParserException
    {
        FileReader moduleReader = null;

        Model model = null;

        try
        {
            moduleReader = new FileReader( moduleFile );

            model = POM_READER.read( moduleReader );
        }
        finally
        {
            IOUtil.close( moduleReader );
        }

        return model;
    }

    public static File getModuleFile( MavenProject project, String module )
    {
        return getModuleFile( project.getBasedir(), module );
    }

    private static File getModuleFile( File basedir, String module )
    {
        File moduleFile = new File( basedir, module );

        if ( moduleFile.isDirectory() )
        {
            moduleFile = new File( moduleFile, "pom.xml" );
        }
        return moduleFile;
    }

    /**
     * Returns all modules of a project, including does specified in profiles, both active and inactive. The key of the
     * returned Map is the name of the module, the value refers to the source of the module (the project or a specific
     * profile).
     * 
     * @param project the project
     * @return all modules, never {@code null}
     */
    public static Map<String, String> getAllModules( MavenProject project )
    {
        Model model = project.getModel();

        return getAllModules( model );
    }

    private static Map<String, String> getAllModules( Model model )
    {
        Map<String, String> modules = new LinkedHashMap<String, String>();

        for ( String module : model.getModules() )
        {
            modules.put( module, "project" ); // id?
        }

        for ( Profile profile : model.getProfiles() )
        {
            for ( String module : profile.getModules() )
            {
                modules.put( module, "profile(id:" + profile.getId() + ")" );
            }
        }

        return Collections.unmodifiableMap( modules );
    }

    /**
     * Returns the upper most folder of this projects and all of its descendants (i.e. modules, their modules, etc.).
     * 
     * @param project the project
     * @return the shared folder
     */
    public static File getJoinedFolder( MavenProject project )
    {
        if ( project == null )
        {
            return null;
        }

        try
        {
            return getJoinedFolder( project.getBasedir(), project.getModel() );
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

        return null; // @todo fix exception handling
    }

    private static File getJoinedFolder( File baseDirectory, Model model )
        throws IOException, XmlPullParserException
    {
        File joinedFolder = baseDirectory;

        for ( String module : getAllModules( model ).keySet() )
        {
            File moduleFile = getModuleFile( baseDirectory, module );

            Model submodel = readModel( moduleFile );

            File modulesJoinedFolder = getJoinedFolder( moduleFile.getParentFile(), submodel );

            joinedFolder = getJoinedFolder( joinedFolder, modulesJoinedFolder );
        }

        return joinedFolder;
    }

    // Don't make this method public, it has nothing to do with a MavenProject.
    // If required on more places, create a separate Utils-class
    protected static File getJoinedFolder( File lhs, File rhs )
    {
        File joinedFolder = null;

        Stack<File> lhsStack = new Stack<File>();

        File lhsAncestor = lhs;

        while ( lhsAncestor != null )
        {
            lhsAncestor = lhsStack.push( lhsAncestor ).getParentFile();
        }

        Stack<File> rhsStack = new Stack<File>();

        File rhsAncestor = rhs;

        while ( rhsAncestor != null )
        {
            rhsAncestor = rhsStack.push( rhsAncestor ).getParentFile();
        }

        while ( !lhsStack.isEmpty() && !rhsStack.isEmpty() )
        {
            File nextFile = lhsStack.pop();

            if ( nextFile.isDirectory() && nextFile.equals( rhsStack.pop() ) )
            {
                joinedFolder = nextFile;
            }
        }

        return joinedFolder;
    }
}
