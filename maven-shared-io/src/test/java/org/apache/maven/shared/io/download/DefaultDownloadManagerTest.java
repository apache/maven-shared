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

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.shared.io.MockManager;
import org.apache.maven.shared.io.TestUtils;
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
import org.easymock.MockControl;

public class DefaultDownloadManagerTest
    extends PlexusTestCase
{

    private MockManager mockManager;

    private MockControl wagonManagerControl;

    private WagonManager wagonManager;

    private MockControl wagonControl;

    private Wagon wagon;

    public void setUp()
        throws Exception
    {
        super.setUp();

        mockManager = new MockManager();

        wagonManagerControl = MockControl.createControl( WagonManager.class );
        mockManager.add( wagonManagerControl );

        wagonManager = (WagonManager) wagonManagerControl.getMock();

        wagonControl = MockControl.createControl( Wagon.class );
        mockManager.add( wagonControl );

        wagon = (Wagon) wagonControl.getMock();
    }

    public void testShouldConstructWithNoParamsAndHaveNonNullMessageHolder()
    {
        new DefaultDownloadManager();
    }

    public void testShouldConstructWithWagonManager()
    {
        MockManager mockManager = new MockManager();

        MockControl ctl = MockControl.createControl( WagonManager.class );
        mockManager.add( ctl );

        WagonManager wagonManager = (WagonManager) ctl.getMock();

        mockManager.replayAll();

        new DefaultDownloadManager( wagonManager );

        mockManager.verifyAll();
    }

    public void testShouldLookupInstanceDefaultRoleHint()
        throws Exception
    {
        lookup( DownloadManager.ROLE, DefaultDownloadManager.ROLE_HINT );
    }

    public void testShouldFailToDownloadMalformedURL()
    {
        MockManager mockManager = new MockManager();

        MockControl ctl = MockControl.createControl( WagonManager.class );
        mockManager.add( ctl );

        WagonManager wagonManager = (WagonManager) ctl.getMock();

        mockManager.replayAll();

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

        mockManager.verifyAll();
    }

    public void testShouldDownloadFromTempFileWithNoTransferListeners()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupDefaultMockConfiguration();

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

        mockManager.verifyAll();
    }

    public void testShouldDownloadFromTempFileTwiceAndUseCache()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupDefaultMockConfiguration();

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        File first = downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

        MessageHolder mh = new DefaultMessageHolder();

        File second = downloadManager.download( tempFile.toURL().toExternalForm(), mh );

        assertSame( first, second );
        assertEquals( 1, mh.size() );
        assertTrue( mh.render().indexOf( "Using cached" ) > -1 );

        mockManager.verifyAll();
    }

    public void testShouldDownloadFromTempFileWithOneTransferListener()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupDefaultMockConfiguration();

        MockControl transferListenerControl = MockControl.createControl( TransferListener.class );
        mockManager.add( transferListenerControl );

        TransferListener transferListener = (TransferListener) transferListenerControl.getMock();

        wagon.addTransferListener( transferListener );

        wagon.removeTransferListener( transferListener );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        downloadManager.download( tempFile.toURL().toExternalForm(), Collections.singletonList( transferListener ),
                                  new DefaultMessageHolder() );

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonProtocolNotFound()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonManagerGetException( new UnsupportedProtocolException( "not supported" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to retrieve wagon." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( TestUtils.toString( e ).indexOf( "UnsupportedProtocolException" ) > -1 );
        }

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonConnectThrowsConnectionException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonConnectionException( new ConnectionException( "connect error" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to connect wagon." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( TestUtils.toString( e ).indexOf( "ConnectionException" ) > -1 );
        }

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonConnectThrowsAuthenticationException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonConnectionException( new AuthenticationException( "bad credentials" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to connect wagon." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( TestUtils.toString( e ).indexOf( "AuthenticationException" ) > -1 );
        }

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonGetThrowsTransferFailedException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonGetException( new TransferFailedException( "bad transfer" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to get resource." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( TestUtils.toString( e ).indexOf( "TransferFailedException" ) > -1 );
        }

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonGetThrowsResourceDoesNotExistException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonGetException( new ResourceDoesNotExistException( "bad resource" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to get resource." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( TestUtils.toString( e ).indexOf( "ResourceDoesNotExistException" ) > -1 );
        }

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonGetThrowsAuthorizationException()
        throws IOException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonGetException( new AuthorizationException( "bad transfer" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        try
        {
            downloadManager.download( tempFile.toURL().toExternalForm(), new DefaultMessageHolder() );

            fail( "should have failed to get resource." );
        }
        catch ( DownloadFailedException e )
        {
            assertTrue( TestUtils.toString( e ).indexOf( "AuthorizationException" ) > -1 );
        }

        mockManager.verifyAll();
    }

    public void testShouldFailToDownloadWhenWagonDisconnectThrowsConnectionException()
        throws IOException, DownloadFailedException
    {
        File tempFile = File.createTempFile( "download-source", "test" );
        tempFile.deleteOnExit();

        setupMocksWithWagonDisconnectException( new ConnectionException( "not connected" ) );

        mockManager.replayAll();

        DownloadManager downloadManager = new DefaultDownloadManager( wagonManager );

        MessageHolder mh = new DefaultMessageHolder();

        downloadManager.download( tempFile.toURL().toExternalForm(), mh );

        assertTrue( mh.render().indexOf( "ConnectionException" ) > -1 );

        mockManager.verifyAll();
    }

    private void setupDefaultMockConfiguration()
    {
        try
        {
            wagonManager.getWagon( "file" );
            wagonManagerControl.setReturnValue( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        wagonManager.getAuthenticationInfo( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        wagonManager.getProxy( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        try
        {
            wagon.connect( new Repository(), new AuthenticationInfo(), new ProxyInfo() );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
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
            wagon.get( "file:///some/path", new File( "." ) );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
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
            wagonManager.getWagon( "file" );
            wagonManagerControl.setThrowable( error );
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
            wagonManager.getWagon( "file" );
            wagonManagerControl.setReturnValue( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        wagonManager.getAuthenticationInfo( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        wagonManager.getProxy( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        try
        {
            wagon.connect( new Repository(), new AuthenticationInfo(), new ProxyInfo() );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
            wagonControl.setThrowable( error );
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
            wagonManager.getWagon( "file" );
            wagonManagerControl.setReturnValue( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        wagonManager.getAuthenticationInfo( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        wagonManager.getProxy( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        try
        {
            wagon.connect( new Repository(), new AuthenticationInfo(), new ProxyInfo() );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
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
            wagon.get( "file:///some/path", new File( "." ) );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
            wagonControl.setThrowable( error );
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
            wagonManager.getWagon( "file" );
            wagonManagerControl.setReturnValue( wagon );
        }
        catch ( UnsupportedProtocolException e )
        {
            fail( "This shouldn't happen!!" );
        }

        wagonManager.getAuthenticationInfo( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        wagonManager.getProxy( "" );
        wagonManagerControl.setMatcher( MockControl.ALWAYS_MATCHER );
        wagonManagerControl.setReturnValue( null );

        try
        {
            wagon.connect( new Repository(), new AuthenticationInfo(), new ProxyInfo() );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
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
            wagon.get( "file:///some/path", new File( "." ) );
            wagonControl.setMatcher( MockControl.ALWAYS_MATCHER );
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
            wagonControl.setThrowable( error );
        }
        catch ( ConnectionException e )
        {
            fail( "This shouldn't happen!!" );
        }
    }
}
