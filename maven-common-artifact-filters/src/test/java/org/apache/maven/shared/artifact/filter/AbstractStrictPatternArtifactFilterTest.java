package org.apache.maven.shared.artifact.filter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Tests subclasses of <code>AbstractStrictPatternArtifactFilter</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see AbstractStrictPatternArtifactFilter
 */
public abstract class AbstractStrictPatternArtifactFilterTest extends TestCase
{
    // fields -----------------------------------------------------------------

    protected Artifact artifact;

    // TestCase methods -------------------------------------------------------

    /*
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        artifact = createArtifact( "groupId", "artifactId", "type", "version" );
    }

    // tests ------------------------------------------------------------------

    public void testExactIncluded()
    {
        assertIncluded( "groupId:artifactId" );
    }

    public void testExactExcluded()
    {
        assertExcluded( "differentGroupId:differentArtifactId" );
    }

    public void testGroupIdIncluded()
    {
        assertIncluded( "groupId" );
    }

    public void testGroupIdExcluded()
    {
        assertExcluded( "differentGroupId" );
    }

    public void testGroupIdWildcardIncluded()
    {
        assertIncluded( "*" );
    }

    public void testGroupIdImplicitWildcardIncluded()
    {
        assertIncluded( "" );
    }

    public void testGroupIdStartsWithWildcardIncluded()
    {
        assertIncluded( "groupId*" );
    }

    public void testGroupIdStartsWithPartialWildcardIncluded()
    {
        assertIncluded( "group*" );
    }

    public void testGroupIdStartsWithWildcardExcluded()
    {
        assertExcluded( "different*" );
    }

    public void testGroupIdEndsWithWildcardIncluded()
    {
        assertIncluded( "*groupId" );
    }

    public void testGroupIdEndsWithPartialWildcardIncluded()
    {
        assertIncluded( "*Id" );
    }

    public void testGroupIdEndsWithWildcardExcluded()
    {
        assertExcluded( "*different" );
    }

    public void testGroupIdContainsWildcardIncluded()
    {
        assertIncluded( "*oup*" );
    }

    public void testGroupIdContainsWildcardExcluded()
    {
        assertExcluded( "*different*" );
    }

    public void testArtifactIdIncluded()
    {
        assertIncluded( ":artifactId" );
    }

    public void testArtifactIdExcluded()
    {
        assertExcluded( ":differentArtifactId" );
    }

    public void testArtifactIdWildcardIncluded()
    {
        assertIncluded( ":*" );
    }

    public void testArtifactIdImplicitWildcardIncluded()
    {
        assertIncluded( ":" );
    }

    public void testArtifactIdStartsWithWildcardIncluded()
    {
        assertIncluded( ":artifactId*" );
    }

    public void testArtifactIdStartsWithPartialWildcardIncluded()
    {
        assertIncluded( ":artifact*" );
    }

    public void testArtifactIdStartsWithWildcardExcluded()
    {
        assertExcluded( ":different*" );
    }

    public void testArtifactIdEndsWithWildcardIncluded()
    {
        assertIncluded( ":*artifactId" );
    }

    public void testArtifactIdEndsWithPartialWildcardIncluded()
    {
        assertIncluded( ":*Id" );
    }

    public void testArtifactIdEndsWithWildcardExcluded()
    {
        assertExcluded( ":*different" );
    }

    public void testArtifactIdContainsWildcardIncluded()
    {
        assertIncluded( ":*fact*" );
    }

    public void testArtifactIdContainsWildcardExcluded()
    {
        assertExcluded( ":*different*" );
    }

    public void testTypeIncluded()
    {
        assertIncluded( "::type" );
    }

    public void testTypeExcluded()
    {
        assertExcluded( "::differentType" );
    }

    public void testTypeWildcardIncluded()
    {
        assertIncluded( "::*" );
    }

    public void testTypeImplicitWildcardIncluded()
    {
        assertIncluded( "::" );
    }

    public void testTypeStartsWithWildcardIncluded()
    {
        assertIncluded( "::type*" );
    }

    public void testTypeStartsWithPartialWildcardIncluded()
    {
        assertIncluded( "::t*" );
    }

    public void testTypeStartsWithWildcardExcluded()
    {
        assertExcluded( "::different*" );
    }

    public void testTypeEndsWithWildcardIncluded()
    {
        assertIncluded( "::*type" );
    }

    public void testTypeEndsWithPartialWildcardIncluded()
    {
        assertIncluded( "::*e" );
    }

    public void testTypeEndsWithWildcardExcluded()
    {
        assertExcluded( "::*different" );
    }

    public void testTypeContainsWildcardIncluded()
    {
        assertIncluded( "::*yp*" );
    }

    public void testTypeContainsWildcardExcluded()
    {
        assertExcluded( "::*different*" );
    }

    public void testVersionIncluded()
    {
        assertIncluded( ":::version" );
    }

    public void testVersionExcluded()
    {
        assertExcluded( ":::differentVersion" );
    }

    public void testVersionWildcardIncluded()
    {
        assertIncluded( ":::*" );
    }

    public void testVersionImplicitWildcardIncluded()
    {
        assertIncluded( ":::" );
    }

    public void testVersionStartsWithWildcardIncluded()
    {
        assertIncluded( ":::version*" );
    }

    public void testVersionStartsWithPartialWildcardIncluded()
    {
        assertIncluded( ":::ver*" );
    }

    public void testVersionStartsWithWildcardExcluded()
    {
        assertExcluded( ":::different*" );
    }

    public void testVersionEndsWithWildcardIncluded()
    {
        assertIncluded( ":::*version" );
    }

    public void testVersionEndsWithPartialWildcardIncluded()
    {
        assertIncluded( ":::*ion" );
    }

    public void testVersionEndsWithWildcardExcluded()
    {
        assertExcluded( ":::*different" );
    }

    public void testVersionContainsWildcardIncluded()
    {
        assertIncluded( ":::*si*" );
    }

    public void testVersionContainsWildcardExcluded()
    {
        assertExcluded( ":::*different*" );
    }

    public void testComplex()
    {
        assertIncluded( "group*:*Id:*:version" );
    }

    public void testSnapshotVersion()
    {
        artifact = createArtifact( "groupId", "artifactId", "type", "version-12345678.123456-1" );

        assertIncluded( ":::*-SNAPSHOT" );
    }
    
    public void testRangeVersion()
    {
        artifact = createArtifact( "groupId", "artifactId", "type", "1.0.1" );
        assertIncluded( "groupId:artifactId:type:[1.0.1]");
        assertIncluded( "groupId:artifactId:type:[1.0,1.1)");
        
        assertExcluded( "groupId:artifactId:type:[1.5,)");
        assertExcluded( "groupId:artifactId:type:(,1.0],[1.2,)");
        assertExcluded( "groupId:artifactId:type:(,1.0],[1.2,)");
    }

    public void testWildcardsWithRangeVersion()
    {
        artifact = createArtifact( "groupId", "artifactId", "type", "1.0.1" );
        assertIncluded( ":::[1.0.1]");
        assertIncluded( ":artifact*:*:[1.0,1.1)");
        
        assertExcluded( "*group*:*:t*e:[1.5,)");

        artifact = createArtifact( "test", "uf", "jar", "0.2.0" );
        assertIncluded( "test:*:*:[0.0.2,)" );
    	
    }
    
    // protected methods ------------------------------------------------------

    /**
     * Creates an artifact with the specified attributes.
     * 
     * @param groupId
     *            the group id for the new artifact
     * @param artifactId
     *            the artifact id for the new artifact
     * @param type
     *            the type for the new artifact
     * @param version
     *            the version for the new artifact
     * @return the artifact
     */
    protected Artifact createArtifact( String groupId, String artifactId, String type, String version )
    {
        VersionRange versionRange = VersionRange.createFromVersion( version );
        ArtifactHandler handler = new DefaultArtifactHandler();

        return new DefaultArtifact( groupId, artifactId, versionRange, null, type, null, handler );
    }

    /**
     * Asserts that the specified pattern is included by the filter being tested.
     * 
     * @param pattern
     *            the pattern to test for inclusion
     * @throws AssertionFailedError
     *             if the assertion fails
     */
    protected void assertIncluded( String pattern )
    {
        assertFilter( true, pattern );
    }

    /**
     * Asserts that the specified pattern is excluded by the filter being tested.
     * 
     * @param pattern
     *            the pattern to test for exclusion
     * @throws AssertionFailedError
     *             if the assertion fails
     */
    protected void assertExcluded( String pattern )
    {
        assertFilter( false, pattern );
    }

    /**
     * Asserts that the filter being tested returns the specified result for the specified pattern.
     * 
     * @param expected
     *            the result expected from the filter
     * @param pattern
     *            the pattern to test
     * @throws AssertionFailedError
     *             if the assertion fails
     */
    protected void assertFilter( boolean expected, String pattern )
    {
        List patterns = Collections.singletonList( pattern );
        AbstractStrictPatternArtifactFilter filter = createFilter( patterns );

        assertEquals( expected, filter.include( artifact ) );
    }

    /**
     * Creates the strict pattern artifact filter to test for the specified patterns.
     * 
     * @param patterns
     *            the list of artifact patterns that the filter should match
     * @return the filter to test
     */
    protected abstract AbstractStrictPatternArtifactFilter createFilter( List patterns );
}
