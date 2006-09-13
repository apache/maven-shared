package org.apache.maven.user.model;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

/**
 * User Security Policy Settings.  
 * 
 * @version $Id$
 * @todo roll password management into it's own object.
 */
public interface UserSecurityPolicy
{
    public static final String ROLE = UserSecurityPolicy.class.getName();
    
    // ----------------------------------------------------------------------
    // Password Management
    // ----------------------------------------------------------------------

    /**
     * Gets the password encoder to use.
     * 
     * @return the PasswordEncoder implementation to use.
     */
    public PasswordEncoder getPasswordEncoder();
    
    
    /**
     * Add a Specific Rule to the Password Rules List.
     * 
     * @param rule the rule to add. 
     */
    void addPasswordRule( PasswordRule rule );
    
    /**
     * Get the Password Rules List.
     * 
     * @return the list of {@link PasswordRule} objects.
     */
    List getPasswordRules();

    /**
     * Set the Password Rules List.
     * 
     * @param rules the list of {@link PasswordRule} objects.
     */
    void setPasswordRules( List rules );
    
    /**
     * Gets the count of Previous Passwords that should be tracked.
     * 
     * @return the count of previous passwords to track.
     */
    public int getPreviousPasswordsCount();
    
    /**
     * Sets the count of previous passwords that should be tracked.
     * 
     * @param count the count of previous passwords to track.
     */
    public void setPreviousPasswordsCount( int count );

    // ----------------------------------------------------------------------
    // Login Attempt Management
    // ----------------------------------------------------------------------

    /**
     * Gets the count of login attempts to allow before locking out a user.
     * 
     * @return the number of login attempts to allow before lockout.
     */
    public int getAllowedLoginAttempts();

    /**
     * Set the count of login attempts to allow before locking out a user.
     * 
     * @param count the number of login attempt to allow before lockout.
     */
    public void setAllowedLoginAttempts( int count );


    /**
     * Number of days before a password expires. 0 means no expiration of passwords.
     * 
     * @return the number of days
     */
    public int getDaysBeforeExpiration();

    /**
     * @see #getDaysBeforeExpiration()
     * 
     * @param count the number of days
     */
    public void setDaysBeforeExpiration( int count );
}
