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

import java.util.Date;

import junit.framework.TestCase;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;

/**
 * Test for {@link MavenUserDetailsService}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class MavenUserDetailsServiceTest
    extends TestCase
{

    private MavenUserDetailsService userDetailsService;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        userDetailsService = new MavenUserDetailsService();
    }

    public void testGetUserDetails()
    {
        User mavenUser = createMockedUser();

        UserDetails userDetails = userDetailsService.getUserDetails( mavenUser );

        assertEquals( userDetails.getUsername(), mavenUser.getUsername() );

        GrantedAuthority[] authorities = userDetails.getAuthorities();
        for ( int i = 0; i < authorities.length; i++ )
        {
            assertEquals( "ROLE_p" + i, authorities[i].getAuthority() );
        }
    }

    public void testAccountExpiration()
    {
        User mavenUser = createMockedUser();

        userDetailsService.setDaysBeforeExpiration( 0 );
        UserDetails userDetails = userDetailsService.getUserDetails( mavenUser );
        assertTrue( userDetails.isAccountNonExpired() );

        userDetailsService.setDaysBeforeExpiration( -1 );
        userDetails = userDetailsService.getUserDetails( mavenUser );
        assertTrue( userDetails.isAccountNonExpired() );

        userDetailsService.setDaysBeforeExpiration( 1 );
        userDetails = userDetailsService.getUserDetails( mavenUser );
        assertTrue( userDetails.isAccountNonExpired() );

        Date twoDaysAgo = new Date( System.currentTimeMillis() - 2 * MavenUserDetailsService.MILLISECONDS_PER_DAY );
        mavenUser.setLastPasswordChange( twoDaysAgo );
        userDetails = userDetailsService.getUserDetails( mavenUser );
        assertFalse( userDetails.isCredentialsNonExpired() );
    }

    private User createMockedUser()
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

        User mavenUser = new User();
        mavenUser.setUsername( "username" );
        mavenUser.setEncodedPassword( shaPassword );
        mavenUser.setGroup( group );

        mavenUser.setLastPasswordChange( new Date() );

        return mavenUser;
    }
}
