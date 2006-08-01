package org.apache.maven.user.acegi;

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
 * Bridge between Maven User {@link PasswordEncoder} and Acegi
 * {@link org.acegisecurity.providers.encoding.PasswordEncoder}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AcegiPasswordEncoder
    implements PasswordEncoder
{

    private org.acegisecurity.providers.encoding.PasswordEncoder encoder;

    public void setEncoder( org.acegisecurity.providers.encoding.PasswordEncoder encoder )
    {
        this.encoder = encoder;
    }

    public org.acegisecurity.providers.encoding.PasswordEncoder getEncoder()
    {
        return encoder;
    }

    /**
     * Delegates to Acegi encoder
     */
    public String encodePassword( String rawPass, Object salt )
    {
        return encoder.encodePassword( rawPass, salt );
    }

    /**
     * Delegates to Acegi encoder
     */
    public boolean isPasswordValid( String encPass, String rawPass, Object salt )
    {
        return isPasswordValid( encPass, rawPass, salt );
    }
}
