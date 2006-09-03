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

import javax.naming.OperationNotSupportedException;

import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserManager;
import org.apache.maven.user.model.UserSecurityPolicy;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * @author Napoleon Esmundo C. Ramirez
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="changeUserPassword"
 */
public class ChangeUserPasswordAction
    extends PlexusActionSupport
{

    /**
     * @plexus.requirement
     */
    private UserManager userManager;
    
    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy securityPolicy;

    private int accountId;

    private String currentPassword;
    
    private String newPassword;
    
    private String confirmPassword;
    
    public String execute()
        throws Exception
    {
        // validate that newPassword and confirmPassword are the same
        if ( !newPassword.equals( confirmPassword ) )
        {
            addActionError( "user.password.mismatch.error" );
        }
        
        // TODO check currentPassword is old password should be done in UserManager
        User user = userManager.getUser( accountId );
        String encodedCurrentPassword = securityPolicy.getPasswordEncoder().encodePassword( currentPassword );
        if ( !user.getEncodedPassword().equals( encodedCurrentPassword ) )
        {
            addActionError( "user.invalid.current.password.error" );
        }
        
        if ( getActionErrors().size() > 0 )
        {
            return INPUT;
        }

        // calls userManager.updateUser( user );
        user.setPassword( newPassword );
        userManager.updateUser( user );
        
        return SUCCESS;
    }
    
    public String doChange()
        throws Exception
    {
        return INPUT;
    }

    public void setCurrentPassword( String currentPassword )
    {
        this.currentPassword = currentPassword;
    }

    public String getCurrentPassword()
    {
        return currentPassword;
    }

    public void setNewPassword( String newPassword )
    {
        this.newPassword = newPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setConfirmPassword( String confirmPassword )
    {
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    public void setAccountId( int accountId )
    {
        this.accountId = accountId;
    }

    public int getAccountId()
    {
        return accountId;
    }

}
