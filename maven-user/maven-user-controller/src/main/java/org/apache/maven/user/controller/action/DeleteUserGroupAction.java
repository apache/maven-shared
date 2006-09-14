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

import java.util.List;

import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * @author Henry Isidro
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="deleteUserGroup" 
 */
public class DeleteUserGroupAction
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    private int id;

    private String name;

    public String execute()
        throws Exception
    {

        // TODO this must be done in the user manager
        User user;
        UserGroup group;
        List users, groups;
        users = userManager.getUsers();

        int i, j;
        for ( i = 0; i < users.size(); i++ )
        {
            user = (User) users.get( i );
            groups = user.getGroups();
            for ( j = 0; j < groups.size(); j++ )
            {
                group = (UserGroup) groups.get( j );
                if ( group.getId() == id )
                {
                    user.removeGroup( group );
                    userManager.updateUser( user );
                }
            }
        }

        userManager.removeUserGroup( id );

        return SUCCESS;
    }

    public String doDelete()
    {
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

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
}
