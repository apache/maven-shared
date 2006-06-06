package org.apache.maven.shared.io.location;

import junit.framework.TestCase;

public class LocatorTest
    extends TestCase
{
    
    public void testClasspathResource()
    {
        String url = getClass().getName().replace( '.', '/' ) + ".class";
        
        Locator locator = new Locator();
        locator.resolve( url );
    }

}
