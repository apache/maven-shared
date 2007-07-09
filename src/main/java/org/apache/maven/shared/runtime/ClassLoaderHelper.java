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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Provides a bridge between a class loader and a Maven runtime visitor.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see MavenRuntimeVisitor
 */
public class ClassLoaderHelper
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
    
    // fields -----------------------------------------------------------------
    
    /**
     * The class loader that this helper uses.
     */
    private final ClassLoader classLoader;

    // constructors -----------------------------------------------------------

    /**
     * Creates a <code>ClassLoaderHelper</code> that uses the specified class loader.
     * 
     * @param classLoader
     *            the class loader to use, not null
     */
    public ClassLoaderHelper(ClassLoader classLoader)
    {
        if ( classLoader == null )
        {
            throw new IllegalArgumentException( "classLoader cannot be null" );
        }

        this.classLoader = classLoader;
    }

    // public methods ---------------------------------------------------------

    /**
     * Invokes the specified visitor on all Maven projects found within this helper's class loader.
     * 
     * @param visitor
     *            the visitor to invoke
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    public void accept( MavenRuntimeVisitor visitor )
        throws MavenRuntimeException
    {
        ClassLoader currentClassLoader = classLoader;

        while ( currentClassLoader != null )
        {
            visitClassLoader( visitor, currentClassLoader );

            currentClassLoader = currentClassLoader.getParent();
        }
    }

    // private methods --------------------------------------------------------
    
    /**
     * Invokes the specified visitor on all Maven projects found within the specified class loader.
     * 
     * @param visitor
     *            the visitor to invoke
     * @param classLoader
     *            the class loader to introspect
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private void visitClassLoader( MavenRuntimeVisitor visitor, ClassLoader classLoader )
        throws MavenRuntimeException
    {
        if ( !( classLoader instanceof URLClassLoader ) )
        {
            throw new MavenRuntimeException( "Cannot introspect non-URL class loader: "
                            + classLoader.getClass().getName() );
        }
        
        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
        
        URL[] urls = urlClassLoader.getURLs();

        for ( int i = 0; i < urls.length; i++ )
        {
            visitProjects( visitor, urls[i] );
        }
    }

    /**
     * Invokes the specified visitor on all Maven projects found within the specified URL.
     * 
     * @param visitor
     *            the visitor to invoke
     * @param url
     *            the URL to introspect
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private void visitProjects( MavenRuntimeVisitor visitor, URL url ) throws MavenRuntimeException
    {
        String path = url.getPath();

        if ( path.endsWith( ".jar" ) )
        {
            visitProjectsInJar( visitor, url );
        }
    }

    /**
     * Invokes the specified visitor on all Maven projects found within the specified Jar URL.
     * 
     * @param visitor
     *            the visitor to invoke
     * @param url
     *            the Jar URL to introspect
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private void visitProjectsInJar( MavenRuntimeVisitor visitor, URL url ) throws MavenRuntimeException
    {
        try
        {
            JarInputStream in = new JarInputStream( url.openStream() );

            JarEntry entry;
            while ( ( entry = in.getNextJarEntry() ) != null )
            {
                String name = entry.getName();

                if ( isProjectPropertiesPath( name ) )
                {
                    visitor.visitProjectProperties( in );
                }
                else if ( isProjectXMLPath( name ) )
                {
                    visitor.visitProjectXML( in );
                }
            }

            in.close();
        }
        catch ( IOException exception )
        {
            throw new MavenRuntimeException( "Cannot read jar", exception );
        }
    }

    /**
     * Gets whether the specified path represents a Maven project properties file.
     * 
     * @param path
     *            the path to examine
     * @return <code>true</code> if the specified path represents a Maven project properties file
     */
    private boolean isProjectPropertiesPath( String path )
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
    private boolean isProjectXMLPath( String path )
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
    private boolean matches( String[] matchTokens, String[] tokens )
    {
        if ( tokens.length != matchTokens.length )
            return false;

        for ( int i = 0; i < tokens.length; i++ )
        {
            if ( matchTokens[i] != null && !tokens[i].equals( matchTokens[i] ) )
                return false;
        }

        return true;
    }
}
