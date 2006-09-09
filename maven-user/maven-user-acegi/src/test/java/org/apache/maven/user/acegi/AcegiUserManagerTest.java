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
import org.acegisecurity.acl.basic.SimpleAclEntry;
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

    private Mock delegate, dao;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        manager = new AcegiUserManager();
        delegate = mock( UserManager.class );
        manager.setUserManager( (UserManager) delegate.proxy() );

        dao = mock( BasicAclExtendedDao.class );
        AclManager aclManager = new AclManager();
        aclManager.setAclDao( (BasicAclExtendedDao) dao.proxy() );
        manager.setAclManager( aclManager );
    }

    public void testGetUsersInstancePermissions()
    {
        List users = new ArrayList();
        User u = new User();
        InstancePermissions p = new InstancePermissions( u );
        users.add( p );
        delegate.expects( once() ).method( "getUsersInstancePermissions" ).will( returnValue( users ) );

        BasicAclEntry[] acls = new BasicAclEntry[1];
        acls[0] = new SimpleAclEntry();
        dao.expects( once() ).method( "getAcls" ).will( returnValue( acls ) );

        List usersInstancePermissions = manager.getUsersInstancePermissions( User.class, new Integer( 1 ) );

        assertNotNull( usersInstancePermissions );
        assertEquals( 1, usersInstancePermissions.size() );

        p = (InstancePermissions) usersInstancePermissions.iterator().next();

        assertEquals( u, p.getUser() );
        assertFalse( p.isBuild() );
        assertFalse( p.isDelete() );
        assertFalse( p.isEdit() );
        assertFalse( p.isView() );
    }

}
