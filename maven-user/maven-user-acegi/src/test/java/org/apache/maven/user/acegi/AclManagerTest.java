package org.apache.maven.user.acegi;

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

import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.jdbc.JdbcDaoImpl;
import org.acegisecurity.acl.basic.jdbc.JdbcExtendedDaoImpl;
import org.apache.maven.user.acegi.acl.basic.ExtendedSimpleAclEntry;
import org.apache.maven.user.model.InstancePermissions;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.codehaus.plexus.PlexusTestCase;

/**
 * Test for {@link AclManager}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AclManagerTest
    extends PlexusTestCase
{

    private AclManager manager;

    private JdbcExtendedDaoImpl aclDao;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        manager = (AclManager) lookup( AclManager.ROLE );
        aclDao = (JdbcExtendedDaoImpl) lookup( BasicAclExtendedDao.class.getName() );
    }

    public void testPermissionInheritance()
    {
        User user = new User();
        user.setUsername( "myUser" );

        /* create object number 1 */
        InstancePermissions permissions = new InstancePermissions();
        permissions.setId( "1" );
        permissions.setInstanceClass( UserGroup.class );
        permissions.setUser( user );
        permissions.setAdminister( true );

        manager.setUsersInstancePermission( permissions );

        NamedEntityObjectIdentity parentAclObjectIdentity = new NamedEntityObjectIdentity( permissions
            .getInstanceClass().getName(), permissions.getId().toString() );
        BasicAclEntry[] acls = aclDao.getAcls( parentAclObjectIdentity );

        assertNotNull( acls );
        assertEquals( 1, acls.length );
        assertEquals( user.getUsername(), acls[0].getRecipient() );
        assertEquals( ExtendedSimpleAclEntry.ADMINISTRATION, acls[0].getMask() );
        assertEquals( parentAclObjectIdentity, acls[0].getAclObjectIdentity() );
        assertNull( acls[0].getAclObjectParentIdentity() );

        /* create sub objects number 1 and 2 */
        permissions = new InstancePermissions();
        permissions.setId( "1" );
        permissions.setInstanceClass( User.class );
        permissions.setParentClass( UserGroup.class );
        permissions.setParentId( "1" );

        manager.setUsersInstancePermission( permissions );

        permissions = new InstancePermissions();
        permissions.setId( "2" );
        permissions.setInstanceClass( User.class );
        permissions.setParentClass( UserGroup.class );
        permissions.setParentId( "1" );

        manager.setUsersInstancePermission( permissions );

        NamedEntityObjectIdentity aclObjectIdentity = new NamedEntityObjectIdentity( permissions.getInstanceClass()
            .getName(), permissions.getId().toString() );
        acls = aclDao.getAcls( aclObjectIdentity );

        assertNotNull( acls );
        assertEquals( 1, acls.length );
        assertEquals( JdbcDaoImpl.RECIPIENT_USED_FOR_INHERITENCE_MARKER, acls[0].getRecipient() );
        assertEquals( ExtendedSimpleAclEntry.NOTHING, acls[0].getMask() );
        assertEquals( aclObjectIdentity, acls[0].getAclObjectIdentity() );
        assertEquals( parentAclObjectIdentity, acls[0].getAclObjectParentIdentity() );
    }
}
