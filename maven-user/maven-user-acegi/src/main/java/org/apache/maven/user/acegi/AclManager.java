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

import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;
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

    protected void create( BasicAclEntry aclEntry )
    {
        getAclDao().create( aclEntry );
    }

    protected void delete( Class clazz, int id )
    {
        getAclDao().delete( createObjectIdentity( clazz, id ) );
    }

    protected NamedEntityObjectIdentity createObjectIdentity( Class clazz, int id )
    {
        return new NamedEntityObjectIdentity( clazz.getName(), Integer.toString( id ) );
    }

    public BasicAclEntry[] getAcls( Class clazz, int id )
    {
        NamedEntityObjectIdentity objectIdentity = createObjectIdentity( clazz, id );
        BasicAclEntry[] acls = getAclDao().getAcls( objectIdentity );
        return acls;
    }

    public BasicAclEntry getAcl( Class clazz, int id, String userName )
    {
        BasicAclEntry[] acls = getAcls( clazz, id );
        for ( int i = 0; i < acls.length; i++ )
        {
            if ( acls[i].getRecipient().equals( userName ) )
            {
                return acls[i];
            }
        }
        return null;
    }

    public void setPermissions( Class clazz, int id, String userName, int permissions, AclObjectIdentity parentAclId )
    {
        BasicAclEntry acl = getAcl( clazz, id, userName );
        NamedEntityObjectIdentity objectIdentity = createObjectIdentity( clazz, id );

        if ( acl == null )
        {
            SimpleAclEntry aclEntry = new SimpleAclEntry();
            aclEntry.setAclObjectIdentity( objectIdentity );
            aclEntry.setRecipient( userName );
            aclEntry.setAclObjectParentIdentity( parentAclId );
            aclEntry.addPermission( permissions );
            create( aclEntry );
        }
        else
        {
            getAclDao().changeMask( objectIdentity, userName, new Integer( permissions ) );
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
