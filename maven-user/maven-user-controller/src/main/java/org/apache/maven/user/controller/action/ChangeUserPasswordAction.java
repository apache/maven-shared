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

import org.apache.maven.user.model.UserManager;
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

    private int accountId;

    private String currentPassword;
    
    private String newPassword;
    
    private String confirmPassword;
    
    public String execute()
        throws Exception
    {
        // TODO validate that newPassword and confirmPassword are the same
        // TODO check currentPassword is old password
        // TODO calls userManager.updateUser( user );

        // not implemented yet
        throw new OperationNotSupportedException( "changePassword is not yet implemented" );
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
