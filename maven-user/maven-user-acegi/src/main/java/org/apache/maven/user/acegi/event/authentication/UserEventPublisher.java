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
import org.acegisecurity.event.authentication.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Hook in Acegi event system to check for login failures and password expired.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class UserEventPublisher
    implements ApplicationEventPublisher
{

    public void publishEvent( ApplicationEvent event )
    {
        if ( event instanceof AuthenticationFailureCredentialsExpiredEvent )
        {
            // TODO expired password, force to change it
        }
        if ( event instanceof AuthenticationFailureBadCredentialsEvent )
        {
            // TODO bad password, increase count
        }
    }

}
