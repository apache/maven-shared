package org.apache.maven.shared.invoker;

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
import java.io.InputStream;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Class intended to be used by clients who wish to invoke a forked Maven process from their applications
 * 
 * @author jdcasey
 */
@Component( role = Invoker.class, hint = "default" )
public class DefaultInvoker
    implements Invoker
{

    public static final String ROLE_HINT = "default";

    private static final InvokerLogger DEFAULT_LOGGER = new SystemOutLogger();

    private static final InvocationOutputHandler DEFAULT_OUTPUT_HANDLER = new SystemOutHandler();

    private File localRepositoryDirectory;

    private InvokerLogger logger = DEFAULT_LOGGER;

    private File workingDirectory;

    private File mavenHome;

    private File mavenExecutable;

    private InvocationOutputHandler outputHandler = DEFAULT_OUTPUT_HANDLER;

    private InputStream inputStream;

    private InvocationOutputHandler errorHandler = DEFAULT_OUTPUT_HANDLER;

    public InvocationResult execute( InvocationRequest request )
        throws MavenInvocationException
    {
        MavenCommandLineBuilder cliBuilder = new MavenCommandLineBuilder();

        InvokerLogger logger = getLogger();
        if ( logger != null )
        {
            cliBuilder.setLogger( getLogger() );
        }

        File localRepo = getLocalRepositoryDirectory();
        if ( localRepo != null )
        {
            cliBuilder.setLocalRepositoryDirectory( getLocalRepositoryDirectory() );
        }

        File mavenHome = getMavenHome();
        if ( mavenHome != null )
        {
            cliBuilder.setMavenHome( getMavenHome() );
        }

        File mavenExecutable = getMavenExecutable();
        if ( mavenExecutable != null )
        {
            cliBuilder.setMavenExecutable( mavenExecutable );
        }

        File workingDirectory = getWorkingDirectory();
        if ( workingDirectory != null )
        {
            cliBuilder.setWorkingDirectory( getWorkingDirectory() );
        }

        Commandline cli;
        try
        {
            cli = cliBuilder.build( request );
        }
        catch ( CommandLineConfigurationException e )
        {
            throw new MavenInvocationException( "Error configuring command-line. Reason: " + e.getMessage(), e );
        }

        DefaultInvocationResult result = new DefaultInvocationResult();

        try
        {
            int exitCode = executeCommandLine( cli, request );

            result.setExitCode( exitCode );
        }
        catch ( CommandLineException e )
        {
            result.setExecutionException( e );
        }

        return result;
    }

    private int executeCommandLine( Commandline cli, InvocationRequest request )
        throws CommandLineException
    {
        int result = Integer.MIN_VALUE;

        InputStream inputStream = request.getInputStream( this.inputStream );
        InvocationOutputHandler outputHandler = request.getOutputHandler( this.outputHandler );
        InvocationOutputHandler errorHandler = request.getErrorHandler( this.errorHandler );

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Executing: " + cli );
        }

        if ( request.isBatchMode() )
        {
            if ( inputStream != null )
            {
                getLogger().info( "Executing in batch mode. The configured input stream will be ignored." );
            }

            result = CommandLineUtils.executeCommandLine( cli, outputHandler, errorHandler );
        }
        else
        {
            if ( inputStream == null )
            {
                getLogger().warn( "Maven will be executed in interactive mode"
                    + ", but no input stream has been configured for this MavenInvoker instance." );

                result = CommandLineUtils.executeCommandLine( cli, outputHandler, errorHandler );
            }
            else
            {
                result = CommandLineUtils.executeCommandLine( cli, inputStream, outputHandler, errorHandler );
            }
        }

        return result;
    }

    public File getLocalRepositoryDirectory()
    {
        return localRepositoryDirectory;
    }

    public InvokerLogger getLogger()
    {
        return logger;
    }

    public Invoker setLocalRepositoryDirectory( File localRepositoryDirectory )
    {
        this.localRepositoryDirectory = localRepositoryDirectory;
        return this;
    }

    public Invoker setLogger( InvokerLogger logger )
    {
        this.logger = ( logger != null ) ? logger : DEFAULT_LOGGER;
        return this;
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    public Invoker setWorkingDirectory( File workingDirectory )
    {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public File getMavenHome()
    {
        return mavenHome;
    }

    public Invoker setMavenHome( File mavenHome )
    {
        this.mavenHome = mavenHome;

        return this;
    }

    public File getMavenExecutable()
    {
        return mavenExecutable;
    }

    public Invoker setMavenExecutable( File mavenExecutable )
    {
        this.mavenExecutable = mavenExecutable;
        return this;
    }

    public Invoker setErrorHandler( InvocationOutputHandler errorHandler )
    {
        this.errorHandler = errorHandler;
        return this;
    }

    public Invoker setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
        return this;
    }

    public Invoker setOutputHandler( InvocationOutputHandler outputHandler )
    {
        this.outputHandler = outputHandler;
        return this;
    }

}
