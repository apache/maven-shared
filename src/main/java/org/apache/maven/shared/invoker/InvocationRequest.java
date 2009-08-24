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
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Specifies the parameters used to control a Maven invocation.
 * 
 * @version $Id$
 */
public interface InvocationRequest
{

    // TODO: handle forced-reactor executions using -r/includes/excludes

    /**
     * Gets the interaction mode of the Maven invocation. By default, Maven is executed in batch mode.
     * 
     * @return <code>true</code> if Maven should be executed in interactive mode, <code>false</code> if the batch
     *         mode is used.
     */
    boolean isInteractive();

    /**
     * Gets the network mode of the Maven invocation. By default, Maven is executed in online mode.
     * 
     * @return <code>true</code> if Maven should be executed in offline mode, <code>false</code> if the online mode
     *         is used.
     */
    boolean isOffline();

    /**
     * Indicates whether Maven should enforce an update check for plugins and snapshots. By default, no update check is
     * performed.
     * 
     * @return <code>true</code> if plugins and snapshots should be updated, <code>false</code> otherwise.
     */
    boolean isUpdateSnapshots();

    /**
     * Gets the recursion behavior of a reactor invocation. By default, Maven will recursive the build into sub modules.
     * 
     * @return <code>true</code> if sub modules should be build, <code>false</code> otherwise.
     */
    boolean isRecursive();

    /**
     * Gets whether Maven should search subdirectories to build a dynamic reactor
     * @return <code>true</code> if we should search subdirectories, <code>false</code> otherwise
     */
    public boolean isActivatedReactor();

    /**
     * Gets the list of subdirectory patterns to search
     * @return list of subdirectory patterns to search, or <code>null</code> in which case defaults should be used
     */
    public String[] getActivatedReactorIncludes();

    /**
     * Gets the list of subdirectory patterns to exclude from search
     * @return list of subdirectory patterns to exclude search, or <code>null</code> in which case nothing should be excluded
     */
    public String[] getActivatedReactorExcludes();


    /**
     * Gets the debug mode of the Maven invocation. By default, Maven is executed in normal mode.
     * 
     * @return <code>true</code> if Maven should be executed in debug mode, <code>false</code> if the normal mode
     *         should be used.
     */
    boolean isDebug();

    /**
     * Gets the exception output mode of the Maven invocation. By default, Maven will not print stack traces of build
     * exceptions.
     * 
     * @return <code>true</code> if Maven should print stack traces, <code>false</code> otherwise.
     */
    boolean isShowErrors();

    /**
     * Indicates whether the environment variables of the current process should be propagated to the Maven invocation.
     * By default, the current environment variables are inherited by the new Maven invocation.
     * 
     * @return <code>true</code> if the environment variables should be propagated, <code>false</code> otherwise.
     */
    boolean isShellEnvironmentInherited();

    /**
     * Indicates whether Maven should check for plugin updates. By default, plugin updates are not suppressed.
     * 
     * @return <code>true</code> if plugin updates should be suppressed, <code>false</code> otherwise.
     */
    boolean isNonPluginUpdates();

    /**
     * Gets the failure mode of the Maven invocation. By default, the mode {@link #REACTOR_FAIL_FAST} is used.
     * 
     * @return The failure mode, one of {@link #REACTOR_FAIL_FAST}, {@link #REACTOR_FAIL_AT_END} and
     *         {@link #REACTOR_FAIL_NEVER}.
     */
    String getFailureBehavior();

    /**
     * Gets the path to the base directory of the local repository to use for the Maven invocation.
     * 
     * @param defaultDirectory The default location to use if no location is configured for this request, may be
     *            <code>null</code>.
     * @return The path to the base directory of the local repository or <code>null</code> to use the location from
     *         the <code>settings.xml</code>.
     */
    File getLocalRepositoryDirectory( File defaultDirectory );

    /**
     * Gets the input stream used to provide input for the invoked Maven build. This is in particular useful when
     * invoking Maven in interactive mode.
     * 
     * @return The input stream used to provide input for the invoked Maven build or <code>null</code> if not set.
     */
    InputStream getInputStream( InputStream defaultStream );

    /**
     * Gets the handler used to capture the standard output from the Maven build.
     * 
     * @return The output handler or <code>null</code> if not set.
     */
    InvocationOutputHandler getOutputHandler( InvocationOutputHandler defaultHandler );

    /**
     * Gets the handler used to capture the error output from the Maven build.
     * 
     * @return The error handler or <code>null</code> if not set.
     */
    InvocationOutputHandler getErrorHandler( InvocationOutputHandler defaultHandler );

    /**
     * Gets the path to the POM for the Maven invocation. If no base directory is set, the parent directory of this POM
     * will be used as the working directory for the Maven invocation.
     * 
     * @return The path to the POM for the Maven invocation or <code>null</code> if not set.
     */
    File getPomFile();

    /**
     * Gets the (unqualified) filename of the POM for the Maven invocation. This setting is ignored if
     * {@link #getPomFile()} does not return <code>null</code>. Otherwise, the base directory is assumed to contain a
     * POM with this name. By default, a file named <code>pom.xml</code> is used.
     * 
     * @return The (unqualified) filename of the POM for the Maven invocation or <code>null</code> if not set.
     */
    String getPomFileName();

    /**
     * Gets the path to the base directory of the POM for the Maven invocation. If {@link #getPomFile()} does not return
     * <code>null</code>, this setting only affects the working directory for the Maven invocation.
     * 
     * @return The path to the base directory of the POM or <code>null</code> if not set.
     */
    File getBaseDirectory();

    /**
     * Gets the path to the base directory of the POM for the Maven invocation. If {@link #getPomFile()} does not return
     * <code>null</code>, this setting only affects the working directory for the Maven invocation.
     * 
     * @param defaultDirectory The default base directory to use if none is configured for this request, may be
     *            <code>null</code>.
     * @return The path to the base directory of the POM or <code>null</code> if not set.
     */
    File getBaseDirectory( File defaultDirectory );

    /**
     * Gets the path to the base directory of the Java installation used to run Maven.
     * 
     * @return The path to the base directory of the Java installation used to run Maven or <code>null</code> to use
     *         the default Java home.
     */
    File getJavaHome();

    /**
     * Gets the system properties for the Maven invocation.
     * 
     * @return The system properties for the Maven invocation or <code>null</code> if not set.
     */
    Properties getProperties();

    /**
     * Gets the goals for the Maven invocation.
     * 
     * @return The goals for the Maven invocation or <code>null</code> if not set.
     */
    List getGoals();

    /**
     * Gets the path to the user settings for the Maven invocation.
     * 
     * @return The path to the user settings for the Maven invocation or <code>null</code> to load the user settings
     *         from the default location.
     */
    File getUserSettingsFile();

    /**
     * Gets the checksum mode of the Maven invocation.
     * 
     * @return The checksum mode, one of {@link #CHECKSUM_POLICY_WARN} and {@link #CHECKSUM_POLICY_FAIL}.
     */
    String getGlobalChecksumPolicy();

    /**
     * Gets the profiles for the Maven invocation.
     * 
     * @return The profiles for the Maven invocation or <code>null</code> if not set.
     */
    List getProfiles();

    /**
     * Gets the environment variables for the Maven invocation.
     * 
     * @return The environment variables for the Maven invocation or <code>null</code> if not set.
     */
    Map getShellEnvironments();

    /**
     * Gets the value of the <code>MAVEN_OPTS</code> environment variable.
     * 
     * @return The value of the <code>MAVEN_OPTS</code> environment variable or <code>null</code> if not set.
     */
    String getMavenOpts();
    
    /**
     * The show version behaviour (-V option)
     * @return The show version behaviour 
     * @since 2.0.11
     */
    boolean isShowVersion();

    // ----------------------------------------------------------------------
    // Reactor Failure Mode
    // ----------------------------------------------------------------------

    /**
     * The failure mode "fail-fast" where the build is stopped by the first failure.
     */
    static final String REACTOR_FAIL_FAST = "fail-fast";

    /**
     * The failure mode "fail-at-end" where the build is only failed at its very end if necessary.
     */
    static final String REACTOR_FAIL_AT_END = "fail-at-end";

    /**
     * The failure mode "fail-never" in which Maven will always exit with code 0 regardless of build failures.
     */
    static final String REACTOR_FAIL_NEVER = "fail-never";

    // ----------------------------------------------------------------------
    // Artifact repository policies
    // ----------------------------------------------------------------------

    /**
     * The strict checksum policy which fails the build if a corrupt artifact is detected.
     */
    static final String CHECKSUM_POLICY_FAIL = "fail";

    /**
     * The lax checksum policy which only outputs a warning if a corrupt artifact is detected.
     */
    static final String CHECKSUM_POLICY_WARN = "warn";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Sets the interaction mode of the Maven invocation.
     * 
     * @param interactive <code>true</code> if Maven should be executed in interactive mode, <code>false</code> if
     *            the batch mode is used.
     * @return This invocation request.
     */
    InvocationRequest setInteractive( boolean interactive );

    /**
     * Sets the network mode of the Maven invocation.
     * 
     * @param offline <code>true</code> if Maven should be executed in offline mode, <code>false</code> if the
     *            online mode is used.
     * @return This invocation request.
     */
    InvocationRequest setOffline( boolean offline );

    /**
     * Sets the debug mode of the Maven invocation.
     * 
     * @param debug <code>true</code> if Maven should be executed in debug mode, <code>false</code> if the normal
     *            mode should be used.
     * @return This invocation request.
     */
    InvocationRequest setDebug( boolean debug );

    /**
     * Sets the exception output mode of the Maven invocation.
     * 
     * @param showErrors <code>true</code> if Maven should print stack traces, <code>false</code> otherwise.
     * @return This invocation request.
     */
    InvocationRequest setShowErrors( boolean showErrors );

    /**
     * Specifies whether Maven should enforce an update check for plugins and snapshots.
     * 
     * @param updateSnapshots <code>true</code> if plugins and snapshots should be updated, <code>false</code>
     *            otherwise.
     * @return This invocation request.
     */
    InvocationRequest setUpdateSnapshots( boolean updateSnapshots );

    /**
     * Sets the failure mode of the Maven invocation.
     * 
     * @param failureBehavior The failure mode, must be one of {@link #REACTOR_FAIL_FAST}, {@link #REACTOR_FAIL_AT_END}
     *            and {@link #REACTOR_FAIL_NEVER}.
     * @return This invocation request.
     */
    InvocationRequest setFailureBehavior( String failureBehavior );

    /**
     * Dynamically constructs a reactor using the subdirectories of the current directory
     * @param includes a list of filename patterns to include, or null, in which case the default is &#x2a;/pom.xml
     * @param excludes a list of filename patterns to exclude, or null, in which case nothing is excluded
     * @return This invocation request
     */
    InvocationRequest activateReactor( String[] includes, String[] excludes );

    /**
     * Sets the path to the base directory of the local repository to use for the Maven invocation.
     * 
     * @param localRepository The path to the base directory of the local repository, may be <code>null</code>.
     * @return This invocation request.
     */
    InvocationRequest setLocalRepositoryDirectory( File localRepository );

    /**
     * Sets the input stream used to provide input for the invoked Maven build. This is in particular useful when
     * invoking Maven in interactive mode.
     * 
     * @param inputStream The input stream used to provide input for the invoked Maven build, may be <code>null</code>
     *            if not required.
     * @return This invocation request.
     */
    InvocationRequest setInputStream( InputStream inputStream );

    /**
     * Sets the handler used to capture the standard output from the Maven build.
     * 
     * @param outputHandler The output handler, may be <code>null</code> if the output is not of interest.
     * @return This invocation request.
     */
    InvocationRequest setOutputHandler( InvocationOutputHandler outputHandler );

    /**
     * Sets the handler used to capture the error output from the Maven build.
     * 
     * @param errorHandler The error handler, may be <code>null</code> if the output is not of interest.
     * @return This invocation request.
     */
    InvocationRequest setErrorHandler( InvocationOutputHandler errorHandler );

    /**
     * Sets the path to the POM for the Maven invocation. If no base directory is set, the parent directory of this POM
     * will be used as the working directory for the Maven invocation.
     * 
     * @param pomFile The path to the POM for the Maven invocation, may be <code>null</code> if not used.
     * @return This invocation request.
     */
    InvocationRequest setPomFile( File pomFile );

    /**
     * Sets the (unqualified) filename of the POM for the Maven invocation. This setting is ignored if
     * {@link #getPomFile()} does not return <code>null</code>. Otherwise, the base directory is assumed to contain a
     * POM with this name.
     * 
     * @param pomFilename The (unqualified) filename of the POM for the Maven invocation, may be <code>null</code> if
     *            not used.
     * @return This invocation request.
     */
    InvocationRequest setPomFileName( String pomFilename );

    /**
     * Sets the path to the base directory of the POM for the Maven invocation. If {@link #getPomFile()} does not return
     * <code>null</code>, this setting only affects the working directory for the Maven invocation.
     * 
     * @param basedir The path to the base directory of the POM, may be <code>null</code> if not used.
     * @return This invocation request.
     */
    InvocationRequest setBaseDirectory( File basedir );

    /**
     * Sets the path to the base directory of the Java installation used to run Maven.
     * 
     * @param javaHome The path to the base directory of the Java installation used to run Maven, may be
     *            <code>null</code> to use the default Java home.
     * @return This invocation request.
     */
    InvocationRequest setJavaHome( File javaHome );

    /**
     * Sets the system properties for the Maven invocation.
     * 
     * @param properties The system properties for the Maven invocation, may be <code>null</code> if not set.
     * @return This invocation request.
     */
    InvocationRequest setProperties( Properties properties );

    /**
     * Sets the goals for the Maven invocation.
     * 
     * @param goals The goals for the Maven invocation, may be <code>null</code> to execute the POMs default goal.
     * @return This invocation request.
     */
    InvocationRequest setGoals( List goals );

    /**
     * Sets the profiles for the Maven invocation.
     * 
     * @param profiles The profiles for the Maven invocation, may be <code>null</code> to use the default profiles.
     * @return This invocation request.
     */
    InvocationRequest setProfiles( List profiles );

    /**
     * Specifies whether the environment variables of the current process should be propagated to the Maven invocation.
     * 
     * @param shellEnvironmentInherited <code>true</code> if the environment variables should be propagated,
     *            <code>false</code> otherwise.
     * @return This invocation request.
     */
    InvocationRequest setShellEnvironmentInherited( boolean shellEnvironmentInherited );

    /**
     * Sets the path to the user settings for the Maven invocation.
     * 
     * @param userSettings The path to the user settings for the Maven invocation, may be <code>null</code> to load
     *            the user settings from the default location.
     * @return This invocation request.
     */
    InvocationRequest setUserSettingsFile( File userSettings );

    /**
     * Sets the checksum mode of the Maven invocation.
     * 
     * @param globalChecksumPolicy The checksum mode, must be one of {@link #CHECKSUM_POLICY_WARN} and
     *            {@link #CHECKSUM_POLICY_FAIL}.
     * @return This invocation request.
     */
    InvocationRequest setGlobalChecksumPolicy( String globalChecksumPolicy );

    /**
     * Specifies whether Maven should check for plugin updates.
     * 
     * @param nonPluginUpdates <code>true</code> if plugin updates should be suppressed, <code>false</code>
     *            otherwise.
     * @return This invocation request.
     */
    InvocationRequest setNonPluginUpdates( boolean nonPluginUpdates );

    /**
     * Sets the recursion behavior of a reactor invocation.
     * 
     * @param recursive <code>true</code> if sub modules should be build, <code>false</code> otherwise.
     * @return This invocation request.
     */
    InvocationRequest setRecursive( boolean recursive );

    /**
     * Adds the specified environment variable to the Maven invocation.
     * 
     * @param name The name of the environment variable, must not be <code>null</code>.
     * @param value The value of the environment variable, must not be <code>null</code>.
     * @return This invocation request.
     */
    InvocationRequest addShellEnvironment( String name, String value );

    /**
     * Sets the value of the <code>MAVEN_OPTS</code> environment variable.
     * 
     * @param mavenOpts The value of the <code>MAVEN_OPTS</code> environment variable, may be <code>null</code> to
     *            use the default options.
     * @return This invocation request.
     */
    InvocationRequest setMavenOpts( String mavenOpts );
    
    /**
     * enable displaying version without stopping the build (-V cli option)
     * @param showVersion enable displaying version 
     * @since 2.0.11
     * @return This invocation request.
     */
    InvocationRequest setShowVersion( boolean showVersion );

}
