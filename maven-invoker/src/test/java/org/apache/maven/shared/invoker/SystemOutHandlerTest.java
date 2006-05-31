package org.apache.maven.shared.invoker;

import junit.framework.TestCase;

public class SystemOutHandlerTest
    extends TestCase
{

    public void testConsumeWithoutAlwaysFlush()
    {
        logTestStart();
        new SystemOutHandler( false ).consumeLine( "This is a test." );
    }
    
    public void testConsumeWithAlwaysFlush()
    {
        logTestStart();
        new SystemOutHandler( true ).consumeLine( "This is a test." );
    }
    
    public void testConsumeNullLine()
    {
        logTestStart();
        new SystemOutHandler().consumeLine( null );
    }
    
    // this is just a debugging helper for separating unit test output...
    private void logTestStart()
    {
        NullPointerException npe = new NullPointerException();
        StackTraceElement element = npe.getStackTrace()[1];

        System.out.println( "Starting: " + element.getMethodName() );
    }
}
