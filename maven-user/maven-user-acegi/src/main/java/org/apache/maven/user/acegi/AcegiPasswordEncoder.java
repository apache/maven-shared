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

import org.acegisecurity.providers.encoding.MessageDigestPasswordEncoder;
import org.apache.maven.user.model.PasswordEncoder;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Bridge between Maven User {@link PasswordEncoder} and Acegi {@link MessageDigestPasswordEncoder}
 * 
 * @plexus.component role="org.apache.maven.user.model.PasswordEncoder" role-hint="acegi"
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AcegiPasswordEncoder
    implements PasswordEncoder, org.acegisecurity.providers.encoding.PasswordEncoder, Initializable
{
    private Object systemSalt;

    private org.acegisecurity.providers.encoding.PasswordEncoder encoder;

    /**
     * @plexus.configuration default-value="SHA-256"
     */
    private String algorithm;

    public void initialize()
        throws InitializationException
    {
        encoder = new MessageDigestPasswordEncoder( algorithm );
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
        return encoder.isPasswordValid( encPass, rawPass, salt );
    }

    public String encodePassword( String rawPass )
    {
        return encodePassword( rawPass, systemSalt );
    }

    public boolean isPasswordValid( String encPass, String rawPass )
    {
        return isPasswordValid( encPass, rawPass, systemSalt );
    }

    public void setSystemSalt( Object salt )
    {
        this.systemSalt = salt;
    }
}
