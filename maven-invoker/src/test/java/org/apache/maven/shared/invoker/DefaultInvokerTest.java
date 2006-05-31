package org.apache.maven.shared.invoker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;

public class DefaultInvokerTest
    extends TestCase
{

    public void testBuildShouldSucceed()
        throws IOException, MavenInvocationException
    {
        File basedir = getBasedirForBuild();

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome( findMavenHome() );
        
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory( basedir );
        
        List goals = new ArrayList();
        goals.add( "clean" );
        goals.add( "package" );
        
        request.setGoals( goals );
        
        InvocationResult result = invoker.execute( request );
        
        assertEquals( 0, result.getExitCode() );
    }

    public void testBuildShouldFail()
        throws IOException, MavenInvocationException
    {
        File basedir = getBasedirForBuild();

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome( findMavenHome() );

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory( basedir );
        
        List goals = new ArrayList();
        goals.add( "clean" );
        goals.add( "package" );
        
        request.setGoals( goals );

        InvocationResult result = invoker.execute( request );

        assertEquals( 1, result.getExitCode() );
    }

    private File findMavenHome()
        throws IOException
    {
        String mavenHome = System.getProperty( "maven.home" );

        if ( mavenHome == null )
        {
            mavenHome = CommandLineUtils.getSystemEnvVars().getProperty( "M2_HOME" );
        }

        if ( mavenHome == null )
        {
            throw new IllegalStateException( "Cannot find Maven application "
                + "directory. Either specify \'maven.home\' system property, or M2_HOME environment variable." );
        }

        return new File( mavenHome );
    }

    private File getBasedirForBuild()
    {
        StackTraceElement element = new NullPointerException().getStackTrace()[1];
        String methodName = element.getMethodName();

        String dirName = StringUtils.addAndDeHump( methodName );

        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL dirResource = cloader.getResource( dirName );

        if ( dirResource == null )
        {
            throw new IllegalStateException( "Project: " + dirName + " for test method: " + methodName + " is missing." );
        }

        return new File( dirResource.getPath() );
    }

}
