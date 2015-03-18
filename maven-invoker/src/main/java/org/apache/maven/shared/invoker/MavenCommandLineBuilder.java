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

    private File mavenExecutable;

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

        setToolchainsLocation( request, cli );
        
        setProperties( request, cli );

        setProfiles( request, cli );

        setGoals( request, cli );

        setThreads( request, cli );
        
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

            cli.createArg().setValue( "-s" );
            cli.createArg().setValue( userSettingsFile.getPath() );
        }
        
        File globalSettingsFile = request.getGlobalSettingsFile();

        if ( globalSettingsFile != null )
        {
            try
            {
                File canSet = globalSettingsFile.getCanonicalFile();
                globalSettingsFile = canSet;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize global settings path: " + globalSettingsFile.getAbsolutePath()
                    + ". Using as-is.", e );
            }

            cli.createArg().setValue( "-gs" );
            cli.createArg().setValue( globalSettingsFile.getPath() );
        }

    }
    
    protected void setToolchainsLocation( InvocationRequest request, Commandline cli )
    {
        File toolchainsFile = request.getToolchainsFile();

        if ( toolchainsFile != null )
        {
            try
            {
                File canSet = toolchainsFile.getCanonicalFile();
                toolchainsFile = canSet;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize toolchains path: " + toolchainsFile.getAbsolutePath()
                    + ". Using as-is.", e );
            }

            cli.createArg().setValue( "-t" );
            cli.createArg().setValue( toolchainsFile.getPath() );
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
                // MSHARED-261: Ensure M2_HOME is not inherited, but gets a proper value
                cli.addEnvironment( "M2_HOME", getMavenHome().getAbsolutePath() );
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

        for ( Map.Entry<String, String> entry : request.getShellEnvironments().entrySet() )
        {
            cli.addEnvironment( entry.getKey(), entry.getValue() );
        }
    }

    protected void setProfiles( InvocationRequest request, Commandline cli )
    {
        List<String> profiles = request.getProfiles();

        if ( ( profiles != null ) && !profiles.isEmpty() )
        {
            cli.createArg().setValue( "-P" );
            cli.createArg().setValue( StringUtils.join( profiles.iterator(), "," ) );
        }

    }

    protected void setGoals( InvocationRequest request, Commandline cli )
    {
        List<String> goals = request.getGoals();

        if ( ( goals != null ) && !goals.isEmpty() )
        {
            cli.createArg().setLine( StringUtils.join( goals.iterator(), " " ) );
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

                cli.createArg().setValue( "-D" );
                cli.createArg().setValue( key + '=' + value );
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

                cli.createArg().setValue( "-f" );
                cli.createArg().setValue( pom.getName() );
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

            cli.createArg().setValue( "-D" );
            cli.createArg().setValue( "maven.repo.local=" + localRepositoryDirectory.getPath() );
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
                cli.createArg().setValue( "-fae" );
            }
            else if ( InvocationRequest.REACTOR_FAIL_NEVER.equals( failureBehavior ) )
            {
                cli.createArg().setValue( "-fn" );
            }
        }

        if ( request.isActivatedReactor() )
        {
            cli.createArg().setValue( "-r" );
            String[] includes = request.getActivatedReactorIncludes();
            String[] excludes = request.getActivatedReactorExcludes();
            if ( includes != null )
            {
                cli.createArg().setValue( "-D" );
                cli.createArg().setValue( "maven.reactor.includes=" + StringUtils.join( includes, "," ) );
            }
            if ( excludes != null )
            {
                cli.createArg().setValue( "-D" );
                cli.createArg().setValue( "maven.reactor.excludes=" + StringUtils.join( excludes, "," ) );
            }
        }

        if ( StringUtils.isNotEmpty( request.getResumeFrom() ) )
        {
            cli.createArg().setValue( "-rf" );
            cli.createArg().setValue( request.getResumeFrom() );
        }

        List<String> projectList = request.getProjects();
        if ( projectList != null )
        {
            cli.createArg().setValue( "-pl" );
            cli.createArg().setValue( StringUtils.join( projectList.iterator(), "," ) );

            if ( request.isAlsoMake() )
            {
                cli.createArg().setValue( "-am" );
            }

            if ( request.isAlsoMakeDependents() )
            {
                cli.createArg().setValue( "-amd" );
            }
        }
    }

    protected void setFlags( InvocationRequest request, Commandline cli )
    {
        if ( !request.isInteractive() )
        {
            cli.createArg().setValue( "-B" );
        }

        if ( request.isOffline() )
        {
            cli.createArg().setValue( "-o" );
        }

        if ( request.isUpdateSnapshots() )
        {
            cli.createArg().setValue( "-U" );
        }

        if ( !request.isRecursive() )
        {
            cli.createArg().setValue( "-N" );
        }

        if ( request.isDebug() )
        {
            cli.createArg().setValue( "-X" );
        }
        // this is superceded by -X, if it exists.
        else if ( request.isShowErrors() )
        {
            cli.createArg().setValue( "-e" );
        }

        String checksumPolicy = request.getGlobalChecksumPolicy();
        if ( InvocationRequest.CHECKSUM_POLICY_FAIL.equals( checksumPolicy ) )
        {
            cli.createArg().setValue( "-C" );
        }
        else if ( InvocationRequest.CHECKSUM_POLICY_WARN.equals( checksumPolicy ) )
        {
            cli.createArg().setValue( "-c" );
        }
        if ( request.isNonPluginUpdates() )
        {
            cli.createArg().setValue( "-npu" );
        }
        
        if ( request.isShowVersion() )
        {
            cli.createArg().setValue( "-V" );
        }
    }

    protected void setThreads( InvocationRequest request, Commandline cli )
    {
        String threads = request.getThreads();
        if ( StringUtils.isNotEmpty( threads ) )
        {
            cli.createArg().setValue( "-T" );
            cli.createArg().setValue( threads );
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
                    if ( binDir != null && "bin".equals( binDir.getName() ) )
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

        if ( mavenExecutable == null || !mavenExecutable.isAbsolute() )
        {
            String executable;
            if ( mavenExecutable != null )
            {
                executable = mavenExecutable.getPath();
            }
            else if ( Os.isFamily( "windows" ) )
            {
                if ( new File( mavenHome, "/bin/mvn.cmd" ).exists() )
                {
                    executable = "mvn.cmd";
                }
                else
                {
                    executable = "mvn.bat";
                }
            }
            else
            {
                executable = "mvn";
            }
            
            mavenExecutable = new File( mavenHome, "/bin/" + executable );
            
            try
            {
                File canonicalMvn = mavenExecutable.getCanonicalFile();
                mavenExecutable = canonicalMvn;
            }
            catch ( IOException e )
            {
                logger.debug( "Failed to canonicalize maven executable: " + mavenExecutable + ". Using as-is.", e );
            }

            if ( !mavenExecutable.isFile() )
            {
                throw new CommandLineConfigurationException( "Maven executable not found at: " + mavenExecutable );
            }
        }

        return mavenExecutable;
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

    /**
     * {@code mavenExecutable} can either be relative to ${maven.home}/bin/ or absolute 
     * 
     * @param mavenExecutable the executable
     */
    public void setMavenExecutable( File mavenExecutable )
    {
        this.mavenExecutable = mavenExecutable;
    }

    public File getMavenExecutable()
    {
        return mavenExecutable;
    }

}
