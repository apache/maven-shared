package org.apache.maven.user.model.impl;

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

import org.apache.maven.user.model.UserSecurityPolicy;

/**
 * User Security Policy. 
 * 
 * @plexus.component role="org.apache.maven.user.model.UserSecurityPolicy"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DefaultUserSecurityPolicy
    implements UserSecurityPolicy
{
    /**
     * @plexus.configuration default-value="3"
     */
    private int allowedLoginAttempts;

    /**
     * @plexus.configuration default-value="6"
     */
    private int previousPasswordsCount;

    /**
     * @plexus.configuration default-value="Step doog ekam Skravdraa"
     */
    private String salt;

    public int getAllowedLoginAttempts()
    {
        return allowedLoginAttempts;
    }

    public int getPreviousPasswordsCount()
    {
        return previousPasswordsCount;
    }

    public void setAllowedLoginAttempts( int allowedLoginAttempts )
    {
        this.allowedLoginAttempts = allowedLoginAttempts;
    }

    public void setPreviousPasswordsCount( int previousPasswordsCount )
    {
        this.previousPasswordsCount = previousPasswordsCount;
    }

    public void setSalt( String salt )
    {
        this.salt = salt;
    }

    public String getSalt()
    {
        return salt;
    }
}
