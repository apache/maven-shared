package org.apache.maven.shared.io.download;

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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.shared.io.Utils;
import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusTestCase;

public class DefaultDownloadManagerTest
    extends PlexusTestCase
{

    private WagonManager wagonManager;

    private Wagon wagon;

    public void setUp()
        throws Exception
    {
        super.setUp();

        wagonManager = createMock( WagonManager.class );
        wagon = createMock( Wagon.class );
    }

    public void testShouldConstructWithNoParamsAndHaveNonNullMessageHolder()
    {
        new DefaultDownloadManager();
    }

    public void testShouldConstructWithWagonManager()
    {
        replay( wagonManager );

        new DefaultDownloadManager( wagonManager );

        verify( wagonManager );
    }

    public void testShouldLookupInstanceDefaultRoleHint()
        throws Exception
    {
        lookup( DownloadManager.ROLE, DefaultDownloadManager.ROLE_HINT );
    }

    public void testShouldFailToDownloadMalformedURL()
    {
        replay( wagonManager );

        DownloadManager mgr = new DefaultDownloadManager( wagonManager );

        try
        {
            mgr.download( "://nothing.com/index.html", new DefaultMessageHolder() );

            fail( "Should not download with invalid URL." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( e.getMessage().indexOf( "invalid URL" ) > -1 );
        }

        verify( wagonManager );
    }

    public void testShouldDownloadFromTempFileWithNoTransferListeners()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupDefaultMockConfiguration();

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

        verify( wagon, wagonManager );
    }

    public void testShouldDownloadFromTempFileTwiceAndUseCache()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupDefaultMockConfiguration();

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        File first = downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

        MessageHolder mh = new DefaultMessageHolder();

        File second = downloadManager.download( tempFile.toURL().toExternalForm(), mh );

        assertSame( first, second );
        assertEquals( 1, mh.size() );
        assertTrue( mh.render().indexOf( "Using cached" ) > -1 );

        verify( wagon, wagonManager );
    }

    public void testShouldDownloadFromTempFileWithOneTransferListener()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupDefaultMockConfiguration();

        TransferListener transferListener = createMock( TransferListener.class );

        wagon.addTransferListener( transferListener );

        wagon.removeTransferListener( transferListener );

        replay( wagon, wagonManager, transferListener );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        downloadManager.download( tempFile.toURL().toExternalForm(), Collections.singletonList( transferListener ),
                                  new DefaultMessageHolder() );

        verify( wagon, wagonManager, transferListener );
    }

    public void testShouldFailToDownloadWhenWagonProtocolNotFound()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonManagerGetException( new UnsupportedProtocolException( "not supported" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to retrieve wagon." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( Utils.toString( e ).indexOf( "UnsupportedProtocolException" ) > -1 );
        }

        verify( wagon, wagonManager );
    }

    public void testShouldFailToDownloadWhenWagonConnectThrowsConnectionException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonConnectionException( new ConnectionException( "connect error" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to connect wagon." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( Utils.toString( e ).indexOf( "ConnectionException" ) > -1 );
        }

        verify( wagon, wagonManager );
    }

    public void testShouldFailToDownloadWhenWagonConnectThrowsAuthenticationException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonConnectionException( new AuthenticationException( "bad credentials" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to connect wagon." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( Utils.toString( e ).indexOf( "AuthenticationException" ) > -1 );
        }

        verify( wagon, wagonManager );
    }

    public void testShouldFailToDownloadWhenWagonGetThrowsTransferFailedException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonGetException( new TransferFailedException( "bad transfer" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to get resource." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( Utils.toString( e ).indexOf( "TransferFailedException" ) > -1 );
        }

        verify( wagon, wagonManager );
    }

    public void testShouldFailToDownloadWhenWagonGetThrowsResourceDoesNotExistException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonGetException( new ResourceDoesNotExistException( "bad resource" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to get resource." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( Utils.toString( e ).indexOf( "ResourceDoesNotExistException" ) > -1 );
        }

        verify( wagon, wagonManager );
    }

    public void testShouldFailToDownloadWhenWagonGetThrowsAuthorizationException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonGetException( new AuthorizationException( "bad transfer" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to get resource." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( Utils.toString( e ).indexOf( "AuthorizationException" ) > -1 );
        }

        verify( wagon, wagonManager );
    }

    public void testShouldFailToDownloadWhenWagonDisconnectThrowsConnectionException()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonDisconnectException( new ConnectionException( "not connected" ) );

        replay( wagon, wagonManager );

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        MessageHolder mh = new DefaultMessageHolder();

        downloadManager.download( tempFile.toURL().toExternalForm(), mh );

        assertTrue( mh.render().indexOf( "ConnectionException" ) > -1 );

        verify( wagon, wagonManager );
    }

    private void setupDefaultMockConfiguration()
    {
        try
        {
            expect( wagonManager.getWagon( "file" ) ).andReturn( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        expect( wagonManager.getAuthenticationInfo( anyString() ) ).andReturn( null );

        expect( wagonManager.getProxy( anyString() ) ).andReturn( null );

        try
        {
            wagon.connect( anyObject( Repository.class ) , anyObject( AuthenticationInfo.class ), anyObject( ProxyInfo.class ) );
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthenticationException e )
        {
            fail( "This shouldn't happen!!" );
        }

        try
        {
            wagon.get( anyString(), anyObject( File.class ) );
        }
        catch ( TransferFailedException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( ResourceDoesNotExistException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthorizationException e )
        {
            fail( "This shouldn't happen!!" );
        }

        try
        {
            wagon.disconnect();
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
    }

    private void setupMocksWithWagonManagerGetException( Throwable error )
    {
        try
        {
            expect( wagonManager.getWagon( "file" ) ).andThrow( error );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }
    }

    private void setupMocksWithWagonConnectionException( Throwable error )
    {
        try
        {
            expect( wagonManager.getWagon( "file" ) ).andReturn( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        expect( wagonManager.getAuthenticationInfo( anyString() ) ).andReturn( null );

        expect( wagonManager.getProxy( anyString() ) ).andReturn( null );

        try
        {
            wagon.connect( anyObject( Repository.class ) , anyObject( AuthenticationInfo.class ), anyObject( ProxyInfo.class ) );
            expectLastCall().andThrow( error );
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthenticationException e )
        {
            fail( "This shouldn't happen!!" );
        }
    }

    private void setupMocksWithWagonGetException( Throwable error )
    {
        try
        {
            expect( wagonManager.getWagon( "file" ) ).andReturn( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        expect( wagonManager.getAuthenticationInfo( anyString() ) ).andReturn( null );

        expect( wagonManager.getProxy( anyString() ) ).andReturn( null );

        try
        {
            wagon.connect( anyObject( Repository.class ) , anyObject( AuthenticationInfo.class ), anyObject( ProxyInfo.class ) );
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthenticationException e )
        {
            fail( "This shouldn't happen!!" );
        }

        try
        {
            wagon.get( anyString(), anyObject( File.class ) );
            expectLastCall().andThrow( error );
        }
        catch ( TransferFailedException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( ResourceDoesNotExistException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthorizationException e )
        {
            fail( "This shouldn't happen!!" );
        }

        try
        {
            wagon.disconnect();
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
    }

    private void setupMocksWithWagonDisconnectException( Throwable error )
    {
        try
        {
            expect( wagonManager.getWagon( "file" ) ).andReturn( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        expect( wagonManager.getAuthenticationInfo( anyString() ) ).andReturn( null );

        expect( wagonManager.getProxy( anyString() ) ).andReturn( null );

        try
        {
            wagon.connect( anyObject( Repository.class ) , anyObject( AuthenticationInfo.class ), anyObject( ProxyInfo.class ) );
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthenticationException e )
        {
            fail( "This shouldn't happen!!" );
        }

        try
        {
            wagon.get( anyString(), anyObject( File.class ) );
        }
        catch ( TransferFailedException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( ResourceDoesNotExistException e )
        {
            fail( "This shouldn't happen!!" );
        }
        catch ( AuthorizationException e )
        {
            fail( "This shouldn't happen!!" );
        }

        try
        {
            wagon.disconnect();
            expectLastCall().andThrow( error );
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
    }
}
