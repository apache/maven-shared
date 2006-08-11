package org.apache.maven.shared.jar;

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

import java.io.File;

/**
 * Jar Test Case
 */
public class JarTest
    extends AbstractJarTestCase
{
    private Jar getJar( String filename )
        throws JarException
    {
        try
        {
            File jarfile = new File( getSampleJarsDirectory(), filename );
            Jar jar = (Jar) lookup( Jar.ROLE );
            jar.setFile( jarfile );
            return jar;
        }
        catch ( Exception e )
        {
            throw new JarException( "Can't load the Jar component", e );
        }
    }

    public void testSealed()
        throws JarException
    {
        Jar evil = getJar( "evil-sealed-regex-1.0.jar" );
        assertTrue( evil.isSealed() );
    }

    public void testNotSealed()
        throws JarException
    {
        Jar codec = getJar( "codec.jar" );
        assertFalse( codec.isSealed() );
    }
}
