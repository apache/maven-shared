package org.apache.maven.shared.io.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class MessageLevels
{

    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_ERROR = 3;
    public static final int LEVEL_SEVERE = 4;
    public static final int LEVEL_DISABLED = 5;
    
    private static final List LEVEL_NAMES;
    
    static
    {
        List names = new ArrayList();
        names.add( "DEBUG" );
        names.add( "INFO" );
        names.add( "WARN" );
        names.add( "ERROR" );
        names.add( "SEVERE" );
        
        LEVEL_NAMES = Collections.unmodifiableList( names );
    }
    
    private MessageLevels()
    {
    }
    
    public static boolean[] getLevelStates( int maxMessageLevel )
    {
        boolean[] states = new boolean[5];
        
        Arrays.fill( states, false );
        
        switch ( maxMessageLevel )
        {
        case (LEVEL_DEBUG): {
            states[LEVEL_DEBUG] = true;
        }
        case (LEVEL_INFO): {
            states[LEVEL_INFO] = true;
        }
        case (LEVEL_WARNING): {
            states[LEVEL_WARNING] = true;
        }
        case (LEVEL_ERROR): {
            states[LEVEL_ERROR] = true;
        }
        case (LEVEL_SEVERE): {
            states[LEVEL_SEVERE] = true;
        }
        }
        
        return states;
    }

    public static String getLevelLabel( int messageLevel )
    {
        if ( messageLevel > -1 && LEVEL_NAMES.size() > messageLevel )
        {
            return (String) LEVEL_NAMES.get( messageLevel );
        }
        
        throw new IllegalArgumentException( "Invalid message level: " + messageLevel );
    }
}
