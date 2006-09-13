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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.maven.user.model.Permission;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import com.opensymphony.webwork.interceptor.ServletRequestAware;

/**
 * @author Henry Isidro
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="editUserGroup"
 */
public class EditUserGroupAction
    extends PlexusActionSupport
    implements ServletRequestAware
{

    private static final long serialVersionUID = 8143169847676423348L;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    private UserGroup userGroup;

    private List staticPermissions;

    private List availablePermissions;

    private Permission staticPermission;

    private Permission permission;

    private String permissionName;

    private boolean addMode = false;
    
    private int id;

    private String name;

    private String description;

    private List permissions;

    private HttpServletRequest request;

    public String execute()
        throws Exception
    {
        if ( name.indexOf( "," ) != -1 )
        {
            name = name.substring( 0, name.indexOf( "," ) );
        }
        if ( description.indexOf( "," ) != -1 )
        {
            description = description.substring( 0, description.indexOf( "," ) );
        }
        
        if ( addMode )
        {
            userGroup = new UserGroup();
            
            userGroup.setName( name );
            userGroup.setDescription( description );

            userManager.addUserGroup( userGroup );    
        }
        else
        {
            userGroup = userManager.getUserGroup( id );
            userGroup.setName( name );
            userGroup.setDescription( description );
            permissions = (List) request.getSession().getAttribute( "permissions" );
            userGroup.setPermissions( permissions );
            userManager.updateUserGroup( userGroup );
        }

        request.getSession().removeAttribute( "addMode" );
        request.getSession().removeAttribute( "id" );
        request.getSession().removeAttribute( "name" );
        request.getSession().removeAttribute( "description" );
        request.getSession().removeAttribute( "permissions" );

        return SUCCESS;
    }

    public String doAdd()
        throws Exception
    {
        addMode = true;
        return INPUT;
    }

    public String doEdit()
        throws Exception
    {
        addMode = false;
        userGroup = userManager.getUserGroup( id );
        // password = user.getPassword(); don't access the password
        name = userGroup.getName();
        description = userGroup.getDescription();
        permissions = userGroup.getPermissions();
        if ( permissions.size() == 1 )
        {
            permissionName = ( (Permission) permissions.get( 0 ) ).getName();
        }

        return INPUT;
    }

    public String doGetAvailablePermissions()
        throws Exception
    {
        int i, j;
        availablePermissions = new ArrayList();
        staticPermissions = userManager.getPermissions();
        permissions = (List) request.getSession().getAttribute( "permissions" );
        if ( permissions == null || permissions.size() == 0 )
        {
            availablePermissions.addAll( staticPermissions );
        }
        else
        {
            for ( i = 0; i < staticPermissions.size(); i++ )
            {
                staticPermission = (Permission) staticPermissions.get( i );
                for ( j = 0; j < permissions.size(); j++ )
                {
                    permission = (Permission) permissions.get( j );
                    if ( permission.getName().equalsIgnoreCase( staticPermission.getName() ) )
                    {
                        break;
                    }
                }
                if ( j >= permissions.size() )
                {
                    availablePermissions.add( staticPermission );
                }
            }
        }

        request.getSession().setAttribute( "addMode", Boolean.valueOf( addMode ) );
        request.getSession().setAttribute( "id", Integer.toString( id ) );
        request.getSession().setAttribute( "name", name );
        request.getSession().setAttribute( "description", description );
        

        return "permissions";
    }

    public String doAddPermission()
        throws Exception
    {
        staticPermissions = userManager.getPermissions();
        int i, j;
        for ( i = 0; i < staticPermissions.size(); i++ )
        {
            permission = (Permission) staticPermissions.get( i );
            if ( permission.getName().equalsIgnoreCase( permissionName ) )
            {
                permissions = (List) request.getSession().getAttribute( "permissions" );
                if ( permissions == null )
                {
                    permissions = new ArrayList();
                    permissions.add( permission );
                }
                else
                {
                    for ( j = 0; j < permissions.size(); j++ )
                    {
                        Permission permission = (Permission) permissions.get( j );
                        if ( permission.getName().equalsIgnoreCase( permissionName ) )
                        {
                            break;
                        }
                    }
                    if ( j >= permissions.size() )
                    {
                        permissions.add( permission );
                    }
                }
                if ( permissions.size() == 1 )
                {
                    permissionName = ( (Permission) permissions.get( 0 ) ).getName();
                }
                break;
            }
        }

        addMode = ( (Boolean) request.getSession().getAttribute( "addMode" ) ).booleanValue();
        id = Integer.parseInt( (String) request.getSession().getAttribute( "id" ) );
        name = (String) request.getSession().getAttribute( "name" );
        description = (String) request.getSession().getAttribute( "description" );

        return INPUT;
    }

    public String doDeletePermission()
        throws Exception
    {
        int i = 0;
        permissions = (List) request.getSession().getAttribute( "permissions" );
        for ( ; i < permissions.size(); i++ )
        {
            permission = (Permission) permissions.get( i );
            if ( permission.getName().equalsIgnoreCase( permissionName ) )
            {
                permissions.remove( i );
                break;
            }
            if ( permissions.size() == 1 )
            {
                permissionName = ( (Permission) permissions.get( 0 ) ).getName();
            }
        }

        return INPUT;
    }

    public List getAvailablePermissions()
    {
        return availablePermissions;
    }

    public String getPermissionName()
    {
        return permissionName;
    }

    public void setPermissionName( String permissionName )
    {
        this.permissionName = permissionName;
    }

    public boolean isAddMode()
    {
        return addMode;
    }

    public void setAddMode( boolean addMode )
    {
        this.addMode = addMode;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public List getPermissions()
    {
        return this.permissions;
    }

    public void setServletRequest( HttpServletRequest request )
    {
        this.request = request;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
}
