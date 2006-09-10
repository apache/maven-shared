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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;
import org.apache.maven.user.model.InstancePermissions;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.springframework.beans.factory.InitializingBean;

/**
 * Utility class to handle ACLs.
 * 
 * @plexus.component role="org.apache.maven.user.acegi.AclManager"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AclManager
    implements Initializable
{
    public static final String ROLE = AclManager.class.getName();

    private BasicAclExtendedDao aclDao;

    public void setAclDao( BasicAclExtendedDao aclDao )
    {
        this.aclDao = aclDao;
    }

    public BasicAclExtendedDao getAclDao()
    {
        return aclDao;
    }

    protected NamedEntityObjectIdentity createObjectIdentity( Class clazz, Object id )
    {
        return new NamedEntityObjectIdentity( clazz.getName(), id.toString() );
    }

    private BasicAclEntry[] getAcls( Class clazz, Object id )
    {
        NamedEntityObjectIdentity objectIdentity = createObjectIdentity( clazz, id );
        BasicAclEntry[] acls = getAclDao().getAcls( objectIdentity );
        return acls;
    }

    private BasicAclEntry getAcl( Class clazz, Object id, String userName )
    {
        BasicAclEntry[] acls = getAcls( clazz, id );
        if ( acls != null )
        {
            /* TODO optimize this, probably the results come ordered in some way */
            for ( int i = 0; i < acls.length; i++ )
            {
                if ( acls[i].getRecipient().equals( userName ) )
                {
                    return acls[i];
                }
            }
        }
        return null;
    }

    /**
     * Get the instance permissions for each user and object ( identified by its class and id )
     * 
     * @param clazz {@link Class} of the object
     * @param id identifier of the object
     * @param userPermissions {@link List} &lt; {@link InstancePermissions} >
     * @return {@link List} &lt; {@link InstancePermissions} >
     */
    public List getUsersInstancePermissions( Class clazz, Object id, List userPermissions )
    {
        BasicAclEntry[] acls = getAcls( clazz, id );

        /* put ACLs in a map indexed by username */
        Map aclsByUserName = new HashMap();
        for ( int i = 0; i < acls.length; i++ )
        {
            BasicAclEntry acl = acls[i];
            String recipient = (String) acl.getRecipient();

            BasicAclEntry p = (BasicAclEntry) aclsByUserName.get( recipient );
            if ( p != null )
            {
                throw new IllegalStateException( "There is more than one ACL for user '" + recipient + "': " + p
                    + " and " + acl );
            }

            aclsByUserName.put( recipient, p );
        }

        /* add permissions to each user, and then return a List with permissions */
        Iterator it = userPermissions.iterator();
        while ( it.hasNext() )
        {
            InstancePermissions p = (InstancePermissions) it.next();
            BasicAclEntry acl = (BasicAclEntry) aclsByUserName.get( p.getUser().getUsername() );
            if ( acl != null )
            {
                aclToPermission( acl, p );
            }
        }
        return userPermissions;
    }

    /**
     * Updates a list of permissions at the same time. If the permission didn't exist it's created.
     * 
     * @param clazz
     * @param id
     * @param permissions {@link Collection} &lt;{@link InstancePermissions}> .
     * Each {@link InstancePermissions}.user only needs to have username, no other properties are required.
     */
    public void setUsersInstancePermissions( Class clazz, Object id, Collection permissions )
    {
        Iterator it = permissions.iterator();
        while ( it.hasNext() )
        {
            InstancePermissions p = (InstancePermissions) it.next();
            String userName = p.getUser().getUsername();

            BasicAclEntry acl = getAcl( clazz, id, userName );

            if ( acl == null )
            {
                NamedEntityObjectIdentity objectIdentity = createObjectIdentity( clazz, id );
                acl = new SimpleAclEntry();
                acl.setAclObjectIdentity( objectIdentity );
                //acl.setAclObjectParentIdentity( parentAclId );
                permissionToAcl( p, acl );

                /* create the ACL only if it has any permission */
                if ( acl.getMask() != SimpleAclEntry.NOTHING )
                {
                    getAclDao().create( acl );
                }
            }
            else
            {
                permissionToAcl( p, acl );

                /* delete the ACL if it has no permissions */
                if ( acl.getMask() != SimpleAclEntry.NOTHING )
                {
                    getAclDao().changeMask( acl.getAclObjectIdentity(), userName, new Integer( acl.getMask() ) );
                }
                else
                {
                    getAclDao().delete( acl.getAclObjectIdentity(), userName );
                }
            }
        }
    }

    private void permissionToAcl( InstancePermissions p, BasicAclEntry basicAcl )
    {
        if ( !( basicAcl instanceof SimpleAclEntry ) )
        {
            throw new IllegalArgumentException( "Can't create ACLs other than " + SimpleAclEntry.class );
        }

        SimpleAclEntry acl = (SimpleAclEntry) basicAcl;

        acl.setRecipient( p.getUser().getUsername() );
        acl.setMask( SimpleAclEntry.NOTHING );

        if ( p.isExecute() )
        {
            acl.addPermission( SimpleAclEntry.CREATE );
        }
        if ( p.isDelete() )
        {
            acl.addPermission( SimpleAclEntry.DELETE );
        }
        if ( p.isRead() )
        {
            acl.addPermission( SimpleAclEntry.READ );
        }
        if ( p.isWrite() )
        {
            acl.addPermission( SimpleAclEntry.WRITE );
        }
        if ( p.isAdminister() )
        {
            acl.addPermission( SimpleAclEntry.ADMINISTRATION );
        }
    }

    /**
     * This method translates Acegi {@link BasicAclEntry} to Maven {@link InstancePermissions}.
     * 
     * @param acl Permissions in Acegi world
     * @param p Permissions in Maven world
     */
    private void aclToPermission( BasicAclEntry acl, InstancePermissions p )
    {
        if ( acl.isPermitted( SimpleAclEntry.CREATE ) )
        {
            p.setExecute( true );
        }
        if ( acl.isPermitted( SimpleAclEntry.DELETE ) )
        {
            p.setDelete( true );
        }
        if ( acl.isPermitted( SimpleAclEntry.READ ) )
        {
            p.setRead( true );
        }
        if ( acl.isPermitted( SimpleAclEntry.WRITE ) )
        {
            p.setWrite( true );
        }
        if ( acl.isPermitted( SimpleAclEntry.ADMINISTRATION ) )
        {
            p.setAdminister( true );
        }
    }

    public void initialize()
        throws InitializationException
    {
        /* execute Spring initialization callback */
        if ( getAclDao() instanceof InitializingBean )
        {
            InitializingBean initializingBean = (InitializingBean) getAclDao();
            try
            {
                initializingBean.afterPropertiesSet();
            }
            catch ( Exception e )
            {
                throw new InitializationException( "Unable to initialize ACL DAO", e );
            }
        }
    }
}
