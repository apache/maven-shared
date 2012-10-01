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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Specifies the parameters used to control a Maven invocation.
 * 
 * @version $Id$
 */
public class DefaultInvocationRequest
    implements InvocationRequest
{

    private File basedir;

    private boolean debug;

    private InvocationOutputHandler errorHandler;

    private String failureBehavior = REACTOR_FAIL_FAST;

    private List<String> goals;

    private InputStream inputStream;

    private boolean interactive;

    private File localRepository;

    private boolean offline;

    private boolean recursive = true;

    private InvocationOutputHandler outputHandler;

    private File pomFile;

    private Properties properties;

    private boolean showErrors;

    private boolean updateSnapshots;

    private boolean shellEnvironmentInherited = true;

    private File userSettings;

    private File globalSettings;
    
    private File toolchains;

    private String globalChecksumPolicy;

    private String pomFilename;

    private File javaHome;

    private List<String> profiles;

    private boolean nonPluginUpdates;

    private Map<String, String> shellEnvironments;

    private String mavenOpts;
    
    private boolean activatedReactor;

    private String[] activatedReactorIncludes, activatedReactorExcludes;
    
    private List<String> projects;

    private boolean alsoMake;

    private boolean alsoMakeDependents;

    private String resumeFrom;

    private boolean showVersion;

    private String threads;

    public InvocationRequest activateReactor( String[] includes, String[] excludes )
    {
        activatedReactor = true;
        activatedReactorIncludes = includes;
        activatedReactorExcludes = excludes;
        return this;
    }

    public File getBaseDirectory()
    {
        return basedir;
    }

    public File getBaseDirectory( File defaultDirectory )
    {
        return basedir == null ? defaultDirectory : basedir;
    }

    public InvocationOutputHandler getErrorHandler( InvocationOutputHandler defaultHandler )
    {
        return errorHandler == null ? defaultHandler : errorHandler;
    }

    public String getFailureBehavior()
    {
        return failureBehavior;
    }

    public List<String> getGoals()
    {
        return goals;
    }

    public InputStream getInputStream( InputStream defaultStream )
    {
        return inputStream == null ? defaultStream : inputStream;
    }

    public File getLocalRepositoryDirectory( File defaultDirectory )
    {
        return localRepository == null ? defaultDirectory : localRepository;
    }

    public InvocationOutputHandler getOutputHandler( InvocationOutputHandler defaultHandler )
    {
        return outputHandler == null ? defaultHandler : outputHandler;
    }

    public File getPomFile()
    {
        return pomFile;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public boolean isInteractive()
    {
        return interactive;
    }

    public boolean isOffline()
    {
        return offline;
    }

    public boolean isShowErrors()
    {
        return showErrors;
    }

    public boolean isUpdateSnapshots()
    {
        return updateSnapshots;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public InvocationRequest setRecursive( boolean recursive )
    {
        this.recursive = recursive;
        return this;
    }

    public InvocationRequest setBaseDirectory( File basedir )
    {
        this.basedir = basedir;
        return this;
    }

    public InvocationRequest setDebug( boolean debug )
    {
        this.debug = debug;
        return this;
    }

    public InvocationRequest setErrorHandler( InvocationOutputHandler errorHandler )
    {
        this.errorHandler = errorHandler;
        return this;
    }

    public InvocationRequest setFailureBehavior( String failureBehavior )
    {
        this.failureBehavior = failureBehavior;
        return this;
    }

    public InvocationRequest setGoals( List<String> goals )
    {
        this.goals = goals;
        return this;
    }

    public InvocationRequest setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
        return this;
    }

    public InvocationRequest setInteractive( boolean interactive )
    {
        this.interactive = interactive;
        return this;
    }

    public InvocationRequest setLocalRepositoryDirectory( File localRepository )
    {
        this.localRepository = localRepository;
        return this;
    }

    public InvocationRequest setOffline( boolean offline )
    {
        this.offline = offline;
        return this;
    }

    public InvocationRequest setOutputHandler( InvocationOutputHandler outputHandler )
    {
        this.outputHandler = outputHandler;
        return this;
    }

    public InvocationRequest setPomFile( File pomFile )
    {
        this.pomFile = pomFile;
        return this;
    }

    public InvocationRequest setProperties( Properties properties )
    {
        this.properties = properties;
        return this;
    }

    public InvocationRequest setShowErrors( boolean showErrors )
    {
        this.showErrors = showErrors;
        return this;
    }

    public InvocationRequest setUpdateSnapshots( boolean updateSnapshots )
    {
        this.updateSnapshots = updateSnapshots;
        return this;
    }

    public boolean isShellEnvironmentInherited()
    {
        return shellEnvironmentInherited;
    }

    public InvocationRequest setShellEnvironmentInherited( boolean shellEnvironmentInherited )
    {
        this.shellEnvironmentInherited = shellEnvironmentInherited;
        return this;
    }

    public File getJavaHome()
    {
        return javaHome;
    }

    public InvocationRequest setJavaHome( File javaHome )
    {
        this.javaHome = javaHome;
        return this;
    }

    public File getUserSettingsFile()
    {
        return userSettings;
    }
    
    public InvocationRequest setUserSettingsFile( File userSettings )
    {
        this.userSettings = userSettings;
        return this;
    }

    public File getGlobalSettingsFile()
    {
        return globalSettings;
    }

    public InvocationRequest setGlobalSettingsFile( File globalSettings )
    {
        this.globalSettings = globalSettings;
        return this;
    }

    public File getToolchainsFile()
    {
        return toolchains;
    }
    
    public InvocationRequest setToolchainsFile( File toolchains )
    {
        this.toolchains = toolchains;
        return this;
    }
    
    public String getGlobalChecksumPolicy()
    {
        return globalChecksumPolicy;
    }

    public InvocationRequest setGlobalChecksumPolicy( String globalChecksumPolicy )
    {
        this.globalChecksumPolicy = globalChecksumPolicy;
        return this;
    }

    public String getPomFileName()
    {
        return pomFilename;
    }

    public InvocationRequest setPomFileName( String pomFilename )
    {
        this.pomFilename = pomFilename;
        return this;
    }

    public List<String> getProfiles()
    {
        return profiles;
    }

    public InvocationRequest setProfiles( List<String> profiles )
    {
        this.profiles = profiles;
        return this;
    }

    public boolean isNonPluginUpdates()
    {
        return nonPluginUpdates;
    }

    public InvocationRequest setNonPluginUpdates( boolean nonPluginUpdates )
    {
        this.nonPluginUpdates = nonPluginUpdates;
        return this;
    }

    public InvocationRequest addShellEnvironment( String name, String value )
    {
        if ( this.shellEnvironmentInherited )
        {
            this.shellEnvironments = new HashMap<String, String>();
        }
        this.shellEnvironments.put( name, value );
        return this;
    }

    public Map<String, String> getShellEnvironments()
    {
        return shellEnvironments == null ? Collections.<String, String>emptyMap(): shellEnvironments;
    }

    public String getMavenOpts()
    {
        return mavenOpts;
    }

    public InvocationRequest setMavenOpts( String mavenOpts )
    {
        this.mavenOpts = mavenOpts;
        return this;
    }
    
    public boolean isActivatedReactor()
    {
        return activatedReactor;
    }

    public String[] getActivatedReactorIncludes()
    {
        return activatedReactorIncludes;
    }

    public String[] getActivatedReactorExcludes()
    {
        return activatedReactorExcludes;
    }

    /**
     * @see org.apache.maven.shared.invoker.InvocationRequest#isShowVersion()
     */
    public boolean isShowVersion()
    {
        return this.showVersion;
    }

    /**
     * @see org.apache.maven.shared.invoker.InvocationRequest#setShowVersion(boolean)
     */
    public InvocationRequest setShowVersion( boolean showVersion )
    {
        this.showVersion = showVersion;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getThreads()
    {
        return threads;
    }
    
    /**
     * {@inheritDoc}
     */
    public InvocationRequest setThreads( String threads )
    {
        this.threads = threads;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getProjects()
    {
        return projects;
    }

    /**
     * {@inheritDoc}
     */
    public InvocationRequest setProjects( List<String> projects )
    {
        this.projects = projects;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAlsoMake()
    {
        return alsoMake;
    }

    /**
     * {@inheritDoc}
     */
    public InvocationRequest setAlsoMake( boolean alsoMake )
    {
        this.alsoMake = alsoMake;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAlsoMakeDependents()
    {
        return alsoMakeDependents;
    }

    /**
     * {@inheritDoc}
     */
    public InvocationRequest setAlsoMakeDependents( boolean alsoMakeDependents )
    {
        this.alsoMakeDependents = alsoMakeDependents;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getResumeFrom()
    {
        return resumeFrom;
    }

    /**
     * {@inheritDoc}
     */
    public InvocationRequest setResumeFrom( String resumeFrom )
    {
        this.resumeFrom = resumeFrom;
        return this;
    }
    
}
