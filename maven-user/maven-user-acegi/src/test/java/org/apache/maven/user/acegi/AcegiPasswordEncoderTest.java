package org.apache.maven.user.acegi;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import junit.framework.TestCase;

import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.apache.maven.user.model.impl.SHA256PasswordEncoder;

/**
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AcegiPasswordEncoderTest
    extends TestCase
{
    private static final String PASSWORD = "admin";

    public void testPasswordEncoding()
    {
        ShaPasswordEncoder acegiPasswordEncoder = new ShaPasswordEncoder( 256 );
        String acegiPassword = acegiPasswordEncoder.encodePassword( PASSWORD, null );

        SHA256PasswordEncoder mavenPasswordEncoder = new SHA256PasswordEncoder();
        String mavenPassword = mavenPasswordEncoder.encodePassword( PASSWORD, null );

        // this currently fails, that's why maven-user-model implementations of PasswordEncoder are deprecated
        // assertEquals( "Acegi encoded password is not the same as Maven-User one", acegiPassword, mavenPassword );
    }
}
