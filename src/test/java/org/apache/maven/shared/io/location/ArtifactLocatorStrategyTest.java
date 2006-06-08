package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.shared.io.MockManager;
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;
import org.easymock.MockControl;

public class ArtifactLocatorStrategyTest
    extends TestCase
{
    
    private MockManager mockManager = new MockManager();

    private MockControl factoryControl;

    private ArtifactFactory factory;

    private MockControl resolverControl;

    private ArtifactResolver resolver;

    private MockControl localRepositoryControl;

    private ArtifactRepository localRepository;

    public void setUp()
    {
        factoryControl = MockControl.createControl( ArtifactFactory.class );
        mockManager.add( factoryControl );

        factory = (ArtifactFactory) factoryControl.getMock();

        resolverControl = MockControl.createControl( ArtifactResolver.class );
        mockManager.add( resolverControl );

        resolver = (ArtifactResolver) resolverControl.getMock();

        localRepositoryControl = MockControl.createControl( ArtifactRepository.class );
        mockManager.add( localRepositoryControl );

        localRepository = (ArtifactRepository) localRepositoryControl.getMock();
    }

    public void testShouldConstructWithoutDefaultArtifactType()
    {
        mockManager.replayAll();

        new ArtifactLocatorStrategy( factory, resolver, localRepository, Collections.EMPTY_LIST );

        mockManager.verifyAll();
    }

    public void testShouldConstructWithDefaultArtifactType()
    {
        mockManager.replayAll();

        new ArtifactLocatorStrategy( factory, resolver, localRepository, Collections.EMPTY_LIST, "zip" );

        mockManager.verifyAll();
    }

    public void testShouldFailToResolveSpecWithOneToken()
    {
        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST, "zip" );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "one-token", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        mockManager.verifyAll();
    }

    public void testShouldFailToResolveSpecWithTwoTokens()
    {
        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST, "zip" );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "two:tokens", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        mockManager.verifyAll();
    }

    public void testShouldResolveSpecWithThreeTokensUsingDefaultType()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );

        factory.createArtifact( "group", "artifact", "version", null, "jar" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        mockManager.verifyAll();
    }

    public void testShouldResolveSpecWithThreeTokensUsingCustomizedDefaultType()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );

        factory.createArtifact( "group", "artifact", "version", null, "zip" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST, "zip" );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        mockManager.verifyAll();
    }

    public void testShouldResolveSpecWithFourTokens()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );

        factory.createArtifact( "group", "artifact", "version", null, "zip" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version:zip", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        mockManager.verifyAll();
    }

    public void testShouldResolveSpecWithFiveTokens()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );

        factory.createArtifactWithClassifier( "group", "artifact", "version", "zip", "classifier" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version:zip:classifier", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        mockManager.verifyAll();
    }

    public void testShouldResolveSpecWithFiveTokensAndEmptyTypeToken()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );

        factory.createArtifactWithClassifier( "group", "artifact", "version", "jar", "classifier" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version::classifier", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        mockManager.verifyAll();
    }

    public void testShouldResolveSpecWithMoreThanFiveTokens()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );
        artifact.getFile();
        artifactControl.setReturnValue( tempFile );

        factory.createArtifactWithClassifier( "group", "artifact", "version", "zip", "classifier" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version:zip:classifier:six:seven", mh );

        assertNotNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( ":six:seven" ) > -1 );

        assertSame( tempFile, location.getFile() );

        mockManager.verifyAll();
    }

    public void testShouldNotResolveSpecToArtifactWithNullFile()
        throws IOException
    {
        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();
        artifact.getFile();
        artifactControl.setReturnValue( null );
        artifact.getId();
        artifactControl.setReturnValue( "<some-artifact-id>" );

        factory.createArtifact( "group", "artifact", "version", null, "jar" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( "<some-artifact-id>" ) > -1 );

        mockManager.verifyAll();
    }

    public void testShouldNotResolveWhenArtifactNotFoundExceptionThrown()
        throws IOException
    {
        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();

        artifact.getId();
        artifactControl.setReturnValue( "<some-artifact-id>" );

        factory.createArtifact( "group", "artifact", "version", null, "jar" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
            resolverControl.setThrowable( new ArtifactNotFoundException( "not found", "group", "artifact", "version",
                                                                         "jar", Collections.EMPTY_LIST,
                                                                         "http://nowhere.com", Collections.EMPTY_LIST,
                                                                         new NullPointerException() ) );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( "<some-artifact-id>" ) > -1 );
        assertTrue( mh.render().indexOf( "not found" ) > -1 );

        mockManager.verifyAll();
    }

    public void testShouldNotResolveWhenArtifactResolutionExceptionThrown()
        throws IOException
    {
        MockControl artifactControl = MockControl.createControl( Artifact.class );
        mockManager.add( artifactControl );

        Artifact artifact = (Artifact) artifactControl.getMock();

        artifact.getId();
        artifactControl.setReturnValue( "<some-artifact-id>" );

        factory.createArtifact( "group", "artifact", "version", null, "jar" );
        factoryControl.setReturnValue( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
            resolverControl.setThrowable( new ArtifactResolutionException( "resolution failed", "group", "artifact",
                                                                           "version", "jar", Collections.EMPTY_LIST,
                                                                           Collections.EMPTY_LIST,
                                                                           new NullPointerException() ) );
        }
        catch ( ArtifactResolutionException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }
        catch ( ArtifactNotFoundException e )
        {
            // should never happen
            fail( "This should NEVER happen. It's a mock!" );
        }

        mockManager.replayAll();

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( "<some-artifact-id>" ) > -1 );
        assertTrue( mh.render().indexOf( "resolution failed" ) > -1 );

        mockManager.verifyAll();
    }

}
