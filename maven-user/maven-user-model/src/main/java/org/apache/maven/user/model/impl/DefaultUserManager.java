package org.apache.maven.user.model.impl;

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

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.apache.maven.user.model.PasswordEncoder;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;

/**
 * Default implementation of the {@link UserManager} interface.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class DefaultUserManager
    implements UserManager
{

    public void addUser( User user )
        throws EntityExistsException
    {
        // TODO Auto-generated method stub

    }

    public void addUserGroup( UserGroup userGroup )
        throws EntityExistsException
    {
        // TODO Auto-generated method stub

    }

    public PasswordEncoder getPasswordEncoder()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public User getUser( int userId )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserGroup getUserGroup( int userGroupId )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getUserGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getUsers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeUser( int userId )
        throws EntityNotFoundException
    {
        // TODO Auto-generated method stub

    }

    public void removeUserGroup( int userGroupId )
        throws EntityNotFoundException
    {
        // TODO Auto-generated method stub

    }

    public void setPasswordEncoder( PasswordEncoder passwordEncoder )
    {
        // TODO Auto-generated method stub

    }

    public void updateUser( User user )
        throws EntityNotFoundException
    {
        // TODO Auto-generated method stub

    }

    public void updateUserGroup( UserGroup userGroup )
        throws EntityNotFoundException
    {
        // TODO Auto-generated method stub

    }

}
