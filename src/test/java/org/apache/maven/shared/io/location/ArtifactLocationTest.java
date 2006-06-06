package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;

public class ArtifactLocationTest
    extends AbstractLocationTest
{
    
    public void testShouldConstructFromTempFileSpecification()
        throws IOException
    {
        File f = File.createTempFile( "artifact-location.", ".test" );

        Artifact a = new DefaultArtifact( "group", "artifact", VersionRange.createFromVersion( "1" ), null, "jar",
                                          null, new DefaultArtifactHandler() );
        
        a.setFile( f );
        
        ArtifactLocation location = new ArtifactLocation( a, f.getAbsolutePath() );
        
        assertSame( f, location.getFile() );
    }

    public void testShouldRead()
        throws IOException
    {
        File f = File.createTempFile( "url-location.", ".test" );

        String testStr = "This is a test";

        writeToFile( f, testStr );

        Artifact a = new DefaultArtifact( "group", "artifact", VersionRange.createFromVersion( "1" ), null, "jar",
                                          null, new DefaultArtifactHandler() );
        
        a.setFile( f );
        
        ArtifactLocation location = new ArtifactLocation( a, f.getAbsolutePath() );

        location.open();

        byte[] buffer = new byte[testStr.length()];

        int read = location.read( buffer );

        assertEquals( testStr.length(), read );

        assertEquals( testStr, new String( buffer ) );
    }

}
