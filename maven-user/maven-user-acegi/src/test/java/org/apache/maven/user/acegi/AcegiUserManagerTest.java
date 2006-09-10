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

import java.util.ArrayList;
import java.util.List;

import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.apache.maven.user.acegi.acl.basic.ExtendedSimpleAclEntry;
import org.apache.maven.user.model.InstancePermissions;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Test for {@link AcegiUserManager}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AcegiUserManagerTest
    extends MockObjectTestCase
{
    private AcegiUserManager manager;

    AclManager aclManager;

    private Mock delegate, dao;

    private User user;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        manager = new AcegiUserManager();
        delegate = mock( UserManager.class );
        manager.setUserManager( (UserManager) delegate.proxy() );

        dao = mock( BasicAclExtendedDao.class );
        aclManager = new AclManager();
        aclManager.setAclDao( (BasicAclExtendedDao) dao.proxy() );
        manager.setAclManager( aclManager );

        user = new User();
        user.setUsername( "myuser" );
    }

    public void testGetUsersInstancePermissions()
    {
        List users = new ArrayList();
        InstancePermissions p = new InstancePermissions( user );
        users.add( p );
        delegate.expects( once() ).method( "getUsersInstancePermissions" ).will( returnValue( users ) );

        BasicAclEntry[] acls = new BasicAclEntry[1];
        acls[0] = new ExtendedSimpleAclEntry();
        dao.expects( once() ).method( "getAcls" ).will( returnValue( acls ) );

        List usersInstancePermissions = manager.getUsersInstancePermissions( User.class, new Integer( 1 ) );

        assertNotNull( usersInstancePermissions );
        assertEquals( 1, usersInstancePermissions.size() );

        p = (InstancePermissions) usersInstancePermissions.iterator().next();

        assertEquals( user, p.getUser() );
        assertFalse( p.isExecute() );
        assertFalse( p.isDelete() );
        assertFalse( p.isWrite() );
        assertFalse( p.isRead() );
        assertFalse( p.isAdminister() );
    }

    public void testSetUsersInstancePermissions()
    {
        List users = new ArrayList();
        InstancePermissions p = new InstancePermissions( user );
        users.add( p );

        BasicAclEntry[] acls = new BasicAclEntry[1];
        BasicAclEntry acl = new ExtendedSimpleAclEntry();
        acl.setRecipient( user.getUsername() );
        acl.setAclObjectIdentity( aclManager.createObjectIdentity( User.class, new Integer( 1 ) ) );
        acls[0] = acl;

        /* *************************************** old ACL *************************************** */

        dao.expects( atLeastOnce() ).method( "getAcls" ).will( returnValue( acls ) );

        /* no permissions */
        dao.expects( once() ).method( "delete" ).with( ANYTHING, eq( user.getUsername() ) );

        manager.setUsersInstancePermissions( User.class, new Integer( 1 ), users );
        dao.verify();

        /* read permission */
        p.setRead( true );
        dao.expects( once() ).method( "changeMask" ).with( ANYTHING, eq( user.getUsername() ),
                                                           eq( ExtendedSimpleAclEntry.READ ) );

        manager.setUsersInstancePermissions( User.class, new Integer( 1 ), users );
        dao.verify();

        /* *************************************** new ACL *************************************** */

        dao.expects( atLeastOnce() ).method( "getAcls" ).will( returnValue( new BasicAclEntry[0] ) );

        /* no permissions */
        p.setRead( false );

        manager.setUsersInstancePermissions( User.class, new Integer( 1 ), users );
        dao.verify();

        /* read permission */
        p.setRead( true );
        acl.setMask( ExtendedSimpleAclEntry.READ );
        dao.expects( once() ).method( "create" ).with( hasProperty( "mask", eq( ExtendedSimpleAclEntry.READ ) ) );

        manager.setUsersInstancePermissions( User.class, new Integer( 1 ), users );
        dao.verify();

    }
}
