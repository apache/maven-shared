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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;

/**
 * @author Olivier Lamy
 * @since 1.0-beta-1
 * @version $Id$
 */
public class PropertyUtilsTest
    extends PlexusTestCase
{
    private static File testDirectory = new File( getBasedir(), "target/test-classes/" );

    public void testBasic()
        throws Exception
    {
        File basicProp = new File( testDirectory, "basic.properties" );

        if ( basicProp.exists() )
        {
            basicProp.delete();
        }

        basicProp.createNewFile();
        FileWriter writer = new FileWriter( basicProp );

        writer.write( "ghost=${non_existent}\n" );
        writer.write( "key=${untat_na_damgo}\n" );
        writer.write( "untat_na_damgo=gani_man\n" );
        writer.flush();
        writer.close();

        Properties prop = PropertyUtils.loadPropertyFile( basicProp, false, false );
        assertTrue( prop.getProperty( "key" ).equals( "gani_man" ) );
        assertTrue( prop.getProperty( "ghost" ).equals( "${non_existent}" ) );
    }

    public void testSystemProperties()
        throws Exception
    {
        File systemProp = new File( testDirectory, "system.properties" );

        if ( systemProp.exists() )
        {
            systemProp.delete();
        }

        systemProp.createNewFile();
        FileWriter writer = new FileWriter( systemProp );

        writer.write( "key=${user.dir}" );
        writer.flush();
        writer.close();

        Properties prop = PropertyUtils.loadPropertyFile( systemProp, false, true );
        assertTrue( prop.getProperty( "key" ).equals( System.getProperty( "user.dir" ) ) );
    }

    public void testException()
        throws Exception
    {
        File nonExistent = new File( testDirectory, "not_existent_file" );

        assertFalse( "property file exist: " + nonExistent.toString(), nonExistent.exists() );

        try
        {
            PropertyUtils.loadPropertyFile( nonExistent, true, false );
            assertTrue( "Exception failed", false );
        }
        catch ( Exception ex )
        {
            // exception ok
        }
    }

    public void testloadpropertiesFile()
        throws Exception
    {
        File propertyFile = new File( getBasedir() + "/src/test/units-files/propertyutils-test.properties" );
        Properties baseProps = new Properties();
        baseProps.put( "pom.version", "realVersion" );

        Properties interpolated = PropertyUtils.loadPropertyFile( propertyFile, baseProps );
        assertEquals( "realVersion", interpolated.get( "version" ) );
        assertEquals( "${foo}", interpolated.get( "foo" ) );
        assertEquals( "realVersion", interpolated.get( "bar" ) );
        assertEquals( "none filtered", interpolated.get( "none" ) );
    }
    
    /**
     * Test case to reproduce MSHARED-417
     * 
     * @throws IOException if problem writing file
     */
    public void testCircularReferences()
        throws IOException
    {
        File basicProp = new File( testDirectory, "circular.properties" );

        if ( basicProp.exists() )
        {
            basicProp.delete();
        }

        basicProp.createNewFile();
        FileWriter writer = new FileWriter( basicProp );

        writer.write( "test=${test2}\n" );
        writer.write( "test2=${test2}\n" );
        writer.flush();
        writer.close();

        MockLogger logger = new MockLogger();

        Properties prop = PropertyUtils.loadPropertyFile( basicProp, null, logger );
        assertEquals( "${test2}", prop.getProperty( "test" ) );
        assertEquals( "${test2}", prop.getProperty( "test2" ) );
        assertEquals( 2, logger.warnMsgs.size() );
        assertWarn( "Circular reference between properties detected: test2 => test2", logger );
        assertWarn( "Circular reference between properties detected: test => test2 => test2", logger );
    }

    /**
     * Test case to reproduce MSHARED-417
     * 
     * @throws IOException if problem writing file
     */
    public void testCircularReferences3Vars()
        throws IOException
    {
        File basicProp = new File( testDirectory, "circular.properties" );

        if ( basicProp.exists() )
        {
            basicProp.delete();
        }

        basicProp.createNewFile();
        FileWriter writer = new FileWriter( basicProp );

        writer.write( "test=${test2}\n" );
        writer.write( "test2=${test3}\n" );
        writer.write( "test3=${test}\n" );
        writer.flush();
        writer.close();

        MockLogger logger = new MockLogger();

        Properties prop = PropertyUtils.loadPropertyFile( basicProp, null, logger );
        assertEquals( "${test2}", prop.getProperty( "test" ) );
        assertEquals( "${test3}", prop.getProperty( "test2" ) );
        assertEquals( "${test}", prop.getProperty( "test3" ) );
        assertEquals( 3, logger.warnMsgs.size() );
        assertWarn( "Circular reference between properties detected: test3 => test => test2 => test3", logger );
        assertWarn( "Circular reference between properties detected: test2 => test3 => test => test2", logger );
        assertWarn( "Circular reference between properties detected: test => test2 => test3 => test", logger );
    }

    private void assertWarn( String expected, MockLogger logger )
    {
        assertTrue( logger.warnMsgs.contains( expected ) );
    }

    private static class MockLogger
        implements Logger
    {

        ArrayList<String> warnMsgs = new ArrayList<String>();

        @Override
        public void debug( String message )
        {
            // nothing
        }

        @Override
        public void debug( String message, Throwable throwable )
        {
            // nothing
        }

        @Override
        public boolean isDebugEnabled()
        {
            return false;
        }

        @Override
        public void info( String message )
        {
            // nothing
        }

        @Override
        public void info( String message, Throwable throwable )
        {
            // nothing
        }

        @Override
        public boolean isInfoEnabled()
        {
            return false;
        }

        @Override
        public void warn( String message )
        {
            warnMsgs.add( message );
        }

        @Override
        public void warn( String message, Throwable throwable )
        {
            // nothing
        }

        @Override
        public boolean isWarnEnabled()
        {
            return false;
        }

        @Override
        public void error( String message )
        {
            // nothing
        }

        @Override
        public void error( String message, Throwable throwable )
        {
            // nothing
        }

        @Override
        public boolean isErrorEnabled()
        {
            return false;
        }

        @Override
        public void fatalError( String message )
        {
            // nothing
        }

        @Override
        public void fatalError( String message, Throwable throwable )
        {
            // nothing
        }

        @Override
        public boolean isFatalErrorEnabled()
        {
            return false;
        }

        @Override
        public int getThreshold()
        {
            return 0;
        }

        @Override
        public void setThreshold( int threshold )
        {
            // nothing
        }

        @Override
        public Logger getChildLogger( String name )
        {
            return null;
        }

        @Override
        public String getName()
        {
            return null;
        }
    }
}
