package org.apache.maven.continuum.security.acegi;

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

import java.util.Date;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.maven.continuum.model.system.ContinuumUser;
import org.apache.maven.continuum.model.system.Permission;
import org.apache.maven.continuum.model.system.UserGroup;

import junit.framework.TestCase;

/**
 * Test for {@link ContinuumUserDetailsService}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class ContinuumUserDetailsServiceTest
    extends TestCase
{

    private ContinuumUserDetailsService userDetailsService;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        userDetailsService = new ContinuumUserDetailsService();
    }

    public void testGetUserDetails()
    {
        ContinuumUser continuumUser = createMockedUser();
       
        UserDetails userDetails = userDetailsService.getUserDetails( continuumUser );

        assertEquals( userDetails.getUsername(), continuumUser.getUsername() );

        GrantedAuthority[] authorities = userDetails.getAuthorities();
        for ( int i = 0; i < authorities.length; i++ )
        {
            assertEquals( "ROLE_p" + i, authorities[i].getAuthority() );
        }
    }

    public void testPasswordEncoding()
    {
        ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder();
        String shaPassword = passwordEncoder.encodePassword( "admin", null );

        ContinuumUser continuumUser = new ContinuumUser();
        continuumUser.setEncodedPassword( shaPassword );

        assertTrue( continuumUser.equalsPassword( "admin" ) );
    }
    
    public void testAccountExpiration()
    {
        ContinuumUser continuumUser = createMockedUser();
       
        userDetailsService.setDaysBeforeExpiration( 0 );
        UserDetails userDetails = userDetailsService.getUserDetails( continuumUser );
        assertTrue(userDetails.isAccountNonExpired());
        
        userDetailsService.setDaysBeforeExpiration( -1 );
        userDetails = userDetailsService.getUserDetails( continuumUser );
        assertTrue(userDetails.isAccountNonExpired());
        
        userDetailsService.setDaysBeforeExpiration( 1 );
        userDetails = userDetailsService.getUserDetails( continuumUser );
        assertTrue(userDetails.isAccountNonExpired());
        
        Date twoDaysAgo = new Date( System.currentTimeMillis() - 2 * ContinuumUserDetailsService.MILLISECONDS_PER_DAY );
        continuumUser.setLastPasswordChange( twoDaysAgo );
        userDetails = userDetailsService.getUserDetails( continuumUser );
        assertFalse(userDetails.isAccountNonExpired());
    }

    private ContinuumUser createMockedUser()
    {
        Permission p0 = new Permission();
        p0.setName( "p0" );
        Permission p1 = new Permission();
        p1.setName( "p1" );
        Permission p2 = new Permission();
        p2.setName( "p2" );

        UserGroup group = new UserGroup();
        group.addPermission( p0 );
        group.addPermission( p1 );
        group.addPermission( p2 );

        ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder();
        String shaPassword = passwordEncoder.encodePassword( "password", null );

        ContinuumUser continuumUser = new ContinuumUser();
        continuumUser.setUsername( "username" );
        continuumUser.setEncodedPassword( shaPassword );
        continuumUser.setGroup( group );

        continuumUser.setLastPasswordChange( new Date() );

        return continuumUser;
    }
}
