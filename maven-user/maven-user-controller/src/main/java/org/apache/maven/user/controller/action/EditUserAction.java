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

import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import com.opensymphony.webwork.interceptor.ServletRequestAware;

/**
 * @author Henry Isidro
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="editUser"
 *   instantiation-strategy="per-lookup"
 */
public class EditUserAction
    extends PlexusActionSupport
    implements ServletRequestAware
{

    private static final long serialVersionUID = 8143169847676423348L;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    private User user;

    private UserGroup userGroup;

    private boolean addMode = false;

    private String username;

    private String password;

    private String confirmPassword;

    private String email;

    private HttpServletRequest request;

    private List groups;

    private List allGroups;
    
    public String execute()
        throws Exception
    {
        if ( username.indexOf( "," ) != -1 )
        {
            username = username.substring( 0, username.indexOf( "," ) );
        }
        if ( password.indexOf( "," ) != -1 )
        {
            password = password.substring( 0, password.indexOf( "," ) );
        }
        if ( email.indexOf( "," ) != -1 )
        {
            email = email.substring( 0, email.indexOf( "," ) );
        }
        if( !StringUtils.isEmpty( password ) && !password.equals( confirmPassword ) )
        {
            addActionError( "user.password.mismatch.error" );
            return INPUT;
        }
        if ( addMode )
        {
            userGroup = userManager.getDefaultUserGroup();

            user = new User();
            user.setUsername( username );
            user.setPassword( password );
            user.setEmail( email );
            user.addGroup( userGroup );
            try
            {
                userManager.addUser( user );
            }
            catch ( PasswordRuleViolationException e )
            {
                PasswordRuleViolations violationsContainer = e.getViolations();
                if( violationsContainer != null && violationsContainer.hasViolations() )
                {
                    setActionErrors( violationsContainer.getLocalizedViolations() );
                    return INPUT;
                }
            }
        }
        else
        {
            user = userManager.getUser( username );
            user.setUsername( username );
            user.setPassword( password );
            user.setEmail( email );
            user.setGroups( groups );
            
            try
            {
                userManager.updateUser( user );
            }
            catch ( PasswordRuleViolationException e )
            {
                PasswordRuleViolations violationsContainer = e.getViolations();
                if( violationsContainer != null && violationsContainer.hasViolations() )
                {
                    setActionErrors( violationsContainer.getLocalizedViolations() );
                    return INPUT;
                }
            }
        }

        request.getSession().removeAttribute( "addMode" );
        request.getSession().removeAttribute( "username" );
        request.getSession().removeAttribute( "password" );
        request.getSession().removeAttribute( "email" );

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
        user = userManager.getUser( username );
        email = user.getEmail();
        groups = user.getGroups();
        allGroups = userManager.getUserGroups();

        return INPUT;
    }
    
    public String editMe()
        throws Exception
    {
        addMode = false;
        user = userManager.getMyUser();
        username = user.getUsername();
        email = user.getEmail();
        groups = user.getGroups();
        allGroups = userManager.getUserGroups();
    
        return INPUT;
    }
    
    public boolean isAddMode()
    {
        return addMode;
    }

    public void setAddMode( boolean addMode )
    {
        this.addMode = addMode;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setServletRequest( HttpServletRequest request )
    {
        this.request = request;
    }

    public List getGroups()
    {
        return groups;
    }

    public void setGroups( List sgroups )
    {
        groups = new ArrayList();
        
        for( int i = 0; i < sgroups.size(); i++)
        {
            UserGroup dgroup = userManager.getUserGroup( Integer.parseInt(sgroups.get(i).toString()) );
            
            groups.add( dgroup );
        }
    }
    
    public List getAllGroups()
    {
        return allGroups;
    }

    public int[] getSelectedGroups()
    {
        int[] selectedGroups = new int[groups.size()];
        
        for( int i = 0; i < groups.size(); i++)
        {
            selectedGroups[i] = ( (UserGroup) groups.get( i ) ).getId();
        }
        
        return selectedGroups;
    }
}
