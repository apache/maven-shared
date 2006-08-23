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
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
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

        jdoFactory.setPersistenceManagerFactoryClass( "org.jpox.PersistenceManagerFactoryImpl" ); //$NON-NLS-1$

        jdoFactory.setDriverName( "org.hsqldb.jdbcDriver" ); //$NON-NLS-1$

        jdoFactory.setUrl( "jdbc:hsqldb:mem:" + getName() ); //$NON-NLS-1$

        jdoFactory.setUserName( "sa" ); //$NON-NLS-1$

        jdoFactory.setPassword( "" ); //$NON-NLS-1$

        jdoFactory.setProperty( "org.jpox.transactionIsolation", "READ_UNCOMMITTED" ); //$NON-NLS-1$ //$NON-NLS-2$

        jdoFactory.setProperty( "org.jpox.poid.transactionIsolation", "READ_UNCOMMITTED" ); //$NON-NLS-1$ //$NON-NLS-2$

        jdoFactory.setProperty( "org.jpox.autoCreateSchema", "true" ); //$NON-NLS-1$ //$NON-NLS-2$

        Properties properties = jdoFactory.getProperties();

        for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            System.setProperty( (String) entry.getKey(), (String) entry.getValue() );
        }

        SchemaTool.createSchemaTables( new URL[] { getClass().getResource( "/META-INF/package.jdo" ) }, null, false ); //$NON-NLS-1$

        PersistenceManagerFactory pmf = jdoFactory.getPersistenceManagerFactory();

        assertNotNull( pmf );

        PersistenceManager pm = pmf.getPersistenceManager();

        pm.close();

        usermanager = (UserManager) lookup( UserManager.ROLE );
    }

    public void testAddGetUserById()
        throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$

        User smcqueen = new User();
        smcqueen.setUsername( "smcqueen" ); //$NON-NLS-1$
        smcqueen.setFullName( "Steve McQueen" ); //$NON-NLS-1$
        smcqueen.setPassword( "the cooler king" ); //$NON-NLS-1$

        /* Keep a reference to the object that was added.
         * Since it has the actual accountId that was managed by jpox/jdo.
         */
        User added = usermanager.addUser( smcqueen );

        assertEquals( 1, usermanager.getUsers().size() );

        /* Fetch user from usermanager using accountId returned earlier */
        User actual = usermanager.getUser( added.getAccountId() );
        assertEquals( smcqueen, actual );
    }

    public void testAddGetUserByName()
        throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$

        User smcqueen = new User();
        smcqueen.setUsername( "smcqueen" ); //$NON-NLS-1$
        smcqueen.setFullName( "Steve McQueen" ); //$NON-NLS-1$
        smcqueen.setPassword( "the cooler king" ); //$NON-NLS-1$
        usermanager.addUser( smcqueen );

        assertEquals( 1, usermanager.getUsers().size() );

        User actual = usermanager.getUser( "smcqueen" ); //$NON-NLS-1$

        assertNotNull( "Should return the smcqueen user.", actual ); //$NON-NLS-1$
        assertEquals( smcqueen, actual );
    }

    public void testUpdateUser() throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$

        User jgarner = new User();
        jgarner.setUsername( "jgarner" ); //$NON-NLS-1$
        jgarner.setFullName( "James Garner" ); //$NON-NLS-1$
        jgarner.setPassword( "the scrounger" ); //$NON-NLS-1$
        usermanager.addUser( jgarner );

        User fetched = usermanager.getUser( "jgarner" ); //$NON-NLS-1$
        assertNotNull( "User should not be null.", fetched ); //$NON-NLS-1$
        fetched.setFullName( "Flight Lt. Hendley" ); //$NON-NLS-1$

        usermanager.updateUser( fetched );

        User actual = usermanager.getUser( "jgarner" ); //$NON-NLS-1$

        assertEquals( "Flight Lt. Hendley", actual.getFullName() ); //$NON-NLS-1$
    }

    public void testRemoveUser() throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$

        User rattenborough = new User();
        rattenborough.setUsername( "rattenborough" ); //$NON-NLS-1$
        rattenborough.setFullName( "Richard Attenborough" ); //$NON-NLS-1$
        rattenborough.setPassword( "the big x" ); //$NON-NLS-1$
        usermanager.addUser( rattenborough );

        User dpleasence = new User();
        dpleasence.setUsername( "dpleasence" ); //$NON-NLS-1$
        dpleasence.setFullName( "Donald Pleasence" ); //$NON-NLS-1$
        dpleasence.setPassword( "the forger" ); //$NON-NLS-1$
        usermanager.addUser( dpleasence );

        assertEquals( 2, usermanager.getUsers().size() );

        User actual = usermanager.getUser( "rattenborough" ); //$NON-NLS-1$
        assertEquals( rattenborough, actual );

        usermanager.removeUser( "rattenborough" ); //$NON-NLS-1$

        try
        {
            actual = usermanager.getUser( "rattenborough" ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( "UserManager.getUser(int) should not throw an Exception: " + e.getClass().getName() + " - " //$NON-NLS-1$ //$NON-NLS-2$
                + e.getMessage() );
        }
        assertNull( "removed user should no longer be returned.", actual ); //$NON-NLS-1$

        User actual2 = usermanager.getUser( "dpleasence" ); //$NON-NLS-1$
        assertEquals( "removed user should not affect existing user ids.", dpleasence, actual2 ); //$NON-NLS-1$
    }

    public void testAddGetUserGroupByName()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" ); //$NON-NLS-1$
        american.setDescription( "United States Army Air Forces" ); //$NON-NLS-1$

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actual = usermanager.getUserGroup( "raf" ); //$NON-NLS-1$
        assertEquals( british, actual );
    }

    public void testAddGetUserGroupById()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$

        /* Keep a reference to the object that was added.
         * Since it has the actual ID that was managed by jpox/jdo.
         */
        UserGroup added = usermanager.addUserGroup( british );

        // Add a second UserGroup to ensure that previous ID doesn't get changed.
        UserGroup american = new UserGroup();
        american.setName( "usaaf" ); //$NON-NLS-1$
        american.setDescription( "United States Army Air Forces" ); //$NON-NLS-1$

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        /* Fetch UserGroup from usermanager using ID returned earlier */
        UserGroup actual = usermanager.getUserGroup( added.getId() );
        assertNotNull( "UserGroup id:1918 should exist.", actual ); //$NON-NLS-1$
        assertEquals( british, actual );
    }

    public void testUpdateUserGroup()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$

        usermanager.addUserGroup( british );

        assertNotNull( usermanager.getUserGroups() );
        assertEquals( 1, usermanager.getUserGroups().size() );

        UserGroup raf = usermanager.getUserGroup( "raf" ); //$NON-NLS-1$
        assertNotNull( raf );
        raf.setDescription( "Royal Air Force, British" ); //$NON-NLS-1$

        usermanager.updateUserGroup( raf );

        UserGroup actual = usermanager.getUserGroup( "raf" ); //$NON-NLS-1$
        assertEquals( raf, actual );
    }

    public void testGetUserGroups()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" ); //$NON-NLS-1$
        american.setDescription( "United States Army Air Forces" ); //$NON-NLS-1$

        usermanager.addUserGroup( american );

        List groups = usermanager.getUserGroups();
        assertNotNull( groups );
        assertEquals( 2, groups.size() );
    }

    public void testRemoveUserGroup()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" ); //$NON-NLS-1$
        american.setDescription( "United States Army Air Forces" ); //$NON-NLS-1$

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actual = usermanager.getUserGroup( "raf" ); //$NON-NLS-1$
        assertEquals( british, actual );

        usermanager.removeUserGroup( "raf" ); //$NON-NLS-1$

        try
        {
            actual = usermanager.getUserGroup( "raf" ); //$NON-NLS-1$
        }
        catch ( Exception e )
        {
            fail( "UserManager.getUserGroup(int) should not throw an Exception: " + e.getClass().getName() + " - " //$NON-NLS-1$ //$NON-NLS-2$
                + e.getMessage() );
        }
        assertNull( "removed user group should no longer be returned.", actual ); //$NON-NLS-1$

        UserGroup actual2 = usermanager.getUserGroup( "usaaf" ); //$NON-NLS-1$
        assertEquals( "removed user should not affect existing user ids.", american, actual2 ); //$NON-NLS-1$
    }

    public void testGetSetUserGroupInUserLoose() throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$

        /* Forget to add UserGroup to usermanager (loose technique) */

        User rattenborough = new User();
        rattenborough.setUsername( "rattenborough" ); //$NON-NLS-1$
        rattenborough.setFullName( "Richard Attenborough" ); //$NON-NLS-1$
        rattenborough.setPassword( "the big x" ); //$NON-NLS-1$
        rattenborough.setGroup( british );

        /* Add new user with new usergroup (Shouldn't work) */
        User added = usermanager.addUser( rattenborough );
        assertNotNull( "Added UserGroup should not by null.", added.getGroup() ); //$NON-NLS-1$

        assertEquals( 1, usermanager.getUsers().size() );
        assertEquals( 1, usermanager.getUserGroups().size() );

        User actual = usermanager.getUser( "rattenborough" ); //$NON-NLS-1$
        assertEquals( added, actual );
        assertNotNull( "Actual UserGroup should not be null.", actual.getGroup() ); //$NON-NLS-1$
        assertEquals( added.getGroup(), actual.getGroup() );
    }

    public void testGetSetUserGroupInUserPreloaded() throws Exception
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$
        
        /* Add UserGroup to usermanager (preload technique) */
        UserGroup addedGroup = usermanager.addUserGroup( british );

        User rattenborough = new User();
        rattenborough.setUsername( "rattenborough" ); //$NON-NLS-1$
        rattenborough.setFullName( "Richard Attenborough" ); //$NON-NLS-1$
        rattenborough.setPassword( "the big x" ); //$NON-NLS-1$
        
        /* Use the (resolved) addedGroup from above.
         * If you use the (unresolved) british UserGroup here, you will
         * inadvertently create 2 UserGroups with the same name, but different IDs.
         */
        rattenborough.setGroup( addedGroup ); 
        User added = usermanager.addUser( rattenborough );
        assertNotNull( "Added UserGroup should not by null.", added.getGroup() ); //$NON-NLS-1$

        assertEquals( 1, usermanager.getUsers().size() );
        assertEquals( 1, usermanager.getUserGroups().size() );

        User actual = usermanager.getUser( "rattenborough" ); //$NON-NLS-1$
        assertEquals( added, actual );
        assertNotNull( "Actual UserGroup should not be null.", actual.getGroup() ); //$NON-NLS-1$
        assertEquals( added.getGroup(), actual.getGroup() );
    }

    public void testGetSetPermissions()
    {
        assertNotNull( usermanager );

        assertEquals( "New UserManager should contain no users.", 0, usermanager.getUsers().size() ); //$NON-NLS-1$
        assertEquals( "New UserManager should contain no groups.", 0, usermanager.getUserGroups().size() ); //$NON-NLS-1$

        Permission canFly = new Permission();
        canFly.setName( "can_fly" ); //$NON-NLS-1$
        canFly.setDescription( "Allows for flight." ); //$NON-NLS-1$

        Permission canBomb = new Permission();
        canBomb.setName( "can_bomb" ); //$NON-NLS-1$
        canBomb.setDescription( "Allows for bombing." ); //$NON-NLS-1$

        UserGroup british = new UserGroup();
        british.setName( "raf" ); //$NON-NLS-1$
        british.setDescription( "Royal Air Force" ); //$NON-NLS-1$
        british.addPermission( canFly );
        british.addPermission( canBomb );

        usermanager.addUserGroup( british );

        UserGroup american = new UserGroup();
        american.setName( "usaaf" ); //$NON-NLS-1$
        american.setDescription( "United States Army Air Forces" ); //$NON-NLS-1$
        american.addPermission( canFly );

        usermanager.addUserGroup( american );

        assertEquals( 2, usermanager.getUserGroups().size() );

        UserGroup actual = usermanager.getUserGroup( "raf" ); //$NON-NLS-1$

        assertNotNull( actual );
        assertNotNull( actual.getPermissions() );
        assertEquals( 2, actual.getPermissions().size() );
    }
}
