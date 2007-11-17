package org.apache.maven.shared.io.location;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.shared.io.logging.DefaultMessageHolder;
import org.apache.maven.shared.io.logging.MessageHolder;

import junit.framework.TestCase;

public class ClasspathResourceLocatorStrategyTest
    extends TestCase
{

    public void testShouldConstructWithNoParams()
    {
        new ClasspathResourceLocatorStrategy();
    }

    public void testShouldConstructWithTempFileOptions()
    {
        new ClasspathResourceLocatorStrategy( "prefix.", ".suffix", true );
    }

    public void testShouldFailToResolveMissingClasspathResource()
    {
        MessageHolder mh = new DefaultMessageHolder();
        Location location = new ClasspathResourceLocatorStrategy().resolve( "/some/missing/path", mh );

        assertNull( location );
        assertEquals( 1, mh.size() );
    }

    public void testShouldResolveExistingClasspathResourceWithoutPrecedingSlash()
    {
        MessageHolder mh = new DefaultMessageHolder();
        Location location = new ClasspathResourceLocatorStrategy().resolve( "META-INF/maven/test.properties", mh );

        assertNotNull( location );
        assertEquals( 0, mh.size() );
    }

}
