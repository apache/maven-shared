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

import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.jdo.ConfigurableJdoFactory;
import org.codehaus.plexus.jdo.DefaultConfigurableJdoFactory;
import org.codehaus.plexus.jdo.JdoFactory;
import org.jpox.SchemaTool;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Test Cases for the Default User Manager.
 * 
 * @version $Id$
 */
public class DefaultUserManagerTest
    extends PlexusTestCase
{
    UserManager usermanager = null;

    /**
     * Creates a new UserManager which contains no data.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        ConfigurableJdoFactory jdoFactory = (ConfigurableJdoFactory) lookup( JdoFactory.ROLE );
        assertEquals( DefaultConfigurableJdoFactory.class.getName(), jdoFactory.getClass().getName() );

        jdoFactory.setPersistenceManagerFactoryClass( "org.jpox.PersistenceManagerFactoryImpl" );

        jdoFactory.setDriverName( "org.hsqldb.jdbcDriver" );

        jdoFactory.setUrl( "jdbc:hsqldb:mem:" + getName() );

        jdoFactory.setUserName( "sa" );

        jdoFactory.setPassword( "" );

        jdoFactory.setProperty( "org.jpox.transactionIsolation", "READ_UNCOMMITTED" );

        jdoFactory.setProperty( "org.jpox.poid.transactionIsolation", "READ_UNCOMMITTED" );

        jdoFactory.setProperty( "org.jpox.autoCreateSchema", "true" );

        Properties properties = jdoFactory.getProperties();

        for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            System.setProperty( (String) entry.getKey(), (String) entry.getValue() );
        }

        SchemaTool.createSchemaTables( new URL[] { getClass().getResource( "/META-INF/package.jdo" ) }, null, false );

        PersistenceManagerFactory pmf = jdoFactory.getPersistenceManagerFactory();

        assertNotNull( pmf );

        PersistenceManager pm = pmf.getPersistenceManager();

        pm.close();

        usermanager = (UserManager) lookup( UserManager.ROLE );
    }

    public void testAddGetUser()
        throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );

        User smcqueen = new User();
        smcqueen.setUsername( "smcqueen" );
        smcqueen.setFullName( "Steve McQueen" );
        usermanager.addUser( smcqueen );

        assertEquals( 1, usermanager.getUsers().size() );

        User actual = usermanager.getUser( 1 );
        assertEquals( smcqueen, actual );
    }

    public void testUpdateUser()
    {
        assertNotNull( usermanager );

        User jgarner = new User();
        jgarner.setUsername( "jgarner" );
        jgarner.setFullName( "James Garner" );
        usermanager.addUser( jgarner );

        User fetched = usermanager.getUserByUsername( "jgarner" );
        fetched.setFullName( "The Scrounger" );

        usermanager.updateUser( fetched );

        User actual = usermanager.getUserByUsername( "jgarner" );

        assertEquals( "The Scrounger", actual.getFullName() );
    }

    public void testGetUser()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );

        User rattenborough = new User();
        rattenborough.setUsername( "rattenborough" );
        rattenborough.setFullName( "Richard Attenborough" );
        usermanager.addUser( rattenborough );

        User dpleasence = new User();
        dpleasence.setUsername( "dpleasence" );
        dpleasence.setFullName( "Donald Pleasence" );
        usermanager.addUser( dpleasence );

        assertEquals( 2, usermanager.getUsers().size() );

        User actual = usermanager.getUser( 1 );
        assertEquals( rattenborough, actual );

        User actual2 = usermanager.getUser( 2 );
        assertEquals( dpleasence, actual2 );
    }

    public void testRemoveUser()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );

        User rattenborough = new User();
        rattenborough.setUsername( "rattenborough" );
        rattenborough.setFullName( "Richard Attenborough" );
        usermanager.addUser( rattenborough );

        User dpleasence = new User();
        dpleasence.setUsername( "dpleasence" );
        dpleasence.setFullName( "Donald Pleasence" );
        usermanager.addUser( dpleasence );

        assertEquals( 2, usermanager.getUsers().size() );

        User actual = usermanager.getUser( 1 );
        assertEquals( rattenborough, actual );

        usermanager.removeUser( 1 );

        try
        {
            actual = usermanager.getUser( 1 );
        }
        catch ( Exception e )
        {
            fail( "UserManager.getUser(int) should not throw an Exception: " + e.getClass().getName() + " - "
                + e.getMessage() );
        }
        assertNull( "removed user should no longer be returned.", actual );

        User actual2 = usermanager.getUser( 2 );
        assertEquals( "removed user should not affect existing user ids.", dpleasence, actual2 );
    }

    public void testAddGetUserGroupByName()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() );

        UserGroup british = new UserGroup();
        british.setName( "raf" );
        british.setDescription( "Royal Air Force" );

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" );
        american.setDescription( "United States Army Air Forces" );

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actual = usermanager.getUserGroup( "raf" );
        assertEquals( british, actual );
    }

    public void testAddGetUserGroupById()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() );

        UserGroup british = new UserGroup();
        british.setName( "raf" );
        british.setDescription( "Royal Air Force" );

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" );
        american.setDescription( "United States Army Air Forces" );

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actualviaName = usermanager.getUserGroup( 1 );
        assertEquals( british, actualviaName );
    }

    public void testUpdateUserGroup()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() );

        UserGroup british = new UserGroup();
        british.setName( "raf" );
        british.setDescription( "Royal Air Force" );

        usermanager.addUserGroup( british );

        assertEquals( 1, usermanager.getUserGroups().size() );

        UserGroup raf = usermanager.getUserGroup( "raf" );
        raf.setDescription( "Royal Air Force, British" );

        usermanager.updateUserGroup( raf );

        UserGroup actual = usermanager.getUserGroup( "raf" );
        assertEquals( raf, actual );
    }

    public void testGetUserGroups()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() );

        UserGroup british = new UserGroup();
        british.setName( "raf" );
        british.setDescription( "Royal Air Force" );

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" );
        american.setDescription( "United States Army Air Forces" );

        usermanager.addUserGroup( american );

        List groups = usermanager.getUserGroups();
        assertNotNull( groups );
        assertEquals( 2, groups.size() );
    }

    public void testRemoveUserGroup()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() );

        UserGroup british = new UserGroup();
        british.setName( "raf" );
        british.setDescription( "Royal Air Force" );

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" );
        american.setDescription( "United States Army Air Forces" );

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actual = usermanager.getUserGroup( "raf" );
        assertEquals( british, actual );

        usermanager.removeUserGroup( 1 );

        try
        {
            actual = usermanager.getUserGroup( "raf" );
        }
        catch ( Exception e )
        {
            fail( "UserManager.getUserGroup(int) should not throw an Exception: " + e.getClass().getName() + " - "
                + e.getMessage() );
        }
        assertNull( "removed user group should no longer be returned.", actual );

        UserGroup actual2 = usermanager.getUserGroup( "usaaf" );
        assertEquals( "removed user should not affect existing user ids.", american, actual2 );
    }

    public void testGetSetPermissions()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() );
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() );

        Permission canFly = new Permission();
        canFly.setName( "can_fly" );
        canFly.setDescription( "Allows for flight." );

        Permission canBomb = new Permission();
        canBomb.setName( "can_bomb" );
        canBomb.setDescription( "Allows for bombing." );

        UserGroup british = new UserGroup();
        british.setName( "raf" );
        british.setDescription( "Royal Air Force" );
        british.addPermission( canFly );
        british.addPermission( canBomb );

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" );
        american.setDescription( "United States Army Air Forces" );
        american.addPermission( canFly );

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actual = usermanager.getUserGroup( "raf" );

        assertEquals( 2, actual.getPermissions().size() );
    }
}
