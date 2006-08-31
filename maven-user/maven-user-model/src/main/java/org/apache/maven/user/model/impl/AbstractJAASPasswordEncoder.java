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

import org.apache.maven.user.model.Messages;
import org.apache.maven.user.model.PasswordEncoder;
import org.apache.maven.user.model.PasswordEncodingException;
import org.codehaus.plexus.util.StringUtils;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract Password Encoder that uses the {@link MessageDigest} from JAAS.
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractJAASPasswordEncoder
    implements PasswordEncoder
{
    private String algorithm;
    private Object systemSalt;

    public AbstractJAASPasswordEncoder( String algorithm )
    {
        this.algorithm = algorithm;
    }

    public void setSystemSalt( Object salt )
    {
        this.systemSalt = salt;
    }
    
    public String encodePassword( String rawPass, Object salt )
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance( this.algorithm );
            if ( salt != null )
            {
                md.update( salt.toString().getBytes( "UTF-8" ) ); //$NON-NLS-1$
            }
            md.update( rawPass.getBytes( "UTF-8" ) ); //$NON-NLS-1$

            byte raw[] = md.digest();
            String hash = ( new BASE64Encoder() ).encode( raw );
            return hash;
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new PasswordEncodingException( Messages
                .getString( "password.encoder.no.such.algoritm", this.algorithm ), e ); //$NON-NLS-1$
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new PasswordEncodingException( Messages.getString( "password.encoder.unsupported.encoding" ), e ); //$NON-NLS-1$
        }
    }

    public boolean isPasswordValid( String encPass, String rawPass, Object salt )
    {
        if ( StringUtils.isEmpty( encPass ) )
        {
            // TODO: Throw exception?
            return false;
        }

        if ( StringUtils.isEmpty( rawPass ) )
        {
            // TODO: Throw exception?
            return false;
        }

        String testPass = encodePassword( rawPass, salt );
        return ( encPass.equals( testPass ) );
    }

    public String encodePassword( String rawPass )
    {
        return encodePassword( rawPass, this.systemSalt );
    }

    public boolean isPasswordValid( String encPass, String rawPass )
    {
        return isPasswordValid( encPass, rawPass, this.systemSalt );
    }

}
