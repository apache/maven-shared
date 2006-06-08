package org.apache.maven.shared.io.location;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.shared.io.logging.MessageHolder;

public class URLLocatorStrategy
    implements LocatorStrategy
{

    private String tempFilePrefix = "location.";

    private String tempFileSuffix = ".url";

    private boolean tempFileDeleteOnExit = true;

    public URLLocatorStrategy()
    {
    }

    public URLLocatorStrategy( String tempFilePrefix, String tempFileSuffix, boolean tempFileDeleteOnExit )
    {
        this.tempFilePrefix = tempFilePrefix;
        this.tempFileSuffix = tempFileSuffix;
        this.tempFileDeleteOnExit = tempFileDeleteOnExit;
    }

    public Location resolve( String locationSpecification, MessageHolder messageHolder )
    {
        Location location = null;

        try
        {
            URL url = new URL( locationSpecification );

            location = new URLLocation( url, locationSpecification, tempFilePrefix, tempFileSuffix,
                                        tempFileDeleteOnExit );
        }
        catch ( MalformedURLException e )
        {
            messageHolder.addMessage( "Building URL from location: " + locationSpecification, e );
        }

        return location;
    }

}
