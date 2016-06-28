package org.apache.maven.shared.artifact.filter.resolve.transform;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.resolve.AbstractFilter;
import org.apache.maven.shared.artifact.filter.resolve.AndFilter;
import org.apache.maven.shared.artifact.filter.resolve.ExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.FilterTransformer;
import org.apache.maven.shared.artifact.filter.resolve.OrFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternInclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;

/**
 * Makes it possible to use the TransformableFilters for Aether and as classic Maven ArtifactFilter. 
 * 
 * <strong>Note:</strong> the {@link AndFilter} and {@link ExclusionsFilter} are transformed to {@link ArtifactFilter}
 * implementations of Maven Core
 * 
 * @author Robert Scholte
 * @since 3.0
 */
public class ArtifactIncludeFilterTransformer implements FilterTransformer<ArtifactFilter>
{
    
    private boolean includeNullScope = true;
    
    private boolean actTransitivelyPattern = false; 
    
    /**
     * Used by {@link #transform(ScopeFilter)}
     * 
     * When filtering on artifacts it is possible that the scope is unknown.
     * Decide if artifact should be included if its scope is {@code null}, default is {@code true}
     * 
     * @param includeNullScope set to {@code false} if {@code null}-scoped Artifacts should not be included
     */
    public void setIncludeNullScope( boolean includeNullScope )
    {
        this.includeNullScope = includeNullScope;
    }
    
    /**
     * Used by {@link #transform(PatternExclusionsFilter)} and {@link #transform(PatternInclusionsFilter)} Determines
     * whether the include/exclude patterns will be applied to the transitive path of a given artifact. If {@code true},
     * and the current artifact is a transitive dependency brought in by another artifact which matches an inclusion or
     * exclusion pattern, then the current artifact has the same inclusion/exclusion logic applied to it as well.
     * Default is {@code false}
     * 
     * @param actTransitivelyPattern set to {@code true} if this artifact should be included/excluded just like one of
     *            its ancestors.
     */
    public void setActTransitivelyPattern( boolean actTransitivelyPattern )
    {
        this.actTransitivelyPattern = actTransitivelyPattern;
    }

    @Override
    public ArtifactFilter transform( final ScopeFilter scopeFilter )
    {
        return new ArtifactFilter()
        {
            @Override
            public boolean include( Artifact artifact )
            {
                if ( artifact.getScope() == null )
                {
                    return includeNullScope;
                }
                
                boolean isIncluded;
                
                if ( scopeFilter.getIncluded() != null )
                {
                    isIncluded = scopeFilter.getIncluded().contains( artifact.getScope() );
                }
                else
                {
                    isIncluded = true;
                }
                
                boolean isExcluded;

                if ( scopeFilter.getExcluded() != null )
                {
                    isExcluded = scopeFilter.getExcluded().contains( artifact.getScope() ); 
                }
                else
                {
                    isExcluded = false;
                }

                return isIncluded && !isExcluded;
            }
        };
    }

    @Override
    public AndArtifactFilter transform( AndFilter andFilter )
    {
        AndArtifactFilter filter = new AndArtifactFilter();

        for ( TransformableFilter subFilter : andFilter.getFilters() )
        {
            filter.add( subFilter.transform( this ) );
        }

        return filter;
    }

    @Override
    public ArtifactFilter transform( final ExclusionsFilter exclusionsFilter )
    {
        return new ExcludesArtifactFilter( new ArrayList<String>( exclusionsFilter.getExcludes() ) );
    }

    @Override
    public ArtifactFilter transform( OrFilter orFilter )
    {
        final Collection<ArtifactFilter> filters = new ArrayList<ArtifactFilter>( orFilter.getFilters().size() );

        for ( TransformableFilter subFilter : orFilter.getFilters() )
        {
            filters.add( subFilter.transform( this ) );
        }

        return new ArtifactFilter()
        {
            @Override
            public boolean include( Artifact artifact )
            {
                for ( ArtifactFilter filter : filters )
                {
                    if ( filter.include( artifact ) )
                    {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public ArtifactFilter transform( PatternExclusionsFilter patternExclusionsFilter )
    {
        return new PatternExcludesArtifactFilter( patternExclusionsFilter.getExcludes(), actTransitivelyPattern );
    }

    @Override
    public ArtifactFilter transform( PatternInclusionsFilter patternInclusionsFilter )
    {
        return new PatternIncludesArtifactFilter( patternInclusionsFilter.getIncludes(), actTransitivelyPattern );
    }

    @Override
    public ArtifactFilter transform( final AbstractFilter filter )
    {
        return new ArtifactFilter()
        {
            @Override
            public boolean include( Artifact artifact )
            {
                return filter.accept( new ArtifactIncludeNode( artifact ), null );
            }
        };
    }
}
