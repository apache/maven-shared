package org.apache.maven.it.verifier;

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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.StringUtils;

/**
 * {@link Verifier} command line
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl </a>
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * @noinspection UseOfSystemOutOrSystemErr,RefusedBequest
 */
public class VerifierCli
{
    public static void main( String args[] )
        throws VerificationException
    {
        String basedir = System.getProperty( "user.dir" );

        List tests = null;

        List argsList = new ArrayList();

        String settingsFile = null;

        // skip options
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i].startsWith( "-D" ) )
            {
                int index = args[i].indexOf( "=" );
                if ( index >= 0 )
                {
                    System.setProperty( args[i].substring( 2, index ), args[i].substring( index + 1 ) );
                }
                else
                {
                    System.setProperty( args[i].substring( 2 ), "true" );
                }
            }
            else if ( "-s".equals( args[i] ) || "--settings".equals( args[i] ) )
            {
                if ( i == args.length - 1 )
                {
                    // should have been detected before
                    throw new IllegalStateException( "missing argument to -s" );
                }
                i += 1;

                settingsFile = args[i];
            }
            else if ( args[i].startsWith( "-" ) )
            {
                System.out.println( "skipping unrecognised argument: " + args[i] );
            }
            else
            {
                argsList.add( args[i] );
            }
        }

        if ( argsList.size() == 0 )
        {
            if ( FileUtils.fileExists( basedir + File.separator + "integration-tests.txt" ) )
            {
                try
                {
                    tests = FileUtils.loadFile( new File( basedir, "integration-tests.txt" ) );
                }
                catch ( IOException e )
                {
                    System.err.println( "Unable to load integration tests file" );

                    System.err.println( e.getMessage() );

                    System.exit( 2 );
                }
            }
            else
            {
                tests = discoverIntegrationTests( "." );
            }
        }
        else
        {
            tests = new ArrayList( argsList.size() );
            NumberFormat fmt = new DecimalFormat( "0000" );
            for ( int i = 0; i < argsList.size(); i++ )
            {
                String test = (String) argsList.get( i );
                if ( test.endsWith( "," ) )
                {
                    test = test.substring( 0, test.length() - 1 );
                }

                if ( StringUtils.isNumeric( test ) )
                {

                    test = "it" + fmt.format( Integer.valueOf( test ) );
                    test.trim();
                    tests.add( test );
                }
                else if ( "it".startsWith( test ) )
                {
                    test = test.trim();
                    if ( test.length() > 0 )
                    {
                        tests.add( test );
                    }
                }
                else if ( FileUtils.fileExists( test ) && new File( test ).isDirectory() )
                {
                    tests.addAll( discoverIntegrationTests( test ) );
                }
                else
                {
                    System.err.println(
                        "[WARNING] rejecting " + test + " as an invalid test or test source directory" );
                }
            }
        }

        if ( tests.size() == 0 )
        {
            System.out.println( "No tests to run" );
        }

        int exitCode = 0;

        List failed = new ArrayList();
        for ( Iterator i = tests.iterator(); i.hasNext(); )
        {
            String test = (String) i.next();

            System.out.print( test + "... " );

            String dir = basedir + "/" + test;

            if ( !new File( dir, "goals.txt" ).exists() )
            {
                System.err.println( "Test " + test + " in " + dir + " does not exist" );

                System.exit( 2 );
            }

            // TODO allow configuration
            Verifier verifier = new CommandLineVerifier( dir );
            verifier.findLocalRepo( settingsFile );

            System.out.println( "Using default local repository: " + verifier.getLocalRepo() );

            try
            {
                runIntegrationTest( verifier );
            }
            catch ( Throwable e )
            {
                verifier.resetStreams();

                System.out.println( "FAILED" );

                verifier.displayStreamBuffers();

                System.out.println( ">>>>>> Error Stacktrace:" );
                e.printStackTrace( System.out );
                System.out.println( "<<<<<< Error Stacktrace" );

                verifier.displayLogFile();

                exitCode = 1;

                failed.add( test );
            }
        }

        System.out.println( tests.size() - failed.size() + "/" + tests.size() + " passed" );
        if ( !failed.isEmpty() )
        {
            System.out.println( "Failed tests: " + failed );
        }

        System.exit( exitCode );
    }

    private static void runIntegrationTest( Verifier verifier )
        throws VerificationException
    {
        verifier.executeHook( "prebuild-hook.txt" );
    
        Properties properties = verifier.loadProperties( "system.properties" );
    
        Properties controlProperties = verifier.loadProperties( "verifier.properties" );
    
        boolean chokeOnErrorOutput =
            Boolean.valueOf( controlProperties.getProperty( "failOnErrorOutput", "true" ) ).booleanValue();
    
        List goals = verifier.loadFile( verifier.getBasedir(), "goals.txt", false );
    
        List cliOptions = verifier.loadFile( verifier.getBasedir(), "cli-options.txt", false );
    
        verifier.setCliOptions( cliOptions );
    
        verifier.setSystemProperties( properties );
    
        verifier.setVerifierProperties( controlProperties );
    
        verifier.executeGoals( goals );
    
        verifier.executeHook( "postbuild-hook.txt" );
    
        System.out.println( "*** Verifying: fail when [ERROR] detected? " + chokeOnErrorOutput + " ***" );
    
        verifier.verify( chokeOnErrorOutput );
    
        verifier.resetStreams();
    
        System.out.println( "OK" );
    }
    
    private static List discoverIntegrationTests( String directory )
        throws VerificationException
    {
        try
        {
            ArrayList tests = new ArrayList();
    
            List subTests = FileUtils.getFiles( new File( directory ), "**/goals.txt", null );
    
            for ( Iterator i = subTests.iterator(); i.hasNext(); )
            {
                File testCase = (File) i.next();
                tests.add( testCase.getParent() );
            }
    
            return tests;
        }
        catch ( IOException e )
        {
            throw new VerificationException( directory + " is not a valid test case container", e );
        }
    }

}

