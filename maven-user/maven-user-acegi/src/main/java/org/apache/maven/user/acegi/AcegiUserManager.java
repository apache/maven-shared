package org.apache.maven.user.acegi;

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

import java.util.Collection;
import java.util.List;

import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.apache.maven.user.model.UserSecurityPolicy;

/**
 * {@link UserManager} that will add Acegi required functionality and delegate to another implementation of {@link UserManager}. 
 * 
 * @plexus.component role="org.apache.maven.user.model.UserManager" role-hint="acegi"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AcegiUserManager
    implements UserManager
{

    /**
     * @plexus.requirement role-hint="default"
     */
    private UserManager userManager;

    /**
     * @plexus.requirement
     */
    private AclManager aclManager;

    public void setUserManager( UserManager userManager )
    {
        this.userManager = userManager;
    }

    public UserManager getUserManager()
    {
        return userManager;
    }

    public void setAclManager( AclManager aclManager )
    {
        this.aclManager = aclManager;
    }

    public AclManager getAclManager()
    {
        return aclManager;
    }

    public List getUsersInstancePermissions( Class clazz, Object id )
    {
        List userPermissions = getUserManager().getUsersInstancePermissions( clazz, id );
        return getAclManager().getUsersInstancePermissions( clazz, id, userPermissions );
    }

    public void setUsersInstancePermissions( Collection permissions )
    {
        getAclManager().setUsersInstancePermissions( permissions );
    }

    //-----------------------------------------------------------------------
    // delegation methods
    //-----------------------------------------------------------------------

    public Permission addPermission( Permission perm )
    {
        return getUserManager().addPermission( perm );
    }

    public User addUser( User user )
        throws PasswordRuleViolationException
    {
        return getUserManager().addUser( user );
    }

    public boolean changePassword( String userName, String oldPassword, String newPassword )
    {
        return getUserManager().changePassword( userName, oldPassword, newPassword );
    }

    public UserGroup addUserGroup( UserGroup userGroup )
    {
        return getUserManager().addUserGroup( userGroup );
    }

    public User getGuestUser()
    {
        return getUserManager().getGuestUser();
    }

    public Permission getPermission( String name )
    {
        return getUserManager().getPermission( name );
    }

    public List getPermissions()
    {
        return getUserManager().getPermissions();
    }

    public User getUser( int accountId )
    {
        return getUserManager().getUser( accountId );
    }

    public User getUser( String username )
    {
        return getUserManager().getUser( username );
    }

    public User getUserByUsername( String username )
    {
        return getUserManager().getUserByUsername( username );
    }

    public UserGroup getUserGroup( int userGroupId )
    {
        return getUserManager().getUserGroup( userGroupId );
    }

    public UserGroup getUserGroup( String name )
    {
        return getUserManager().getUserGroup( name );
    }

    public List getUserGroups()
    {
        return getUserManager().getUserGroups();
    }

    public List getUsers()
    {
        return getUserManager().getUsers();
    }

    public boolean login( String username, String rawpassword )
    {
        return getUserManager().login( username, rawpassword );
    }

    public void loginFailed( String username )
    {
        getUserManager().loginFailed( username );
    }

    public void loginSuccessful( String username )
    {
        getUserManager().loginSuccessful( username );
    }

    public void removeUser( int accountId )
    {
        getUserManager().removeUser( accountId );
    }

    public void removeUser( String username )
    {
        getUserManager().removeUser( username );
    }

    public void removeUserGroup( int userGroupId )
    {
        getUserManager().removeUserGroup( userGroupId );
    }

    public void removeUserGroup( String name )
    {
        getUserManager().removeUserGroup( name );
    }

    public void updateUser( User user )
        throws PasswordRuleViolationException
    {
        getUserManager().updateUser( user );
    }

    public void updateUserGroup( UserGroup userGroup )
    {
        getUserManager().updateUserGroup( userGroup );
    }

    public User getMyUser()
    {
        return getUserManager().getMyUser();
    }

    public UserSecurityPolicy getSecurityPolicy()
    {
        return getUserManager().getSecurityPolicy();
    }

    public UserGroup getDefaultUserGroup()
    {
        return getUserManager().getDefaultUserGroup();
    }
}
