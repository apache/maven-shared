package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.util.FileUtils;

public class URLLocation
    extends FileLocation
{

    private final URL url;

    private final String tempFilePrefix;

    private final String tempFileSuffix;

    private final boolean tempFileDeleteOnExit;

    public URLLocation( URL url, String specification, String tempFilePrefix, String tempFileSuffix,
                        boolean tempFileDeleteOnExit )
    {
        super( specification );

        this.url = url;
        this.tempFilePrefix = tempFilePrefix;
        this.tempFileSuffix = tempFileSuffix;
        this.tempFileDeleteOnExit = tempFileDeleteOnExit;
    }

    protected void initFile()
        throws IOException
    {
        // TODO: Log this in the debug log-level...
        if ( unsafeGetFile() == null )
        {
            File tempFile = File.createTempFile( tempFilePrefix, tempFileSuffix );

            if ( tempFileDeleteOnExit )
            {
                tempFile.deleteOnExit();
            }

            FileUtils.copyURLToFile( url, tempFile );
            
            setFile( tempFile );
        }
    }

}
