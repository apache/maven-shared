package org.apache.maven.continuum.security.acegi.aspectj;

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

import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.acegi.intercept.method.aspectj.SecurityAspect;

/**
 * <p>Acegi interceptor for {@link UserManager} method calls.</p>
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public aspect UserSecurityAspect extends SecurityAspect
{

    protected pointcut securedExecution():
        target(UserManager) &&
        execution(public * *(..)) &&
        !within(UserSecurityAspect);

}
