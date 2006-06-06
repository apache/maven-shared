package org.apache.maven.shared.io.location;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.shared.io.logging.MessageHolder;

public class UrlBasedLocatorStrategy
    implements LocatorStrategy
{

    private String tempFilePrefix = "location.";

    private String tempFileSuffix = ".url";

    private boolean tempFileDeleteOnExit = true;

    public UrlBasedLocatorStrategy()
    {
    }

    public UrlBasedLocatorStrategy( String tempFilePrefix, String tempFileSuffix, boolean tempFileDeleteOnExit )
    {
        this.tempFilePrefix = tempFilePrefix;
        this.tempFileSuffix = tempFileSuffix;
        this.tempFileDeleteOnExit = tempFileDeleteOnExit;
    }

    public Location resolve( String locationSpecification, MessageHolder messageHolder )
    {
        Location location = null;

        messageHolder.append( "Building URL from location: " + locationSpecification );
        try
        {
            URL url = new URL( locationSpecification );

            location = new URLLocation( url, locationSpecification, tempFilePrefix, tempFileSuffix,
                                        tempFileDeleteOnExit );
        }
        catch ( MalformedURLException e )
        {
            messageHolder.append( e );
        }

        return location;
    }

}
