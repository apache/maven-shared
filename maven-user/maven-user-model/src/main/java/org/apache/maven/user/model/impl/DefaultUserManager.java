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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.user.model.InstancePermissions;
import org.apache.maven.user.model.Messages;
import org.apache.maven.user.model.PasswordRule;
import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserHolder;
import org.apache.maven.user.model.UserManager;
import org.apache.maven.user.model.UserSecurityPolicy;
import org.apache.maven.user.model.store.UserStore;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Default implementation of the {@link UserManager} interface.
 * 
 * @plexus.component role="org.apache.maven.user.model.UserManager" role-hint="default"
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
     * @plexus.requirement
     */
    private UserSecurityPolicy securityPolicy;

    /**
     * @plexus.requirement
     */
    private UserHolder userHolder;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {

    }

    public boolean login( String username, String rawPassword )
    {
        User user = getUser( username );
        if ( user == null )
        {
            return false;
        }

        if ( user.isLocked() )
        {
            return false;
        }

        // Ensure that user cannot set password during login.
        user.setPassword( null );

        boolean validPassword = securityPolicy.getPasswordEncoder().isPasswordValid( user.getEncodedPassword(),
                                                                                     rawPassword );

        if ( validPassword )
        {
            // successful login. reset any failed login attempts counter.
            user.setFailedLoginAttempts( 0 );
        }
        else
        {
            // failed login. increment and test.
            if ( user.incrementFailedLoginAttempts() >= securityPolicy.getAllowedLoginAttempts() )
            {
                user.setLocked( true );
            }

            this.updateUser( user );
        }

        return validPassword;
    }

    public void loginFailed( String username )
    {
        User user = getUser( username );
        if ( user != null )
        {
            user.incrementFailedLoginAttempts();
            updateUser( user );
        }
    }

    public void loginSuccessful( String username )
    {
        User user = getUser( username );
        user.setLastLogin( new Date() );
        user.setFailedLoginAttempts( 0 );
        updateUser( user );
    }

    /**
     * Sets the Security Policy to use.
     * 
     * @param policy the policy to use.
     */
    public void setSecurityPolicy( UserSecurityPolicy policy )
    {
        this.securityPolicy = policy;
    }

    public UserSecurityPolicy getSecurityPolicy()
    {
        return securityPolicy;
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
        if ( user.isGuest() )
        {
            user.setEncodedPassword( null );
            return;
        }

        validatePassword( user );

        // set the current encoded password.
        user.setEncodedPassword( securityPolicy.getPasswordEncoder().encodePassword( user.getPassword() ) );
        user.setPassword( null );

        // push new password onto list of previous password.
        List previousPasswords = new ArrayList();
        previousPasswords.add( user.getEncodedPassword() );

        if ( !user.getPreviousEncodedPasswords().isEmpty() )
        {
            int oldCount = Math.min( securityPolicy.getPreviousPasswordsCount() - 1, user.getPreviousEncodedPasswords()
                .size() );
            //modified sublist start index as the previous value results to nothing being added to the list. 
            //TODO Please check and verify if it satisfies the objective.
            List sublist = user.getPreviousEncodedPasswords().subList( 0, oldCount );
            previousPasswords.addAll( sublist );
        }
        user.setPreviousEncodedPasswords( previousPasswords );

        // Update timestamp for password change.
        user.setLastPasswordChange( new Date() );
    }

    private void validatePassword( User user )
        throws PasswordRuleViolationException
    {
        // Trim password.
        user.setPassword( StringUtils.trim( user.getPassword() ) );

        PasswordRuleViolations violations = new PasswordRuleViolations();

        Iterator it = securityPolicy.getPasswordRules().iterator();
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
    
    /**
     * This implementation return empty permissions for each user. 
     */
    public List getUsersInstancePermissions( Class clazz, Object id )
    {
        List users = getUsers();
        List permissions = new ArrayList( users.size() );
        Iterator it = users.iterator();
        while ( it.hasNext() )
        {
            User user = (User) it.next();
            permissions.add( new InstancePermissions( user ) );
        }
        return permissions;
    }

    /**
     * Do nothing
     */
    public void setUsersInstancePermissions( Collection permissions )
    {
    }

    public User getMyUser()
    {
        return getUser( userHolder.getCurrentUserName() );
    }
}
