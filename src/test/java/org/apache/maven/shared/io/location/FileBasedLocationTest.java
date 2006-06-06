package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileBasedLocationTest
    extends AbstractLocationTest
{
    
    public void testShouldConstructWithFileThenRetrieveSameFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        FileBasedLocation location = new FileBasedLocation( file, file.getAbsolutePath() );
        
        assertSame( file, location.getFile() );
        assertEquals( file.getAbsolutePath(), location.getSpecification() );
    }
    
    public void testShouldReadFileContentsUsingByteBuffer() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        String testStr = "This is a test";
        
        writeToFile( file, testStr );
        
        FileBasedLocation location = new FileBasedLocation( file, file.getAbsolutePath() );
        
        location.open();
        
        ByteBuffer buffer = ByteBuffer.allocate( testStr.length() );
        location.read( buffer );
        
        assertEquals( testStr, new String( buffer.array() ) );
    }
    
    public void testShouldReadFileContentsUsingByteArray() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        String testStr = "This is a test";
        
        writeToFile( file, testStr );
        
        FileBasedLocation location = new FileBasedLocation( file, file.getAbsolutePath() );
        
        location.open();
        
        byte[] buffer = new byte[ testStr.length() ];
        location.read( buffer );
        
        assertEquals( testStr, new String( buffer ) );
    }
    
    public void testShouldReadThenClose() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        String testStr = "This is a test";
        
        writeToFile( file, testStr );
        
        FileBasedLocation location = new FileBasedLocation( file, file.getAbsolutePath() );
        
        location.open();
        
        byte[] buffer = new byte[ testStr.length() ];
        location.read( buffer );
        
        assertEquals( testStr, new String( buffer ) );
        
        location.close();
    }
    
    public void testShouldOpenThenFailToSetFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        TestFileLocation location = new TestFileLocation( file.getAbsolutePath() );
        
        location.open();
        
        try
        {
            location.setFile( file );
            
            fail( "should not succeed." );
        }
        catch( IllegalStateException e )
        {
        }
    }
    
    public void testShouldConstructWithoutFileThenSetFileThenOpen() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        TestFileLocation location = new TestFileLocation( file.getAbsolutePath() );
        
        location.setFile( file );
        location.open();
    }
    
    public void testShouldConstructWithLocationThenRetrieveEquivalentFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();
        
        Location location = new TestFileLocation( file.getAbsolutePath() );
        
        assertEquals( file, location.getFile() );
        assertEquals( file.getAbsolutePath(), location.getSpecification() );
    }
    
    private static final class TestFileLocation extends FileBasedLocation
    {

        TestFileLocation( String specification )
        {
            super( specification );
        }
        
    }

}
