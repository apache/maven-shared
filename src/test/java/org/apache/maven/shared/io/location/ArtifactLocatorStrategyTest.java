package org.apache.maven.shared.io.location;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

import static org.easymock.EasyMock.*;

public class ArtifactLocatorStrategyTest
    extends TestCase
{

    private ArtifactFactory factory;

    private ArtifactResolver resolver;

    private ArtifactRepository localRepository;

    public void setUp()
    {
        factory = createMock( ArtifactFactory.class );
        resolver = createMock( ArtifactResolver.class );
        localRepository = createMock( ArtifactRepository.class );
    }

    public void testShouldConstructWithoutDefaultArtifactType()
    {
        replay( factory, resolver, localRepository );

        new ArtifactLocatorStrategy( factory, resolver, localRepository, Collections.EMPTY_LIST );

        verify( factory, resolver, localRepository );
    }

    public void testShouldConstructWithDefaultArtifactType()
    {
        replay( factory, resolver, localRepository );

        new ArtifactLocatorStrategy( factory, resolver, localRepository, Collections.EMPTY_LIST, "zip" );

        verify( factory, resolver, localRepository );
    }

    public void testShouldFailToResolveSpecWithOneToken()
    {
        replay( factory, resolver, localRepository );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST, "zip" );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "one-token", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        verify( factory, resolver, localRepository );
    }

    public void testShouldFailToResolveSpecWithTwoTokens()
    {
        replay( factory, resolver, localRepository );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST, "zip" );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "two:tokens", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        verify( factory, resolver, localRepository );
    }

    public void testShouldResolveSpecWithThreeTokensUsingDefaultType()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( tempFile );
        expect( artifact.getFile() ).andReturn( tempFile );
        
        expect( factory.createArtifact( "group", "artifact", "version", null, "jar" ) ).andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldResolveSpecWithThreeTokensUsingCustomizedDefaultType()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( tempFile );
        expect( artifact.getFile() ).andReturn( tempFile );
        
        expect( factory.createArtifact( "group", "artifact", "version", null, "zip" ) ).andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST, "zip" );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldResolveSpecWithFourTokens()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( tempFile );
        expect( artifact.getFile() ).andReturn( tempFile );
        
        expect( factory.createArtifact( "group", "artifact", "version", null, "zip" ) ).andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version:zip", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldResolveSpecWithFiveTokens()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( tempFile );
        expect( artifact.getFile() ).andReturn( tempFile );
        
        expect( factory.createArtifactWithClassifier( "group", "artifact", "version", "zip", "classifier" ) )
                .andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version:zip:classifier", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldResolveSpecWithFiveTokensAndEmptyTypeToken()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( tempFile );
        expect( artifact.getFile() ).andReturn( tempFile );
        
        expect( factory.createArtifactWithClassifier( "group", "artifact", "version", "jar", "classifier" ) )
                .andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version::classifier", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );

        assertSame( tempFile, location.getFile() );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldResolveSpecWithMoreThanFiveTokens()
        throws IOException
    {
        File tempFile = File.createTempFile( "artifact-location.", ".temp" );
        tempFile.deleteOnExit();

        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( tempFile );
        expect( artifact.getFile() ).andReturn( tempFile );
        
        expect( factory.createArtifactWithClassifier( "group", "artifact", "version", "zip", "classifier" ) )
                .andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version:zip:classifier:six:seven", mh );

        assertNotNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( ":six:seven" ) > -1 );

        assertSame( tempFile, location.getFile() );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldNotResolveSpecToArtifactWithNullFile()
        throws IOException
    {
        Artifact artifact = createMock( Artifact.class );
        
        expect( artifact.getFile() ).andReturn( null );
        expect( artifact.getId() ).andReturn( "<some-artifact-id>" );
        
        expect( factory.createArtifact( "group", "artifact", "version", null, "jar" )).andReturn( artifact );

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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( "<some-artifact-id>" ) > -1 );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldNotResolveWhenArtifactNotFoundExceptionThrown()
        throws IOException
    {
        Artifact artifact = createMock( Artifact.class );

        expect( artifact.getId() ).andReturn( "<some-artifact-id>" );

        expect( factory.createArtifact( "group", "artifact", "version", null, "jar" ) ).andReturn( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
            expectLastCall().andThrow( new ArtifactNotFoundException( "not found", "group", "artifact", "version",
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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( "<some-artifact-id>" ) > -1 );
        assertTrue( mh.render().indexOf( "not found" ) > -1 );

        verify( factory, resolver, localRepository, artifact );
    }

    public void testShouldNotResolveWhenArtifactResolutionExceptionThrown()
        throws IOException
    {
        Artifact artifact = createMock( Artifact.class );

        expect( artifact.getId() ).andReturn( "<some-artifact-id>" );

        expect( factory.createArtifact( "group", "artifact", "version", null, "jar" ) ).andReturn( artifact );

        try
        {
            resolver.resolve( artifact, Collections.EMPTY_LIST, localRepository );
            expectLastCall().andThrow( new ArtifactResolutionException( "resolution failed", "group", "artifact",
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

        replay( factory, resolver, localRepository, artifact );

        LocatorStrategy strategy = new ArtifactLocatorStrategy( factory, resolver, localRepository,
                                                                Collections.EMPTY_LIST );
        MessageHolder mh = new DefaultMessageHolder();

        Location location = strategy.resolve( "group:artifact:version", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );

        assertTrue( mh.render().indexOf( "<some-artifact-id>" ) > -1 );
        assertTrue( mh.render().indexOf( "resolution failed" ) > -1 );

        verify( factory, resolver, localRepository, artifact );
    }

}
