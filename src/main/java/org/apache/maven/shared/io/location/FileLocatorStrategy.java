package org.apache.maven.shared.io.location;

import java.io.File;

import org.apache.maven.shared.io.logging.MessageHolder;

public class FileLocatorStrategy
    implements LocatorStrategy
{

    public Location resolve( String locationSpecification, MessageHolder messageHolder )
    {
        File file = new File( locationSpecification );
        
        Location location = null;
        
        if ( file.exists() )
        {
            location = new FileLocation( file, locationSpecification );
        }
        else
        {
            messageHolder.addMessage( "File: " + file.getAbsolutePath() + " does not exist." );
        }
        
        return location;
    }

}
