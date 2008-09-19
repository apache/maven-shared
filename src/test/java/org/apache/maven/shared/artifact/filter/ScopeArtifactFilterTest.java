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
package org.apache.maven.shared.artifact.filter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.codehaus.plexus.PlexusTestCase;
import org.easymock.MockControl;

public class ScopeArtifactFilterTest
    extends PlexusTestCase
{
    
    public void testExcludedArtifactWithRangeShouldNotCauseNPE()
        throws Exception
    {
        ArtifactFactory factory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
        
        Artifact excluded = factory.createDependencyArtifact( "group", "artifact", VersionRange.createFromVersionSpec( "[1.2.3]" ), "jar", null, Artifact.SCOPE_PROVIDED );
        
        ArtifactFilter filter = new ScopeArtifactFilter( Artifact.SCOPE_RUNTIME );
        
        assertFalse( filter.include( excluded ) );
    }

    private MockManager mockManager = new MockManager();

    public void testScopesShouldIncludeArtifactWithSameScope()
    {
        verifyIncluded( Artifact.SCOPE_COMPILE, Artifact.SCOPE_COMPILE );
        verifyIncluded( Artifact.SCOPE_PROVIDED, Artifact.SCOPE_PROVIDED );
        verifyIncluded( Artifact.SCOPE_RUNTIME, Artifact.SCOPE_RUNTIME );
        verifyIncluded( Artifact.SCOPE_SYSTEM, Artifact.SCOPE_SYSTEM );
        verifyIncluded( Artifact.SCOPE_TEST, Artifact.SCOPE_TEST );
        verifyIncluded( null, null );
    }

    public void testCompileScopeShouldIncludeOnlyArtifactsWithNullSystemProvidedOrCompileScopes()
    {
        String scope = Artifact.SCOPE_COMPILE;

        verifyIncluded( scope, null );
        verifyIncluded( scope, Artifact.SCOPE_COMPILE );
        verifyIncluded( scope, Artifact.SCOPE_PROVIDED );
        verifyIncluded( scope, Artifact.SCOPE_SYSTEM );

        verifyExcluded( scope, Artifact.SCOPE_RUNTIME );
        verifyExcluded( scope, Artifact.SCOPE_TEST );
    }

    public void testRuntimeScopeShouldIncludeOnlyArtifactsWithNullRuntimeOrCompileScopes()
    {
        String scope = Artifact.SCOPE_RUNTIME;

        verifyIncluded( scope, null );
        verifyIncluded( scope, Artifact.SCOPE_COMPILE );
        verifyIncluded( scope, Artifact.SCOPE_RUNTIME );

        verifyExcluded( scope, Artifact.SCOPE_PROVIDED );
        verifyExcluded( scope, Artifact.SCOPE_SYSTEM );
        verifyExcluded( scope, Artifact.SCOPE_TEST );
    }

    public void testTestScopeShouldIncludeAllScopes()
    {
        String scope = Artifact.SCOPE_TEST;

        verifyIncluded( scope, null );
        verifyIncluded( scope, Artifact.SCOPE_COMPILE );
        verifyIncluded( scope, Artifact.SCOPE_RUNTIME );

        verifyIncluded( scope, Artifact.SCOPE_PROVIDED );
        verifyIncluded( scope, Artifact.SCOPE_SYSTEM );
        verifyIncluded( scope, Artifact.SCOPE_TEST );
    }

    public void testProvidedScopeShouldIncludeOnlyArtifactsWithNullOrProvidedScopes()
    {
        String scope = Artifact.SCOPE_PROVIDED;

        verifyIncluded( scope, null );
        verifyExcluded( scope, Artifact.SCOPE_COMPILE );
        verifyExcluded( scope, Artifact.SCOPE_RUNTIME );

        verifyIncluded( scope, Artifact.SCOPE_PROVIDED );

        verifyExcluded( scope, Artifact.SCOPE_SYSTEM );
        verifyExcluded( scope, Artifact.SCOPE_TEST );
    }

    public void testSystemScopeShouldIncludeOnlyArtifactsWithNullOrSystemScopes()
    {
        String scope = Artifact.SCOPE_SYSTEM;

        verifyIncluded( scope, null );
        verifyExcluded( scope, Artifact.SCOPE_COMPILE );
        verifyExcluded( scope, Artifact.SCOPE_RUNTIME );
        verifyExcluded( scope, Artifact.SCOPE_PROVIDED );

        verifyIncluded( scope, Artifact.SCOPE_SYSTEM );

        verifyExcluded( scope, Artifact.SCOPE_TEST );
    }

    private void verifyIncluded( String filterScope, String artifactScope )
    {
        ArtifactMockAndControl mac = new ArtifactMockAndControl( artifactScope );

        mockManager.replayAll();

        ArtifactFilter filter = new ScopeArtifactFilter( filterScope );

        assertTrue( "Artifact scope: " + artifactScope + " NOT included using filter scope: " + filterScope, filter
            .include( mac.artifact ) );

        mockManager.verifyAll();

        // enable multiple calls to this method within a single test.
        mockManager.clear();
    }

    private void verifyExcluded( String filterScope, String artifactScope )
    {
        ArtifactMockAndControl mac = new ArtifactMockAndControl( artifactScope );

        mockManager.replayAll();

        ArtifactFilter filter = new ScopeArtifactFilter( filterScope );

        assertFalse( "Artifact scope: " + artifactScope + " NOT excluded using filter scope: " + filterScope, filter
            .include( mac.artifact ) );

        mockManager.verifyAll();

        // enable multiple calls to this method within a single test.
        mockManager.clear();
    }

    private final class ArtifactMockAndControl
    {
        Artifact artifact;

        MockControl control;

        ArtifactMockAndControl( String scope )
        {
            control = MockControl.createControl( Artifact.class );
            mockManager.add( control );

            artifact = (Artifact) control.getMock();

            artifact.getScope();
            control.setReturnValue( scope, MockControl.ZERO_OR_MORE );
            
            artifact.getId();
            control.setReturnValue( "group:artifact:type:version", MockControl.ZERO_OR_MORE );
            
            artifact.getVersionRange();
            control.setReturnValue( null, MockControl.ZERO_OR_MORE );
        }
    }

}
