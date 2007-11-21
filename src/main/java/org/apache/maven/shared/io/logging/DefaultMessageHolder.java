package org.apache.maven.shared.io.logging;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

    private int defaultMessageLevel = MessageLevels.LEVEL_INFO;

    private boolean[] messageLevelStates;

    private MessageSink onDemandSink;

    public DefaultMessageHolder()
    {
        this.messageLevelStates = MessageLevels.getLevelStates( MessageLevels.LEVEL_INFO );
    }

    public DefaultMessageHolder( int maxMessageLevel, int defaultMessageLevel )
    {
        this.defaultMessageLevel = defaultMessageLevel;
        this.messageLevelStates = MessageLevels.getLevelStates( maxMessageLevel );
    }

    public DefaultMessageHolder( int maxMessageLevel, int defaultMessageLevel, MessageSink onDemandSink )
    {
        this.defaultMessageLevel = defaultMessageLevel;
        this.onDemandSink = onDemandSink;
        this.messageLevelStates = MessageLevels.getLevelStates( maxMessageLevel );
    }

    public MessageHolder addMessage( CharSequence messagePart, Throwable error )
    {
        return addMessage( defaultMessageLevel, messagePart, error );
    }

    protected MessageHolder addMessage( int level, CharSequence messagePart, Throwable error )
    {
        newMessage( level );
        append( messagePart.toString() );
        append( error );

        return this;
    }

    public MessageHolder addMessage( CharSequence messagePart )
    {
        return addMessage( defaultMessageLevel, messagePart );
    }

    protected MessageHolder addMessage( int level, CharSequence messagePart )
    {
        newMessage( level );
        append( messagePart.toString() );

        return this;
    }

    public MessageHolder addMessage( Throwable error )
    {
        return addMessage( defaultMessageLevel, error );
    }

    protected MessageHolder addMessage( int level, Throwable error )
    {
        newMessage( level );
        append( error );

        return this;
    }

    public MessageHolder append( CharSequence messagePart )
    {
        if ( currentMessage == null )
        {
            newMessage();
        }

        currentMessage.append( messagePart.toString() );

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
        newMessage( defaultMessageLevel );

        return this;
    }

    protected void newMessage( int messageLevel )
    {
        if ( onDemandSink != null && currentMessage != null )
        {
            renderTo( currentMessage, onDemandSink );
        }

        currentMessage = new Message( messageLevel, onDemandSink );
        messages.add( currentMessage );
    }

    public String render()
    {
        StringBuffer buffer = new StringBuffer();

        int counter = 1;
        for ( Iterator it = messages.iterator(); it.hasNext(); )
        {
            Message message = (Message) it.next();

            int ml = message.getMessageLevel();

            if ( ml >= messageLevelStates.length || ml < 0 )
            {
                ml = MessageLevels.LEVEL_DEBUG;
            }

            if ( !messageLevelStates[ml] )
            {
                continue;
            }

            CharSequence content = message.render();
            String label = MessageLevels.getLevelLabel( message.getMessageLevel() );

            if ( content.length() > label.length() + 3 )
            {
                buffer.append( '[' ).append( counter++ ).append( "] " );
                buffer.append( content.toString() );

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

        private final int messageLevel;

        private final MessageSink onDemandSink;

        public Message( int messageLevel, MessageSink onDemandSink )
        {
            this.messageLevel = messageLevel;

            this.onDemandSink = onDemandSink;
        }

        public Message setError( Throwable error )
        {
            this.error = error;
            return this;
        }

        public Message append( CharSequence message )
        {
            this.message.append( message.toString() );
            return this;
        }

        public int getMessageLevel()
        {
            return messageLevel;
        }

        public CharSequence render()
        {
            StringBuffer buffer = new StringBuffer();

            if ( onDemandSink == null )
            {
                buffer.append( '[' ).append( MessageLevels.getLevelLabel( messageLevel ) ).append( "] " );
            }
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

    public MessageHolder addDebugMessage( CharSequence messagePart, Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_DEBUG, messagePart, error );
    }

    public MessageHolder addDebugMessage( CharSequence messagePart )
    {
        return addMessage( MessageLevels.LEVEL_DEBUG, messagePart );
    }

    public MessageHolder addDebugMessage( Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_DEBUG, error );
    }

    public MessageHolder addErrorMessage( CharSequence messagePart, Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_ERROR, messagePart, error );
    }

    public MessageHolder addErrorMessage( CharSequence messagePart )
    {
        return addMessage( MessageLevels.LEVEL_ERROR, messagePart );
    }

    public MessageHolder addErrorMessage( Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_ERROR, error );
    }

    public MessageHolder addInfoMessage( CharSequence messagePart, Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_INFO, messagePart, error );
    }

    public MessageHolder addInfoMessage( CharSequence messagePart )
    {
        return addMessage( MessageLevels.LEVEL_INFO, messagePart );
    }

    public MessageHolder addInfoMessage( Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_INFO, error );
    }

    public MessageHolder addSevereMessage( CharSequence messagePart, Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_SEVERE, messagePart, error );
    }

    public MessageHolder addSevereMessage( CharSequence messagePart )
    {
        return addMessage( MessageLevels.LEVEL_SEVERE, messagePart );
    }

    public MessageHolder addSevereMessage( Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_SEVERE, error );
    }

    public MessageHolder addWarningMessage( CharSequence messagePart, Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_WARNING, messagePart, error );
    }

    public MessageHolder addWarningMessage( CharSequence messagePart )
    {
        return addMessage( MessageLevels.LEVEL_WARNING, messagePart );
    }

    public MessageHolder addWarningMessage( Throwable error )
    {
        return addMessage( MessageLevels.LEVEL_WARNING, error );
    }

    public int countDebugMessages()
    {
        return countMessagesOfType( MessageLevels.LEVEL_DEBUG );
    }

    public int countErrorMessages()
    {
        return countMessagesOfType( MessageLevels.LEVEL_ERROR );
    }

    public int countInfoMessages()
    {
        return countMessagesOfType( MessageLevels.LEVEL_INFO );
    }

    public int countMessages()
    {
        return size();
    }

    public int countSevereMessages()
    {
        return countMessagesOfType( MessageLevels.LEVEL_SEVERE );
    }

    public int countWarningMessages()
    {
        return countMessagesOfType( MessageLevels.LEVEL_WARNING );
    }

    private int countMessagesOfType( int messageLevel )
    {
        int count = 0;

        for ( Iterator it = messages.iterator(); it.hasNext(); )
        {
            Message message = (Message) it.next();
            if ( messageLevel == message.getMessageLevel() )
            {
                count++;
            }
        }

        return count;
    }

    public boolean isDebugEnabled()
    {
        return messageLevelStates[MessageLevels.LEVEL_DEBUG];
    }

    public boolean isErrorEnabled()
    {
        return messageLevelStates[MessageLevels.LEVEL_ERROR];
    }

    public boolean isInfoEnabled()
    {
        return messageLevelStates[MessageLevels.LEVEL_INFO];
    }

    public boolean isSevereEnabled()
    {
        return messageLevelStates[MessageLevels.LEVEL_SEVERE];
    }

    public boolean isWarningEnabled()
    {
        return messageLevelStates[MessageLevels.LEVEL_WARNING];
    }

    public MessageHolder newDebugMessage()
    {
        if ( isDebugEnabled() )
        {
            newMessage( MessageLevels.LEVEL_DEBUG );
        }

        return this;
    }

    public MessageHolder newErrorMessage()
    {
        if ( isErrorEnabled() )
        {
            newMessage( MessageLevels.LEVEL_ERROR );
        }

        return this;
    }

    public MessageHolder newInfoMessage()
    {
        if ( isInfoEnabled() )
        {
            newMessage( MessageLevels.LEVEL_INFO );
        }

        return this;
    }

    public MessageHolder newSevereMessage()
    {
        if ( isSevereEnabled() )
        {
            newMessage( MessageLevels.LEVEL_SEVERE );
        }

        return this;
    }

    public MessageHolder newWarningMessage()
    {
        if ( isWarningEnabled() )
        {
            newMessage( MessageLevels.LEVEL_WARNING );
        }

        return this;
    }

    public void setDebugEnabled( boolean enabled )
    {
        messageLevelStates[MessageLevels.LEVEL_DEBUG] = enabled;
    }

    public void setErrorEnabled( boolean enabled )
    {
        messageLevelStates[MessageLevels.LEVEL_ERROR] = enabled;
    }

    public void setInfoEnabled( boolean enabled )
    {
        messageLevelStates[MessageLevels.LEVEL_INFO] = enabled;
    }

    public void setSevereEnabled( boolean enabled )
    {
        messageLevelStates[MessageLevels.LEVEL_SEVERE] = enabled;
    }

    public void setWarningEnabled( boolean enabled )
    {
        messageLevelStates[MessageLevels.LEVEL_WARNING] = enabled;
    }

    public void flush()
    {
        if ( onDemandSink != null && currentMessage != null )
        {
            renderTo( currentMessage, onDemandSink );
            currentMessage = null;
        }
    }

    public void render( MessageSink sink )
    {
        for ( Iterator it = messages.iterator(); it.hasNext(); )
        {
            Message message = (Message) it.next();

            renderTo( message, sink );
        }
    }

    protected void renderTo( Message message, MessageSink sink )
    {
        switch( message.getMessageLevel() )
        {
        case( MessageLevels.LEVEL_SEVERE ):
        {
            sink.severe( message.render().toString() );
            break;
        }
        case( MessageLevels.LEVEL_ERROR ):
        {
            sink.error( message.render().toString() );
            break;
        }
        case( MessageLevels.LEVEL_WARNING ):
        {
            sink.warning( message.render().toString() );
            break;
        }
        case( MessageLevels.LEVEL_INFO ):
        {
            sink.info( message.render().toString() );
            break;
        }
        default:
        {
            sink.debug( message.render().toString() );
            break;
        }
        }
    }

}
