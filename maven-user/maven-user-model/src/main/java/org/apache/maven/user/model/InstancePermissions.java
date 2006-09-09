package org.apache.maven.user.model;

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

/**
 * User instance permissions
 * 
 * @author <a href="mailto:hisidro@exist.com">Henry Isidro</a>
 */
public class InstancePermissions
{
    private User user;

    private boolean view;

    private boolean edit;

    private boolean delete;

    private boolean build;

    public InstancePermissions()
    {
        this.view = false;
        this.edit = false;
        this.delete = false;
        this.build = false;
    }

    public InstancePermissions( User user )
    {
        this.user = user;
        this.view = false;
        this.edit = false;
        this.delete = false;
        this.build = false;
    }

    public boolean isBuild()
    {
        return build;
    }

    public void setBuild( boolean build )
    {
        this.build = build;
    }

    public boolean isDelete()
    {
        return delete;
    }

    public void setDelete( boolean delete )
    {
        this.delete = delete;
    }

    public boolean isEdit()
    {
        return edit;
    }

    public void setEdit( boolean edit )
    {
        this.edit = edit;
    }

    public boolean isView()
    {
        return view;
    }

    public void setView( boolean view )
    {
        this.view = view;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }
}
