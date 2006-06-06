package org.apache.maven.shared.io.location;

import org.apache.maven.shared.io.logging.MessageHolder;

public interface LocatorStrategy
{
    
    Location resolve( String locationSpecification, MessageHolder messageHolder );

}
