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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.codehaus.plexus.util.IOUtil;

/**
 * Provides various methods of applying Maven runtime visitors.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see MavenRuntimeVisitor
 */
public final class MavenRuntimeVisitorUtils
{
    // constants --------------------------------------------------------------

    /**
     * The path elements of a Maven project properties file, where <code>null</code> is a wildcard.
     */
    private static final String[] PROPERTIES_PATH_TOKENS =
        new String[] { "META-INF", "maven", null, null, "pom.properties" };

    /**
     * The path elements of a Maven project XML file, where <code>null</code> is a wildcard.
     */
    private static final String[] XML_PATH_TOKENS = new String[] { "META-INF", "maven", null, null, "pom.xml" };
    
    /**
     * The path element index of a Maven project properties/XML file that contains the project group id.
     */
    private static final int GROUP_ID_TOKEN_INDEX = 2;
    
    /**
     * The path element index of a Maven project properties/XML file that contains the project artifact id.
     */
    private static final int ARTIFACT_ID_TOKEN_INDEX = 3;
    
    /**
     * List of known class loaders that are child-delegating.
     */
    private static final String[] CHILD_DELEGATING_CLASSLOADERS = new String [] {
        "org.apache.maven.shared.runtime.ChildDelegatingClassLoader",
        "org.apache.maven.surefire.booter.IsolatedClassLoader"
    };
    
    // constructors -----------------------------------------------------------

    /**
     * <code>MavenRuntimeVisitorUtils</code> is not intended to be instantiated.
     */
    private MavenRuntimeVisitorUtils()
    {
        // private constructor for utility class
    }

    // public methods ---------------------------------------------------------

    /**
     * Invokes the specified visitor on all Maven projects found within the specified class loader.
     * 
     * @param classLoader
     *            the class loader to introspect
     * @param visitor
     *            the visitor to invoke
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    public static void accept( ClassLoader classLoader, MavenRuntimeVisitor visitor )
        throws MavenRuntimeException
    {
        List classLoaders = getOrderedClassLoaders( classLoader );
        
        Set visitedProjectProperties = new HashSet();
        Set visitedProjectXML = new HashSet();

        for ( Iterator iterator = classLoaders.iterator(); iterator.hasNext(); )
        {
            ClassLoader currentClassLoader = (ClassLoader) iterator.next();

            acceptClassLoader( currentClassLoader, visitor, visitedProjectProperties, visitedProjectXML );
        }
    }
    
    /**
     * Invokes the specified visitor on the specified class's Maven project.
     * 
     * @param klass
     *            the class to introspect
     * @param visitor
     *            the visitor to invoke
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    public static void accept( Class klass, MavenRuntimeVisitor visitor )
        throws MavenRuntimeException
    {
        try
        {
            accept( ClassUtils.getBaseURL( klass ), visitor );
        }
        catch ( MalformedURLException exception )
        {
            throw new MavenRuntimeException( "Cannot obtain base URL for class: " + klass.getName(), exception );
        }
    }
    
    /**
     * Invokes the specified visitor on all Maven projects found within the specified URL.
     * 
     * @param url
     *            the URL to introspect
     * @param visitor
     *            the visitor to invoke
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    public static void accept( URL url, MavenRuntimeVisitor visitor ) throws MavenRuntimeException
    {
        acceptURL( url, visitor, new HashSet(), new HashSet() );
    }

    // private methods --------------------------------------------------------
    
    /**
     * Gets the class loader hierarchy for the specified class loader in search order precedence. Precedence is
     * determined by the hierarchy of class loaders and whether a class loader is deemed to be parent- or
     * child-delegating. The method of delegation is determined by <code>isChildDelegating</code>.
     * 
     * @param classLoader
     *            the class loader to obtain the ordered class loader hierarchy of
     * @return the class loader hierarchy for the specified class loader in search order precedence
     * @see #isChildDelegating(ClassLoader)
     */
    private static List getOrderedClassLoaders( ClassLoader classLoader )
    {
        List classLoaders = new ArrayList();
        List childDelegatingClassLoaders = new ArrayList();

        ClassLoader currentClassLoader = classLoader;

        while ( currentClassLoader != null )
        {
            if ( isChildDelegating( currentClassLoader ) )
            {
                childDelegatingClassLoaders.add( currentClassLoader );
            }
            else
            {
                classLoaders.add( 0, currentClassLoader );
            }

            currentClassLoader = currentClassLoader.getParent();
        }

        classLoaders.addAll( 0, childDelegatingClassLoaders );

        return classLoaders;
    }

    /**
     * Gets whether the specified class loader is parent- or child-delegating.
     * 
     * @param classLoader
     *            the class loader to examine
     * @return <code>true</code> if the class loader is child-delegating or <code>false</code> if it is
     *         parent-delegating
     */
    private static boolean isChildDelegating( ClassLoader classLoader )
    {
        String className = classLoader.getClass().getName();

        for ( int i = 0; i < CHILD_DELEGATING_CLASSLOADERS.length; i++ )
        {
            if ( CHILD_DELEGATING_CLASSLOADERS[i].equals( className ) )
            {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Invokes the specified visitor on all Maven projects found within the specified class loader.
     * 
     * @param classLoader
     *            the class loader to introspect
     * @param visitor
     *            the visitor to invoke
     * @param visitedProjectProperties
     *            the ids of projects' properties that have been visited
     * @param visitedProjectXML
     *            the ids of projects' XML that have been visited
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private static void acceptClassLoader( ClassLoader classLoader, MavenRuntimeVisitor visitor,
                                           Set visitedProjectProperties, Set visitedProjectXML )
        throws MavenRuntimeException
    {
        if ( classLoader instanceof URLClassLoader )
        {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            
            URL[] urls = urlClassLoader.getURLs();

            for ( int i = 0; i < urls.length; i++ )
            {
                acceptURL( urls[i], visitor, visitedProjectProperties, visitedProjectXML );
            }
        }
    }
    
    /**
     * Invokes the specified visitor on all Maven projects found within the specified URL.
     * 
     * @param url
     *            the URL to introspect
     * @param visitor
     *            the visitor to invoke
     * @param visitedProjectProperties
     *            the ids of projects' properties that have been visited
     * @param visitedProjectXML
     *            the ids of projects' XML that have been visited
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    public static void acceptURL( URL url, MavenRuntimeVisitor visitor, Set visitedProjectProperties,
                                  Set visitedProjectXML ) throws MavenRuntimeException
    {
        if ( url.getPath().endsWith( ".jar" ) )
        {
            acceptJar( url, visitor, visitedProjectProperties, visitedProjectXML );
        }
    }

    /**
     * Invokes the specified visitor on all Maven projects found within the specified Jar URL.
     * 
     * @param url
     *            the Jar URL to introspect
     * @param visitor
     *            the visitor to invoke
     * @param visitedProjectProperties
     *            the ids of projects' properties that have been visited
     * @param visitedProjectXML
     *            the ids of projects' XML that have been visited
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private static void acceptJar( URL url, MavenRuntimeVisitor visitor, Set visitedProjectProperties,
                                   Set visitedProjectXML ) throws MavenRuntimeException
    {
        JarInputStream in = null;
        
        try
        {
            URLConnection connection = url.openConnection();
            connection.setUseCaches( false );
            
            in = new JarInputStream( connection.getInputStream() );

            JarEntry entry;
            
            while ( ( entry = in.getNextJarEntry() ) != null )
            {
                acceptJarEntry( url, entry, visitor, visitedProjectProperties, visitedProjectXML );
            }
        }
        catch ( IOException exception )
        {
            throw new MavenRuntimeException( "Cannot read jar", exception );
        }
        finally
        {
            IOUtil.close( in );
        }
    }
    
    /**
     * Invokes the specified visitor on the specified Jar entry if it corresponds to a Maven project XML or properties
     * file.
     * 
     * @param jarURL
     *            a URL to the Jar file for this entry
     * @param entry
     *            the Jar entry to introspect
     * @param visitor
     *            the visitor to invoke
     * @param visitedProjectProperties
     *            the ids of projects' properties that have been visited
     * @param visitedProjectXML
     *            the ids of projects' XML that have been visited
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private static void acceptJarEntry( URL jarURL, JarEntry entry, MavenRuntimeVisitor visitor,
                                        Set visitedProjectProperties, Set visitedProjectXML )
        throws MavenRuntimeException
    {
        String name = entry.getName();

        try
        {
            URL url = new URL("jar:" + jarURL + "!/" + entry.getName());
            
            if ( isProjectPropertiesPath( name ) )
            {
                String projectId = getProjectId(name);
                
                if ( !visitedProjectProperties.contains( projectId ) )
                {
                    visitor.visitProjectProperties( url );
                    
                    visitedProjectProperties.add( projectId );
                }
            }
            else if ( isProjectXMLPath( name ) )
            {
                String projectId = getProjectId(name);
                
                if ( !visitedProjectXML.contains( projectId ) )
                {
                    visitor.visitProjectXML( url );
                    
                    visitedProjectXML.add( projectId );
                }
            }
        }
        catch ( MalformedURLException exception )
        {
            throw new MavenRuntimeException( "Cannot read jar entry", exception );
        }
    }
    
    /**
     * Gets a unique project identifier for the specified Maven project properties/XML file.
     * 
     * @param path
     *            the path to a Maven project properties/XML file
     * @return the unique project identifier
     */
    private static String getProjectId( String path )
    {
        String[] tokens = path.split( "/" );
        
        String groupId = tokens[GROUP_ID_TOKEN_INDEX];
        String artifactId = tokens[ARTIFACT_ID_TOKEN_INDEX];
        
        return groupId + ":" + artifactId;
    }

    /**
     * Gets whether the specified path represents a Maven project properties file.
     * 
     * @param path
     *            the path to examine
     * @return <code>true</code> if the specified path represents a Maven project properties file
     */
    private static boolean isProjectPropertiesPath( String path )
    {
        return matches( PROPERTIES_PATH_TOKENS, path.split( "/" ) );
    }

    /**
     * Gets whether the specified path represents a Maven project XML file.
     * 
     * @param path
     *            the path to examine
     * @return <code>true</code> if the specified path represents a Maven project XML file
     */
    private static boolean isProjectXMLPath( String path )
    {
        return matches( XML_PATH_TOKENS, path.split( "/" ) );
    }

    /**
     * Gets whether the specified string arrays are equal, with wildcard support.
     * 
     * @param matchTokens
     *            the string tokens to match, where <code>null</code> represents a wildcard
     * @param tokens
     *            the string tokens to test
     * @return <code>true</code> if the <code>tokens</code> array equals the <code>matchTokens</code>, treating
     *         any <code>null</code> <code>matchTokens</code> values as wildcards
     */
    private static boolean matches( String[] matchTokens, String[] tokens )
    {
        if ( tokens.length != matchTokens.length )
        {
            return false;
        }

        for ( int i = 0; i < tokens.length; i++ )
        {
            if ( matchTokens[i] != null && !tokens[i].equals( matchTokens[i] ) )
            {
                return false;
            }
        }

        return true;
    }
}
