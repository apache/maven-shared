package org.apache.maven.shared.artifact.resolver;

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

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.apache.maven.artifact.Artifact;
import org.junit.Test;

public class CumlativeScopeArtifactFilterTest
{

    @Test
    public void testNothingEnabledWhenNoScopesAdded()
    {
        assertScopeInclusion( false, false, false, false );
    }

    @Test
    public void testProvidedOnlyEnabledWhenProvidedScopeOnlyIsAdded()
    {
        assertScopeInclusion( false, false, false, true, Artifact.SCOPE_PROVIDED );
    }

    @Test
    public void testCompileAndProvidedEnabledWhenCompileScopeOnlyIsAdded()
    {
        assertScopeInclusion( true, false, false, true, Artifact.SCOPE_COMPILE );
    }

    @Test
    public void testCompileAndRuntimeEnabledWhenRuntimeScopeOnlyIsAdded()
    {
        assertScopeInclusion( true, false, true, false, Artifact.SCOPE_RUNTIME );
    }

    @Test
    public void testCompileRuntimeProvidedAndTestEnabledWhenTestScopeOnlyIsAdded()
    {
        assertScopeInclusion( true, true, true, true, Artifact.SCOPE_TEST );
    }

    @Test
    public void testCompileRuntimeProvidedAndTestEnabledWhenTestAndCompileScopesAreAdded()
    {
        assertScopeInclusion( true, true, true, true, Artifact.SCOPE_TEST, Artifact.SCOPE_COMPILE );
    }

    @Test
    public void testCompileRuntimeAndProvidedEnabledWhenRuntimeAndCompileScopeAreAdded()
    {
        assertScopeInclusion( true, false, true, true, Artifact.SCOPE_RUNTIME, Artifact.SCOPE_COMPILE );
    }

    private void assertScopeInclusion( boolean compileIncluded, boolean testIncluded, boolean runtimeIncluded,
                                       boolean providedIncluded, String...scopes )
    {
        CumulativeScopeArtifactFilter filter = new CumulativeScopeArtifactFilter();
        if ( scopes != null && scopes.length > 0 )
        {
            for ( String scope : scopes )
            {
                filter.addScope( scope );
            }
        }

        Artifact compile = createMock( Artifact.class );
        expect( compile.getScope() ).andReturn( Artifact.SCOPE_COMPILE ).anyTimes();

        Artifact runtime = createMock( Artifact.class );
        expect( runtime.getScope() ).andReturn( Artifact.SCOPE_RUNTIME ).anyTimes();

        Artifact test = createMock( Artifact.class );
        expect( test.getScope() ).andReturn( Artifact.SCOPE_TEST ).anyTimes();

        Artifact provided = createMock( Artifact.class );
        expect( provided.getScope() ).andReturn( Artifact.SCOPE_PROVIDED ).anyTimes();

        replay( compile, runtime, test, provided );

        assertEquals( "Compile scope should " + ( compileIncluded ? "" : "not " ) + "be included.", compileIncluded,
                      filter.include( compile ) );

        assertEquals( "Runtime scope should " + ( runtimeIncluded ? "" : "not " ) + "be included.", runtimeIncluded,
                      filter.include( runtime ) );

        assertEquals( "Test scope should " + ( testIncluded ? "" : "not " ) + "be included.", testIncluded, filter.include( test ) );

        assertEquals( "Provided scope should " + ( providedIncluded ? "" : "not " ) + "be included.", providedIncluded,
                      filter.include( provided ) );

        verify( compile, runtime, test, provided );
    }
}
