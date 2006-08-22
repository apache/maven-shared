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

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

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
     * Add a new user. User password will be encoded using the {@link #getPasswordEncoder()}
     * before storing it.
     * 
     * @param user
     * @throws EntityExistsException if the user already exists
     */
    User addUser( User user )
        throws EntityExistsException, PasswordRuleViolationException;

    /**
     * Update user data. User password will be encoded using the {@link #getPasswordEncoder()}
     * before storing it.
     * 
     * @param user
     * @throws EntityNotFoundException if the user does not exist
     */
    void updateUser( User user )
        throws EntityNotFoundException, PasswordRuleViolationException;

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
     * @throws EntityNotFoundException if the user does not exist
     */
    void removeUser( int accountId )
        throws EntityNotFoundException;
    
    /**
     * Delete a user
     * 
     * @param username
     * @throws EntityNotFoundException if the user does not exist
     */
    void removeUser( String username )
        throws EntityNotFoundException;

    // ----------------------------------------------------------------------
    // Login
    // ----------------------------------------------------------------------
    
    /**
     * Perform login attempt to see if username and password are valid. 
     * 
     * @param username
     * @param rawpassword
     * @return true if user is able to log in. false if username or password is invalid.
     */
    boolean login(String username, String rawpassword);
    
    // ----------------------------------------------------------------------
    // Passwords
    // ----------------------------------------------------------------------
    
    /**
     * Set the password encoder to be used for password operations 
     * 
     * @param passwordEncoder
     */
    void setPasswordEncoder( PasswordEncoder passwordEncoder );

    /**
     * Get the password encoder to be used for password operations
     * 
     * @return the encoder
     */
    PasswordEncoder getPasswordEncoder();
    
    /**
     * Set the Password Rules List.
     * 
     * @param rules the list of {@link PasswordRule} objects.
     */
    void setPasswordRules( List rules );
    
    /**
     * Get the Password Rules List.
     * 
     * @return the list of {@link PasswordRule} objects.
     */
    List getPasswordRules();

    /**
     * Add a Specific Rule to the Password Rules List.
     * 
     * @param rule the rule to add. 
     */
    void addPasswordRule( PasswordRule rule );

    // ----------------------------------------------------------------------
    // User Group
    // ----------------------------------------------------------------------

    /**
     * Add a new user group
     * 
     * @param userGroup
     * @throws EntityExistsException if the user group already exists
     */
    UserGroup addUserGroup( UserGroup userGroup )
        throws EntityExistsException;

    /**
     * Update an existing UserGroup.
     * 
     * @param userGroup
     * @throws EntityNotFoundException if the user group does not exist
     */
    void updateUserGroup( UserGroup userGroup )
        throws EntityNotFoundException;

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
     * @throws EntityNotFoundException if the user does not exist
     */
    void removeUserGroup( int userGroupId )
        throws EntityNotFoundException;
    
    /**
     * Remove the named UserGroup
     * 
     * @param name the user group name to remove
     * @throws EntityNotFoundException if the user group name does not exist.
     */
    void removeUserGroup( String name )
        throws EntityNotFoundException;

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
    * Get all the permission for a user
    * 
    * @return user's permission
    */
    Permission getPermission( String name )
        throws EntityNotFoundException;

    /**
    * Add a permission
    * 
    * @return permission added
    */
    Permission addPermission( Permission perm );

}
