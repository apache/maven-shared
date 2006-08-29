package org.apache.maven.user.model.rules;

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

import org.apache.maven.user.model.PasswordRule;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserSecurityPolicy;
import org.codehaus.plexus.util.StringUtils;

/**
 * Basic Password Rule, Checks for non-empty Passwords in non guest users.
 * 
 * @plexus.component role="org.apache.maven.user.model.PasswordRule" role-hint="must-have"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class MustHavePasswordRule
    implements PasswordRule
{
    public void testPassword( PasswordRuleViolations violations, User user, UserSecurityPolicy securityPolicy )
    {
        if ( StringUtils.isEmpty( user.getPassword() ) )
        {
            violations.addViolation( "user.password.violation.missing" ); //$NON-NLS-1$
        }
    }

}
