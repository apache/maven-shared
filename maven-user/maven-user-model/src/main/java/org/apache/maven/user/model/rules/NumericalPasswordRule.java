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
 * Basic Password Rule, Checks for non-empty passwords that have at least {@link #setMinimumCount(int)} of 
 * numerical characters contained within. 
 * 
 * @plexus.component role="org.apache.maven.user.model.PasswordRule" role-hint="numerical-count"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class NumericalPasswordRule
implements PasswordRule
{
    private int minimumCount;
    
    public NumericalPasswordRule()
    {
        this.minimumCount = 1;
    }

    private int countDigitCharacters( String password )
    {
        int count = 0;

        if ( StringUtils.isEmpty( password ) )
        {
            return count;
        }

        /* TODO: Eventually upgrade to the JDK 1.5 Technique
         * 
         * // Doing this via iteration of code points to take in account localized numbers.
         * for ( int i = 0; i < password.length(); i++ )
         * {
         *     int codepoint = password.codePointAt( i );
         *     if ( Character.isDigit( codepoint ) )
         *     {
         *         count++;
         *     }
         * }
         */
        
        // JDK 1.4 Technique - NOT LOCALIZED.
        for ( int i = 0; i < password.length(); i++ )
        {
            char c = password.charAt( i );
            if ( Character.isDigit( c ) )
            {
                count++;
            }
        }

        return count;
    }

    public int getMinimumCount()
    {
        return minimumCount;
    }

    public void setMinimumCount( int minimumCount )
    {
        this.minimumCount = minimumCount;
    }

    public void setUserSecurityPolicy( UserSecurityPolicy policy )
    {
        // Ignore, policy not needed in this rule.
    }
    
    public void testPassword( PasswordRuleViolations violations, User user )
    {
        if ( countDigitCharacters( user.getPassword() ) < this.minimumCount )
        {
            violations.addViolation( "user.password.violation.numeric", new Object[] { new Integer( minimumCount ) } ); //$NON-NLS-1$
        }
    }
}
