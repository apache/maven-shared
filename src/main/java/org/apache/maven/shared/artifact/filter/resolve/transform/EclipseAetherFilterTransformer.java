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
import java.util.List;

import org.apache.maven.shared.artifact.filter.resolve.AbstractFilter;
import org.apache.maven.shared.artifact.filter.resolve.AndFilter;
import org.apache.maven.shared.artifact.filter.resolve.ExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.FilterTransformer;
import org.apache.maven.shared.artifact.filter.resolve.OrFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternInclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.filter.AndDependencyFilter;
import org.eclipse.aether.util.filter.ExclusionsDependencyFilter;
import org.eclipse.aether.util.filter.OrDependencyFilter;
import org.eclipse.aether.util.filter.PatternExclusionsDependencyFilter;
import org.eclipse.aether.util.filter.PatternInclusionsDependencyFilter;
import org.eclipse.aether.util.filter.ScopeDependencyFilter;

/**
 * FilterTransformer implementation for Eclipses Aether
 * 
 * @author Robert Scholte
 * @since 3.0
 */
public class EclipseAetherFilterTransformer
    implements FilterTransformer<DependencyFilter>
{
    @Override
    public AndDependencyFilter transform( AndFilter andFilter )
    {
        Collection<DependencyFilter> filters = new ArrayList<DependencyFilter>();
        for ( TransformableFilter filter : andFilter.getFilters() )
        {
            filters.add( filter.transform( this ) );
        }
        return new AndDependencyFilter( filters );
    }

    @Override
    public ExclusionsDependencyFilter transform( ExclusionsFilter filter )
    {
        return new ExclusionsDependencyFilter( filter.getExcludes() );
    }

    @Override
    public OrDependencyFilter transform( OrFilter orFilter )
    {
        Collection<DependencyFilter> filters = new ArrayList<DependencyFilter>();
        for ( TransformableFilter filter : orFilter.getFilters() )
        {
            filters.add( filter.transform( this ) );
        }
        return new OrDependencyFilter( filters );
    }

    @Override
    public ScopeDependencyFilter transform( ScopeFilter filter )
    {
        return new ScopeDependencyFilter( filter.getIncluded(), filter.getExcluded() );
    }
    
    @Override
    public DependencyFilter transform( PatternExclusionsFilter filter )
    {
        return new PatternExclusionsDependencyFilter( filter.getExcludes() );
    } 

    @Override
    public DependencyFilter transform( PatternInclusionsFilter filter )
    {
        return new PatternInclusionsDependencyFilter( filter.getIncludes() );
    }
    
    @Override
    public DependencyFilter transform( final AbstractFilter filter )
    {
        return new DependencyFilter()
        {
            @Override
            public boolean accept( DependencyNode node, List<DependencyNode> parents )
            {
                return filter.accept( new EclipseAetherNode( node ), null );
            }
        }; 
    }
}
