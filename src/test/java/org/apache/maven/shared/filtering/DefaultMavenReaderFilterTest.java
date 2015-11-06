package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import org.apache.maven.shared.utils.io.IOUtil;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author Kristian Rosenvold
 */
public class DefaultMavenReaderFilterTest
    extends PlexusTestCase
{
    public void testJustDoSomeFiltering()
        throws Exception
    {
        assertNotNull( DefaultMavenReaderFilter.class );
        MavenReaderFilter readerFilter = lookup( MavenReaderFilter.class );

        StringReader src = new StringReader( "toto@titi.com ${foo}" );
        MavenReaderFilterRequest req = new MavenReaderFilterRequest();
        Properties additionalProperties = new Properties();
        additionalProperties.setProperty( "foo", "bar" );
        req.setFrom( src );
        req.setFiltering( true );
        req.setAdditionalProperties( additionalProperties );

        final Reader filter = readerFilter.filter( req );

        assertEquals( "toto@titi.com bar", IOUtil.toString( filter ) );
    }
}
