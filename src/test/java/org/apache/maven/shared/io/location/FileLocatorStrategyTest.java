package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;

import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

import junit.framework.TestCase;

public class FileLocatorStrategyTest
    extends TestCase
{
    
    public void testShouldResolveExistingTempFileLocation() throws IOException
    {
        File f = File.createTempFile( "file-locator.", ".test" );
        f.deleteOnExit();
        
        FileLocatorStrategy fls = new FileLocatorStrategy();
        
        MessageHolder mh = new DefaultMessageHolder();
        
        Location location = fls.resolve( f.getAbsolutePath(), mh );
        
        assertNotNull( location );
        
        assertTrue( mh.isEmpty() );
        
        assertEquals( f, location.getFile() );
    }

    public void testShouldFailToResolveNonExistentFileLocation() throws IOException
    {
        File f = File.createTempFile( "file-locator.", ".test" );
        f.delete();
        
        FileLocatorStrategy fls = new FileLocatorStrategy();
        
        MessageHolder mh = new DefaultMessageHolder();
        
        Location location = fls.resolve( f.getAbsolutePath(), mh );
        
        assertNull( location );
        
        System.out.println( mh.render() );
        
        assertEquals( 1, mh.size() );
    }

}
