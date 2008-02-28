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
            acceptClassLoader( currentClassLoader, visitor );

            currentClassLoader = currentClassLoader.getParent();
        }
    }

    // private methods --------------------------------------------------------
    
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
    private void acceptClassLoader( ClassLoader classLoader, MavenRuntimeVisitor visitor )
        throws MavenRuntimeException
    {
        if ( classLoader instanceof URLClassLoader )
        {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            
            URL[] urls = urlClassLoader.getURLs();

            for ( int i = 0; i < urls.length; i++ )
            {
                acceptURL( urls[i], visitor );
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
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private void acceptURL( URL url, MavenRuntimeVisitor visitor ) throws MavenRuntimeException
    {
        if ( url.getPath().endsWith( ".jar" ) )
        {
            acceptJar( url, visitor );
        }
    }

    /**
     * Invokes the specified visitor on all Maven projects found within the specified Jar URL.
     * 
     * @param url
     *            the Jar URL to introspect
     * @param visitor
     *            the visitor to invoke
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private void acceptJar( URL url, MavenRuntimeVisitor visitor ) throws MavenRuntimeException
    {
        JarInputStream in = null;
        
        try
        {
            in = new JarInputStream( url.openStream() );

            JarEntry entry;
            
            while ( ( entry = in.getNextJarEntry() ) != null )
            {
                acceptJarEntry( url, entry, visitor );
            }
        }
        catch ( IOException exception )
        {
            throw new MavenRuntimeException( "Cannot read jar", exception );
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch ( IOException exception )
            {
                throw new MavenRuntimeException( "Cannot close jar", exception );
            }
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
     * @throws MavenRuntimeException
     *             if an error occurs visiting the projects
     */
    private void acceptJarEntry( URL jarURL, JarEntry entry, MavenRuntimeVisitor visitor )
        throws MavenRuntimeException
    {
        String name = entry.getName();

        try
        {
            URL url = new URL("jar:" + jarURL + "!/" + entry.getName());
            
            if ( isProjectPropertiesPath( name ) )
            {
                visitor.visitProjectProperties( url );
            }
            else if ( isProjectXMLPath( name ) )
            {
                visitor.visitProjectXML( url );
            }
        }
        catch ( MalformedURLException exception )
        {
            throw new MavenRuntimeException( "Cannot read jar entry", exception );
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
