package org.apache.maven.shared.runtime;

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

import junit.framework.TestCase;

/**
 * Tests <code>MavenProjectProperties</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see MavenProjectProperties
 */
public class MavenProjectPropertiesTest extends TestCase
{
    // tests ------------------------------------------------------------------

    public void testConstructor()
    {
        MavenProjectProperties properties = new MavenProjectProperties( "a", "b", "c" );

        assertEquals( "Group id", "a", properties.getGroupId() );
        assertEquals( "Artifact id", "b", properties.getArtifactId() );
        assertEquals( "Version", "c", properties.getVersion() );
    }

    public void testConstructorNullGroupId()
    {
        try
        {
            new MavenProjectProperties( null, "b", "c" );

            fail( "IllegalArgumentException expected" );
        }
        catch ( IllegalArgumentException exception )
        {
            assertEquals( "groupId cannot be null", exception.getMessage() );
        }
    }

    public void testConstructorNullArtifactId()
    {
        try
        {
            new MavenProjectProperties( "a", null, "c" );

            fail( "IllegalArgumentException expected" );
        }
        catch ( IllegalArgumentException exception )
        {
            assertEquals( "artifactId cannot be null", exception.getMessage() );
        }
    }

    public void testConstructorNullVersion()
    {
        try
        {
            new MavenProjectProperties( "a", "b", null );

            fail( "IllegalArgumentException expected" );
        }
        catch ( IllegalArgumentException exception )
        {
            assertEquals( "version cannot be null", exception.getMessage() );
        }
    }

    public void testHashCodeEqual()
    {
        MavenProjectProperties properties1 = new MavenProjectProperties( "a", "b", "c" );
        MavenProjectProperties properties2 = new MavenProjectProperties( "a", "b", "c" );

        assertEquals( properties1.hashCode(), properties2.hashCode() );
    }

    public void testEqualsEqual()
    {
        MavenProjectProperties properties1 = new MavenProjectProperties( "a", "b", "c" );
        MavenProjectProperties properties2 = new MavenProjectProperties( "a", "b", "c" );

        assertEquals( properties1, properties2 );
    }

    public void testEqualsDifferentGroupId()
    {
        MavenProjectProperties properties1 = new MavenProjectProperties( "a", "b", "c" );
        MavenProjectProperties properties2 = new MavenProjectProperties( "x", "b", "c" );

        assertFalse( properties1.equals( properties2 ) );
    }

    public void testEqualsDifferentArtifactId()
    {
        MavenProjectProperties properties1 = new MavenProjectProperties( "a", "b", "c" );
        MavenProjectProperties properties2 = new MavenProjectProperties( "a", "x", "c" );

        assertFalse( properties1.equals( properties2 ) );
    }

    public void testEqualsDifferentVersion()
    {
        MavenProjectProperties properties1 = new MavenProjectProperties( "a", "b", "c" );
        MavenProjectProperties properties2 = new MavenProjectProperties( "a", "b", "x" );

        assertFalse( properties1.equals( properties2 ) );
    }

    public void testEqualsDifferentClass()
    {
        MavenProjectProperties properties = new MavenProjectProperties( "a", "b", "c" );

        assertFalse( properties.equals( new Object() ) );
    }

    public void testToString()
    {
        MavenProjectProperties properties = new MavenProjectProperties( "a", "b", "c" );

        assertEquals( "org.apache.maven.shared.runtime.MavenProjectProperties[groupId=a,artifactId=b,version=c]",
                      properties.toString() );
    }
}
