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

    /**
     * By default, Maven is executed in batch mode. This mean no interaction with the Maven process can be done.
     * 
     * @return <code>true</code> if Maven should be executed in batch mode, <code>false</code> if Maven is executed in
     *         interactive mode.
     * @since 3.0.0
     */
    boolean isBatchMode();

    /**
     * Gets the network mode of the Maven invocation. By default, Maven is executed in online mode.
     * 
     * @return <code>true</code> if Maven should be executed in offline mode, <code>false</code> if the online mode is
     *         used.
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
     * A list of specified reactor projects to build instead of all projects. A project can be specified by
     * [groupId]:artifactId or by its relative path.
     * 
     * @return the list of projects to add to reactor build, otherwise {@code null}
     * @since 2.1
     */
    List<String> getProjects();

    /**
     * Get the value of the {@code also-make} argument.
     * 
     * @return {@code true} if the argument {@code also-make} was specified, otherwise {@code false}
     * @since 2.1
     */
    boolean isAlsoMake();

    /**
     * Get the value of the {@code also-make-dependents}
     * 
     * @return {@code true} if the argument {@code also-make-dependents} was specified, otherwise {@code false}
     * @since 2.1
     */
    boolean isAlsoMakeDependents();

    /**
     * Get the value of {@code resume-from}
     * 
     * @return specified reactor project to resume from
     * @since 2.1
     */
    String getResumeFrom();

    /**
     * Gets the debug mode of the Maven invocation. By default, Maven is executed in normal mode.
     * 
     * @return <code>true</code> if Maven should be executed in debug mode, <code>false</code> if the normal mode should
     *         be used.
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
     * Gets the failure mode of the Maven invocation. By default, the mode {@link ReactorFailureBehavior#FailFast} is
     * used.
     * 
     * @return The failure mode, one of {@link ReactorFailureBehavior#FailFast},
     *         {@link ReactorFailureBehavior#FailAtEnd} and {@link ReactorFailureBehavior#FailNever}.
     * @since 3.0.0
     */
    ReactorFailureBehavior getReactorFailureBehavior();

    /**
     * Gets the path to the base directory of the local repository to use for the Maven invocation.
     * 
     * @param defaultDirectory The default location to use if no location is configured for this request, may be
     *            <code>null</code>.
     * @return The path to the base directory of the local repository or <code>null</code> to use the location from the
     *         <code>settings.xml</code>.
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
     * @return The path to the base directory of the Java installation used to run Maven or <code>null</code> to use the
     *         default Java home.
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
    List<String> getGoals();

    /**
     * Gets the path to the user settings for the Maven invocation.
     * 
     * @return The path to the user settings for the Maven invocation or <code>null</code> to load the user settings
     *         from the default location.
     */
    File getUserSettingsFile();

    /**
     * Gets the path to the global settings for the Maven invocation.
     * 
     * @return The path to the global settings for the Maven invocation or <code>null</code> to load the global settings
     *         from the default location.
     * @since 2.1
     */
    File getGlobalSettingsFile();

    /**
     * Gets the path to the custom toolchains file
     * 
     * @return The path to the custom toolchains file or <code>null</code> to load the toolchains from the default
     *         location
     * @since 2.1
     */
    File getToolchainsFile();

    /**
     * Alternate path for the global toolchains file <b>Note. This is available starting with Maven 3.3.1</b>
     * 
     * @return The path to the custom global toolchains file or <code>null</code> to load the global toolchains from the
     *         default location.
     * @since 3.0.0
     */
    File getGlobalToolchainsFile();

    /**
     * Gets the checksum mode of the Maven invocation.
     * 
     * @return The checksum mode, one of {@link CheckSumPolicy#Warn} and {@link CheckSumPolicy#Fail}.
     * @since 3.0.0
     */
    CheckSumPolicy getGlobalChecksumPolicy();

    /**
     * Gets the profiles for the Maven invocation.
     * 
     * @return The profiles for the Maven invocation or <code>null</code> if not set.
     */
    List<String> getProfiles();

    /**
     * Gets the environment variables for the Maven invocation.
     * 
     * @return The environment variables for the Maven invocation or <code>null</code> if not set.
     */
    Map<String, String> getShellEnvironments();

    /**
     * Gets the value of the <code>MAVEN_OPTS</code> environment variable.
     * 
     * @return The value of the <code>MAVEN_OPTS</code> environment variable or <code>null</code> if not set.
     */
    String getMavenOpts();

    /**
     * The show version behavior (-V option)
     * 
     * @return The show version behavior
     * @since 2.0.11
     */
    boolean isShowVersion();

    /**
     * Get the value of the {@code threads} argument.
     * 
     * @return the value of the {@code threads} argument or {@code null} if not set
     * @since 2.1
     */
    String getThreads();

    // ----------------------------------------------------------------------
    // Reactor Failure Mode
    // ----------------------------------------------------------------------

    /**
     * The reactor failure behavior which to be used during Maven invocation.
     */
    enum ReactorFailureBehavior
    {
        /**
         * Stop at first failure in reactor builds
         */
        FailFast( "ff", "fail-fast" ),
        /**
         * Only fail the build afterwards. allow all non-impacted builds to continue.
         */
        FailAtEnd( "fae", "fail-at-end" ),
        /**
         * <b>NEVER</b> fail the build, regardless of project result
         */
        FailNever( "fn", "fail-never" );

        private String shortOption;

        private String longOption;

        private ReactorFailureBehavior( String shortOption, String longOption )
        {
            this.shortOption = shortOption;
            this.longOption = longOption;
        }

        public String getShortOption()
        {
            return this.shortOption;
        }

        public String getLongOption()
        {
            return this.longOption;
        }

        /**
         * Returns the enumeration type which is related to the given long option.
         * 
         * @param longOption The type which is searched for.
         * @return The appropriate {@link ReactorFailureBehavior}
         * @throws IllegalArgumentException in case of an long option which does not exists.
         */
        public static ReactorFailureBehavior valueOfByLongOption( String longOption )
        {
            for ( ReactorFailureBehavior item : ReactorFailureBehavior.values() )
            {
                if ( item.getLongOption().equals( longOption ) )
                {
                    return item;
                }
            }
            throw new IllegalArgumentException( "The string '" + longOption
                + "' can not be converted to enumeration." );
        }
    };

    // ----------------------------------------------------------------------
    // Artifact repository policies
    // ----------------------------------------------------------------------

    /**
     * The kind of checksum policy which should be used during Maven invocation.
     */
    enum CheckSumPolicy
    {

        /**
         * Strict checksum checking equivalent of {@code --strict-checksums}
         */
        Fail,
        /**
         * Warn checksum failures equivalent {@code --lax-checksums}.
         */
        Warn;

    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Sets the interaction mode of the Maven invocation. Equivalent of {@code -B} and {@code --batch-mode}
     * 
     * @param batchMode <code>true</code> if Maven should be executed in non-interactive mode, <code>false</code> if the
     *            interactive modes is used.
     * @return This invocation request.
     * @since 3.0.0
     */
    InvocationRequest setBatchMode( boolean batchMode );

    /**
     * Sets the network mode of the Maven invocation. Equivalent of {@code -o} and {@code --offline}
     * 
     * @param offline <code>true</code> if Maven should be executed in offline mode, <code>false</code> if the online
     *            mode is used.
     * @return This invocation request.
     */
    InvocationRequest setOffline( boolean offline );

    /**
     * Sets the debug mode of the Maven invocation. Equivalent of {@code -X} and {@code --debug}
     * 
     * @param debug <code>true</code> if Maven should be executed in debug mode, <code>false</code> if the normal mode
     *            should be used.
     * @return This invocation request.
     */
    InvocationRequest setDebug( boolean debug );

    /**
     * Sets the exception output mode of the Maven invocation. Equivalent of {@code -e} and {@code --errors}
     * 
     * @param showErrors <code>true</code> if Maven should print stack traces, <code>false</code> otherwise.
     * @return This invocation request.
     */
    InvocationRequest setShowErrors( boolean showErrors );

    /**
     * Specifies whether Maven should enforce an update check for plugins and snapshots. Equivalent of {@code -U} and
     * {@code --update-snapshots}
     * 
     * @param updateSnapshots <code>true</code> if plugins and snapshots should be updated, <code>false</code>
     *            otherwise.
     * @return This invocation request.
     */
    InvocationRequest setUpdateSnapshots( boolean updateSnapshots );

    /**
     * Sets the failure mode of the Maven invocation. Equivalent of {@code -ff} and {@code --fail-fast}, {@code -fae}
     * and {@code --fail-at-end}, {@code -fn} and {@code --fail-never}
     * 
     * @param failureBehavior The failure mode, must be one of {@link ReactorFailureBehavior#FailFast},
     *            {@link ReactorFailureBehavior#FailAtEnd} and {@link ReactorFailureBehavior#FailNever}.
     * @return This invocation request.
     * @since 3.0.0
     */
    InvocationRequest setReactorFailureBehavior( ReactorFailureBehavior failureBehavior );

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
    InvocationRequest setGoals( List<String> goals );

    /**
     * Sets the profiles for the Maven invocation. Equivalent of {@code -P} and {@code --active-profiles}
     * 
     * @param profiles The profiles for the Maven invocation, may be <code>null</code> to use the default profiles.
     * @return This invocation request.
     */
    InvocationRequest setProfiles( List<String> profiles );

    /**
     * Specifies whether the environment variables of the current process should be propagated to the Maven invocation.
     * 
     * @param shellEnvironmentInherited <code>true</code> if the environment variables should be propagated,
     *            <code>false</code> otherwise.
     * @return This invocation request.
     */
    InvocationRequest setShellEnvironmentInherited( boolean shellEnvironmentInherited );

    /**
     * Sets the path to the user settings for the Maven invocation. Equivalent of {@code -s} and {@code --settings}
     * 
     * @param userSettings The path to the user settings for the Maven invocation, may be <code>null</code> to load the
     *            user settings from the default location.
     * @return This invocation request.
     */
    InvocationRequest setUserSettingsFile( File userSettings );

    /**
     * Sets the path to the global settings for the Maven invocation. Equivalent of {@code -gs} and
     * {@code --global-settings}
     * 
     * @param globalSettings The path to the global settings for the Maven invocation, may be <code>null</code> to load
     *            the global settings from the default location.
     * @return This invocation request.
     * @since 2.1
     */
    InvocationRequest setGlobalSettingsFile( File globalSettings );

    /**
     * Sets the alternate path for the user toolchains file Equivalent of {@code -t} or {@code --toolchains}
     * 
     * @param toolchains the alternate path for the user toolchains file
     * @return This invocation request
     * @since 2.1
     */
    InvocationRequest setToolchainsFile( File toolchains );

    /**
     * Sets the alternate path for the global toolchains file Equivalent of {@code -gt} or {@code --global-toolchains}
     * 
     * @param toolchains the alternate path for the global toolchains file
     * @return This invocation request
     * @since 3.0.0
     */
    InvocationRequest setGlobalToolchainsFile( File toolchains );

    /**
     * Sets the checksum mode of the Maven invocation. Equivalent of {@code -c} or {@code --lax-checksums}, {@code -C}
     * or {@code --strict-checksums}
     * 
     * @param globalChecksumPolicy The checksum mode, must be one of {@link CheckSumPolicy#Warn} and
     *            {@link CheckSumPolicy#Fail}.
     * @return This invocation request.
     * @since 3.0.0
     */
    InvocationRequest setGlobalChecksumPolicy( CheckSumPolicy globalChecksumPolicy );

    /**
     * Specifies whether Maven should check for plugin updates.
     * <p>
     * Equivalent of {@code -npu} or {@code --no-plugin-updates}<br/>
     * <strong>note: </strong>Ineffective with Maven3, only kept for backward compatibility
     * </p>
     * 
     * @param nonPluginUpdates <code>true</code> if plugin updates should be suppressed, <code>false</code> otherwise.
     * @return This invocation request.
     */
    InvocationRequest setNonPluginUpdates( boolean nonPluginUpdates );

    /**
     * Sets the recursion behavior of a reactor invocation. <em>Inverse</em> equivalent of {@code -N} and
     * {@code --non-recursive}
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
     * @param mavenOpts The value of the <code>MAVEN_OPTS</code> environment variable, may be <code>null</code> to use
     *            the default options.
     * @return This invocation request.
     */
    InvocationRequest setMavenOpts( String mavenOpts );

    /**
     * enable displaying version without stopping the build Equivalent of {@code -V} or {@code --show-version}
     * 
     * @param showVersion enable displaying version
     * @return This invocation request.
     * @since 2.0.11
     */
    InvocationRequest setShowVersion( boolean showVersion );

    /**
     * Thread count, for instance 2.0C where C is core multiplied Equivalent of {@code -T} or {@code --threads}
     * <p>
     * <strong>note: </strong>available since Maven3
     * </p>
     * 
     * @param threads the threadcount
     * @return This invocation request.
     * @since 2.1
     */
    InvocationRequest setThreads( String threads );

    /**
     * Sets the reactor project list. Equivalent of {@code -pl} or {@code --projects}
     * 
     * @param projects the reactor project list
     * @return This invocation request.
     * @since 2.1
     */
    InvocationRequest setProjects( List<String> projects );

    /**
     * Enable the 'also make' mode. Equivalent of {@code -am} or {@code --also-make}
     * 
     * @param alsoMake enable 'also make' mode
     * @return This invocation request.
     * @since 2.1
     */
    InvocationRequest setAlsoMake( boolean alsoMake );

    /**
     * Enable the 'also make dependents' mode. Equivalent of {@code -amd} or {@code --also-make-dependents}
     * 
     * @param alsoMakeDependents enable 'also make' mode
     * @return This invocation request.
     * @since 2.1
     */
    InvocationRequest setAlsoMakeDependents( boolean alsoMakeDependents );

    /**
     * Resume reactor from specified project. Equivalent of {@code -rf} or {@code --resume-from}
     * 
     * @param resumeFrom set the project to resume from
     * @return This invocation request
     * @since 2.1
     */
    InvocationRequest setResumeFrom( String resumeFrom );

    /**
     * The id of the build strategy to use. equivalent of {@code --builder id}.
     * 
     * @param id The builder id.
     * @return {@link InvocationRequest} FIXME: How to identify if this is a valid command line option?
     * @since 3.2.1
     */
    InvocationRequest setBuilder( String id );

    /**
     * Get the current set builder strategy id equivalent of {@code --builder id}.
     * 
     * @return The current set build id.
     */
    String getBuilder();

}
