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

import java.util.Collection;
import java.util.List;

/**
 * Facade for user related operations.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * 
 * @todo policy: new user verification (no password, email verification link and/or uniq code, generate password)
 * @todo policy: user self service password reset (request, send email, click on link and/or supply uniq code, password reset)
 */
public interface UserManager
{
    public static final String ROLE = UserManager.class.getName();

    // ----------------------------------------------------------------------
    // User
    // ----------------------------------------------------------------------

    /**
     * Add a new user. User password may be encoded before storing it.
     * 
     * @param user
     */
    User addUser( User user )
        throws PasswordRuleViolationException;

    /**
     * Change user password.
     * 
     * @param userName
     * @param oldPassword current password of the user
     * @param newPassword new password of the user
     * @return <code>true</code> if the old password matches the oldPassword parameter,
     * <code>false</code> otherwise.
     */
    boolean changePassword( String userName, String oldPassword, String newPassword );

    /**
     * Update user data. User password may be encoded before storing it.
     * 
     * @param user
     */
    void updateUser( User user )
        throws PasswordRuleViolationException;

    /**
     * Get all users. Users password won't be returned for security reasons.
     * 
     * @return all users in the system
     */
    List getUsers();

    /**
     * <p>
     * Get a user by id. User password won't be returned for security reasons.
     * </p>
     * 
     * <p>
     * NOTE: The User.accountId value is set by the underlying UserManager implementation.
     * </p>
     * 
     * @param accountId
     * @return null if the user doesn't exist
     */
    User getUser( int accountId );

    /**
     * Get a user by name. User password won't be returned for security reasons.
     * 
     * @param username
     * @return null if the user doesn't exist
     * @deprecated use {@link #getUser(String)} instead.
     */
    User getUserByUsername( String username );

    /**
     * Get a user by name. User password won't be returned for security reasons.
     * 
     * @param username
     * @return null if the user doesn't exist
     */
    User getUser( String username );

    /**
     * Get guest user
     * 
     * @return null if the user doesn't exist
     */
    User getGuestUser();

    /**
     * Delete a user
     * 
     * <p>
     * NOTE: The User.accountId value is set by the underlying UserManager implementation.
     * </p>
     * 
     * @param accountId
     */
    void removeUser( int accountId );

    /**
     * Delete a user
     * 
     * @param username
     */
    void removeUser( String username );

    // ----------------------------------------------------------------------
    // Login
    // ----------------------------------------------------------------------

    /**
     * Perform login attempt to see if username and password are valid. 
     * 
     * @deprecated use other services like maven-user-acegi to log in 
     * 
     * @param username
     * @param rawpassword
     * @return true if user is able to log in. false if username or password is invalid.
     */
    boolean login( String username, String rawpassword );

    /**
     * Do required operations on a failed login, like increase the number of login attemps.
     * 
     * @param username user name of the user that attempted to log in
     */
    void loginFailed( String username );

    /**
     * Do required operations on a successful login, like reset the number of login attemps.
     * 
     * @param username user name of the user that logged in
     */
    void loginSuccessful( String username );

    // ----------------------------------------------------------------------
    // User Group
    // ----------------------------------------------------------------------

    /**
     * Add a new user group
     * 
     * @param userGroup
     */
    UserGroup addUserGroup( UserGroup userGroup );

    /**
     * Update an existing UserGroup.
     * 
     * @param userGroup
     */
    void updateUserGroup( UserGroup userGroup );

    /**
     * Get all UserGroup's.
     * 
     * @return all the user groups in the system
     */
    List getUserGroups();

    /**
     * Get UserGroup by id.
     * 
     * <p>
     * NOTE: The UserGroup.id value is set by the underlying UserManager implementation.
     * </p>
     *  
     * @param userGroupId
     * @return null if the group doesn't exist
     */
    UserGroup getUserGroup( int userGroupId );

    /**
     * Get the {@link UserGroup} by name
     * 
     * @param name
     * @return null if the group doesn't exist
     */
    UserGroup getUserGroup( String name );

    /**
     * <p>
     * Remove the UserGroup using the UserGroup.id value.
     * </p>
     * 
     * <p>
     * NOTE: The UserGroup.id value is set by the underlying UserManager implementation.
     * </p> 
     * 
     * @param userGroupId
     */
    void removeUserGroup( int userGroupId );

    /**
     * Remove the named UserGroup
     * 
     * @param name the user group name to remove
     */
    void removeUserGroup( String name );

    // ----------------------------------------------------------------------
    // Permissions
    // ----------------------------------------------------------------------

    /**
     * Get all the available permissions
     * 
     * @return all permissions defined in system
     */
    List getPermissions();

    /**
     * Get the permission for a user
     * 
     * @return user's permission
     */
    Permission getPermission( String name );

    /**
     * Add a permission
     * 
     * @return permission added
     */
    Permission addPermission( Permission perm );

    /**
     * Get all users instance permissions for an object ( identified by its class and id )
     * 
     * @param clazz {@link Class} of the object
     * @param id identifier of the object
     * @return {@link List} &lt; {@link InstancePermissions} >
     */
    List getUsersInstancePermissions( Class clazz, Object id );

    /**
     * Set users instance permissions for a bunch of objects.
     * 
     * @param permissions {@link Collection} &lt;{@link InstancePermissions}> .
     * Each {@link InstancePermissions}.user only needs to have username, no other properties are required.
     */
    void setUsersInstancePermissions( Collection permissions );

    /**
     * Get current user
     * 
     * @return null if the user doesn't exist
     */
    User getMyUser();

    /**
     * Gets the Security Policy to use.
     * 
     * @return the security policy.
     */
    UserSecurityPolicy getSecurityPolicy();

    /**
     * Gets the default user group.
     * 
     * @return the default user group.
     */
    UserGroup getDefaultUserGroup();
}
