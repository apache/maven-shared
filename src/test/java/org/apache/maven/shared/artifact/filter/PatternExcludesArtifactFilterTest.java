package org.apache.maven.shared.artifact.filter;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;

import java.util.List;

import junit.framework.TestCase;


public class PatternExcludesArtifactFilterTest
    extends TestCase
{
    
    private PatternArtifactFilterTCK tck = new PatternArtifactFilterTCK()
    {

        protected ArtifactFilter createFilter( List patterns )
        {
            return new PatternExcludesArtifactFilter( patterns );
        }

        protected ArtifactFilter createFilter( List patterns, boolean actTransitively )
        {
            return new PatternExcludesArtifactFilter( patterns, actTransitively );
        }
        
    };
    
    public void testShouldNotIncludeDirectlyMatchedArtifactByDependencyConflictId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByDependencyConflictId( true );
    }

    public void testShouldNotIncludeDirectlyMatchedArtifactByGroupIdArtifactId()
    {
        tck.testShouldIncludeDirectlyMatchedArtifactByGroupIdArtifactId( true );
    }

    public void testShouldNotIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled()
    {
        tck.testShouldIncludeWhenPatternMatchesDependencyTrailAndTransitivityIsEnabled( true );
    }

    public void testShouldIncludeWhenArtifactIdDiffers()
    {
        tck.testShouldNotIncludeWhenArtifactIdDiffers( true );
    }

    public void testShouldIncludeWhenBothIdElementsDiffer()
    {
        tck.testShouldNotIncludeWhenBothIdElementsDiffer( true );
    }

    public void testShouldIncludeWhenGroupIdDiffers()
    {
        tck.testShouldNotIncludeWhenGroupIdDiffers( true );
    }

    public void testShouldIncludeWhenNegativeMatch()
    {
        tck.testShouldNotIncludeWhenNegativeMatch( true );
    }

    public void testShouldNotIncludeWhenWildcardMatchesInsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesInsideSequence( true );
    }

    public void testShouldIncludeWhenWildcardMatchesOutsideSequence()
    {
        tck.testShouldIncludeWhenWildcardMatchesOutsideSequence( true );
    }
    
    public void testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent()
    {
        tck.testShouldIncludeTransitiveDependencyWhenWildcardMatchesButDoesntMatchParent( true );
    }
    
    public void testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild()
    {
        tck.testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild( true );
    }
}
