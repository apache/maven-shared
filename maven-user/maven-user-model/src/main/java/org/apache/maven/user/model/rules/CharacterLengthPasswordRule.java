package org.apache.maven.user.model.rules;

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

import org.apache.maven.user.model.PasswordRule;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserSecurityPolicy;
import org.codehaus.plexus.util.StringUtils;

/**
 * Basic Password Rule, Checks for non-empty passwords that have between {@link #setMinimumCharacters(int)} and 
 * {@link #setMaximumCharacters(int)} characters in length. 
 * 
 * @plexus.component role="org.apache.maven.user.model.PasswordRule" role-hint="character-length"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class CharacterLengthPasswordRule
    implements PasswordRule
{
    private int minimumCharacters;
    private int maximumCharacters;
    
    public CharacterLengthPasswordRule()
    {
        minimumCharacters = 1;
        maximumCharacters = 8;
    }

    public int getMaximumCharacters()
    {
        return maximumCharacters;
    }

    public int getMinimumCharacters()
    {
        return minimumCharacters;
    }

    public void setMaximumCharacters( int maximumCharacters )
    {
        this.maximumCharacters = maximumCharacters;
    }

    public void setMinimumCharacters( int minimumCharacters )
    {
        this.minimumCharacters = minimumCharacters;
    }

    public void testPassword( PasswordRuleViolations violations, User user, UserSecurityPolicy securityPolicy )
    {
        if(minimumCharacters > maximumCharacters)
        {
            /* this should caught up front during the configuration of the component */
            // TODO: Throw runtime exception instead?
            violations.addViolation( "user.password.violation.length.misconfigured", new Object[] {
                new Integer( minimumCharacters ),
                new Integer( maximumCharacters ) } ); //$NON-NLS-1$
        }
        
        String password = user.getPassword();
        
        if ( StringUtils.isEmpty( password ) 
            || password.length() < minimumCharacters
            || password.length() > maximumCharacters )
        {
            violations.addViolation( "user.password.violation.length", new Object[] {
                new Integer( minimumCharacters ),
                new Integer( maximumCharacters ) } ); //$NON-NLS-1$
        }
    }
}
