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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * @version $Id$
 */
public class MavenCommandLineBuilder
{

    private static final InvokerLogger DEFAULT_LOGGER = new SystemOutLogger();

    private InvokerLogger logger = DEFAULT_LOGGER;

    private File workingDirectory;

    private File localRepositoryDirectory;

    private File mavenHome;

    private File mvnCommand;

    private Properties systemEnvVars;

    public Commandline build( InvocationRequest request )
        throws CommandLineConfigurationException
    {
        try
        {
            checkRequiredState();
        }
        catch ( IOException e )
        {
            throw new CommandLineConfigurationException( e.getMessage(), e );
        }
        File mvn = null;
        try
        {
            mvn = findMavenExecutable();
        }
        catch ( IOException e )
        {
            throw new CommandLineConfigurationException( e.getMessage(), e );
        }
        Commandline cli = new Commandline();

        cli.setExecutable( mvn.getAbsolutePath() );

        // handling for OS-level envars
        setShellEnvironment( request, cli );

        // interactive, offline, update-snapshots,
        // debug/show-errors, checksum policy
        setFlags( request, cli );

        // failure behavior and [eventually] forced-reactor
        // includes/excludes, etc.
        setReactorBehavior( request, cli );

        // working directory and local repository location
        setEnvironmentPaths( request, cli );

        // pom-file and basedir handling
        setPomLocation( request, cli );

        setSettingsLocation( request, cli );

        setProperties( request, cli );

        setProfiles( request, cli );

        setGoals( request, cli );

        return cli;
    }

    protected void checkRequiredState()
        throws IOException
    {
        if ( logger == null )
        {
            throw new IllegalStateException( "A logger instance is required." );
        }

        if ( ( mavenHome == null ) && ( System.getProperty( "maven.home" ) == null ) )
        // can be restored with 1.5
        // && ( System.getenv( "M2_HOME" ) != null ) )
        {
            if ( !getSystemEnvVars().containsKey( "M2_HOME" ) )
            {
                throw new IllegalStateException( "Maven application directory was not "
                    + "specified, and ${maven.home} is not provided in the system "
                    + "properties. Please specify at least on of these." );
            }
        }
    }

    protected void setSettingsLocation( InvocationRequest request, Commandline cli )
    {
        File userSettingsFile = request.getUserSettingsFile();

        if ( userSettingsFile != null )
        {
            try
            {
                File canSet = userSettingsFile.getCanonicalFile();
                userSettingsFile = canSet;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize user settings path: " + userSettingsFile.getAbsolutePath()
                    + ". Using as-is.", e );
            }

            cli.createArgument().setValue( "-s" );
            cli.createArgument().setValue( userSettingsFile.getPath() );
        }
    }

    protected void setShellEnvironment( InvocationRequest request, Commandline cli )
        throws CommandLineConfigurationException
    {
        if ( request.isShellEnvironmentInherited() )
        {
            try
            {
                cli.addSystemEnvironment();
                cli.addEnvironment( "MAVEN_TERMINATE_CMD", "on" );
            }
            catch ( IOException e )
            {
                throw new CommandLineConfigurationException( "Error reading shell environment variables. Reason: "
                    + e.getMessage(), e );
            }
            catch ( Exception e )
            {
                if ( e instanceof RuntimeException )
                {
                    throw (RuntimeException) e;
                }
                else
                {
                    IllegalStateException error =
                        new IllegalStateException( "Unknown error retrieving shell environment variables. Reason: "
                            + e.getMessage() );
                    error.initCause( e );

                    throw error;
                }
            }
        }

        if ( request.getJavaHome() != null )
        {
            cli.addEnvironment( "JAVA_HOME", request.getJavaHome().getAbsolutePath() );
        }

        if ( request.getMavenOpts() != null )
        {
            cli.addEnvironment( "MAVEN_OPTS", request.getMavenOpts() );
        }

        for ( Iterator iterator = request.getShellEnvironments().keySet().iterator(); iterator.hasNext(); )
        {
            String key = (String) iterator.next();
            String value = (String) request.getShellEnvironments().get( key );
            cli.addEnvironment( key, value );
        }
    }

    protected void setProfiles( InvocationRequest request, Commandline cli )
    {
        List profiles = request.getProfiles();

        if ( ( profiles != null ) && !profiles.isEmpty() )
        {
            cli.createArgument().setValue( "-P" );
            cli.createArgument().setValue( StringUtils.join( profiles.iterator(), "," ) );
        }

    }

    protected void setGoals( InvocationRequest request, Commandline cli )
    {
        List goals = request.getGoals();

        if ( ( goals != null ) && !goals.isEmpty() )
        {
            cli.createArgument().setLine( StringUtils.join( goals.iterator(), " " ) );
        }
    }

    protected void setProperties( InvocationRequest request, Commandline cli )
    {
        Properties properties = request.getProperties();

        if ( properties != null )
        {
            for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                cli.createArgument().setValue( "-D" );
                cli.createArgument().setValue( key + '=' + value );
            }
        }
    }

    protected void setPomLocation( InvocationRequest request, Commandline cli )
    {
        boolean pomSpecified = false;

        File pom = request.getPomFile();
        String pomFilename = request.getPomFileName();
        File baseDirectory = request.getBaseDirectory();

        if ( pom != null )
        {
            pomSpecified = true;
        }
        else if ( baseDirectory != null )
        {
            if ( baseDirectory.isDirectory() )
            {
                if ( pomFilename != null )
                {
                    pom = new File( baseDirectory, pomFilename );

                    pomSpecified = true;
                }
                else
                {
                    pom = new File( baseDirectory, "pom.xml" );
                }
            }
            else
            {
                logger.warn( "Base directory is a file. Using base directory as POM location." );

                pom = baseDirectory;

                pomSpecified = true;
            }
        }

        if ( pomSpecified )
        {
            try
            {
                File canPom = pom.getCanonicalFile();
                pom = canPom;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize the POM path: " + pom + ". Using as-is.", e );
            }

            if ( !"pom.xml".equals( pom.getName() ) )
            {
                logger.debug( "Specified POM file is not named \'pom.xml\'. "
                    + "Using the \'-f\' command-line option to accommodate non-standard filename..." );

                cli.createArgument().setValue( "-f" );
                cli.createArgument().setValue( pom.getName() );
            }
        }
    }

    protected void setEnvironmentPaths( InvocationRequest request, Commandline cli )
    {
        File workingDirectory = request.getBaseDirectory();

        if ( workingDirectory == null )
        {
            File pomFile = request.getPomFile();
            if ( pomFile != null )
            {
                workingDirectory = pomFile.getParentFile();
            }
        }

        if ( workingDirectory == null )
        {
            workingDirectory = this.workingDirectory;
        }

        if ( workingDirectory == null )
        {
            workingDirectory = new File( System.getProperty( "user.dir" ) );
        }
        else if ( workingDirectory.isFile() )
        {
            logger.warn( "Specified base directory (" + workingDirectory + ") is a file."
                + " Using its parent directory..." );

            workingDirectory = workingDirectory.getParentFile();
        }

        try
        {
            cli.setWorkingDirectory( workingDirectory.getCanonicalPath() );
        }
        catch ( IOException e )
        {
            logger.debug( "Failed to canonicalize base directory: " + workingDirectory + ". Using as-is.", e );

            cli.setWorkingDirectory( workingDirectory.getAbsolutePath() );
        }

        File localRepositoryDirectory = request.getLocalRepositoryDirectory( this.localRepositoryDirectory );

        if ( localRepositoryDirectory != null )
        {
            try
            {
                File canLRD = localRepositoryDirectory.getCanonicalFile();
                localRepositoryDirectory = canLRD;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize local repository directory: " + localRepositoryDirectory
                    + ". Using as-is.", e );
            }

            if ( !localRepositoryDirectory.isDirectory() )
            {
                throw new IllegalArgumentException( "Local repository location: \'" + localRepositoryDirectory
                    + "\' is NOT a directory." );
            }

            cli.createArgument().setValue( "-D" );
            cli.createArgument().setValue( "maven.repo.local=" + localRepositoryDirectory.getPath() );
        }
    }

    protected void setReactorBehavior( InvocationRequest request, Commandline cli )
    {
        // NOTE: The default is "fail-fast"
        String failureBehavior = request.getFailureBehavior();

        if ( StringUtils.isNotEmpty( failureBehavior ) )
        {
            if ( InvocationRequest.REACTOR_FAIL_AT_END.equals( failureBehavior ) )
            {
                cli.createArgument().setValue( "-fae" );
            }
            else if ( InvocationRequest.REACTOR_FAIL_NEVER.equals( failureBehavior ) )
            {
                cli.createArgument().setValue( "-fn" );
            }
        }

        if ( request.isActivatedReactor() )
        {
            cli.createArgument().setValue( "-r" );
            String[] includes = request.getActivatedReactorIncludes();
            String[] excludes = request.getActivatedReactorExcludes();
            if ( includes != null )
            {
                cli.createArgument().setValue( "-D" );
                cli.createArgument().setValue( "maven.reactor.includes=" + StringUtils.join( includes, "," ) );
            }
            if ( excludes != null )
            {
                cli.createArgument().setValue( "-D" );
                cli.createArgument().setValue( "maven.reactor.excludes=" + StringUtils.join( excludes, "," ) );
            }
        }
    }

    protected void setFlags( InvocationRequest request, Commandline cli )
    {
        if ( !request.isInteractive() )
        {
            cli.createArgument().setValue( "-B" );
        }

        if ( request.isOffline() )
        {
            cli.createArgument().setValue( "-o" );
        }

        if ( request.isUpdateSnapshots() )
        {
            cli.createArgument().setValue( "-U" );
        }

        if ( !request.isRecursive() )
        {
            cli.createArgument().setValue( "-N" );
        }

        if ( request.isDebug() )
        {
            cli.createArgument().setValue( "-X" );
        }
        // this is superceded by -X, if it exists.
        else if ( request.isShowErrors() )
        {
            cli.createArgument().setValue( "-e" );
        }

        String checksumPolicy = request.getGlobalChecksumPolicy();
        if ( InvocationRequest.CHECKSUM_POLICY_FAIL.equals( checksumPolicy ) )
        {
            cli.createArgument().setValue( "-C" );
        }
        else if ( InvocationRequest.CHECKSUM_POLICY_WARN.equals( checksumPolicy ) )
        {
            cli.createArgument().setValue( "-c" );
        }
        if ( request.isNonPluginUpdates() )
        {
            cli.createArgument().setValue( "-npu" );
        }
        
        if ( request.isShowVersion() )
        {
            cli.createArg().setValue( "-V" );
        }
    }

    protected File findMavenExecutable()
        throws CommandLineConfigurationException, IOException
    {
        if ( mavenHome == null )
        {
            String mavenHomeProperty = System.getProperty( "maven.home" );
            if ( mavenHomeProperty != null )
            {
                mavenHome = new File( mavenHomeProperty );
                if ( !mavenHome.isDirectory() )
                {
                    File binDir = mavenHome.getParentFile();
                    if ( "bin".equals( binDir.getName() ) )
                    {
                        // ah, they specified the mvn
                        // executable instead...
                        mavenHome = binDir.getParentFile();
                    }
                    else
                    {
                        throw new IllegalStateException( "${maven.home} is not specified as a directory: \'"
                            + mavenHomeProperty + "\'." );
                    }
                }
            }

            if ( ( mavenHome == null ) && ( getSystemEnvVars().getProperty( "M2_HOME" ) != null ) )
            {
                mavenHome = new File( getSystemEnvVars().getProperty( "M2_HOME" ) );
            }
        }

        logger.debug( "Using ${maven.home} of: \'" + mavenHome + "\'." );

        if ( mvnCommand == null )
        {
            if ( Os.isFamily( "windows" ) )
            {
                mvnCommand = new File( mavenHome, "/bin/mvn.bat" );
            }
            else
            {
                mvnCommand = new File( mavenHome, "/bin/mvn" );
            }

            try
            {
                File canonicalMvn = mvnCommand.getCanonicalFile();
                mvnCommand = canonicalMvn;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize maven executable: " + mvnCommand + ". Using as-is.", e );
            }

            if ( !mvnCommand.exists() )
            {
                throw new CommandLineConfigurationException( "Maven executable not found at: " + mvnCommand );
            }
        }

        return mvnCommand;
    }

    /**
     * Wraps a path with quotes to handle paths with spaces. If no spaces are found, the original string is returned.
     * 
     * @param path string to wrap if containing spaces
     * @return quote wrapped string
     * @deprecated Quoting of command line arguments should be left to the Commandline from plexus-utils.
     */
    public String wrapStringWithQuotes( String path )
    {
        if ( path.indexOf( " " ) > -1 )
        {
            return "\"" + path + "\"";
        }
        else
        {
            return path;
        }
    }

    private Properties getSystemEnvVars()
        throws IOException
    {
        if ( this.systemEnvVars == null )
        {
            // with 1.5 replace with System.getenv()
            this.systemEnvVars = CommandLineUtils.getSystemEnvVars();
        }
        return this.systemEnvVars;
    }

    public File getLocalRepositoryDirectory()
    {
        return localRepositoryDirectory;
    }

    public void setLocalRepositoryDirectory( File localRepositoryDirectory )
    {
        this.localRepositoryDirectory = localRepositoryDirectory;
    }

    public InvokerLogger getLogger()
    {
        return logger;
    }

    public void setLogger( InvokerLogger logger )
    {
        this.logger = logger;
    }

    public File getMavenHome()
    {
        return mavenHome;
    }

    public void setMavenHome( File mavenHome )
    {
        this.mavenHome = mavenHome;
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    public void setWorkingDirectory( File workingDirectory )
    {
        this.workingDirectory = workingDirectory;
    }

}
