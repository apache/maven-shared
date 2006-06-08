package org.apache.maven.shared.io.location;

import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

import junit.framework.TestCase;

public class ClasspathResourceLocatorStrategyTest
    extends TestCase
{
    
    public void testShouldConstructWithNoParams()
    {
        new ClasspathResourceLocatorStrategy();
    }

    public void testShouldConstructWithTempFileOptions()
    {
        new ClasspathResourceLocatorStrategy( "prefix.", ".suffix", true );
    }

    public void testShouldFailToResolveMissingClasspathResource()
    {
        MessageHolder mh = new DefaultMessageHolder();
        Location location = new ClasspathResourceLocatorStrategy().resolve( "/some/missing/path", mh );
        
        assertNull( location );
        assertEquals( 1, mh.size() );
    }
    
    public void testShouldResolveExistingClasspathResourceWithoutPrecedingSlash()
    {
        MessageHolder mh = new DefaultMessageHolder();
        Location location = new ClasspathResourceLocatorStrategy().resolve( "META-INF/maven/test.properties", mh );
        
        assertNotNull( location );
        assertEquals( 0, mh.size() );
    }
    
}
