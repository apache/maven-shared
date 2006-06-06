package org.apache.maven.shared.io.download;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.repository.Repository;

public class DefaultDownloadManager implements DownloadManager
{
    
    private WagonManager wagonManager;
    
    public DefaultDownloadManager()
    {
    }
    
    public DefaultDownloadManager( WagonManager wagonManager )
    {
        this.wagonManager = wagonManager;
    }
    
    public File download( String url ) throws DownloadFailedException
    {
        return download( url, Collections.EMPTY_LIST );
    }
    
    public File download( String url, List transferListeners ) throws DownloadFailedException
    {
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

        try
        {
            // Retrieve the correct Wagon instance used to download the remote archive
            wagon = wagonManager.getWagon( sourceUrl.getProtocol() );

            // create the landing file in /tmp for the downloaded source archive
            File downloaded = File.createTempFile( "source-archive-", null );
            downloaded.deleteOnExit();

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
            
            wagon.connect( repo, wagonManager.getAuthenticationInfo( repo.getId() ), wagonManager.getProxy( sourceUrl
                .getProtocol() ) );
            
            wagon.get( remotePath, downloaded );
            
            return downloaded;
        }
        catch ( UnsupportedProtocolException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
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
        catch ( ConnectionException e )
        {
            throw new DownloadFailedException( url, "Download failed. Reason: " + e.getMessage(), e );
        }
        catch ( AuthenticationException e )
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
                    wagon.disconnect();
                }
                catch ( ConnectionException e )
                {
//                    getLog().debug( "Failed to disconnect wagon for: " + url, e );
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
