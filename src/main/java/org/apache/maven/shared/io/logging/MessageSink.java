package org.apache.maven.shared.io.logging;


public interface MessageSink
{
    
    void debug( String message );
    
    void info( String message );
    
    void warning( String message );
    
    void error( String message );
    
    void severe( String message );

}
