package org.apache.maven.shared.invoker;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;

public class DefaultInvokerTest
    extends TestCase
{

    public void testBuildShouldSucceed()
        throws IOException, MavenInvocationException, URISyntaxException
    {
        File basedir = getBasedirForBuild();

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome( findMavenHome() );

        Properties props = new Properties();
        props.put( "key with spaces", "value with spaces" );

        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory( basedir );
        request.setProperties( props );

        List goals = new ArrayList();
        goals.add( "clean" );
        goals.add( "package" );
        
        request.setGoals( goals );
        
        InvocationResult result = invoker.execute( request );
        
        assertEquals( 0, result.getExitCode() );
    }

    public void testBuildShouldFail()
        throws IOException, MavenInvocationException, URISyntaxException
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
        throws URISyntaxException
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

        return new File( new URI( dirResource.toString() ).getPath() );
    }

}
