package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.maven.shared.io.TestUtils;
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

public class URLLocatorStrategyTest
    extends TestCase
{

    public void testShouldConstructWithNoParams()
    {
        new URLLocatorStrategy();
    }

    public void testShouldConstructWithTempFileOptions()
    {
        new URLLocatorStrategy( "prefix.", ".suffix", true );
    }

    public void testShouldFailToResolveWithMalformedUrl()
    {
        MessageHolder mh = new DefaultMessageHolder();

        Location location = new URLLocatorStrategy().resolve( "://www.google.com", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );
    }

    public void testShouldResolveUrlForTempFile() throws IOException
    {
        File tempFile = File.createTempFile( "prefix.", ".suffix" );
        tempFile.deleteOnExit();
        
        String testStr = "This is a test.";
        
        TestUtils.writeToFile( tempFile, testStr );

        MessageHolder mh = new DefaultMessageHolder();

        Location location = new URLLocatorStrategy().resolve( tempFile.toURL().toExternalForm(), mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );
        
        location.open();
        
        byte[] buffer = new byte[testStr.length()];
        location.read( buffer );
        
        assertEquals( testStr, new String( buffer ) );
    }

}
