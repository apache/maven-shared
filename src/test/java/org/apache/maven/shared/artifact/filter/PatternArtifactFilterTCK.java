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
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.easymock.MockControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public abstract class PatternArtifactFilterTCK extends TestCase
{

    private final MockManager mockManager = new MockManager();

    protected abstract ArtifactFilter createFilter( List patterns );

    protected abstract ArtifactFilter createFilter( List patterns, boolean actTransitively );

    public void testShouldTriggerBothPatternsWithWildcards( final boolean reverse )
    {
        final String groupId1 = "group";
        final String artifactId1 = "artifact";

        final String groupId2 = "group2";
        final String artifactId2 = "artifact2";

        final ArtifactMockAndControl mac1 = new ArtifactMockAndControl( groupId1, artifactId1 );
        final ArtifactMockAndControl mac2 = new ArtifactMockAndControl( groupId2, artifactId2 );

        mockManager.replayAll();

        final List patterns = new ArrayList();
        patterns.add( groupId1 + ":" + artifactId1 + ":*" );
        patterns.add( groupId2 + ":" + artifactId2 + ":*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertFalse( filter.include( mac1.artifact ) );
            assertFalse( filter.include( mac2.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac1.artifact ) );
            assertTrue( filter.include( mac2.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldTriggerBothPatternsWithNonColonWildcards( final boolean reverse )
    {
        final String groupId1 = "group";
        final String artifactId1 = "artifact";

        final String groupId2 = "group2";
        final String artifactId2 = "artifact2";

        final ArtifactMockAndControl mac1 = new ArtifactMockAndControl( groupId1, artifactId1 );
        final ArtifactMockAndControl mac2 = new ArtifactMockAndControl( groupId2, artifactId2 );

        mockManager.replayAll();

        final List patterns = new ArrayList();
        patterns.add( groupId1 + "*" );
        patterns.add( groupId2 + "*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertFalse( filter.include( mac1.artifact ) );
            assertFalse( filter.include( mac2.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac1.artifact ) );
            assertTrue( filter.include( mac2.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( Collections.singletonList( groupId + ":" + artifactId ) );

        if ( reverse )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( Collections.singletonList( groupId + ":" + artifactId + ":jar" ) );

        if ( reverse )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenGroupIdDiffers( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();
        final List patterns = new ArrayList();

        patterns.add( "otherGroup:" + artifactId + ":jar" );
        patterns.add( "otherGroup:" + artifactId );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenArtifactIdDiffers( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List patterns = new ArrayList();

        patterns.add( groupId + "otherArtifact:jar" );
        patterns.add( groupId + "otherArtifact" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenBothIdElementsDiffer( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List patterns = new ArrayList();

        patterns.add( "otherGroup:otherArtifact:jar" );
        patterns.add( "otherGroup:otherArtifact" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final String rootDepTrailItem = "current:project:jar:1.0";
        final String depTrailItem = "otherGroup:otherArtifact";

        final List depTrail = Arrays.asList( new String[] { rootDepTrailItem, depTrailItem + ":jar:1.0" } );
        final List patterns = Collections.singletonList( depTrailItem );

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, depTrail );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( patterns, true );

        if ( reverse )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testIncludeWhenPatternMatchesDepTrailWithTransitivityUsingNonColonWildcard( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final String rootDepTrailItem = "current:project:jar:1.0";
        final String depTrailItem = "otherGroup:otherArtifact";

        final List depTrail = Arrays.asList( new String[] { rootDepTrailItem, depTrailItem + ":jar:1.0" } );
        final List patterns = Collections.singletonList( "otherGroup*" );

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, depTrail );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( patterns, true );

        if ( reverse )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenNegativeMatch( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List patterns = new ArrayList();

        patterns.add( "!group:artifact:jar" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenWildcardMatchesInsideSequence( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List patterns = new ArrayList();

        patterns.add( "group:*:jar" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenWildcardMatchesOutsideSequence( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List patterns = new ArrayList();

        patterns.add( "*:artifact:*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( reverse )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent( final boolean reverse )
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final String otherGroup = "otherGroup";
        final String otherArtifact = "otherArtifact";
        final String otherType = "ejb";

        final String depTrailItem = otherGroup + ":" + otherArtifact + ":" + otherType + ":version";
        final List depTrail = Collections.singletonList( depTrailItem );
        final List patterns = Collections.singletonList( "*:jar:*" );

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, "jar", depTrail );
        final ArtifactMockAndControl otherMac =
            new ArtifactMockAndControl( otherGroup, otherArtifact, otherType, Collections.EMPTY_LIST );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( patterns, true );

        if ( reverse )
        {
            assertTrue( filter.include( otherMac.artifact ) );
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( otherMac.artifact ) );
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    // FIXME: Not sure what this is even trying to test.
    // public void testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild(
    // boolean reverse )
    // {
    // String groupId = "group";
    // String artifactId = "artifact";
    //
    // String otherGroup = "otherGroup";
    // String otherArtifact = "otherArtifact";
    // String otherType = "ejb";
    //
    // String depTrailItem = otherGroup + ":" + otherArtifact + ":" + otherType + ":version";
    // List depTrail = Collections.singletonList( depTrailItem );
    // List patterns = Collections.singletonList( "!*:ejb:*" );
    //
    // ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, "jar", depTrail );
    // ArtifactMockAndControl otherMac = new ArtifactMockAndControl( otherGroup, otherArtifact, otherType, null );
    //
    // mockManager.replayAll();
    //
    // ArtifactFilter filter = createFilter( patterns, true );
    //
    // if ( reverse )
    // {
    // assertTrue( filter.include( otherMac.artifact ) );
    // assertFalse( filter.include( mac.artifact ) );
    // }
    // else
    // {
    // assertFalse( filter.include( otherMac.artifact ) );
    // assertFalse( filter.include( mac.artifact ) );
    // }
    //
    // mockManager.verifyAll();
    // }

    private final class ArtifactMockAndControl
    {
        MockControl control;

        Artifact artifact;

        String groupId;

        String artifactId;

        String version;

        List dependencyTrail;

        String type;

        ArtifactMockAndControl( final String groupId, final String artifactId, final List depTrail )
        {
            this( groupId, artifactId, "jar", depTrail );
        }

        ArtifactMockAndControl( final String groupId, final String artifactId, final String type,
                                final List dependencyTrail )
        {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.dependencyTrail = dependencyTrail;
            this.type = type;

            control = MockControl.createControl( Artifact.class );
            mockManager.add( control );

            artifact = (Artifact) control.getMock();

            enableGetDependencyConflictId();
            enableGetGroupIdArtifactIdAndVersion();
            enableGetId();

            if ( dependencyTrail != null )
            {
                enableGetDependencyTrail();
            }
        }

        public ArtifactMockAndControl( final String groupId, final String artifactId )
        {
            this( groupId, artifactId, "jar", null );
        }

        public ArtifactMockAndControl( final String groupId, final String artifactId, final String type )
        {
            this( groupId, artifactId, type, null );
        }

        void enableGetId()
        {
            artifact.getId();
            control.setReturnValue( groupId + ":" + artifactId + ":" + type + ":version", MockControl.ZERO_OR_MORE );
        }

        void enableGetDependencyTrail()
        {
            artifact.getDependencyTrail();
            control.setReturnValue( dependencyTrail, MockControl.ZERO_OR_MORE );
        }

        void enableGetDependencyConflictId()
        {
            artifact.getDependencyConflictId();
            control.setReturnValue( groupId + ":" + artifactId + ":" + type, MockControl.ONE_OR_MORE );
        }

        void enableGetGroupIdArtifactIdAndVersion()
        {
            artifact.getGroupId();
            control.setReturnValue( groupId, MockControl.ONE_OR_MORE );

            artifact.getArtifactId();
            control.setReturnValue( artifactId, MockControl.ONE_OR_MORE );

            artifact.getVersion();
            control.setReturnValue( version, MockControl.ZERO_OR_MORE );

        }
    }

}
