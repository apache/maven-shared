package org.apache.maven.shared.io;

import java.io.IOException;

import junit.framework.TestCase;

public class LocatorTest
    extends TestCase
{
    
    public void testClasspathResource()
    {
        String url = getClass().getName().replace( '.', '/' ) + ".class";
        
        Locator locator = new Locator();
        try
        {
            locator.resolveLocation( url );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail( "Cannot resolve location for this test class." );
        }
    }

}
