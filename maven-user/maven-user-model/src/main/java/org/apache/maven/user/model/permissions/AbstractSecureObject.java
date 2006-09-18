package org.apache.maven.user.model.permissions;

import org.apache.maven.user.model.InstancePermissions;

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
 * Adds the permissions required for objects that have ACLs.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public abstract class AbstractSecureObject
    implements SecureObject
{
    private InstancePermissions permissions;

    public void setPermissions( InstancePermissions instancePermissions )
    {
        this.permissions = instancePermissions;
    }

    public InstancePermissions getPermissions()
    {
        return permissions;
    }
}
