package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.io.IOUtil;
import org.codehaus.plexus.logging.Logger;

/**
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 * @author William Ferguson
 */
public final class PropertyUtils
{
    /**
     * Private empty constructor to prevent instantiation.
     */
    private PropertyUtils()
    {
        // prevent instantiation
    }

    /**
     * Reads a property file, resolving all internal variables, using the supplied base properties.
     * <p>
     * The properties are resolved iteratively, so if the value of property A refers to property B, then after
     * resolution the value of property B will contain the value of property B.
     * </p>
     * 
     * @param propFile The property file to load.
     * @param baseProps Properties containing the initial values to substitute into the properties file.
     * @return Properties object containing the properties in the file with their values fully resolved.
     * @throws IOException if profile does not exist, or cannot be read.
     */
    public static Properties loadPropertyFile( File propFile, Properties baseProps )
        throws IOException
    {
        return loadPropertyFile( propFile, baseProps, null );
    }


    /**
     * Reads a property file, resolving all internal variables, using the supplied base properties.
     * <p>
     * The properties are resolved iteratively, so if the value of property A refers to property B, then after
     * resolution the value of property B will contain the value of property B.
     * </p>
     * 
     * @param propFile The property file to load.
     * @param baseProps Properties containing the initial values to substitute into the properties file.
     * @param logger Logger instance
     * @return Properties object containing the properties in the file with their values fully resolved.
     * @throws IOException if profile does not exist, or cannot be read.
     * 
     * @since 3.1.2
     */
    public static Properties loadPropertyFile( File propFile, Properties baseProps, Logger logger )
        throws IOException
    {
        if ( !propFile.exists() )
        {
            throw new FileNotFoundException( propFile.toString() );
        }

        final Properties fileProps = new Properties();
        final FileInputStream inStream = new FileInputStream( propFile );
        try
        {
            fileProps.load( inStream );
        }
        finally
        {
            IOUtil.close( inStream );
        }

        final Properties combinedProps = new Properties();
        combinedProps.putAll( baseProps == null ? new Properties() : baseProps );
        combinedProps.putAll( fileProps );

        // The algorithm iterates only over the fileProps which is all that is required to resolve
        // the properties defined within the file. This is slightly different to current, however
        // I suspect that this was the actual original intent.
        //
        // The difference is that #loadPropertyFile(File, boolean, boolean) also resolves System properties
        // whose values contain expressions. I believe this is unexpected and is not validated by the test cases,
        // as can be verified by replacing the implementation of #loadPropertyFile(File, boolean, boolean)
        // with the commented variant I have provided that reuses this method.

        for ( Object o : fileProps.keySet() )
        {
            final String k = (String) o;
            final String propValue = getPropertyValue( k, combinedProps, logger );
            fileProps.setProperty( k, propValue );
        }

        return fileProps;
    }

    /**
     * Reads a property file, resolving all internal variables.
     *
     * @param propfile The property file to load
     * @param fail whether to throw an exception when the file cannot be loaded or to return null
     * @param useSystemProps whether to incorporate System.getProperties settings into the returned Properties object.
     * @return the loaded and fully resolved Properties object
     * @throws IOException if profile does not exist, or cannot be read.
     */
    public static Properties loadPropertyFile( File propfile, boolean fail, boolean useSystemProps )
        throws IOException
    {
        return loadPropertyFile( propfile, fail, useSystemProps, null );
    }

    /**
     * Reads a property file, resolving all internal variables.
     *
     * @param propfile The property file to load
     * @param fail whether to throw an exception when the file cannot be loaded or to return null
     * @param useSystemProps whether to incorporate System.getProperties settings into the returned Properties object.
     * @param logger Logger instance
     * @return the loaded and fully resolved Properties object
     * @throws IOException if profile does not exist, or cannot be read.
     * 
     * @since 3.1.2
     */
    public static Properties loadPropertyFile( File propfile, boolean fail, boolean useSystemProps, Logger logger )
        throws IOException
    {

        final Properties baseProps = new Properties();

        if ( useSystemProps )
        {
            baseProps.putAll( System.getProperties() );
        }

        final Properties resolvedProps = new Properties();
        try
        {
            resolvedProps.putAll( loadPropertyFile( propfile, baseProps, logger ) );
        }
        catch ( FileNotFoundException e )
        {
            if ( fail )
            {
                throw new FileNotFoundException( propfile.toString() );
            }
        }

        if ( useSystemProps )
        {
            resolvedProps.putAll( baseProps );
        }

        return resolvedProps;
    }

    /**
     * Retrieves a property value, replacing values like ${token} using the Properties to look them up. It will leave
     * unresolved properties alone, trying for System properties, and implements reparsing (in the case that the value
     * of a property contains a key), and will not loop endlessly on a pair like test = ${test}.
     *
     * @param k
     * @param p
     * @param logger Logger instance
     * @return The filtered property value.
     */
    
    private static String getPropertyValue( String k, Properties p, Logger logger )
    {
        // This can also be done using InterpolationFilterReader,
        // but it requires reparsing the file over and over until
        // it doesn't change.

        // for cycle detection
        LinkedList<String> valueChain = new LinkedList<String>();
        valueChain.add( k );

        String v = p.getProperty( k );
        String defaultValue = v;
        String ret = "";
        int idx, idx2;

        while ( ( idx = v.indexOf( "${" ) ) >= 0 )
        {
            // append prefix to result
            ret += v.substring( 0, idx );

            // strip prefix from original
            v = v.substring( idx + 2 );

            // if no matching } then bail
            idx2 = v.indexOf( '}' );
            if ( idx2 < 0 )
            {
                break;
            }

            // strip out the key and resolve it
            // resolve the key/value for the ${statement}
            String nk = v.substring( 0, idx2 );
            v = v.substring( idx2 + 1 );
            String nv = p.getProperty( nk );

            if ( valueChain.contains( nk ) )
            {
                if ( logger != null )
                {
                    logCircularDetection( valueChain, nk, logger );
                }
                return defaultValue;
            }
            else
            {
                valueChain.add( nk );

                // try global environment..
                if ( nv == null && !StringUtils.isEmpty( nk ) )
                {
                    nv = System.getProperty( nk );
                }

                // if the key cannot be resolved,
                // leave it alone ( and don't parse again )
                // else prefix the original string with the
                // resolved property ( so it can be parsed further )
                // taking recursion into account.
                if ( nv == null || nv.equals( k ) || k.equals( nk ) )
                {
                    ret += "${" + nk + "}";
                }
                else
                {
                    v = nv + v;
                }
            }
        }

        return ret + v;
    }

    /**
     * Logs the detected cycle in properties resolution
     * @param valueChain the secuence of properties resolved so fa
     * @param nk the key the closes the cycle
     * @param logger Logger instance
     */
    private static void logCircularDetection( LinkedList<String> valueChain, String nk, Logger logger )
    {
        StringBuilder sb = new StringBuilder( "Circular reference between properties detected: " );
        for ( String key : valueChain )
        {
            sb.append( key ).append( " => " );
        }
        sb.append( nk );
        logger.warn( sb.toString() );
    }
}
