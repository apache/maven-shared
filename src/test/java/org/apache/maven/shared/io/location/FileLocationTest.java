package org.apache.maven.shared.io.location;

import org.apache.maven.shared.io.TestUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class FileLocationTest
    extends TestCase
{

    public void testShouldConstructWithFileThenRetrieveSameFile() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        assertSame( file, location.getFile() );
        assertEquals( file.getAbsolutePath(), location.getSpecification() );
    }

    public void testShouldReadFileContentsUsingByteBuffer() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeToFile( file, testStr );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        location.open();

        ByteBuffer buffer = ByteBuffer.allocate( testStr.length() );
        location.read( buffer );

        assertEquals( testStr, new String( buffer.array() ) );
    }

    public void testShouldReadFileContentsUsingStream() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeToFile( file, testStr );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

        location.open();

        InputStream stream = location.getInputStream();
        StringWriter writer = new StringWriter();
        IOUtil.copy( stream, writer );

        assertEquals( testStr, writer.toString() );
    }

    public void testShouldReadFileContentsUsingByteArray() throws IOException
    {
        File file = File.createTempFile( "test.", ".file-location" );
        file.deleteOnExit();

        String testStr = "This is a test";

        TestUtils.writeToFile( file, testStr );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

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

        TestUtils.writeToFile( file, testStr );

        FileLocation location = new FileLocation( file, file.getAbsolutePath() );

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

    private static final class TestFileLocation extends FileLocation
    {

        TestFileLocation( String specification )
        {
            super( specification );
        }

    }

}
