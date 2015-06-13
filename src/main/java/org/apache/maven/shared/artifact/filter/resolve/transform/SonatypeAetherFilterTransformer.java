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

import org.apache.maven.shared.artifact.filter.resolve.AndFilter;
import org.apache.maven.shared.artifact.filter.resolve.ExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.FilterTransformer;
import org.apache.maven.shared.artifact.filter.resolve.OrFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternInclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.util.filter.AndDependencyFilter;
import org.sonatype.aether.util.filter.ExclusionsDependencyFilter;
import org.sonatype.aether.util.filter.OrDependencyFilter;
import org.sonatype.aether.util.filter.PatternExclusionsDependencyFilter;
import org.sonatype.aether.util.filter.PatternInclusionsDependencyFilter;
import org.sonatype.aether.util.filter.ScopeDependencyFilter;

/**
 * FilterTransformer implementation for Sonatypes Aether
 * 
 * @author Robert Scholte  
 * @since 3.0
 */
public class SonatypeAetherFilterTransformer
    implements FilterTransformer<DependencyFilter>
{
    @Override
    public AndDependencyFilter transform( AndFilter filter )
    {
        Collection<DependencyFilter> filters = new ArrayList<DependencyFilter>( filter.getFilters().size() );
        for ( TransformableFilter dependencyFilter : filter.getFilters() )
        {
            filters.add( dependencyFilter.transform( this ) );
        }
        return new AndDependencyFilter( filters );
    }

    @Override
    public ExclusionsDependencyFilter transform( ExclusionsFilter filter )
    {
        return new ExclusionsDependencyFilter( filter.getExcludes() );
    }
    
    @Override
    public OrDependencyFilter transform( OrFilter filter )
    {
        Collection<DependencyFilter> filters = new ArrayList<DependencyFilter>( filter.getFilters().size() );
        for ( TransformableFilter dependencyFilter : filter.getFilters() )
        {
            filters.add( dependencyFilter.transform( this ) );
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
}
