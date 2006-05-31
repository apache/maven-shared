package org.apache.maven.shared.invoker;

public class SystemOutHandler
    implements InvocationOutputHandler
{
    
    private boolean alwaysFlush;
    
    public SystemOutHandler()
    {
    }

    public SystemOutHandler( boolean alwaysFlush )
    {
        this.alwaysFlush = alwaysFlush;
    }

    public void consumeLine( String line )
    {
        if ( line == null )
        {
            System.out.println();
        }
        else
        {
            System.out.println( line );
        }
        
        if ( alwaysFlush )
        {
            System.out.flush();
        }
    }

}
