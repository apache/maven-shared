package org.apache.maven.shared.monitor;

/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public abstract class AbstractMonitor
    implements Monitor
{

    private boolean[] levelsEnabled = new boolean[5];

    protected AbstractMonitor()
    {
        levelsEnabled[ERROR_LEVEL] = true;
    }

    protected boolean isEnabled( int messageLevel )
    {
        return levelsEnabled[messageLevel];
    }

    public void setMessageLevel( int messageLevel )
    {
        // error level is always enabled
        for ( int i = 0; i <= messageLevel; i++ )
        {
            levelsEnabled[i] = true;
        }

        for ( int i = messageLevel + 1; i < levelsEnabled.length; i++ )
        {
            levelsEnabled[i] = false;
        }
    }

    public void setMessageLevelLabel( String messageLevel )
    {
        if ( messageLevel == null || messageLevel.trim().length() < 1 )
        {
            return;
        }

        for ( int i = 0; i < MESSAGE_LEVELS.length; i++ )
        {
            levelsEnabled[i] = true;

            if ( messageLevel.trim().toLowerCase().equals( MESSAGE_LEVELS[i] ) )
            {
                break;
            }
        }
    }

    public int getMessageLevel()
    {
        for ( int i = 0; i < levelsEnabled.length; i++ )
        {
            if ( !levelsEnabled[i] )
            {
                return i - 1;
            }
        }

        return 0;
    }

    public String getMessageLevelLabel()
    {
        for ( int i = 0; i < levelsEnabled.length; i++ )
        {
            if ( !levelsEnabled[i] )
            {
                if ( i == 0 )
                {
                    return null;
                }
                else
                {
                    return MESSAGE_LEVELS[i - 1];
                }
            }
        }

        return ERROR;
    }

    public boolean isVerboseEnabled()
    {
        return levelsEnabled[VERBOSE_LEVEL];
    }

    public boolean isDebugEnabled()
    {
        return levelsEnabled[DEBUG_LEVEL];
    }

    public boolean isInfoEnabled()
    {
        return levelsEnabled[INFO_LEVEL];
    }

    public boolean isWarnEnabled()
    {
        return levelsEnabled[WARN_LEVEL];
    }

    public boolean isErrorEnabled()
    {
        return levelsEnabled[ERROR_LEVEL];
    }

    public void setVerboseEnabled( boolean enabled )
    {
        this.levelsEnabled[VERBOSE_LEVEL] = enabled;
    }

    public void setDebugEnabled( boolean enabled )
    {
        this.levelsEnabled[DEBUG_LEVEL] = enabled;
    }

    public void setInfoEnabled( boolean enabled )
    {
        this.levelsEnabled[INFO_LEVEL] = enabled;
    }

    public void setWarnEnabled( boolean enabled )
    {
        this.levelsEnabled[WARN_LEVEL] = enabled;
    }

    public void setErrorEnabled( boolean enabled )
    {
        this.levelsEnabled[ERROR_LEVEL] = enabled;
    }

}
