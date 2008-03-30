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
 * @todo handle forced-reactor executions using -r/includes/excludes
 */
public interface InvocationRequest
{
    boolean isInteractive();

    boolean isOffline();

    boolean isUpdateSnapshots();

    boolean isRecursive();

    boolean isDebug();

    boolean isShowErrors();

    boolean isShellEnvironmentInherited();

    boolean isNonPluginUpdates();
    
    String getFailureBehavior();

    File getLocalRepositoryDirectory( File defaultDirectory );

    InputStream getInputStream( InputStream defaultStream );

    InvocationOutputHandler getOutputHandler( InvocationOutputHandler defaultHandler );

    InvocationOutputHandler getErrorHandler( InvocationOutputHandler defaultHandler );

    File getPomFile();

    String getPomFileName();

    File getBaseDirectory();

    File getBaseDirectory( File defaultDirectory );

    File getJavaHome();

    Properties getProperties();

    List getGoals();

    File getUserSettingsFile();

    String getGlobalChecksumPolicy();

    List getProfiles();
    
    Map getShellEnvironments();
    
    String getMavenOpts();

    // ----------------------------------------------------------------------
    // Reactor Failure Mode
    // ----------------------------------------------------------------------

    static final String REACTOR_FAIL_FAST = "fail-fast";

    static final String REACTOR_FAIL_AT_END = "fail-at-end";

    static final String REACTOR_FAIL_NEVER = "fail-never";

    // ----------------------------------------------------------------------
    // Artifactr repository policies
    // ----------------------------------------------------------------------

    static final String CHECKSUM_POLICY_FAIL = "fail";

    static final String CHECKSUM_POLICY_WARN = "warn";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    InvocationRequest setInteractive( boolean interactive );

    InvocationRequest setOffline( boolean offline );

    InvocationRequest setDebug( boolean debug );

    InvocationRequest setShowErrors( boolean showErrors );

    InvocationRequest setUpdateSnapshots( boolean updateSnapshots );

    InvocationRequest setFailureBehavior( String failureBehavior );

    InvocationRequest activateReactor( String[] includes, String[] excludes );

    InvocationRequest setLocalRepositoryDirectory( File localRepository );

    InvocationRequest setInputStream( InputStream inputStream );

    InvocationRequest setOutputHandler( InvocationOutputHandler outputHandler );

    InvocationRequest setErrorHandler( InvocationOutputHandler errorHandler );

    InvocationRequest setPomFile( File pomFile );

    InvocationRequest setPomFileName( String pomFilename );

    InvocationRequest setBaseDirectory( File basedir );

    InvocationRequest setJavaHome( File javaHome );

    InvocationRequest setProperties( Properties properties );

    InvocationRequest setGoals( List goals );

    InvocationRequest setProfiles( List profiles );

    InvocationRequest setShellEnvironmentInherited( boolean shellEnvironmentInherited );

    InvocationRequest setUserSettingsFile( File userSettings );

    InvocationRequest setGlobalChecksumPolicy( String globalChecksumPolicy );
    
    InvocationRequest setNonPluginUpdates( boolean nonPluginUpdates);
    
    InvocationRequest setRecursive( boolean recursive );
    
    InvocationRequest addShellEnvironment( String name, String value );

    InvocationRequest setMavenOpts( String mavenOpts );
}
