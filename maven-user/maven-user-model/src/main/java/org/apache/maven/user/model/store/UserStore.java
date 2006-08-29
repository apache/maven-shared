package org.apache.maven.user.model.store;

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

import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;

/**
 * DAO to manage persistence of user related objects.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public interface UserStore
{

    User addUser( User user );

    UserGroup addUserGroup( UserGroup userGroup );

    User getUser( int accountId );

    User getUser( String username );

    User getGuestUser();

    UserGroup getUserGroup( int userGroupId );

    UserGroup getUserGroup( String name );

    List getUserGroups();

    List getUsers();

    void removeUser( int userId );

    void removeUser( String username );

    void removeUserGroup( int userGroupId );

    void removeUserGroup( String userGroupName );

    void updateUser( User user );

    void updateUserGroup( UserGroup userGroup );

    List getPermissions();

    Permission getPermission( String name );

    Permission addPermission( Permission perm );

}