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

import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * Action to change password after it hs expired.
 * 
 * @author Lester Ecarma
 * @version $Id$
 * 
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="expiredPassword"
 *   instantiation-strategy="per-lookup"
 */
public class ExpiredPasswordAction
    extends PlexusActionSupport
{
    private static final long serialVersionUID = 8143169847676423348L;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;

    private String username;

    private String oldPassword;

    private String password;

    private String confirmPassword;

    public String execute()
        throws Exception
    {
        addActionError( "user.expired.password.error" );
        return INPUT;
    }

    public String doChangePassword()
        throws Exception
    {
        User user = userManager.getUser( username );

        if ( StringUtils.isEmpty( oldPassword ) )
        {
            addActionError( "user.invalid.current.password.error" );
            return INPUT;
        }

        if ( !userManager.getSecurityPolicy().getPasswordEncoder().encodePassword( oldPassword )
            .equals( user.getEncodedPassword() ) )
        {
            addActionError( "user.invalid.current.password.error" );
            return INPUT;
        }

        if ( StringUtils.isEmpty( password ) )
        {
            addActionError( "user.empty.password.error" );
            return INPUT;
        }

        if ( !password.equals( confirmPassword ) )
        {
            addActionError( "user.password.mismatch.error" );
            return INPUT;
        }

        user.setPassword( password );

        try
        {
            // TODO use change password and remove a good number of previous checks
            userManager.updateUser( user );
        }
        catch ( PasswordRuleViolationException e )
        {
            PasswordRuleViolations violationsContainer = e.getViolations();
            if ( violationsContainer != null && violationsContainer.hasViolations() )
            {
                setActionErrors( violationsContainer.getLocalizedViolations() );
                return INPUT;
            }
        }

        return SUCCESS;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void setConfirmPassword( String confirmPassword )
    {
        this.confirmPassword = confirmPassword;
    }

    public void setOldPassword( String oldPassword )
    {
        this.oldPassword = oldPassword;
    }

}
