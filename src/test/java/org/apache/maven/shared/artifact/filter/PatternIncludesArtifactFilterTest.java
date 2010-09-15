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

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import java.util.List;

import junit.framework.TestCase;

public class PatternIncludesArtifactFilterTest extends TestCase
{
    private final PatternArtifactFilterTCK tck = new PatternArtifactFilterTCK()
    {

        protected ArtifactFilter createFilter( final List patterns )
        {
            return new PatternIncludesArtifactFilter( patterns );
        }

        protected ArtifactFilter createFilter( final List patterns, final boolean actTransitively )
        {
            return new PatternIncludesArtifactFilter( patterns, actTransitively );
        }

    };

    public void testShouldTriggerBothPatternsWithNonColonWildcards()
    {
        tck.testShouldTriggerBothPatternsWithNonColonWildcards( false );
    }

    public void testShouldTriggerBothPatternsWithWildcards()
    {
        tck.testShouldTriggerBothPatternsWithWildcards( false );
    }

    public void testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId( false );
    }

    public void testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId( false );
    }

    public void testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled()
    {
        tck.testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled( false );
    }

    public void testIncludeWhenPatternMatchesDepTrailWithTransitivityUsingNonColonWildcard()
    {
        tck.testIncludeWhenPatternMatchesDepTrailWithTransitivityUsingNonColonWildcard( false );
    }

    public void testShouldNotIncludeWhenArtifactIdDiffers()
    {
        tck.testShouldNotIncludeWhenArtifactIdDiffers( false );
    }

    public void testShouldNotIncludeWhenBothIdElementsDiffer()
    {
        tck.testShouldNotIncludeWhenBothIdElementsDiffer( false );
    }

    public void testShouldNotIncludeWhenGroupIdDiffers()
    {
        tck.testShouldNotIncludeWhenGroupIdDiffers( false );
    }

    public void testShouldNotIncludeWhenNegativeMatch()
    {
        tck.testShouldNotIncludeWhenNegativeMatch( false );
    }

    public void testShouldIncludeWhenWildcardMatchesInsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesInsideSequence( false );
    }

    public void testShouldIncludeWhenWildcardMatchesOutsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesOutsideSequence( false );
    }

    public void testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent()
    {
        tck.testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent( false );
    }

    // See comment in TCK.
    // public void testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild()
    // {
    // tck.testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild( false );
    // }
}
