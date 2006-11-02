package org.apache.maven.shared.artifact.filter;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;

import java.util.List;

import junit.framework.TestCase;


public class PatternIncludesArtifactFilterTest
    extends TestCase
{
    private PatternArtifactFilterTCK tck = new PatternArtifactFilterTCK()
    {

        protected ArtifactFilter createFilter( List patterns )
        {
            return new PatternIncludesArtifactFilter( patterns );
        }

        protected ArtifactFilter createFilter( List patterns, boolean actTransitively )
        {
            return new PatternIncludesArtifactFilter( patterns, actTransitively );
        }
        
    };
    
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
    
    public void testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild()
    {
        tck.testShouldIncludeDirectDependencyWhenInvertedWildcardMatchesButDoesntMatchTransitiveChild( false );
    }
}
