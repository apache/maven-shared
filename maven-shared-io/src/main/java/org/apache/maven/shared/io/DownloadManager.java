package org.apache.maven.shared.io;

import java.io.File;
import java.util.List;

public interface DownloadManager
{

    File download( String url )
        throws DownloadFailedException;

    File download( String url, List transferListeners )
        throws DownloadFailedException;

}