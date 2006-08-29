package org.apache.maven.user.model;

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

/**
 * User Security Policy Settings.  
 */
public interface UserSecurityPolicy
{
    public static final String ROLE = UserSecurityPolicy.class.getName();

    public int getAllowedLoginAttempts();

    public int getPreviousPasswordsCount();

    /**
     * Salt to be used in addiotion to the algorithm when encoding a password
     * 
     * @return the salt
     */
    public String getSalt();
}
