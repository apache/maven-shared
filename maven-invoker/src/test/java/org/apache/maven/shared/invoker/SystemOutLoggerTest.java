package org.apache.maven.shared.invoker;

import java.net.MalformedURLException;

import junit.framework.TestCase;

public class SystemOutLoggerTest
    extends TestCase
{
    
    private static final Throwable EXCEPTION = new MalformedURLException( "This is meant to happen. It's part of the test." );
    
    private static final String MESSAGE = "This is a test message.";

    public void testDebugWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().debug( MESSAGE );
    }
    
    public void testDebugWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().debug( MESSAGE, EXCEPTION );
    }
    
    public void testDebugWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().debug( null );
    }

    public void testDebugWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().debug( null, EXCEPTION );
    }

    public void testDebugWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().debug( MESSAGE, null );
    }

    public void testInfoWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().info( MESSAGE );
    }
    
    public void testInfoWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().info( MESSAGE, EXCEPTION );
    }
    
    public void testInfoWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().info( null );
    }

    public void testInfoWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().info( null, EXCEPTION );
    }

    public void testInfoWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().info( MESSAGE, null );
    }

    public void testWarnWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().warn( MESSAGE );
    }
    
    public void testWarnWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().warn( MESSAGE, EXCEPTION );
    }
    
    public void testWarnWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().warn( null );
    }

    public void testWarnWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().warn( null, EXCEPTION );
    }

    public void testWarnWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().warn( MESSAGE, null );
    }

    public void testErrorWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().error( MESSAGE );
    }
    
    public void testErrorWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().error( MESSAGE, EXCEPTION );
    }
    
    public void testErrorWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().error( null );
    }

    public void testErrorWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().error( null, EXCEPTION );
    }

    public void testErrorWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().error( MESSAGE, null );
    }

    public void testFatalErrorWithMessageOnly()
    {
        logTestStart();
        new SystemOutLogger().fatalError( MESSAGE );
    }
    
    public void testFatalErrorWithMessageAndError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( MESSAGE, EXCEPTION );
    }
    
    public void testFatalErrorWithNullMessageAndNoError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( null );
    }

    public void testFatalErrorWithNullMessageError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( null, EXCEPTION );
    }

    public void testFatalErrorWithMessageNullError()
    {
        logTestStart();
        new SystemOutLogger().fatalError( MESSAGE, null );
    }

    // this is just a debugging helper for separating unit test output...
    private void logTestStart()
    {
        NullPointerException npe = new NullPointerException();
        StackTraceElement element = npe.getStackTrace()[1];

        System.out.println( "Starting: " + element.getMethodName() );
    }
}
