package org.apache.maven.shared.io.logging;


public interface MessageHolder
{
    
    MessageHolder newMessage();
    
    MessageHolder newDebugMessage();
    
    MessageHolder newInfoMessage();
    
    MessageHolder newWarningMessage();
    
    MessageHolder newErrorMessage();
    
    MessageHolder newSevereMessage();
    
    MessageHolder append( CharSequence messagePart );

    MessageHolder append( Throwable error );
    
    MessageHolder addMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addMessage( CharSequence messagePart );

    MessageHolder addMessage( Throwable error );
    
    MessageHolder addDebugMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addDebugMessage( CharSequence messagePart );

    MessageHolder addDebugMessage( Throwable error );
    
    MessageHolder addInfoMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addInfoMessage( CharSequence messagePart );

    MessageHolder addInfoMessage( Throwable error );
    
    MessageHolder addWarningMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addWarningMessage( CharSequence messagePart );

    MessageHolder addWarningMessage( Throwable error );
    
    MessageHolder addErrorMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addErrorMessage( CharSequence messagePart );

    MessageHolder addErrorMessage( Throwable error );
    
    MessageHolder addSevereMessage( CharSequence messagePart, Throwable error );
    
    MessageHolder addSevereMessage( CharSequence messagePart );

    MessageHolder addSevereMessage( Throwable error );
    
    int size();
    
    int countMessages();
    
    int countDebugMessages();
    
    int countInfoMessages();
    
    int countWarningMessages();
    
    int countErrorMessages();
    
    int countSevereMessages();
    
    boolean isDebugEnabled();
    
    void setDebugEnabled( boolean enabled );
    
    boolean isInfoEnabled();
    
    void setInfoEnabled( boolean enabled );
    
    boolean isWarningEnabled();
    
    void setWarningEnabled( boolean enabled );
    
    boolean isErrorEnabled();
    
    void setErrorEnabled( boolean enabled );
    
    boolean isSevereEnabled();
    
    void setSevereEnabled( boolean enabled );
    
    boolean isEmpty();
    
    String render();
    
    void render( MessageSink sink );
    
    void flush();

}
