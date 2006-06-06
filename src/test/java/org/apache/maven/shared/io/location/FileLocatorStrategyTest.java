package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;

import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

import junit.framework.TestCase;

/*
 * NOTE: Coverage reporting shows that this strategy is a bit on the low side.
 * However, looking deeper it becomes apparent that the reason for this is the 
 * try/catch when trying to canonicalize the file...and I haven't been able to 
 * find a reliable way to break canonicalization. Either way, it will only change
 * the message output, not the resulting location's reachability...so this is
 * non-critical.
 */
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
        
        assertEquals( 1, mh.size() );
    }

}
