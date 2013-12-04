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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.plexus.PlexusTestCase;

import static org.easymock.EasyMock.*;

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

    public void testNullScopeDisabled()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeNullScope( false );
        
        verifyExcluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_TestScope()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeTestScope( true );
        
        verifyExcluded( filter, Artifact.SCOPE_COMPILE );
        verifyExcluded( filter, Artifact.SCOPE_PROVIDED );
        verifyExcluded( filter, Artifact.SCOPE_RUNTIME );
        verifyExcluded( filter, Artifact.SCOPE_SYSTEM );
        verifyIncluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_CompileScope()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeCompileScope( true );
        
        verifyIncluded( filter, Artifact.SCOPE_COMPILE );
        verifyExcluded( filter, Artifact.SCOPE_PROVIDED );
        verifyExcluded( filter, Artifact.SCOPE_RUNTIME );
        verifyExcluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_RuntimeScope()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeRuntimeScope( true );
        
        verifyExcluded( filter, Artifact.SCOPE_COMPILE );
        verifyExcluded( filter, Artifact.SCOPE_PROVIDED );
        verifyIncluded( filter, Artifact.SCOPE_RUNTIME );
        verifyExcluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_ProvidedScope()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeProvidedScope( true );
        
        verifyExcluded( filter, Artifact.SCOPE_COMPILE );
        verifyIncluded( filter, Artifact.SCOPE_PROVIDED );
        verifyExcluded( filter, Artifact.SCOPE_RUNTIME );
        verifyExcluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_SystemScope()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeSystemScope( true );
        
        verifyExcluded( filter, Artifact.SCOPE_COMPILE );
        verifyExcluded( filter, Artifact.SCOPE_PROVIDED );
        verifyExcluded( filter, Artifact.SCOPE_RUNTIME );
        verifyIncluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_ProvidedAndRuntimeScopes()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeRuntimeScope( true );
        filter.setIncludeProvidedScope( true );
        
        verifyExcluded( filter, Artifact.SCOPE_COMPILE );
        verifyIncluded( filter, Artifact.SCOPE_PROVIDED );
        verifyIncluded( filter, Artifact.SCOPE_RUNTIME );
        verifyExcluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrained_IncludeOnlyScopesThatWereEnabled_SystemAndRuntimeScopes()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeRuntimeScope( true );
        filter.setIncludeSystemScope( true );
        
        verifyExcluded( filter, Artifact.SCOPE_COMPILE );
        verifyExcluded( filter, Artifact.SCOPE_PROVIDED );
        verifyIncluded( filter, Artifact.SCOPE_RUNTIME );
        verifyIncluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
        verifyIncluded( filter, null );
    }

    public void testFineGrainedWithImplications_CompileScopeShouldIncludeOnlyArtifactsWithNullSystemProvidedOrCompileScopes()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeCompileScopeWithImplications( true );

        verifyIncluded( filter, null );
        verifyIncluded( filter, Artifact.SCOPE_COMPILE );
        verifyIncluded( filter, Artifact.SCOPE_PROVIDED );
        verifyIncluded( filter, Artifact.SCOPE_SYSTEM );

        verifyExcluded( filter, Artifact.SCOPE_RUNTIME );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
    }

    public void testFineGrainedWithImplications_RuntimeScopeShouldIncludeOnlyArtifactsWithNullRuntimeOrCompileScopes()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeRuntimeScopeWithImplications( true );

        verifyIncluded( filter, null );
        verifyIncluded( filter, Artifact.SCOPE_COMPILE );
        verifyIncluded( filter, Artifact.SCOPE_RUNTIME );

        verifyExcluded( filter, Artifact.SCOPE_PROVIDED );
        verifyExcluded( filter, Artifact.SCOPE_SYSTEM );
        verifyExcluded( filter, Artifact.SCOPE_TEST );
    }

    public void testFineGrainedWithImplications_TestScopeShouldIncludeAllScopes()
    {
        ScopeArtifactFilter filter = new ScopeArtifactFilter();
        filter.setIncludeTestScopeWithImplications( true );

        verifyIncluded( filter, null );
        verifyIncluded( filter, Artifact.SCOPE_COMPILE );
        verifyIncluded( filter, Artifact.SCOPE_RUNTIME );

        verifyIncluded( filter, Artifact.SCOPE_PROVIDED );
        verifyIncluded( filter, Artifact.SCOPE_SYSTEM );
        verifyIncluded( filter, Artifact.SCOPE_TEST );
    }
    
    public void testScopesShouldIncludeArtifactWithSameScope()
    {
        verifyIncluded( Artifact.SCOPE_COMPILE, Artifact.SCOPE_COMPILE );
        verifyIncluded( Artifact.SCOPE_PROVIDED, Artifact.SCOPE_PROVIDED );
        verifyIncluded( Artifact.SCOPE_RUNTIME, Artifact.SCOPE_RUNTIME );
        verifyIncluded( Artifact.SCOPE_SYSTEM, Artifact.SCOPE_SYSTEM );
        verifyIncluded( Artifact.SCOPE_TEST, Artifact.SCOPE_TEST );
        verifyIncluded( (String) null, null );
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
        Artifact artifact = createMockArtifact( artifactScope );

        replay( artifact );

        ArtifactFilter filter = new ScopeArtifactFilter( filterScope );

        assertTrue( "Artifact scope: " + artifactScope + " NOT included using filter scope: " + filterScope, filter
            .include( artifact ) );

        verify( artifact );
    }

    private void verifyExcluded( String filterScope, String artifactScope )
    {
        Artifact artifact = createMockArtifact( artifactScope );
        
        replay( artifact );

        ArtifactFilter filter = new ScopeArtifactFilter( filterScope );

        assertFalse( "Artifact scope: " + artifactScope + " NOT excluded using filter scope: " + filterScope, filter
            .include( artifact ) );

        verify( artifact );
    }

    private void verifyIncluded( ScopeArtifactFilter filter, String artifactScope )
    {
        Artifact artifact = createMockArtifact( artifactScope );
                
        replay( artifact );

        assertTrue( "Artifact scope: " + artifactScope + " SHOULD BE included", filter.include( artifact ) );

        verify( artifact );
    }

    private void verifyExcluded( ScopeArtifactFilter filter, String artifactScope )
    {
        Artifact artifact = createMockArtifact( artifactScope );
                
        replay( artifact );

        assertFalse( "Artifact scope: " + artifactScope + " SHOULD BE excluded", filter.include( artifact ) );

        verify( artifact );
    }

    private Artifact createMockArtifact( String scope )
    {
        Artifact artifact = createMock( Artifact.class );

        expect( artifact.getScope() ).andReturn( scope ).anyTimes();
        expect( artifact.getId() ).andReturn( "group:artifact:type:version" ).anyTimes();
        expect( artifact.getVersionRange() ).andReturn( null ).anyTimes();

        return artifact;
    }

}
