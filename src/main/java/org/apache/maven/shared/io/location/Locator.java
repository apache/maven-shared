package org.apache.maven.shared.io.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

public final class Locator
{
    
    private List strategies;
    private final MessageHolder messageHolder;
    
    public Locator( List strategies, MessageHolder messageHolder )
    {
        this.messageHolder = messageHolder;
        this.strategies = new ArrayList( strategies );
    }
    
    public Locator()
    {
        this.messageHolder = new DefaultMessageHolder();
        this.strategies = new ArrayList();
    }
    
    public MessageHolder getMessageHolder()
    {
        return messageHolder;
    }
    
    public void addStrategy( LocatorStrategy strategy )
    {
        this.strategies.add( strategy );
    }

    public void removeStrategy( LocatorStrategy strategy )
    {
        this.strategies.remove( strategy );
    }
    
    public void setStrategies( List strategies )
    {
        this.strategies.clear();
        this.strategies.addAll( strategies );
    }

    public List getStrategies()
    {
        return strategies;
    }
    
    public Location resolve( String locationSpecification )
    {
        Location location = null;
        
        for ( Iterator it = strategies.iterator(); location == null && it.hasNext(); )
        {
            LocatorStrategy strategy = (LocatorStrategy) it.next();
            
            location = strategy.resolve( locationSpecification, messageHolder );
        }
        
        return location;
    }
    
}
