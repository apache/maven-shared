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

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.maven.user.model.Messages;
import org.apache.maven.user.model.PasswordEncoder;
import org.apache.maven.user.model.PasswordRule;
import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.jdo.PlexusObjectNotFoundException;
import org.codehaus.plexus.jdo.PlexusStoreException;
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
    private JdoFactory jdoFactory;
    
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
    
    private PersistenceManagerFactory pmf;
    
       
    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------
    
    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();
        rules = new ArrayList();
        
        // TODO: Find way to have plexus initialize this list with only 1 item.
        addPasswordRule(new MustHavePasswordRule());
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
        if(user.getAccountId() > 0)
        {
            throw new IllegalStateException( Messages.getString("user.manager.cannot.add.user.with.accountId") ); //$NON-NLS-1$
        }
        
        processPasswordChange( user );
        
        return (User) addObject( user );
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
        if(userGroup.getId() > 0)
        {
            throw new IllegalStateException( Messages.getString("user.manager.cannot.add.group.with.id") ); //$NON-NLS-1$
        }
        
        return (UserGroup) addObject( userGroup );
    }

    public PasswordEncoder getPasswordEncoder()
    {
        return passwordEncoder;
    }

    public User getUser( int accountId )
    {
        User user = null;
        
        try
        {
            user = (User) getObjectById( User.class, accountId );
        }
        catch ( PlexusStoreException pse )
        {
            // TODO log exception
        }
        return user;

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
        PersistenceManager pm = getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( User.class, true );

            Query query = pm.newQuery( extent );

            query.declareImports( "import java.lang.String" ); //$NON-NLS-1$

            query.declareParameters( "String username" ); //$NON-NLS-1$

            query.setFilter( "this.username == username" ); //$NON-NLS-1$

            Collection result = (Collection) query.execute( username );

            if ( result.size() == 0 )
            {
                tx.commit();

                return null;
            }

            Object object = pm.detachCopy( result.iterator().next() );

            tx.commit();

            return (User) object;
        }
        finally
        {
            rollback( tx );
        }
    }    

    public User getGuestUser()
    {
        PersistenceManager pm = getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( User.class, true );

            Query query = pm.newQuery( extent );

            query.setFilter( "this.guest == true" ); //$NON-NLS-1$

            Collection result = (Collection) query.execute();

            if ( result.size() == 0 )
            {
                tx.commit();

                return null;
            }

            Object object = pm.detachCopy( result.iterator().next() );

            tx.commit();

            return (User) object;
        }
        finally
        {
            rollback( tx );
        }
    }

    public UserGroup getUserGroup( int userGroupId )
    {
        UserGroup userGroup = null;
        
        try
        {
            userGroup = (UserGroup) getObjectById( UserGroup.class, userGroupId );
        }
        catch ( PlexusStoreException pse )
        {
            //TODO log exception
        }
        return userGroup;
    }

    public UserGroup getUserGroup( String name )
    {
        PersistenceManager pm = getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( UserGroup.class, true );

            Query query = pm.newQuery( extent );

            query.declareImports( "import java.lang.String" ); //$NON-NLS-1$

            query.declareParameters( "String name" ); //$NON-NLS-1$

            query.setFilter( "this.name == name" ); //$NON-NLS-1$

            Collection result = (Collection) query.execute( name );

            if ( result.size() == 0 )
            {
                tx.commit();

                return null;
            }

            Object object = pm.detachCopy( result.iterator().next() );

            tx.commit();

            return (UserGroup) object;
        }
        finally
        {
            rollback( tx );
        }
    }

    public List getUserGroups()
    {
        return getAllObjectsDetached( UserGroup.class );
    }

    public List getUsers()
    {
        return getAllObjectsDetached( User.class );
    }

    public void removeUser( int userId )
    {
        User user = getUser( userId );
        
        removeObject( user );
    }

    public void removeUser( String username )
    {
        User user = getUser( username );
        
        removeObject( user );
    }
    
    public void removeUserGroup( int userGroupId )
    {
        UserGroup userGroup = getUserGroup( userGroupId );
        
        removeObject( userGroup );
    }
    
    public void removeUserGroup( String userGroupName )
    {
        UserGroup userGroup = getUserGroup( userGroupName );

        removeObject( userGroup );
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
        try
        {
            // If password is supplied, assume changing of password.
            if ( !StringUtils.isEmpty( user.getPassword() ) )
            {
                processPasswordChange( user );
            }

            updateObject( user );
        }
        catch ( PlexusStoreException pse )
        {
            //log exception
        }
    }

    public void updateUserGroup( UserGroup userGroup )
    {
        try
        {
            updateObject( userGroup );
        }
        catch ( PlexusStoreException pse )
        {
            //log exception
        }
    }

    public List getPermissions()
    {
        return getAllObjectsDetached( Permission.class );
    }

    public Permission getPermission( String name )
    {
        PersistenceManager pm = getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( Permission.class, true );

            Query query = pm.newQuery( extent );

            query.declareImports( "import java.lang.String" ); //$NON-NLS-1$

            query.declareParameters( "String name" ); //$NON-NLS-1$

            query.setFilter( "this.name == name" ); //$NON-NLS-1$

            Collection result = (Collection) query.execute( name );

            if ( result.size() == 0 )
            {
                tx.commit();

                return null;
            }

            Object object = pm.detachCopy( result.iterator().next() );

            tx.commit();

            return (Permission) object;
        }
        finally
        {
            rollback( tx );
        }
    }

    public Permission addPermission( Permission perm )
    {
        if(perm.getId() > 0)
        {
            throw new IllegalStateException( Messages.getString("user.manager.cannot.add.permission.with.id") ); //$NON-NLS-1$
        }
        
        return (Permission) addObject( perm );
    }
    
    private Object addObject( Object object )
    {
        return PlexusJdoUtils.addObject( getPersistenceManager(), object );
    }
    
    private Object getObjectById( Class clazz, int id )
        throws PlexusStoreException
    {
        return getObjectById( clazz, id, null );
    }
    
    private Object getObjectById( Class clazz, int id, String fetchGroup )
        throws PlexusStoreException
    {
        try
        {
            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id, fetchGroup );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            // TODO make PlexusObjectNotFoundException runtime or change plexus not to wrap jdo exceptions
            throw new RuntimeException( e.getMessage() );
        }
        catch ( PlexusStoreException e )
        {
            throw new PlexusStoreException( e.getMessage(), e );
        }
    }
    
    private List getAllObjectsDetached( Class clazz )
    {
        return getAllObjectsDetached( clazz, null );
    }

    private List getAllObjectsDetached( Class clazz, String fetchGroup )
    {
        return getAllObjectsDetached( clazz, null, fetchGroup );
    }

    private List getAllObjectsDetached( Class clazz, String ordering, String fetchGroup )
    {
        return PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), clazz, ordering, fetchGroup );
    }
    
    private void removeObject( Object o )
    {
        PlexusJdoUtils.removeObject( getPersistenceManager(), o );
    }
    
    private void updateObject( Object object )
        throws PlexusStoreException
    {
        PlexusJdoUtils.updateObject( getPersistenceManager(), object );
    }
    
    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( -1 );

        return pm;
    } 

    private void rollback( Transaction tx )
    {
        PlexusJdoUtils.rollbackIfActive( tx );
    }

}
