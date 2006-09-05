package org.apache.maven.user.controller.action;

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

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.apache.maven.user.model.impl.DefaultUserManager;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.jdo.ConfigurableJdoFactory;
import org.codehaus.plexus.jdo.DefaultConfigurableJdoFactory;
import org.codehaus.plexus.jdo.JdoFactory;
import org.jpox.SchemaTool;

/**
 * @author <a href="mailto:nramirez@exist.com">Napoleon Esmundo C. Ramirez</a>
 */
public class ChangeUserPasswordActionTest
    extends PlexusTestCase
{

    private DefaultUserManager userManager = null;

    private User user = null;

    private ChangeUserPasswordAction action = null;

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

        SchemaTool.createSchemaTables( new URL[] { getClass().getResource( "/org/apache/maven/user/model/package.jdo" ) }, null, false ); //$NON-NLS-1$

        PersistenceManagerFactory pmf = jdoFactory.getPersistenceManagerFactory();

        assertNotNull( pmf );

        PersistenceManager pm = pmf.getPersistenceManager();

        pm.close();

        userManager = (DefaultUserManager) lookup( UserManager.ROLE );

        user = new User();
        user.setUsername( "nramirez" );
        user.setPassword( "abc123" );
        user.setEmail( "nramirez@exist.com" );
        user = userManager.addUser( user );

        action = (ChangeUserPasswordAction) lookup( "com.opensymphony.xwork.Action", "changeUserPassword" );
    }

    public void testInvalidCurrentPasswordFail()
        throws Exception
    {
        action.setAccountId( user.getAccountId() );
        action.setCurrentPassword( "123abc" );
        action.setNewPassword( "" );
        action.setConfirmPassword( "" );

        action.execute();

        assertEquals( action.getActionErrors().size(), 1 );
        assertTrue( action.getActionErrors().contains( "user.invalid.current.password.error" ) );
    }

    public void testInvalidCurrentPasswordSuccess()
        throws Exception
    {
        action.setAccountId( user.getAccountId() );
        action.setCurrentPassword( "abc123" );
        action.setNewPassword( "" );
        action.setConfirmPassword( "" );

        action.execute();

        assertEquals( action.getActionErrors().size(), 0 );
        assertFalse( action.getActionErrors().contains( "user.invalid.current.password.error" ) );
    }

    public void testPasswordMismatchFail()
        throws Exception
    {
        action.setAccountId( user.getAccountId() );
        action.setCurrentPassword( "abc123" );
        action.setNewPassword( "welcome!" );
        action.setConfirmPassword( "!welcome" );

        action.execute();

        assertEquals( action.getActionErrors().size(), 1 );
        assertTrue( action.getActionErrors().contains( "user.password.mismatch.error" ) );
    }

    public void testPasswordMismatchSuccess()
        throws Exception
    {
        action.setAccountId( user.getAccountId() );
        action.setCurrentPassword( "abc123" );
        action.setNewPassword( "welcome!" );
        action.setConfirmPassword( "welcome!" );

        action.execute();

        assertEquals( action.getActionErrors().size(), 0 );
        assertFalse( action.getActionErrors().contains( "user.password.mismatch.error" ) );
    }

    public void testInvalidCurrentPasswordAndPasswordMismatchFail()
        throws Exception
    {
        action.setAccountId( user.getAccountId() );
        action.setCurrentPassword( "123abc" );
        action.setNewPassword( "welcome!" );
        action.setConfirmPassword( "!welcome" );

        action.execute();

        assertEquals( action.getActionErrors().size(), 2 );
        assertTrue( action.getActionErrors().contains( "user.invalid.current.password.error" ) );
        assertTrue( action.getActionErrors().contains( "user.password.mismatch.error" ) );
    }

    public void testInvalidCurrentPasswordAndPasswordMismatchSuccess()
        throws Exception
    {
        action.setAccountId( user.getAccountId() );
        action.setCurrentPassword( "abc123" );
        action.setNewPassword( "welcome!" );
        action.setConfirmPassword( "welcome!" );

        action.execute();

        assertEquals( action.getActionErrors().size(), 0 );
        assertFalse( action.getActionErrors().contains( "user.invalid.current.password.error" ) );
        assertFalse( action.getActionErrors().contains( "user.password.mismatch.error" ) );
    }
}
