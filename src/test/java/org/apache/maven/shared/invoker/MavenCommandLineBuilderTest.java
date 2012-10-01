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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.maven.shared.utils.Os;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.shared.utils.io.IOUtil;

public class MavenCommandLineBuilderTest
    extends TestCase
{

    private List<File> toDelete = new ArrayList<File>();

    private Properties sysProps;

    public void testWrapwithQuotes()
    {
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        String test = "noSpacesInHere";

        assertSame( test, tcb.wrapStringWithQuotes( test ) );
        assertEquals( "noSpacesInHere", tcb.wrapStringWithQuotes( test ) );

        test = "bunch of spaces in here";
        assertNotSame( test, tcb.wrapStringWithQuotes( test ) );
        assertEquals( "\"bunch of spaces in here\"", tcb.wrapStringWithQuotes( test ) );

    }

    public void testShouldFailToSetLocalRepoLocationGloballyWhenItIsAFile()
        throws IOException
    {
        logTestStart();

        File lrd = File.createTempFile( "workdir-test", "file" ).getCanonicalFile();

        toDelete.add( lrd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setLocalRepositoryDirectory( lrd );

        Commandline cli = new Commandline();

        try
        {
            tcb.setEnvironmentPaths( newRequest(), cli );
            fail( "Should not set local repo location to point to a file." );
        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testShouldFailToSetLocalRepoLocationFromRequestWhenItIsAFile()
        throws IOException
    {
        logTestStart();

        File lrd = File.createTempFile( "workdir-test", "file" ).getCanonicalFile();

        toDelete.add( lrd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();

        Commandline cli = new Commandline();

        try
        {
            tcb.setEnvironmentPaths( newRequest().setLocalRepositoryDirectory( lrd ), cli );
            fail( "Should not set local repo location to point to a file." );
        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testShouldSetLocalRepoLocationGlobally()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File lrd = new File( tmpDir, "workdir" ).getCanonicalFile();

        lrd.mkdirs();
        toDelete.add( lrd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setLocalRepositoryDirectory( lrd );

        Commandline cli = new Commandline();

        tcb.setEnvironmentPaths( newRequest(), cli );

        assertArgumentsPresentInOrder( cli, "-D", "maven.repo.local=" + lrd.getPath() );
    }

    public void testShouldSetLocalRepoLocationFromRequest()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File lrd = new File( tmpDir, "workdir" ).getCanonicalFile();

        lrd.mkdirs();
        toDelete.add( lrd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();

        Commandline cli = new Commandline();

        tcb.setEnvironmentPaths( newRequest().setLocalRepositoryDirectory( lrd ), cli );

        assertArgumentsPresentInOrder( cli, "-D", "maven.repo.local=" + lrd.getPath() );
    }

    public void testRequestProvidedLocalRepoLocationShouldOverrideGlobal()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File lrd = new File( tmpDir, "workdir" ).getCanonicalFile();
        File glrd = new File( tmpDir, "global-workdir" ).getCanonicalFile();

        lrd.mkdirs();
        glrd.mkdirs();

        toDelete.add( lrd );
        toDelete.add( glrd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setLocalRepositoryDirectory( glrd );

        Commandline cli = new Commandline();

        tcb.setEnvironmentPaths( newRequest().setLocalRepositoryDirectory( lrd ), cli );

        assertArgumentsPresentInOrder( cli, "-D", "maven.repo.local=" + lrd.getPath() );
    }

    public void testShouldSetWorkingDirectoryGlobally()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File wd = new File( tmpDir, "workdir" );

        wd.mkdirs();

        toDelete.add( wd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setWorkingDirectory( wd );

        Commandline cli = new Commandline();

        tcb.setEnvironmentPaths( newRequest(), cli );

        assertEquals( cli.getWorkingDirectory(), wd );
    }

    public void testShouldSetWorkingDirectoryFromRequest()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File wd = new File( tmpDir, "workdir" );

        wd.mkdirs();

        toDelete.add( wd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        InvocationRequest req = newRequest();
        req.setBaseDirectory( wd );

        Commandline cli = new Commandline();

        tcb.setEnvironmentPaths( req, cli );

        assertEquals( cli.getWorkingDirectory(), wd );
    }

    public void testRequestProvidedWorkingDirectoryShouldOverrideGlobal()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File wd = new File( tmpDir, "workdir" );
        File gwd = new File( tmpDir, "global-workdir" );

        wd.mkdirs();
        gwd.mkdirs();

        toDelete.add( wd );
        toDelete.add( gwd );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setWorkingDirectory( gwd );

        InvocationRequest req = newRequest();
        req.setBaseDirectory( wd );

        Commandline cli = new Commandline();

        tcb.setEnvironmentPaths( req, cli );

        assertEquals( cli.getWorkingDirectory(), wd );
    }

    public void testShouldUseSystemOutLoggerWhenNoneSpecified()
        throws Exception
    {
        logTestStart();
        setupTempMavenHomeIfMissing();

        TestCommandLineBuilder tclb = new TestCommandLineBuilder();
        tclb.checkRequiredState();
    }

    private File setupTempMavenHomeIfMissing()
        throws Exception
    {
        String mavenHome = System.getProperty( "maven.home" );

        File appDir = null;

        if ( ( mavenHome == null ) || !new File( mavenHome ).exists() )
        {
            File tmpDir = getTempDir();
            appDir = new File( tmpDir, "invoker-tests/maven-home" );

            File binDir = new File( appDir, "bin" );

            binDir.mkdirs();
            toDelete.add( appDir );

            if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            {
                createDummyFile( binDir, "mvn.bat" );
            }
            else
            {
                createDummyFile( binDir, "mvn" );
            }

            Properties props = System.getProperties();
            props.setProperty( "maven.home", appDir.getCanonicalPath() );

            System.setProperties( props );
        }
        else
        {
            appDir = new File( mavenHome );
        }

        return appDir;
    }

    public void testShouldFailIfLoggerSetToNull()
    {
        logTestStart();

        TestCommandLineBuilder tclb = new TestCommandLineBuilder();
        tclb.setLogger( null );

        try
        {
            tclb.checkRequiredState();
            fail( "Should not allow execution to proceed when logger is missing." );
        }
        catch ( IllegalStateException e )
        {
            assertTrue( true );
        }
        catch ( IOException e )
        {
            fail( e.getMessage() );
        }
    }

    public void testShouldFindDummyMavenExecutable()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();

        File base = new File( tmpDir, "invoker-tests" );

        File dummyMavenHomeBin = new File( base, "dummy-maven-home/bin" );

        dummyMavenHomeBin.mkdirs();

        toDelete.add( base );

        File check;
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            check = createDummyFile( dummyMavenHomeBin, "mvn.bat" );
        }
        else
        {
            check = createDummyFile( dummyMavenHomeBin, "mvn" );
        }

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setMavenHome( dummyMavenHomeBin.getParentFile() );

        File mavenExe = tcb.findMavenExecutable();

        assertEquals( check.getCanonicalPath(), mavenExe.getCanonicalPath() );
    }

    public void testShouldSetBatchModeFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setInteractive( false ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-B" ) );
    }

    public void testShouldSetOfflineFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setOffline( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-o" ) );
    }

    public void testShouldSetUpdateSnapshotsFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setUpdateSnapshots( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-U" ) );
    }

    public void testShouldSetDebugFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setDebug( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-X" ) );
    }

    public void testShouldSetErrorFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setShowErrors( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-e" ) );
    }

    public void testDebugOptionShouldMaskShowErrorsOption()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setDebug( true ).setShowErrors( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-X" ) );
        assertArgumentsNotPresent( cli, Collections.singleton( "-e" ) );
    }
    
    public void testActivateReactor()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().activateReactor( null, null ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-r" ) );
    }
    
    public void testActivateReactorIncludesExcludes()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        String[] includes = new String[] {"foo", "bar"};
        String[] excludes = new String[] {"baz", "quz"};
        
        tcb.setReactorBehavior( newRequest().activateReactor( includes, excludes ), cli );
        
        Set<String> args = new HashSet<String>();
        args.add( "-r" );
        args.add( "-D" );
        args.add( "maven.reactor.includes=foo,bar" );
        args.add( "maven.reactor.excludes=baz,quz" );

        assertArgumentsPresent( cli, args );
    }
    
    public void testAlsoMake()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setAlsoMake( true ), cli );

        //-am is only useful with -pl
        assertArgumentsNotPresent( cli, Collections.singleton( "-am" ) );
    }

    public void testProjectsAndAlsoMake()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setProjects( Collections.singletonList( "proj1" ) ).setAlsoMake( true ), cli );

        assertArgumentsPresentInOrder( cli, "-pl", "proj1", "-am" );
    }

    public void testAlsoMakeDependents()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setAlsoMakeDependents( true ), cli );

        //-amd is only useful with -pl
        assertArgumentsNotPresent( cli, Collections.singleton( "-amd" ) );
    }

    public void testProjectsAndAlsoMakeDependents()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setProjects( Collections.singletonList( "proj1" ) ).setAlsoMakeDependents( true ), cli );

        assertArgumentsPresentInOrder( cli, "-pl", "proj1", "-amd" );
    }

    public void testProjectsAndAlsoMakeAndAlsoMakeDependents()
    {
        logTestStart();
        
        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setProjects( Collections.singletonList( "proj1" ) ).setAlsoMake( true ).setAlsoMakeDependents( true ), cli );

        assertArgumentsPresentInOrder( cli, "-pl", "proj1", "-am", "-amd" );
    }

    public void testShouldSetStrictChecksumPolityFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setGlobalChecksumPolicy( InvocationRequest.CHECKSUM_POLICY_FAIL ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-C" ) );
    }

    public void testShouldSetLaxChecksumPolicyFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setGlobalChecksumPolicy( InvocationRequest.CHECKSUM_POLICY_WARN ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-c" ) );
    }

    public void testShouldSetFailAtEndFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setFailureBehavior( InvocationRequest.REACTOR_FAIL_AT_END ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-fae" ) );
    }

    public void testShouldSetFailNeverFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setFailureBehavior( InvocationRequest.REACTOR_FAIL_NEVER ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-fn" ) );
    }

    public void testShouldUseDefaultOfFailFastWhenSpecifiedInRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setFailureBehavior( InvocationRequest.REACTOR_FAIL_FAST ), cli );

        Set<String> banned = new HashSet<String>();
        banned.add( "-fae" );
        banned.add( "-fn" );

        assertArgumentsNotPresent( cli, banned );
    }

    public void testShouldSpecifyFileOptionUsingNonStandardPomFileLocation()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "file-option-nonstd-pom-file-location" ).getCanonicalFile();

        projectDir.mkdirs();

        File pomFile = createDummyFile( projectDir, "non-standard-pom.xml" ).getCanonicalFile();

        Commandline cli = new Commandline();

        InvocationRequest req = newRequest().setPomFile( pomFile );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setEnvironmentPaths( req, cli );
        tcb.setPomLocation( req, cli );

        assertEquals( projectDir, cli.getWorkingDirectory() );

        Set<String> args = new HashSet<String>();
        args.add( "-f" );
        args.add( "non-standard-pom.xml" );

        assertArgumentsPresent( cli, args );
    }

    public void testShouldSpecifyFileOptionUsingNonStandardPomInBasedir()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "file-option-nonstd-basedir" ).getCanonicalFile();

        projectDir.mkdirs();

        File basedir = createDummyFile( projectDir, "non-standard-pom.xml" ).getCanonicalFile();

        Commandline cli = new Commandline();

        InvocationRequest req = newRequest().setBaseDirectory( basedir );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setEnvironmentPaths( req, cli );
        tcb.setPomLocation( req, cli );

        assertEquals( projectDir, cli.getWorkingDirectory() );

        Set<String> args = new HashSet<String>();
        args.add( "-f" );
        args.add( "non-standard-pom.xml" );

        assertArgumentsPresent( cli, args );
    }

    public void testShouldNotSpecifyFileOptionUsingStandardPomFileLocation()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "std-pom-file-location" ).getCanonicalFile();

        projectDir.mkdirs();

        File pomFile = createDummyFile( projectDir, "pom.xml" ).getCanonicalFile();

        Commandline cli = new Commandline();

        InvocationRequest req = newRequest().setPomFile( pomFile );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setEnvironmentPaths( req, cli );
        tcb.setPomLocation( req, cli );

        assertEquals( projectDir, cli.getWorkingDirectory() );

        Set<String> args = new HashSet<String>();
        args.add( "-f" );
        args.add( "pom.xml" );

        assertArgumentsNotPresent( cli, args );
    }

    public void testShouldNotSpecifyFileOptionUsingStandardPomInBasedir()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "std-basedir-is-pom-file" ).getCanonicalFile();

        projectDir.mkdirs();

        File basedir = createDummyFile( projectDir, "pom.xml" ).getCanonicalFile();

        Commandline cli = new Commandline();

        InvocationRequest req = newRequest().setBaseDirectory( basedir );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setEnvironmentPaths( req, cli );
        tcb.setPomLocation( req, cli );

        assertEquals( projectDir, cli.getWorkingDirectory() );

        Set<String> args = new HashSet<String>();
        args.add( "-f" );
        args.add( "pom.xml" );

        assertArgumentsNotPresent( cli, args );
    }

    public void testShouldUseDefaultPomFileWhenBasedirSpecifiedWithoutPomFileName()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "std-basedir-no-pom-filename" ).getCanonicalFile();

        projectDir.mkdirs();

        Commandline cli = new Commandline();

        InvocationRequest req = newRequest().setBaseDirectory( projectDir );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setEnvironmentPaths( req, cli );
        tcb.setPomLocation( req, cli );

        assertEquals( projectDir, cli.getWorkingDirectory() );

        Set<String> args = new HashSet<String>();
        args.add( "-f" );
        args.add( "pom.xml" );

        assertArgumentsNotPresent( cli, args );
    }

    public void testShouldSpecifyPomFileWhenBasedirSpecifiedWithPomFileName()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "std-basedir-with-pom-filename" ).getCanonicalFile();

        projectDir.mkdirs();

        Commandline cli = new Commandline();

        InvocationRequest req = newRequest().setBaseDirectory( projectDir ).setPomFileName( "non-standard-pom.xml" );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setEnvironmentPaths( req, cli );
        tcb.setPomLocation( req, cli );

        assertEquals( projectDir, cli.getWorkingDirectory() );

        Set<String> args = new HashSet<String>();
        args.add( "-f" );
        args.add( "non-standard-pom.xml" );

        assertArgumentsPresent( cli, args );
    }

    public void testShouldSpecifyCustomUserSettingsLocationFromRequest()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "custom-settings" ).getCanonicalFile();

        projectDir.mkdirs();

        File settingsFile = createDummyFile( projectDir, "settings.xml" );

        Commandline cli = new Commandline();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setSettingsLocation( newRequest().setUserSettingsFile( settingsFile ), cli );

        Set<String> args = new HashSet<String>();
        args.add( "-s" );
        args.add( settingsFile.getCanonicalPath() );

        assertArgumentsPresent( cli, args );
    }
    
    public void testShouldSpecifyCustomGlobalSettingsLocationFromRequest()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "custom-settings" ).getCanonicalFile();

        projectDir.mkdirs();

        File settingsFile = createDummyFile( projectDir, "settings.xml" );

        Commandline cli = new Commandline();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setSettingsLocation( newRequest().setGlobalSettingsFile( settingsFile ), cli );

        Set<String> args = new HashSet<String>();
        args.add( "-gs" );
        args.add( settingsFile.getCanonicalPath() );

        assertArgumentsPresent( cli, args );
    }

    public void testShouldSpecifyCustomToolchainsLocationFromRequest()
        throws Exception
    {
        logTestStart();

        File tmpDir = getTempDir();
        File base = new File( tmpDir, "invoker-tests" );

        toDelete.add( base );

        File projectDir = new File( base, "custom-toolchains" ).getCanonicalFile();

        projectDir.mkdirs();

        File toolchainsFile = createDummyFile( projectDir, "toolchains.xml" );

        Commandline cli = new Commandline();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setToolchainsLocation( newRequest().setToolchainsFile( toolchainsFile ), cli );

        Set<String> args = new HashSet<String>();
        args.add( "-t" );
        args.add( toolchainsFile.getCanonicalPath() );

        assertArgumentsPresent( cli, args );
    }
    public void testShouldSpecifyCustomPropertyFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        Properties properties = new Properties();
        properties.setProperty( "key", "value" );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setProperties( newRequest().setProperties( properties ), cli );

        assertArgumentsPresentInOrder( cli, "-D", "key=value" );
    }

    public void testShouldSpecifyCustomPropertyWithSpacesInValueFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        Properties properties = new Properties();
        properties.setProperty( "key", "value with spaces" );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setProperties( newRequest().setProperties( properties ), cli );

        assertArgumentsPresentInOrder( cli, "-D", "key=value with spaces" );
    }

    public void testShouldSpecifyCustomPropertyWithSpacesInKeyFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        Properties properties = new Properties();
        properties.setProperty( "key with spaces", "value with spaces" );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setProperties( newRequest().setProperties( properties ), cli );

        assertArgumentsPresentInOrder( cli, "-D", "key with spaces=value with spaces" );
    }

    public void testShouldSpecifySingleGoalFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        List<String> goals = new ArrayList<String>();
        goals.add( "test" );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setGoals( newRequest().setGoals( goals ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "test" ) );
    }

    public void testShouldSpecifyTwoGoalsFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        List<String> goals = new ArrayList<String>();
        goals.add( "test" );
        goals.add( "clean" );

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setGoals( newRequest().setGoals( goals ), cli );

        assertArgumentsPresent( cli, new HashSet<String>( goals ) );
        assertArgumentsPresentInOrder( cli, goals );
    }

    public void testShouldSpecifyThreadsFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setThreads( newRequest().setThreads( "2.0C" ), cli );

        assertArgumentsPresentInOrder( cli, "-T", "2.0C");
    }
    public void testBuildTypicalMavenInvocationEndToEnd()
        throws Exception
    {
        logTestStart();
        File mavenDir = setupTempMavenHomeIfMissing();

        InvocationRequest request = newRequest();

        File tmpDir = getTempDir();
        File projectDir = new File( tmpDir, "invoker-tests/typical-end-to-end-cli-build" );

        projectDir.mkdirs();
        toDelete.add( projectDir.getParentFile() );

        request.setBaseDirectory( projectDir );

        Set<String> expectedArgs = new HashSet<String>();
        Set<String> bannedArgs = new HashSet<String>();

        createDummyFile( projectDir, "pom.xml" );

        bannedArgs.add( "-f" );
        bannedArgs.add( "pom.xml" );

        Properties properties = new Properties();
        // this is REALLY bad practice, but since it's just a test...
        properties.setProperty( "maven.tests.skip", "true" );

        expectedArgs.add( "maven.tests.skip=true" );

        request.setProperties( properties );

        request.setOffline( true );

        expectedArgs.add( "-o" );

        List<String> goals = new ArrayList<String>();

        goals.add( "post-clean" );
        goals.add( "deploy" );
        goals.add( "site-deploy" );

        request.setGoals( goals );

        MavenCommandLineBuilder commandLineBuilder = new MavenCommandLineBuilder();

        Commandline commandline = commandLineBuilder.build( request );

        assertArgumentsPresent( commandline, expectedArgs );
        assertArgumentsNotPresent( commandline, bannedArgs );
        assertArgumentsPresentInOrder( commandline, goals );

        File mavenFile;
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            mavenFile = new File( mavenDir, "bin/mvn.bat" );
        }
        else
        {
            mavenFile = new File( mavenDir, "bin/mvn" );
        }

        String executable = commandline.getExecutable();
        System.out.println( "Executable is: " + executable );

        assertTrue( executable.indexOf( mavenFile.getCanonicalPath() ) > -1 );
        assertEquals( projectDir.getCanonicalPath(), commandline.getWorkingDirectory().getCanonicalPath() );
    }

    public void testShouldSetEnvVar_MAVEN_TERMINATE_CMD()
        throws Exception
    {
        logTestStart();
        setupTempMavenHomeIfMissing();

        InvocationRequest request = newRequest();

        File tmpDir = getTempDir();
        File projectDir = new File( tmpDir, "invoker-tests/maven-terminate-cmd-options-set" );

        projectDir.mkdirs();
        toDelete.add( projectDir.getParentFile() );

        request.setBaseDirectory( projectDir );

        createDummyFile( projectDir, "pom.xml" );

        List<String> goals = new ArrayList<String>();

        goals.add( "clean" );
        request.setGoals( goals );

        MavenCommandLineBuilder commandLineBuilder = new MavenCommandLineBuilder();

        Commandline commandline = commandLineBuilder.build( request );

        String[] environmentVariables = commandline.getEnvironmentVariables();
        String envVarMavenTerminateCmd = null;
        for ( int i = 0; i < environmentVariables.length; i++ )
        {
            String envVar = environmentVariables[i];
            if ( envVar.startsWith( "MAVEN_TERMINATE_CMD=" ) )
            {
                envVarMavenTerminateCmd = envVar;
                break;
            }
        }
        assertEquals( "MAVEN_TERMINATE_CMD=on", envVarMavenTerminateCmd );

    }

    public void testShouldInsertActivatedProfiles()
        throws Exception
    {
        setupTempMavenHomeIfMissing();

        String profile1 = "profile-1";
        String profile2 = "profile-2";

        InvocationRequest request = newRequest();

        List<String> profiles = new ArrayList<String>();
        profiles.add( profile1 );
        profiles.add( profile2 );

        request.setProfiles( profiles );

        MavenCommandLineBuilder commandLineBuilder = new MavenCommandLineBuilder();

        Commandline commandline = commandLineBuilder.build( request );

        assertArgumentsPresentInOrder( commandline, "-P", profile1 + "," + profile2 );
    }

    public void setUp()
    {
        sysProps = System.getProperties();

        Properties p = new Properties( sysProps );

        System.setProperties( p );
    }

    public void tearDown()
        throws IOException
    {
        System.setProperties( sysProps );

        for ( File file : toDelete )
        {
            if ( file.exists() )
            {
                if ( file.isDirectory() )
                {
                    FileUtils.deleteDirectory( file );
                }
                else
                {
                    file.delete();
                }
            }
        }
    }

    // this is just a debugging helper for separating unit test output...
    private void logTestStart()
    {
        NullPointerException npe = new NullPointerException();
        StackTraceElement element = npe.getStackTrace()[1];

        System.out.println( "Starting: " + element.getMethodName() );
    }

    private void assertArgumentsPresentInOrder( Commandline cli, String... expected )
    {
        assertArgumentsPresentInOrder( cli, Arrays.asList( expected ) );
    }

    private void assertArgumentsPresentInOrder( Commandline cli, List<String> expected )
    {
        String[] arguments = cli.getArguments();

        int expectedCounter = 0;

        for ( int i = 0; i < arguments.length; i++ )
        {
            if ( arguments[i].equals( expected.get( expectedCounter ) ) )
            {
                expectedCounter++;
            }
        }

        assertEquals( "Arguments: " + expected + " were not found or are in the wrong order: "
            + Arrays.asList( arguments ), expected.size(), expectedCounter );
    }

    private void assertArgumentsPresent( Commandline cli, Set<String> requiredArgs )
    {
        String[] argv = cli.getArguments();
        List<String> args = Arrays.asList( argv );

        for ( String arg : requiredArgs )
        {
            assertTrue( "Command-line argument: \'" + arg + "\' is missing in " + args, args.contains( arg ) );
        }
    }

    private void assertArgumentsNotPresent( Commandline cli, Set<String> bannedArgs )
    {
        String[] argv = cli.getArguments();
        List<String> args = Arrays.asList( argv );

        for ( String arg : bannedArgs )
        {
            assertFalse( "Command-line argument: \'" + arg + "\' should not be present.", args.contains( arg ) );
        }
    }

    private File createDummyFile( File directory, String filename )
        throws IOException
    {
        File dummyFile = new File( directory, filename );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( dummyFile );
            writer.write( "This is a dummy file." );
        }
        finally
        {
            IOUtil.close( writer );
        }

        toDelete.add( dummyFile );

        return dummyFile;
    }

    private static final class TestCommandLineBuilder
        extends MavenCommandLineBuilder
    {
        public void checkRequiredState()
            throws IOException
        {
            super.checkRequiredState();
        }

        public File findMavenExecutable()
            throws CommandLineConfigurationException, IOException
        {
            return super.findMavenExecutable();
        }

        public void setEnvironmentPaths( InvocationRequest request, Commandline cli )
        {
            super.setEnvironmentPaths( request, cli );
        }

        public void setFlags( InvocationRequest request, Commandline cli )
        {
            super.setFlags( request, cli );
        }

        public void setGoals( InvocationRequest request, Commandline cli )
        {
            super.setGoals( request, cli );
        }

        public void setPomLocation( InvocationRequest request, Commandline cli )
        {
            super.setPomLocation( request, cli );
        }

        public void setProperties( InvocationRequest request, Commandline cli )
        {
            super.setProperties( request, cli );
        }

        public void setReactorBehavior( InvocationRequest request, Commandline cli )
        {
            super.setReactorBehavior( request, cli );
        }

        public void setSettingsLocation( InvocationRequest request, Commandline cli )
        {
            super.setSettingsLocation( request, cli );
        }

        public void setShellEnvironment( InvocationRequest request, Commandline cli )
            throws CommandLineConfigurationException
        {
            super.setShellEnvironment( request, cli );
        }

    }

    private File getTempDir()
        throws Exception
    {
        return new File( System.getProperty( "java.io.tmpdir" ) ).getCanonicalFile();
    }

    private InvocationRequest newRequest()
    {
        return new DefaultInvocationRequest();
    }

}
