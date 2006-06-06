package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class URLLocationTest
    extends AbstractLocationTest
{
    
    public void testShouldConstructFromUrlAndTempFileSpecifications() throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );
        
        URL url = f.toURL();
        
        new URLLocation( url, f.getAbsolutePath(), "prefix.", ".suffix", true );
    }
    
    public void testShouldTransferFromTempFile() throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );
        
        URL url = f.toURL();
        
        URLLocation location = new URLLocation( url, f.getAbsolutePath(), "prefix.", ".suffix", true );
        
        assertNotNull( location.getFile() );
        assertFalse( f.equals( location.getFile() ) );
    }

    public void testShouldTransferFromTempFileThenRead() throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );
        
        String testStr = "This is a test";
        
        writeToFile( f, testStr );
        
        URL url = f.toURL();
        
        URLLocation location = new URLLocation( url, f.getAbsolutePath(), "prefix.", ".suffix", true );
        
        location.open();
        
        byte[] buffer = new byte[ testStr.length() ];
        
        int read = location.read( buffer );
        
        assertEquals( testStr.length(), read );
        
        assertEquals( testStr, new String( buffer ) );
    }

}
