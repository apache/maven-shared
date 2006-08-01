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
 */
public interface UserManager
{

    // ----------------------------------------------------------------------
    // User
    // ----------------------------------------------------------------------

    /**
     * Add a new user
     * 
     * @param user
     * @throws EntityExistsException if the user already exists
     */
    void addUser( User user )
        throws EntityExistsException;

    /**
     * Update user data
     * 
     * @param user
     * @throws EntityNotFoundException if the user does not exist
     */
    void updateUser( User user )
        throws EntityNotFoundException;

    /**
     * Get all users
     * 
     * @return all users in the system
     */
    List getUsers();

    /**
     * Get a user by id
     * 
     * @param userId
     * @return null if the user doesn't exist
     */
    User getUser( int userId );

    /**
     * Delete a user
     * 
     * @param userId
     * @throws EntityNotFoundException  if the user does not exist
     */
    void removeUser( int userId )
        throws EntityNotFoundException;

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
    
    // ----------------------------------------------------------------------
    // User Group
    // ----------------------------------------------------------------------

    /**
     * Add a new user group
     * 
     * @param userGroup
     * @throws EntityExistsException if the user group already exists
     */
    void addUserGroup( UserGroup userGroup )
        throws EntityExistsException;

    /**
     * 
     * @param userGroup
     * @throws EntityNotFoundException if the user group does not exist
     */
    void updateUserGroup( UserGroup userGroup )
        throws EntityNotFoundException;

    /**
     * 
     * @return all the user groups in the system
     */
    List getUserGroups();

    /**
     * 
     * @param userGroupId
     * @return null if the group doesn't exist
     */
    UserGroup getUserGroup( int userGroupId );

    /**
     * 
     * @param userGroupId
     * @throws EntityNotFoundException if the user does not exist
     */
    void removeUserGroup( int userGroupId )
        throws EntityNotFoundException;

}
