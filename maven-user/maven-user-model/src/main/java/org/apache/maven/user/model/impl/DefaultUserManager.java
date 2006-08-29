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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.user.model.Messages;
import org.apache.maven.user.model.PasswordEncoder;
import org.apache.maven.user.model.PasswordRule;
import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.apache.maven.user.model.store.UserStore;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Default implementation of the {@link UserManager} interface.
 * 
 * @plexus.component role="org.apache.maven.user.model.UserManager"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class DefaultUserManager
    implements UserManager, Initializable
{
    /**
     * @plexus.requirement
     */
    private UserStore userStore;

    /**
     * @plexus.requirement role-hint="sha256"
     */
    private PasswordEncoder passwordEncoder;

    /**
     * @plexus.configuration default-value="Step doog ekam Skravdraa"
     */
    private String salt;

    /**
     * The List of {@link PasswordRule} objects.
     */
    private List rules;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        rules = new ArrayList();

        // TODO: Find way to have plexus initialize this list with only 1 item.
        addPasswordRule( new MustHavePasswordRule() );
    }

    public boolean login( String username, String rawPassword )
    {
        User user = getUser( username );
        if ( user == null )
        {
            return false;
        }

        return this.passwordEncoder.isPasswordValid( user.getEncodedPassword(), rawPassword, salt );
    }

    public User addUser( User user )
        throws PasswordRuleViolationException
    {
        if ( user.getAccountId() > 0 )
        {
            throw new IllegalStateException( Messages.getString( "user.manager.cannot.add.user.with.accountId" ) ); //$NON-NLS-1$
        }

        processPasswordChange( user );

        return userStore.addUser( user );
    }

    private void processPasswordChange( User user )
        throws PasswordRuleViolationException
    {
        validatePassword( user );

        if ( user.isGuest() )
        {
            //TODO we shouldn't allow password changes for guest users, throw exception before getting here
            user.setEncodedPassword( null );
        }
        else
        {
            user.setEncodedPassword( this.passwordEncoder.encodePassword( user.getPassword(), salt ) );
        }
        user.setPassword( null );

        user.setLastPasswordChange( new Date() ); // update timestamp to now.
    }

    private void validatePassword( User user )
        throws PasswordRuleViolationException
    {
        PasswordRuleViolations violations = new PasswordRuleViolations();

        Iterator it = this.rules.iterator();
        while ( it.hasNext() )
        {
            PasswordRule rule = (PasswordRule) it.next();
            rule.testPassword( violations, user );
        }

        if ( violations.hasViolations() )
        {
            PasswordRuleViolationException exception = new PasswordRuleViolationException();
            exception.setViolations( violations );
            throw exception;
        }
    }

    public UserGroup addUserGroup( UserGroup userGroup )
    {
        if ( userGroup.getId() > 0 )
        {
            throw new IllegalStateException( Messages.getString( "user.manager.cannot.add.group.with.id" ) ); //$NON-NLS-1$
        }

        return userStore.addUserGroup( userGroup );
    }

    public PasswordEncoder getPasswordEncoder()
    {
        return passwordEncoder;
    }

    public User getUser( int accountId )
    {
        return userStore.getUser( accountId );
    }

    /**
     * Get a user by name. User password won't be returned for security reasons.
     * 
     * @param username
     * @return null if the user doesn't exist
     * @deprecated use {@link #getUser(String)} instead.
     */
    public User getUserByUsername( String username )
    {
        return getUser( username );
    }

    public User getUser( String username )
    {
        return userStore.getUser( username );
    }

    public User getGuestUser()
    {
        return userStore.getGuestUser();
    }

    public UserGroup getUserGroup( int userGroupId )
    {
        return userStore.getUserGroup( userGroupId );
    }

    public UserGroup getUserGroup( String name )
    {
        return userStore.getUserGroup( name );
    }

    public List getUserGroups()
    {
        return userStore.getUserGroups();
    }

    public List getUsers()
    {
        return userStore.getUsers();
    }

    public void removeUser( int userId )
    {
        userStore.removeUser( userId );
    }

    public void removeUser( String username )
    {
        userStore.removeUser( username );
    }

    public void removeUserGroup( int userGroupId )
    {
        userStore.removeUserGroup( userGroupId );
    }

    public void removeUserGroup( String userGroupName )
    {
        userStore.removeUserGroup( userGroupName );
    }

    public void setPasswordEncoder( PasswordEncoder passwordEncoder )
    {
        this.passwordEncoder = passwordEncoder;
    }

    public void addPasswordRule( PasswordRule rule )
    {
        // TODO: check for duplicates?

        this.rules.add( rule );
    }

    public List getPasswordRules()
    {
        return this.rules;
    }

    public void setPasswordRules( List rules )
    {
        this.rules = rules;
    }

    public void updateUser( User user )
        throws PasswordRuleViolationException
    {
        // If password is supplied, assume changing of password.
        if ( !StringUtils.isEmpty( user.getPassword() ) )
        {
            processPasswordChange( user );
        }

        userStore.updateUser( user );
    }

    public void updateUserGroup( UserGroup userGroup )
    {
        userStore.updateUserGroup( userGroup );
    }

    public List getPermissions()
    {
        return userStore.getPermissions();
    }

    public Permission getPermission( String name )
    {
        return userStore.getPermission( name );
    }

    public Permission addPermission( Permission perm )
    {
        if ( perm.getId() > 0 )
        {
            throw new IllegalStateException( Messages.getString( "user.manager.cannot.add.permission.with.id" ) ); //$NON-NLS-1$
        }

        return (Permission) userStore.addPermission( perm );
    }
}
