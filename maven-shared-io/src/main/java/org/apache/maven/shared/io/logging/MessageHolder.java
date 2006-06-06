package org.apache.maven.shared.io.logging;

public interface MessageHolder
{
    
    MessageHolder newMessage();
    
    MessageHolder append( CharSequence messagePart );

    MessageHolder append( Throwable error );
    
    MessageHolder addMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addMessage( CharSequence messagePart );

    MessageHolder addMessage( Throwable error );
    
    int size();
    
    boolean isEmpty();
    
    String render();

}
