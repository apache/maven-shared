package org.apache.maven.shared.io.location;

import java.io.File;
import java.io.IOException;

import org.apache.maven.shared.io.logging.MessageHolder;

public class FileLocatorStrategy
    implements LocatorStrategy
{

    public Location resolve( String locationSpecification, MessageHolder messageHolder )
    {
        File file = new File( locationSpecification );
        
        try
        {
            File canFile = file.getCanonicalFile();
            file = canFile;
        }
        catch ( IOException e )
        {
            messageHolder.addMessage( "Failed to canonicalize: " + file.getAbsolutePath(), e );
        }
        
        Location location = null;
        
        if ( file.exists() )
        {
            location = new FileBasedLocation( file, locationSpecification );
        }
        else
        {
            messageHolder.addMessage( "File: " + file.getAbsolutePath() + " does not exist." );
        }
        
        return location;
    }

}
