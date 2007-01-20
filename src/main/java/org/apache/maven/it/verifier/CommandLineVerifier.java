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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.cli.CommandLineException;
import org.apache.maven.it.util.cli.CommandLineUtils;
import org.apache.maven.it.util.cli.Commandline;
import org.apache.maven.it.util.cli.StreamConsumer;
import org.apache.maven.it.util.cli.WriterStreamConsumer;

/**
 * {@link Verifier} for command line invocation of Maven.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl </a>
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * @noinspection UseOfSystemOutOrSystemErr,RefusedBequest
 */
public class CommandLineVerifier
    extends AbstractVerifier
    implements Verifier
{
    private static final String LOG_FILENAME = "log.txt";

    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private PrintStream originalOut;

    private PrintStream originalErr;

    private List cliOptions = new ArrayList();

    private Properties systemProperties = new Properties();

    private Properties verifierProperties = new Properties();

    private boolean debug;

    public CommandLineVerifier( String basedir, String settingsFile )
        throws VerificationException
    {
        this( basedir, settingsFile, false );
    }

    public CommandLineVerifier( String basedir, String settingsFile, boolean debug )
        throws VerificationException
    {
        super( basedir, settingsFile, debug );
    }

    public CommandLineVerifier( String basedir )
        throws VerificationException
    {
        this( basedir, null );
    }

    public CommandLineVerifier( String basedir, boolean debug )
        throws VerificationException
    {
        this( basedir, null, debug );
    }

    public void resetStreams()
    {
        if ( !debug )
        {
            System.setOut( originalOut );

            System.setErr( originalErr );
        }
    }

    public void displayStreamBuffers()
    {
        String out = outStream.toString();

        if ( out != null && out.trim().length() > 0 )
        {
            System.out.println( "----- Standard Out -----" );

            System.out.println( out );
        }

        String err = errStream.toString();

        if ( err != null && err.trim().length() > 0 )
        {
            System.err.println( "----- Standard Error -----" );

            System.err.println( err );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void verifyErrorFreeLog()
        throws VerificationException
    {
        List lines;
        lines = loadFile( getBasedir(), LOG_FILENAME, false );

        for ( Iterator i = lines.iterator(); i.hasNext(); )
        {
            String line = (String) i.next();

            // A hack to keep stupid velocity resource loader errors from triggering failure
            if ( line.indexOf( "[ERROR]" ) >= 0 && line.indexOf( "VM_global_library.vm" ) == -1 )
            {
                throw new VerificationException( "Error in execution." );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void executeGoals( List goals, Map envVars )
        throws VerificationException
    {
        String mavenHome = System.getProperty( "maven.home" );

        if ( goals.size() == 0 )
        {
            throw new VerificationException( "No goals specified" );
        }

        List allGoals = new ArrayList();

        allGoals.add( "clean:clean" );

        allGoals.addAll( goals );

        int ret;

        File logFile = new File( getBasedir(), LOG_FILENAME );
        try
        {
            Commandline cli = new Commandline();

            for ( Iterator i = envVars.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                cli.addEnvironment( key, (String) envVars.get( key ) );

                try
                {
                    FileUtils.fileWrite( "/tmp/foo.txt", "setting envar[ " + key + " = " + envVars.get( key ) );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                System.out.println();
            }

            cli.setWorkingDirectory( getBasedir() );

            String executable;

            // Use a strategy for finding the maven executable, John has a simple method like this
            // but a little strategy + chain of command would be nicer.

            if ( mavenHome != null )
            {
                executable = mavenHome + "/bin/mvn";
            }
            else
            {
                File f = new File( System.getProperty( "user.home" ), "m2/bin/mvn" );

                if ( f.exists() )
                {
                    executable = f.getAbsolutePath();
                }
                else
                {
                    executable = "mvn";
                }
            }

            cli.setExecutable( executable );

            for ( Iterator it = cliOptions.iterator(); it.hasNext(); )
            {
                String key = (String) it.next();

                String resolvedArg = resolveCommandLineArg( key );

                cli.createArgument().setLine( resolvedArg );
            }

            cli.createArgument().setValue( "-e" );

            cli.createArgument().setValue( "--no-plugin-registry" );

            cli.createArgument().setValue( "--batch-mode" );

            for ( Iterator i = systemProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();
                cli.createArgument().setLine( "-D" + key + "=" + systemProperties.getProperty( key ) );
            }

            boolean useMavenRepoLocal =
                Boolean.valueOf( verifierProperties.getProperty( "use.mavenRepoLocal", "true" ) ).booleanValue();

            if ( useMavenRepoLocal )
            {
                // Note: Make sure that the repo is surrounded by quotes as it can possibly have
                // spaces in its path.
                cli.createArgument().setLine( "-Dmaven.repo.local=" + "\"" + getLocalRepo() + "\"" );
            }

            for ( Iterator i = allGoals.iterator(); i.hasNext(); )
            {
                cli.createArgument().setValue( (String) i.next() );
            }

            Writer logWriter = new FileWriter( logFile );

            StreamConsumer out = new WriterStreamConsumer( logWriter );

            StreamConsumer err = new WriterStreamConsumer( logWriter );

            System.out.println( "Command: " + Commandline.toString( cli.getCommandline() ) );

            ret = CommandLineUtils.executeCommandLine( cli, out, err );

            logWriter.close();
        }
        catch ( CommandLineException e )
        {
            throw new VerificationException( e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( e );
        }

        if ( ret > 0 )
        {
            System.err.println( "Exit code: " + ret );

            throw new VerificationException(
                "Exit code was non-zero: " + ret + "; log = \n" + getLogContents( logFile ) );
        }
    }

    private static String getLogContents( File logFile )
    {
        try
        {
            return FileUtils.fileRead( logFile );
        }
        catch ( IOException e )
        {
            // ignore
            return "(Error reading log contents: " + e.getMessage() + ")";
        }
    }

    private String resolveCommandLineArg( String key )
    {
        String result = key.replaceAll( "\\$\\{basedir\\}", getBasedir() );
        if ( result.indexOf( "\\\\" ) >= 0 )
        {
            result = result.replaceAll( "\\\\", "\\" );
        }
        result = result.replaceAll( "\\/\\/", "\\/" );

        return result;
    }
}

