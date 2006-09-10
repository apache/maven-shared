package org.apache.maven.user.model;

/*
 * Copyright 2006 The Apache Software Foundation.
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

/**
 * User instance permissions
 * 
 * @author <a href="mailto:hisidro@exist.com">Henry Isidro</a>
 */
public class InstancePermissions
{
    private User user;

    private boolean read, write, delete, execute, administer;

    public InstancePermissions()
    {
    }

    public InstancePermissions( User user )
    {
        this.user = user;
    }

    public boolean isExecute()
    {
        return execute;
    }

    public void setExecute( boolean execute )
    {
        this.execute = execute;
    }

    public boolean isDelete()
    {
        return delete;
    }

    public void setDelete( boolean delete )
    {
        this.delete = delete;
    }

    public boolean isWrite()
    {
        return write;
    }

    public void setWrite( boolean write )
    {
        this.write = write;
    }

    public boolean isRead()
    {
        return read;
    }

    public void setRead( boolean read )
    {
        this.read = read;
    }

    public boolean isAdminister()
    {
        return administer;
    }

    public void setAdminister( boolean administer )
    {
        this.administer = administer;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( getUser().getUsername() );
        sb.append( ": " );

        char[] permissions = "-----".toCharArray();
        if ( isRead() )
        {
            permissions[0] = 'r';
        }
        if ( isWrite() )
        {
            permissions[1] = 'w';
        }
        if ( isDelete() )
        {
            permissions[2] = 'd';
        }
        if ( isExecute() )
        {
            permissions[3] = 'x';
        }
        if ( isAdminister() )
        {
            permissions[4] = 'a';
        }

        sb.append( permissions );
        return sb.toString();
    }

    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }
        if ( !( other instanceof InstancePermissions ) )
        {
            return false;
        }
        InstancePermissions that = (InstancePermissions) other;
        boolean result = getUser().equals( that.getUser() );
        result &= isRead() == that.isRead();
        result &= isWrite() == that.isWrite();
        result &= isDelete() == that.isDelete();
        result &= isExecute() == that.isExecute();
        result &= isAdminister() == that.isAdminister();
        return result;
    }
}
