package org.apache.maven.user.controller.action;

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

import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * @author Henry Isidro
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="deleteUser" 
 */
public class DeleteUserAction
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    private int id;
    
    private String username;

    public String execute()
        throws Exception
    {
        userManager.removeUser( id );

        return SUCCESS;
    }

    public String doDelete()
    {
        User user = userManager.getUser( id );
        username = user.getUsername();
        
        return "delete";
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }
}
