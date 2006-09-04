package org.apache.maven.user.model.impl;

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

import org.apache.maven.user.model.PasswordEncoder;

/**
 * SHA-1 Password Encoder.
 * 
 * @deprecated use AcegiPasswordEncoder from maven-user-acegi
 * 
 * @plexus.component role="org.apache.maven.user.model.PasswordEncoder" role-hint="sha1"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class SHA1PasswordEncoder
    extends AbstractJAASPasswordEncoder
    implements PasswordEncoder
{
    public SHA1PasswordEncoder()
    {
        super( "SHA-1" ); //$NON-NLS-1$
    }
}
