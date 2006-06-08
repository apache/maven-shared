package org.apache.maven.shared.io.location;

import java.net.URL;

import org.apache.maven.shared.io.logging.MessageHolder;

public class ClasspathResourceLocatorStrategy
    implements LocatorStrategy
{

    private String tempFilePrefix = "location.";

    private String tempFileSuffix = ".cpurl";

    private boolean tempFileDeleteOnExit = true;

    public ClasspathResourceLocatorStrategy()
    {
    }

    public ClasspathResourceLocatorStrategy( String tempFilePrefix, String tempFileSuffix, boolean tempFileDeleteOnExit )
    {
        this.tempFilePrefix = tempFilePrefix;
        this.tempFileSuffix = tempFileSuffix;
        this.tempFileDeleteOnExit = tempFileDeleteOnExit;
    }

    public Location resolve( String locationSpecification, MessageHolder messageHolder )
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();

        URL resource = cloader.getResource( locationSpecification );

        Location location = null;

        if ( resource != null )
        {
            location = new URLLocation( resource, locationSpecification, tempFilePrefix, tempFileSuffix,
                                        tempFileDeleteOnExit );
        }
        else
        {
            messageHolder.addMessage( "Failed to resolve classpath resource: " + locationSpecification
                + " from classloader: " + cloader );
        }

        return location;
    }

}
