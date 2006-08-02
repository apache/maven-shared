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

import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * @author Teody Cue
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="addUserRole"
 */
public class AddUserRoleAction
    extends PlexusActionSupport
{

    private static final long serialVersionUID = 6977844294060864622L;

    private UserManager userManager;

    private User user;

    private List permissions;

    private Permission permission;

    private int accountId;

    private String permissionName;

    public String execute()
        throws Exception
    {
//        try
//        {
            user = userManager.getUser( accountId );
            permissions = userManager.getPermissions();
            int i;
            for ( i = 0; i < permissions.size(); i++ )
            {
                permission = (Permission) permissions.get( i );
                if ( permission.getName().equalsIgnoreCase( permissionName ) )
                {
                    break;
                }
            }
            if ( i < permissions.size() )
            {
                user.getGroup().addPermission( permission );
                userManager.updateUser( user );
            }
            else
            {
                addActionMessage( "Can't add user role (id=" + accountId + ", role=" + permissionName
                    + ") : Role does not exist." );
            }
//        }
//        catch ( ContinuumException e )
//        {
//            addActionMessage( "Can't add user role (id=" + accountId + ", role=" + permissionName + ") : "
//                + e.getMessage() );
//
//            e.printStackTrace();
//
//            return ERROR;
//        }

        return SUCCESS;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public void setAccountId( int accountId )
    {
        this.accountId = accountId;
    }

    public String getPermissionName()
    {
        return permissionName;
    }

    public void setPermissionName( String permissionName )
    {
        this.permissionName = permissionName;
    }

}
