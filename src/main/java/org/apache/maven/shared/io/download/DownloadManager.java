package org.apache.maven.shared.io.download;

import java.io.File;
import java.util.List;

import org.apache.maven.shared.io.logging.MessageHolder;

public interface DownloadManager
{
    String ROLE = DownloadManager.class.getName();

    File download( String url, MessageHolder messageHolder )
        throws DownloadFailedException;

    File download( String url, List transferListeners, MessageHolder messageHolder )
        throws DownloadFailedException;

}