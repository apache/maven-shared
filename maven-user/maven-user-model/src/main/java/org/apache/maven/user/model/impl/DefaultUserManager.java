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

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.apache.maven.user.model.PasswordEncoder;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.apache.maven.user.model.Permission;

import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.jdo.PlexusObjectNotFoundException;
import org.codehaus.plexus.jdo.PlexusStoreException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Default implementation of the {@link UserManager} interface.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class DefaultUserManager
    implements UserManager
{
    /**
    * @plexus.requirement
    */
    private JdoFactory jdoFactory;
    
    private PersistenceManagerFactory pmf;
       
    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------
    
    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();
    }
   
    public void addUser( User user )
        throws EntityExistsException
    {
        addObject( user );
    }

    public void addUserGroup( UserGroup userGroup )
        throws EntityExistsException
    {
        addObject( userGroup );
    }

    public PasswordEncoder getPasswordEncoder()
    {
        return null;
    }

    public User getUser( int userId )
    {
        User user = null;
        
        try
        {
            user = ( User ) getObjectById( User.class, userId );
        }
        catch ( PlexusStoreException pse )
        {
            //log exception
        }
        catch ( EntityExistsException eee )
        {
            return null;
        }
        return user;
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
            //log exception
        }
        catch ( EntityExistsException eee )
        {
            return null;
        }
        return userGroup;
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
        throws EntityNotFoundException
    {
        User user = getUser( userId );
        
        removeObject( user );
    }

    public void removeUserGroup( int userGroupId )
        throws EntityNotFoundException
    {
        UserGroup userGroup = getUserGroup( userGroupId );
        
        removeObject( userGroup );
    }

    public void setPasswordEncoder( PasswordEncoder passwordEncoder )
    {
        // TODO Auto-generated method stub

    }

    public void updateUser( User user )
        throws EntityNotFoundException
    {
        try
        {
            updateObject( user );
        }
        catch ( PlexusStoreException pse )
        {
            //log exception
        }
    }

    public void updateUserGroup( UserGroup userGroup )
        throws EntityNotFoundException
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
    
    private Object addObject( Object object )
    {
        return PlexusJdoUtils.addObject( getPersistenceManager(), object );
    }
    
    private Object getObjectById( Class clazz, int id )
        throws PlexusStoreException, EntityNotFoundException
    {
        return getObjectById( clazz, id, null );
    }
    
    private Object getObjectById( Class clazz, int id, String fetchGroup )
        throws PlexusStoreException, EntityNotFoundException
    {
        try
        {
            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id, fetchGroup );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new EntityNotFoundException( e.getMessage() );
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

}
