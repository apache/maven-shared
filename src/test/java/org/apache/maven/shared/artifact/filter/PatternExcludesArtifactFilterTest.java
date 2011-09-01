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

import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

public class PatternExcludesArtifactFilterTest
    extends TestCase
{

    private final PatternArtifactFilterTCK tck = new PatternArtifactFilterTCK()
    {

        @Override
        protected ArtifactFilter createFilter( final List patterns )
        {
            return new PatternExcludesArtifactFilter( patterns );
        }

        @Override
        protected ArtifactFilter createFilter( final List patterns, final boolean actTransitively )
        {
            return new PatternExcludesArtifactFilter( patterns, actTransitively );
        }

        @Override
        protected boolean isInclusionExpected()
        {
            return false;
        }

    };

    public void testShouldTriggerBothPatternsWithNonColonWildcards()
    {
        tck.testShouldTriggerBothPatternsWithNonColonWildcards();
    }

    public void testIncludeWhenPatternMatchesDepTrailWithTransitivityUsingNonColonWildcard()
    {
        tck.testIncludeWhenPatternMatchesDepTrailWithTransitivityUsingNonColonWildcard();
    }

    public void testShouldTriggerBothPatternsWithWildcards()
    {
        tck.testShouldTriggerBothPatternsWithWildcards();
    }

    public void testShouldNotIncludeDirectlyMatchedArtifactByDependencyConflictId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId();
    }

    public void testShouldNotIncludeDirectlyMatchedArtifactByGroupIdArtifactId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId();
    }

    public void testShouldNotIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled()
    {
        tck.testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled();
    }

    public void testShouldIncludeWhenArtifactIdDiffers()
    {
        tck.testShouldNotIncludeWhenArtifactIdDiffers();
    }

    public void testShouldIncludeWhenBothIdElementsDiffer()
    {
        tck.testShouldNotIncludeWhenBothIdElementsDiffer();
    }

    public void testShouldIncludeWhenGroupIdDiffers()
    {
        tck.testShouldNotIncludeWhenGroupIdDiffers();
    }

    public void testShouldIncludeWhenNegativeMatch()
    {
        tck.testShouldNotIncludeWhenNegativeMatch();
    }

    public void testShouldNotIncludeWhenWildcardMatchesInsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesInsideSequence();
    }

    public void testShouldIncludeWhenWildcardMatchesOutsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesOutsideSequence();
    }

    public void testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent()
    {
        tck.testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent();
    }

    public void testShouldIncludeWhenWildcardMatchesMiddleOfArtifactId()
    {
        tck.testShouldIncludeWhenWildcardMatchesMiddleOfArtifactId();
    }

    public void testShouldIncludeWhenWildcardCoversPartOfGroupIdAndEverythingElse()
    {
        tck.testShouldIncludeWhenWildcardCoversPartOfGroupIdAndEverythingElse();
    }

    public void testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId();
    }

    public void testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId();
    }

    public void testShouldNotIncludeWhenGroupIdDiffers()
    {
        tck.testShouldNotIncludeWhenGroupIdDiffers();
    }

    public void testShouldNotIncludeWhenArtifactIdDiffers()
    {
        tck.testShouldNotIncludeWhenArtifactIdDiffers();
    }

    public void testShouldNotIncludeWhenBothIdElementsDiffer()
    {
        tck.testShouldNotIncludeWhenBothIdElementsDiffer();
    }

    public void testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled()
    {
        tck.testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled();
    }

    public void testShouldNotIncludeWhenNegativeMatch()
    {
        tck.testShouldNotIncludeWhenNegativeMatch();
    }

    public void testShouldIncludeWhenWildcardMatchesInsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesInsideSequence();
    }

    // See comment in TCK.
    // public void testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild()
    // {
    // tck.testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild();
    // }
}
