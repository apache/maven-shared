package org.apache.maven.it;

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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.apache.maven.shared.utils.cli.CommandLineUtils;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.cli.StreamConsumer;
import org.apache.maven.shared.utils.cli.WriterStreamConsumer;

/**
 * @author Benjamin Bentmann
 */
class ForkedLauncher
    implements MavenLauncher
{

    private final String mavenHome;

    private final String executable;

    public ForkedLauncher( String mavenHome )
    {
        this( mavenHome, false );
    }

    public ForkedLauncher( String mavenHome, boolean debugJvm )
    {
        this.mavenHome = mavenHome;

        String script = debugJvm ? "mvnDebug" : "mvn";

        if ( mavenHome != null )
        {
            executable = new File( mavenHome, "bin/" + script ).getPath();
        }
        else
        {
            executable = script;
        }
    }

    public int run( String[] cliArgs, Map envVars, String workingDirectory, File logFile )
        throws IOException, LauncherException
    {
        Commandline cmd = new Commandline();

        cmd.setExecutable( executable );

        if ( mavenHome != null )
        {
            cmd.addEnvironment( "M2_HOME", mavenHome );
        }

        if ( envVars != null )
        {
            for ( Object o : envVars.keySet() )
            {
                String key = (String) o;

                cmd.addEnvironment( key, (String) envVars.get( key ) );
            }
        }

        if ( envVars == null || envVars.get( "JAVA_HOME" ) == null )
        {
            cmd.addEnvironment( "JAVA_HOME", System.getProperty( "java.home" ) );
        }

        cmd.addEnvironment( "MAVEN_TERMINATE_CMD", "on" );

        cmd.setWorkingDirectory( workingDirectory );

        for ( String cliArg : cliArgs )
        {
            cmd.createArg().setValue( cliArg );
        }

        Writer logWriter = new FileWriter( logFile );

        StreamConsumer out = new WriterStreamConsumer( logWriter );

        StreamConsumer err = new WriterStreamConsumer( logWriter );

        try
        {
            return CommandLineUtils.executeCommandLine( cmd, out, err );
        }
        catch ( CommandLineException e )
        {
            throw new LauncherException( "Failed to run Maven: " + e.getMessage() + "\n" + cmd, e );
        }
        finally
        {
            logWriter.close();
        }
    }

    public int run( String[] cliArgs, String workingDirectory, File logFile )
        throws IOException, LauncherException
    {
        return run( cliArgs, Collections.EMPTY_MAP, workingDirectory, logFile );
    }

}
