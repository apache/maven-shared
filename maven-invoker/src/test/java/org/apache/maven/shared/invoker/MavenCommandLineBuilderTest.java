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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class MavenCommandLineBuilderTest
{

    private List<File> toDelete = new ArrayList<File>();

    private Properties sysProps;

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testShouldUseSystemOutLoggerWhenNoneSpecified()
        throws Exception
    {
        logTestStart();
        setupTempMavenHomeIfMissing( false );

        TestCommandLineBuilder tclb = new TestCommandLineBuilder();
        tclb.checkRequiredState();
    }

    private File setupTempMavenHomeIfMissing( boolean forceDummy )
        throws Exception
    {
        String mavenHome = System.getProperty( "maven.home" );

        File appDir = null;

        if ( forceDummy || ( mavenHome == null ) || !new File( mavenHome ).exists() )
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

    @Test
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

    @Test
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

    @Test
    public void testShouldSetBatchModeFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setBatchMode( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-B" ) );
    }

    @Test
    public void testShouldSetOfflineFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setOffline( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-o" ) );
    }

    @Test
    public void testShouldSetUpdateSnapshotsFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setUpdateSnapshots( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-U" ) );
    }

    @Test
    public void testShouldSetDebugFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setDebug( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-X" ) );
    }

    @Test
    public void testShouldSetErrorFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setShowErrors( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-e" ) );
    }

    @Test
    public void testDebugOptionShouldMaskShowErrorsOption()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setDebug( true ).setShowErrors( true ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-X" ) );
        assertArgumentsNotPresent( cli, Collections.singleton( "-e" ) );
    }

    @Test
    public void testAlsoMake()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setAlsoMake( true ), cli );

        // -am is only useful with -pl
        assertArgumentsNotPresent( cli, Collections.singleton( "-am" ) );
    }

    @Test
    public void testProjectsAndAlsoMake()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setProjects( Collections.singletonList( "proj1" ) ).setAlsoMake( true ),
                                cli );

        assertArgumentsPresentInOrder( cli, "-pl", "proj1", "-am" );
    }

    @Test
    public void testAlsoMakeDependents()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setAlsoMakeDependents( true ), cli );

        // -amd is only useful with -pl
        assertArgumentsNotPresent( cli, Collections.singleton( "-amd" ) );
    }

    @Test
    public void testProjectsAndAlsoMakeDependents()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setProjects( Collections.singletonList( "proj1" ) ).setAlsoMakeDependents( true ),
                                cli );

        assertArgumentsPresentInOrder( cli, "-pl", "proj1", "-amd" );
    }

    @Test
    public void testProjectsAndAlsoMakeAndAlsoMakeDependents()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setProjects( Collections.singletonList( "proj1" ) ).setAlsoMake( true ).setAlsoMakeDependents( true ),
                                cli );

        assertArgumentsPresentInOrder( cli, "-pl", "proj1", "-am", "-amd" );
    }

    @Test
    public void testShouldSetResumeFrom()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setResumeFrom( ":module3" ), cli );

        assertArgumentsPresentInOrder( cli, "-rf", ":module3" );
    }

    @Test
    public void testShouldSetStrictChecksumPolityFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setGlobalChecksumPolicy( InvocationRequest.CheckSumPolicy.Fail ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-C" ) );
    }

    @Test
    public void testShouldSetLaxChecksumPolicyFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setFlags( newRequest().setGlobalChecksumPolicy( InvocationRequest.CheckSumPolicy.Warn ), cli );

        assertArgumentsPresent( cli, Collections.singleton( "-c" ) );
    }

    @Test
    public void testShouldSetFailAtEndFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setReactorFailureBehavior( InvocationRequest.ReactorFailureBehavior.FailAtEnd ),
                                cli );

        assertArgumentsPresent( cli, Collections.singleton( "-fae" ) );
    }

    @Test
    public void testShouldSetFailNeverFlagFromRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setReactorFailureBehavior( InvocationRequest.ReactorFailureBehavior.FailNever ),
                                cli );

        assertArgumentsPresent( cli, Collections.singleton( "-fn" ) );
    }

    @Test
    public void testShouldUseDefaultOfFailFastWhenSpecifiedInRequest()
    {
        logTestStart();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        Commandline cli = new Commandline();

        tcb.setReactorBehavior( newRequest().setReactorFailureBehavior( InvocationRequest.ReactorFailureBehavior.FailFast ),
                                cli );

        Set<String> banned = new HashSet<String>();
        banned.add( "-fae" );
        banned.add( "-fn" );

        assertArgumentsNotPresent( cli, banned );
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testShouldSpecifyThreadsFromRequest()
        throws IOException
    {
        logTestStart();

        Commandline cli = new Commandline();

        TestCommandLineBuilder tcb = new TestCommandLineBuilder();
        tcb.setThreads( newRequest().setThreads( "2.0C" ), cli );

        assertArgumentsPresentInOrder( cli, "-T", "2.0C" );
    }

    @Test
    public void testBuildTypicalMavenInvocationEndToEnd()
        throws Exception
    {
        logTestStart();
        File mavenDir = setupTempMavenHomeIfMissing( false );

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

        String executable = commandline.getExecutable();

        assertTrue( executable.indexOf( new File( mavenDir, "bin/mvn" ).getCanonicalPath() ) > -1 );
        assertEquals( projectDir.getCanonicalPath(), commandline.getWorkingDirectory().getCanonicalPath() );
    }

    @Test
    public void testShouldSetEnvVar_MAVEN_TERMINATE_CMD()
        throws Exception
    {
        logTestStart();
        setupTempMavenHomeIfMissing( false );

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

    @Test
    public void testShouldInsertActivatedProfiles()
        throws Exception
    {
        setupTempMavenHomeIfMissing( false );

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

    @Test
    public void testShouldSetEnvVar_M2_HOME()
        throws Exception
    {
        Assume.assumeNotNull( System.getenv( "M2_HOME" ) );

        logTestStart();
        setupTempMavenHomeIfMissing( true );

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
        File mavenHome2 = new File( System.getProperty( "maven.home" ) );
        commandLineBuilder.setMavenHome( mavenHome2 );

        Commandline commandline = commandLineBuilder.build( request );

        String[] environmentVariables = commandline.getEnvironmentVariables();
        String m2Home = null;
        for ( int i = 0; i < environmentVariables.length; i++ )
        {
            String envVar = environmentVariables[i];
            if ( envVar.startsWith( "M2_HOME=" ) )
            {
                m2Home = envVar;
            }
        }
        assertEquals( "M2_HOME=" + mavenHome2.getAbsolutePath(), m2Home );
    }

    @Test
    public void testMvnCommand()
        throws Exception
    {
        MavenCommandLineBuilder commandLineBuilder = new MavenCommandLineBuilder();
        File mavenExecutable = new File( "mvnDebug" );
        commandLineBuilder.setMavenExecutable( mavenExecutable );
        File executable = commandLineBuilder.findMavenExecutable();
        assertTrue( "Expected executable to exist", executable.exists() );
        assertTrue( "Expected executable to be absolute", executable.isAbsolute() );
    }

    @Test
    public void testAddShellEnvironment()
        throws Exception
    {
        setupTempMavenHomeIfMissing( false );

        InvocationRequest request = newRequest();

        String envVar1Name = "VAR-1";
        String envVar1Value = "VAR-1-VALUE";

        String envVar2Name = "VAR-2";
        String envVar2Value = "VAR-2-VALUE";

        request.addShellEnvironment( envVar1Name, envVar1Value );
        request.addShellEnvironment( envVar2Name, envVar2Value );

        MavenCommandLineBuilder commandLineBuilder = new MavenCommandLineBuilder();

        Commandline commandline = commandLineBuilder.build( request );

        assertEnvironmentVariablePresent( commandline, envVar1Name, envVar1Value );
        assertEnvironmentVariablePresent( commandline, envVar2Name, envVar2Value );
    }

    @Before
    public void setUp()
    {
        sysProps = System.getProperties();

        Properties p = new Properties( sysProps );

        System.setProperties( p );
    }

    @After
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

    private void assertEnvironmentVariablePresent( Commandline cli, String varName, String varValue )
        throws CommandLineException
    {
        List<String> environmentVariables = Arrays.asList( cli.getEnvironmentVariables() );

        String expectedDeclaration = varName + "=" + varValue;

        assertTrue( "Environment variable setting: \'" + expectedDeclaration + "\' is mssing in "
            + environmentVariables, environmentVariables.contains( expectedDeclaration ) );
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
