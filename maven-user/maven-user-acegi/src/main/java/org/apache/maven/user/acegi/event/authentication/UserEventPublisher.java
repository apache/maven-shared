package org.apache.maven.user.acegi.event.authentication;

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

import org.acegisecurity.event.authentication.AuthenticationFailureBadCredentialsEvent;
import org.acegisecurity.event.authentication.AuthenticationSuccessEvent;
import org.acegisecurity.userdetails.User;
import org.apache.maven.user.model.UserManager;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Hook in Acegi event system to delegate successful and failed login events to {@link UserManager}.
 * 
 * @plexus.component role="org.springframework.context.ApplicationEventPublisher"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class UserEventPublisher
    implements ApplicationEventPublisher
{

    /**
     * @plexus.requirement role-hint="acegi"
     */
    private UserManager userManager;

    public void setUserManager( UserManager userManager )
    {
        this.userManager = userManager;
    }

    public UserManager getUserManager()
    {
        return userManager;
    }

    public void publishEvent( ApplicationEvent event )
    {
        if ( event instanceof AuthenticationSuccessEvent )
        {
            success( (AuthenticationSuccessEvent) event );
        }
        if ( event instanceof AuthenticationFailureBadCredentialsEvent )
        {
            badCredentials( (AuthenticationFailureBadCredentialsEvent) event );
        }
    }

    private void success( AuthenticationSuccessEvent event )
    {
        User user = (User) event.getAuthentication().getPrincipal();
        getUserManager().loginSuccessful( user.getUsername() );
    }

    private void badCredentials( AuthenticationFailureBadCredentialsEvent event )
    {
        String username = (String) event.getAuthentication().getPrincipal();
        getUserManager().loginFailed( username );
    }
}
