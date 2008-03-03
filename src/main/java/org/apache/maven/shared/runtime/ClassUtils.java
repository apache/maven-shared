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

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.plexus.util.StringUtils;

/**
 * Provides various utility methods for working with classes.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
final class ClassUtils
{
    // constructors -----------------------------------------------------------
    
    private ClassUtils()
    {
        // private constructor for utility class
    }

    // public methods ---------------------------------------------------------
    
    /**
     * Gets a URL to the specified class's default package. For example, if the class <code>foo.Bar</code> is
     * supplied, then a URL to the directory above <code>foo</code> is returned. If the class's default package
     * resides at the root of a Jar, then a URL to the Jar file itself is returned.
     * 
     * @param klass
     *            the class to obtain the base URL for
     * @return a URL to the class's default package, or a URL to the owning Jar file if the default package resides at
     *         the root of a Jar
     * @throws MalformedURLException
     *             if the base URL cannot be determined
     */
    public static URL getBaseURL( Class klass )
        throws MalformedURLException
    {
        URL url = getURL( klass );

        String className = klass.getName();

        int n = StringUtils.countMatches( className, "." );
        String relativePath = StringUtils.repeat( "../", n );

        URL baseURL = new URL( url, relativePath );

        // unwrap Jar URL if at the root
        if ( "jar".equals( baseURL.getProtocol() ) && baseURL.getPath().endsWith( "!/" ) )
        {
            String basePath = baseURL.getPath();

            basePath = basePath.substring( 0, basePath.length() - "!/".length() );

            baseURL = new URL( basePath );
        }

        return baseURL;
    }

    /**
     * Gets a URL to the specified class.
     * 
     * @param klass
     *            the class to obtain the URL for
     * @return a URL to the class, or <code>null</code> if it cannot be found
     */
    public static URL getURL( Class klass )
    {
        ClassLoader classLoader = klass.getClassLoader();

        String path = klass.getName().replace( '.', '/' ) + ".class";

        return classLoader.getResource( path );
    }
}
