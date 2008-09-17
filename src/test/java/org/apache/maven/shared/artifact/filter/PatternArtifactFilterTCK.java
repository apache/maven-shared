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

import java.util.ArrayList;
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

    private MockManager mockManager = new MockManager();

    protected abstract ArtifactFilter createFilter( List patterns );

    protected abstract ArtifactFilter createFilter( List patterns, boolean actTransitively );

    public void testShouldTriggerBothPatternsWithWildcards( boolean reverse )
    {
        String groupId1 = "group";
        String artifactId1 = "artifact";

        String groupId2 = "group2";
        String artifactId2 = "artifact2";

        ArtifactMockAndControl mac1 = new ArtifactMockAndControl( groupId1, artifactId1 );
        ArtifactMockAndControl mac2 = new ArtifactMockAndControl( groupId2, artifactId2 );

        mockManager.replayAll();

        List patterns = new ArrayList();
        patterns.add( groupId1 + ":" + artifactId1 + ":*" );
        patterns.add( groupId2 + ":" + artifactId2 + ":*" );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        ArtifactFilter filter = createFilter( Collections.singletonList( groupId + ":" + artifactId ) );

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

    public void testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        ArtifactFilter filter = createFilter( Collections.singletonList( groupId + ":" + artifactId + ":jar" ) );

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

    public void testShouldNotIncludeWhenGroupIdDiffers( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();
        List patterns = new ArrayList();

        patterns.add( "otherGroup:" + artifactId + ":jar" );
        patterns.add( "otherGroup:" + artifactId );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldNotIncludeWhenArtifactIdDiffers( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        List patterns = new ArrayList();

        patterns.add( groupId + "otherArtifact:jar" );
        patterns.add( groupId + "otherArtifact" );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldNotIncludeWhenBothIdElementsDiffer( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        List patterns = new ArrayList();

        patterns.add( "otherGroup:otherArtifact:jar" );
        patterns.add( "otherGroup:otherArtifact" );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        String depTrailItem = "otherGroup:otherArtifact";
        List depTrail = Collections.singletonList( depTrailItem + ":jar:1.0" );
        List patterns = Collections.singletonList( depTrailItem );

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, depTrail );

        mockManager.replayAll();

        ArtifactFilter filter = createFilter( patterns, true );

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

    public void testShouldNotIncludeWhenNegativeMatch( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        List patterns = new ArrayList();

        patterns.add( "!group:artifact:jar" );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldIncludeWhenWildcardMatchesInsideSequence( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        List patterns = new ArrayList();

        patterns.add( "group:*:jar" );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldIncludeWhenWildcardMatchesOutsideSequence( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId );

        mockManager.replayAll();

        List patterns = new ArrayList();

        patterns.add( "*:artifact:*" );

        ArtifactFilter filter = createFilter( patterns );

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

    public void testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent( boolean reverse )
    {
        String groupId = "group";
        String artifactId = "artifact";

        String otherGroup = "otherGroup";
        String otherArtifact = "otherArtifact";
        String otherType = "ejb";

        String depTrailItem = otherGroup + ":" + otherArtifact + ":" + otherType + ":version";
        List depTrail = Collections.singletonList( depTrailItem );
        List patterns = Collections.singletonList( "*:jar:*" );

        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, "jar", depTrail );
        ArtifactMockAndControl otherMac = new ArtifactMockAndControl( otherGroup, otherArtifact, otherType,
                                                                      Collections.EMPTY_LIST );

        mockManager.replayAll();

        ArtifactFilter filter = createFilter( patterns, true );

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
//    public void testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild(
//                                                                                                           boolean reverse )
//    {
//        String groupId = "group";
//        String artifactId = "artifact";
//
//        String otherGroup = "otherGroup";
//        String otherArtifact = "otherArtifact";
//        String otherType = "ejb";
//
//        String depTrailItem = otherGroup + ":" + otherArtifact + ":" + otherType + ":version";
//        List depTrail = Collections.singletonList( depTrailItem );
//        List patterns = Collections.singletonList( "!*:ejb:*" );
//
//        ArtifactMockAndControl mac = new ArtifactMockAndControl( groupId, artifactId, "jar", depTrail );
//        ArtifactMockAndControl otherMac = new ArtifactMockAndControl( otherGroup, otherArtifact, otherType, null );
//
//        mockManager.replayAll();
//
//        ArtifactFilter filter = createFilter( patterns, true );
//
//        if ( reverse )
//        {
//            assertTrue( filter.include( otherMac.artifact ) );
//            assertFalse( filter.include( mac.artifact ) );
//        }
//        else
//        {
//            assertFalse( filter.include( otherMac.artifact ) );
//            assertFalse( filter.include( mac.artifact ) );
//        }
//
//        mockManager.verifyAll();
//    }

    private final class ArtifactMockAndControl
    {
        MockControl control;

        Artifact artifact;

        String groupId;

        String artifactId;

        List dependencyTrail;

        String type;

        ArtifactMockAndControl( String groupId, String artifactId, List depTrail )
        {
            this( groupId, artifactId, "jar", depTrail );
        }

        ArtifactMockAndControl( String groupId, String artifactId, String type, List dependencyTrail )
        {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.dependencyTrail = dependencyTrail;
            this.type = type;

            control = MockControl.createControl( Artifact.class );
            mockManager.add( control );

            artifact = (Artifact) control.getMock();

            enableGetDependencyConflictId();
            enableGetGroupIdAndArtifactId();
            enableGetId();

            if ( dependencyTrail != null )
            {
                enableGetDependencyTrail();
            }
        }

        public ArtifactMockAndControl( String groupId, String artifactId )
        {
            this( groupId, artifactId, "jar", null );
        }

        public ArtifactMockAndControl( String groupId, String artifactId, String type )
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

        void enableGetGroupIdAndArtifactId()
        {
            artifact.getGroupId();
            control.setReturnValue( groupId, MockControl.ONE_OR_MORE );

            artifact.getArtifactId();
            control.setReturnValue( artifactId, MockControl.ONE_OR_MORE );
        }
    }

}
