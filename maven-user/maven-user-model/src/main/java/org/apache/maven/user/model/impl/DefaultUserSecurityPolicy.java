package org.apache.maven.user.model.impl;

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

import org.apache.maven.user.model.PasswordEncoder;
import org.apache.maven.user.model.PasswordRule;
import org.apache.maven.user.model.UserSecurityPolicy;
import org.apache.maven.user.model.rules.MustHavePasswordRule;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User Security Policy. 
 * 
 * @plexus.component role="org.apache.maven.user.model.UserSecurityPolicy"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DefaultUserSecurityPolicy
    implements UserSecurityPolicy, Initializable
{
    /**
     * @plexus.configuration default-value="3"
     */
    private int allowedLoginAttempts;

    /**
     * @plexus.configuration default-value="6"
     */
    private int previousPasswordsCount;
    
    /**
     * @plexus.requirement role-hint="sha256"
     */
    private PasswordEncoder passwordEncoder;

    /**
     * The List of {@link PasswordRule} objects.
     */
    private List rules;

    public int getAllowedLoginAttempts()
    {
        return allowedLoginAttempts;
    }

    public int getPreviousPasswordsCount()
    {
        return previousPasswordsCount;
    }

    /**
     * Set the count of login attempts to allow before locking out a user.
     * 
     * @param allowedLoginAttempts the number of login attempt to allow before lockout.
     */
    public void setAllowedLoginAttempts( int allowedLoginAttempts )
    {
        this.allowedLoginAttempts = allowedLoginAttempts;
    }

    /**
     * Sets the count of previous passwords that should be tracked.
     * 
     * @param previousPasswordsCount the count of previous passwords to track.
     */
    public void setPreviousPasswordsCount( int previousPasswordsCount )
    {
        this.previousPasswordsCount = previousPasswordsCount;
    }

    /**
     * Get the password encoder to be used for password operations
     * 
     * @return the encoder
     */
    public PasswordEncoder getPasswordEncoder()
    {
        return passwordEncoder;
    }

    /**
     * Add a Specific Rule to the Password Rules List.
     * 
     * @param rule the rule to add. 
     */
    public void addPasswordRule( PasswordRule rule )
    {
        // TODO: check for duplicates? if so, check should only be based on Rule class name.
        
        rule.setUserSecurityPolicy( this );
        this.rules.add( rule );
    }

    /**
     * Get the Password Rules List.
     * 
     * @return the list of {@link PasswordRule} objects.
     */
    public List getPasswordRules()
    {
        return this.rules;
    }

    /**
     * Set the Password Rules List.
     * 
     * @param newRules the list of {@link PasswordRule} objects.
     */
    public void setPasswordRules( List newRules )
    {
        this.rules.clear();
        
        if ( newRules == null )
        {
            return;
        }
        
        // Intentionally iterating to ensure policy settings in provided rules.
        
        Iterator it = newRules.iterator();
        while ( it.hasNext() )
        {
            PasswordRule rule = (PasswordRule) it.next();
            rule.setUserSecurityPolicy( this );
            this.rules.add( rule );
        }
    }

    public void initialize()
        throws InitializationException
    {
        rules = new ArrayList();

        // TODO: Find way to have plexus initialize this list with only 1 item.
        addPasswordRule( new MustHavePasswordRule() );
    }
}
