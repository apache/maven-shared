package org.apache.maven.shared.io.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DefaultMessageHolder
    implements MessageHolder
{
    
    private List messages = new ArrayList();
    private Message currentMessage;

    public MessageHolder addMessage( CharSequence messagePart, Throwable error )
    {
        newMessage();
        append( messagePart );
        append( error );
        
        return this;
    }

    public MessageHolder addMessage( CharSequence messagePart )
    {
        newMessage();
        append( messagePart );
        
        return this;
    }

    public MessageHolder addMessage( Throwable error )
    {
        newMessage();
        append( error );
        
        return this;
    }

    public MessageHolder append( CharSequence messagePart )
    {
        if ( currentMessage == null )
        {
            newMessage();
        }        
        
        currentMessage.append( messagePart );
        
        return this;
    }

    public MessageHolder append( Throwable error )
    {
        if ( currentMessage == null )
        {
            newMessage();
        }        
        
        currentMessage.setError( error );
        
        return this;
    }

    public boolean isEmpty()
    {
        return messages.isEmpty();
    }

    public MessageHolder newMessage()
    {
        currentMessage = new Message();
        messages.add( currentMessage );
        
        return this;
    }

    public String render()
    {
        StringBuffer buffer = new StringBuffer();
        
        int counter = 1;
        for ( Iterator it = messages.iterator(); it.hasNext(); )
        {
            Message message = (Message) it.next();
            
            CharSequence content = message.render();
            
            if ( content.length() > 0 )
            {
                buffer.append( '[' ).append( counter++ ).append( "] " );
                buffer.append( content );
                
                if ( it.hasNext() )
                {
                    buffer.append( "\n\n" );
                }
            }
        }
        
        return buffer.toString();
    }

    public int size()
    {
        return messages.size();
    }
    
    private static final class Message
    {
        private StringBuffer message = new StringBuffer();
        private Throwable error;
        
        public Message setError( Throwable error )
        {
            this.error = error;
            return this;
        }
        
        public Message append( CharSequence message )
        {
            this.message.append( message );
            return this;
        }
        
        public CharSequence render()
        {
            StringBuffer buffer = new StringBuffer();
            
            if ( message != null && message.length() > 0 )
            {
                buffer.append( message );
                
                if ( error != null )
                {
                    buffer.append( '\n' );
                }
            }
            
            if ( error != null )
            {
                buffer.append( "Error:\n" );
                
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter( sw );
                error.printStackTrace( pw );
                
                buffer.append( sw.toString() );
            }
            
            return buffer;
        }
    }

}
