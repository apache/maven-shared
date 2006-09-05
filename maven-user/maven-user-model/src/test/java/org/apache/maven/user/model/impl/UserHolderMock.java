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

import org.apache.maven.user.model.UserHolder;

/**
 * {@link UserHolder} that can be used for testing purposes.
 * 
 * @plexus.component role="org.apache.maven.user.model.UserHolder" role-hint="mock"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class UserHolderMock
    implements UserHolder
{

    private String userName;

    public String getCurrentUserName()
    {
        return userName;
    }

    /**
     * Set the user name that {@link #getCurrentUserName()} will return
     * @param userName
     */
    public void setUserName( String userName )
    {
        this.userName = userName;
    }
}
