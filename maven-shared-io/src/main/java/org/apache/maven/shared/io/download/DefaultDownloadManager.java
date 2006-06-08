package org.apache.maven.shared.io.download;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.shared.io.logging.MessageHolder;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.repository.Repository;

public class DefaultDownloadManager
    implements DownloadManager
{

    public static final String ROLE_HINT = "default";

    private WagonManager wagonManager;

    private Map cache = new HashMap();

    public DefaultDownloadManager()
    {
    }

    public DefaultDownloadManager( WagonManager wagonManager )
    {
        this.wagonManager = wagonManager;
    }

    public File download( String url, MessageHolder messageHolder )
        throws DownloadFailedException
    {
        return download( url, Collections.EMPTY_LIST, messageHolder );
    }

    public File download( String url, List transferListeners, MessageHolder messageHolder )
        throws DownloadFailedException
    {
        File downloaded = (File) cache.get( url );

        if ( downloaded != null && downloaded.exists() )
        {
            messageHolder.addMessage( "Using cached download: " + downloaded.getAbsolutePath() );

            return downloaded;
        }

        URL sourceUrl;
        try
        {
            sourceUrl = new URL( url );
        }
        catch ( MalformedURLException e )
        {
            throw new DownloadFailedException( url, "Download failed due to invalid URL. Reason: " + e.getMessage(), e );
        }

        Wagon wagon = null;

        // Retrieve the correct Wagon instance used to download the remote archive
        try
        {
            wagon = wagonManager.getWagon( sourceUrl.getProtocol() );
        }
        catch ( UnsupportedProtocolException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }

        messageHolder.addMessage( "Using wagon: " + wagon + " to download: " + url );

        try
        {
            // create the landing file in /tmp for the downloaded source archive
            downloaded = File.createTempFile( "download-", null );

            // delete when the JVM exits, to avoid polluting the temp dir...
            downloaded.deleteOnExit();
        }
        catch ( IOException e )
        {
            throw new DownloadFailedException( url, "Failed to create temporary file target for download. Reason: "
                + e.getMessage(), e );
        }

        messageHolder.addMessage( "Download target is: " + downloaded.getAbsolutePath() );

        // split the download URL into base URL and remote path for connecting, then retrieving.
        String remotePath = sourceUrl.getPath();
        String baseUrl = url.substring( 0, url.length() - remotePath.length() );

        for ( Iterator it = transferListeners.iterator(); it.hasNext(); )
        {
            TransferListener listener = (TransferListener) it.next();
            wagon.addTransferListener( listener );
        }

        // connect to the remote site, and retrieve the archive. Note the separate methods in which
        // base URL and remote path are used.
        Repository repo = new Repository( sourceUrl.getHost(), baseUrl );

        messageHolder.addMessage( "Connecting to: " + repo.getHost() + "(baseUrl: " + repo.getUrl() + ")" );

        try
        {
            wagon.connect( repo, wagonManager.getAuthenticationInfo( repo.getId() ), wagonManager.getProxy( sourceUrl
                .getProtocol() ) );
        }
        catch ( ConnectionException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }
        catch ( AuthenticationException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }

        messageHolder.addMessage( "Getting: " + remotePath );

        try
        {
            wagon.get( remotePath, downloaded );

            // cache this for later download requests to the same instance...
            cache.put( url, downloaded );

            return downloaded;
        }
        catch ( TransferFailedException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }
        catch ( ResourceDoesNotExistException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }
        catch ( AuthorizationException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }
        finally
        {
            // ensure the Wagon instance is closed out properly.
            if ( wagon != null )
            {
                try
                {
                    messageHolder.addMessage( "Disconnecting." );

                    wagon.disconnect();
                }
                catch ( ConnectionException e )
                {
                    messageHolder.addMessage( "Failed to disconnect wagon for: " + url, e );
                }

                for ( Iterator it = transferListeners.iterator(); it.hasNext(); )
                {
                    TransferListener listener = (TransferListener) it.next();
                    wagon.removeTransferListener( listener );
                }
            }
        }
    }

}
