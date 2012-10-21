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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.tools.easymock.MockManager;
import org.easymock.MockControl;

public abstract class PatternArtifactFilterTCK
    extends TestCase
{

    private final MockManager mockManager = new MockManager();

    protected abstract ArtifactFilter createFilter( List<String> patterns );

    protected abstract ArtifactFilter createFilter( List<String> patterns, boolean actTransitively );

    protected abstract boolean isInclusionExpected();

    public void testShouldTriggerBothPatternsWithWildcards()
    {
        final String groupId1 = "group";
        final String artifactId1 = "artifact";

        final String groupId2 = "group2";
        final String artifactId2 = "artifact2";

        final ArtifactMockAndControl mac1 = new ArtifactMockAndControl( groupId1, artifactId1 );
        final ArtifactMockAndControl mac2 = new ArtifactMockAndControl( groupId2, artifactId2 );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();
        patterns.add( groupId1 + ":" + artifactId1 + ":*" );
        patterns.add( groupId2 + ":" + artifactId2 + ":*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
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

    public void testShouldTriggerBothPatternsWithNonColonWildcards()
    {
        final String groupId1 = "group";
        final String artifactId1 = "artifact";

        final String groupId2 = "group2";
        final String artifactId2 = "artifact2";

        final ArtifactMockAndControl mac1 = new ArtifactMockAndControl( groupId1, artifactId1 );
        final ArtifactMockAndControl mac2 = new ArtifactMockAndControl( groupId2, artifactId2 );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();
        patterns.add( groupId1 + "*" );
        patterns.add( groupId2 + "*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
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

    public void testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( Collections.singletonList( groupId + ":" + artifactId ) );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( Collections.singletonList( groupId + ":" + artifactId + ":jar" ) );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenGroupIdDiffers()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();
        final List<String> patterns = new ArrayList<String>();

        patterns.add( "otherGroup:" + artifactId + ":jar" );
        patterns.add( "otherGroup:" + artifactId );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenArtifactIdDiffers()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( groupId + "otherArtifact:jar" );
        patterns.add( groupId + "otherArtifact" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenBothIdElementsDiffer()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( "otherGroup:otherArtifact:jar" );
        patterns.add( "otherGroup:otherArtifact" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final String rootDepTrailItem = "current:project:jar:1.0";
        final String depTrailItem = "otherGroup:otherArtifact";

        final List<String> depTrail = Arrays.asList( new String[] { rootDepTrailItem, depTrailItem + ":jar:1.0" } );
        final List<String> patterns = Collections.singletonList( depTrailItem );

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, depTrail );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( patterns, true );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testIncludeWhenPatternMatchesDepTrailWithTransitivityUsingNonColonWildcard()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final String rootDepTrailItem = "current:project:jar:1.0";
        final String depTrailItem = "otherGroup:otherArtifact";

        final List<String> depTrail = Arrays.asList( new String[] { rootDepTrailItem, depTrailItem + ":jar:1.0" } );
        final List<String> patterns = Collections.singletonList( "otherGroup*" );

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, depTrail );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( patterns, true );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldNotIncludeWhenNegativeMatch()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( "!group:artifact:jar" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertTrue( filter.include( mac.artifact ) );
        }
        else
        {
            assertFalse( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenWildcardMatchesInsideSequence()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( "group:*:jar" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenWildcardMatchesOutsideSequence()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( "*:artifact:*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenWildcardMatchesMiddleOfArtifactId()
    {
        final String groupId = "group";
        final String artifactId = "some-artifact-id";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( "group:some-*-id" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeWhenWildcardCoversPartOfGroupIdAndEverythingElse()
    {
        final String groupId = "some.group.id";
        final String artifactId = "some-artifact-id";

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        final List<String> patterns = new ArrayList<String>();

        patterns.add( "some.group*" );

        final ArtifactFilter filter = createFilter( patterns );

        if ( !isInclusionExpected() )
        {
            assertFalse( filter.include( mac.artifact ) );
        }
        else
        {
            assertTrue( filter.include( mac.artifact ) );
        }

        mockManager.verifyAll();
    }

    public void testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent()
    {
        final String groupId = "group";
        final String artifactId = "artifact";

        final String otherGroup = "otherGroup";
        final String otherArtifact = "otherArtifact";
        final String otherType = "ejb";

        final String depTrailItem = otherGroup + ":" + otherArtifact + ":" + otherType + ":version";
        final List<String> depTrail = Collections.singletonList( depTrailItem );
        final List<String> patterns = Collections.singletonList( "*:jar:*" );

        final ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, "jar", depTrail );
        final ArtifactMockAndControl otherMac =
            new ArtifactMockAndControl( otherGroup, otherArtifact, otherType, Collections.<String> emptyList() );

        mockManager.replayAll();

        final ArtifactFilter filter = createFilter( patterns, true );

        if ( !isInclusionExpected() )
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
    // if ( isInclusionExpected() )
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

        List<String> dependencyTrail;

        String type;

        ArtifactMockAndControl( final String groupId, final String artifactId, final List<String> depTrail )
        {
            this( groupId, artifactId, "jar", depTrail );
        }

        ArtifactMockAndControl( final String groupId, final String artifactId, final String type,
                                final List<String> dependencyTrail )
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

        ArtifactMockAndControl( final String groupId, final String artifactId )
        {
            this( groupId, artifactId, "jar", null );
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
