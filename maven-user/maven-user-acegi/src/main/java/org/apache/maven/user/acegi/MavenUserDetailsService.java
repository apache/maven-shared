package org.apache.maven.user.acegi;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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
 *
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.springframework.dao.DataAccessException;

/**
 * Acegi {@link UserDetailsService} that loads user info from Maven {@link UserManager}.
 *
 * @plexus.component role="org.acegisecurity.userdetails.UserDetailsService" role-hint="maven"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author Henry Isidro
 * @version $Id$
 */
public class MavenUserDetailsService
    implements UserDetailsService
{
    static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    /**
     * @plexus.configuration default-value="60"
     */
    private int daysBeforeExpiration;

    public UserDetails loadUserByUsername( String username )
        throws UsernameNotFoundException, DataAccessException
    {
        User user = userManager.getUser( username );

        if ( user == null )
        {
            throw new UsernameNotFoundException( "Could not find user: " + username );
        }
        return getUserDetails( user );
    }

    /**
     * Convert a Maven user into an Acegi user
     * 
     * @param user the continuum user loaded from DB
     * @return the Acegi user
     */
    public UserDetails getUserDetails( User user )
    {
        List permissions = user.getGroup().getPermissions();

        List grantedAuthorities = new ArrayList( permissions.size() + 1 );
        Iterator it = permissions.iterator();
        while ( it.hasNext() )
        {
            Permission permission = (Permission) it.next();
            grantedAuthorities.add( new GrantedAuthorityImpl( "ROLE_" + permission.getName() ) );
        }

        if ( user.isGuest() )
        {
            // TODO externalize this String
            grantedAuthorities.add( new GrantedAuthorityImpl( "ROLE_ANONYMOUS" ) );
        }

        GrantedAuthority[] grantedAuthoritiesAsArray = (GrantedAuthority[]) grantedAuthorities
            .toArray( new GrantedAuthority[0] );

        String username = user.getUsername();
        String password = user.getEncodedPassword();
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean accountNonLocked = !user.isLocked();
        boolean credentialsNonExpired = true;

        if ( user.getLastPasswordChange() != null && daysBeforeExpiration > 0 )
        {
            long lastPasswordChange = user.getLastPasswordChange().getTime();
            long currentTime = new Date().getTime();
            credentialsNonExpired = lastPasswordChange + daysBeforeExpiration * MILLISECONDS_PER_DAY > currentTime;
        }

        UserDetails userDetails = new org.acegisecurity.userdetails.User( username, password, enabled,
                                                                          accountNonExpired, credentialsNonExpired,
                                                                          accountNonLocked, grantedAuthoritiesAsArray );

        return userDetails;
    }

    protected void setDaysBeforeExpiration( int daysBeforeExpiration )
    {
        this.daysBeforeExpiration = daysBeforeExpiration;
    }
}
