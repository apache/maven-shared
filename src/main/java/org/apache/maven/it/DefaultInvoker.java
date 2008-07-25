package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.cli.CommandLineException;
import org.apache.maven.it.util.cli.CommandLineUtils;
import org.apache.maven.it.util.cli.Commandline;
import org.apache.maven.it.util.cli.StreamConsumer;
import org.apache.maven.it.util.cli.WriterStreamConsumer;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 * @noinspection UseOfSystemOutOrSystemErr,RefusedBequest
 */
public class DefaultInvoker
{
    private static final String LOG_FILENAME = "log.txt";

    public String localRepo;

    private final String basedir;

    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private PrintStream originalOut;

    private PrintStream originalErr;

    private List cliOptions = new ArrayList();

    private Properties systemProperties = new Properties();

    private Properties verifierProperties = new Properties();

    private boolean autoclean = true;

    private boolean debug;

    private String defaultMavenHome;

    public DefaultInvoker( String basedir )
    {
        this.basedir = basedir;
    }

    public void executeGoal( String goal )
        throws VerificationException
    {
        executeGoal( goal, Collections.EMPTY_MAP );
    }

    public void executeGoal( String goal, Map envVars )
        throws VerificationException
    {
        executeGoals( Arrays.asList( new String[] { goal } ), envVars );
    }

    public void executeGoals( List goals )
        throws VerificationException
    {
        executeGoals( goals, Collections.EMPTY_MAP );
    }

    public String getExecutable()
    {
        // Use a strategy for finding the maven executable, John has a simple method like this
        // but a little strategy + chain of command would be nicer.

        String mavenHome = System.getProperty( "maven.home" );

        if ( mavenHome != null )
        {
            return mavenHome + "/bin/mvn";
        }
        else
        {
            mavenHome = defaultMavenHome;

            if ( mavenHome != null )
            {
                return mavenHome + "/bin/mvn";
            }

            File f = new File( System.getProperty( "user.home" ), "m2/bin/mvn" );

            if ( f.exists() )
            {
                return f.getAbsolutePath();
            }
            else
            {
                return "mvn";
            }
        }
    }

    public void executeGoals( List goals, Map envVars )
        throws VerificationException
    {
        if ( goals.size() == 0 )
        {
            throw new VerificationException( "No goals specified" );
        }

        List allGoals = new ArrayList();

        if ( autoclean )
        {
            allGoals.add( "clean:clean" );
        }

        allGoals.addAll( goals );

        int ret;

        File logFile = new File( getBasedir(), LOG_FILENAME );
        try
        {
            Commandline cli = createCommandLine();

            for ( Iterator i = envVars.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                cli.addEnvironment( key, (String) envVars.get( key ) );
            }

            if ( envVars.get( "JAVA_HOME" ) == null )
            {
                cli.addEnvironment( "JAVA_HOME", System.getProperty( "java.home" ) );
            }

            cli.setWorkingDirectory( getBasedir() );

            for ( Iterator it = cliOptions.iterator(); it.hasNext(); )
            {
                String key = String.valueOf( it.next() );

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

            boolean useMavenRepoLocal = Boolean.valueOf( verifierProperties.getProperty( "use.mavenRepoLocal", "true" ) ).booleanValue();

            if ( useMavenRepoLocal )
            {
                // Note: Make sure that the repo is surrounded by quotes as it can possibly have
                // spaces in its path.
                cli.createArgument().setLine( "-Dmaven.repo.local=" + "\"" + localRepo + "\"" );
            }

            for ( Iterator i = allGoals.iterator(); i.hasNext(); )
            {
                cli.createArgument().setValue( (String) i.next() );
            }

            // System.out.println( "Command: " + Commandline.toString( cli.getCommandline() ) );

            ret = runCommandLine( System.getProperty( "maven.home" ), cli, logFile );
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

            throw new VerificationException( "Exit code was non-zero: " + ret + "; log = \n" + getLogContents( logFile ) );
        }
    }

    private Commandline createCommandLine()
    {
        Commandline cmd = new Commandline();
        String executable = getExecutable();
        if ( executable.endsWith( "/bin/mvn" ) )
        {
            cmd.addEnvironment( "M2_HOME", executable.substring( 0, executable.length() - 8 ) );
        }
        cmd.setExecutable( executable );
        return cmd;
    }

    private int runCommandLine( String mavenHome, Commandline cli, File logFile )
        throws CommandLineException, IOException
    {
        Writer logWriter = new FileWriter( logFile );

        StreamConsumer out = new WriterStreamConsumer( logWriter );

        StreamConsumer err = new WriterStreamConsumer( logWriter );

        try
        {
            return CommandLineUtils.executeCommandLine( cli, out, err );
        }
        finally
        {
            logWriter.close();
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

    public List getCliOptions()
    {
        return cliOptions;
    }

    public void setCliOptions( List cliOptions )
    {
        this.cliOptions = cliOptions;
    }

    public Properties getSystemProperties()
    {
        return systemProperties;
    }

    public void setSystemProperties( Properties systemProperties )
    {
        this.systemProperties = systemProperties;
    }

    public Properties getVerifierProperties()
    {
        return verifierProperties;
    }

    public boolean isAutoclean()
    {
        return autoclean;
    }

    public void setAutoclean( boolean autoclean )
    {
        this.autoclean = autoclean;
    }

    public String getBasedir()
    {
        return basedir;
    }
}
