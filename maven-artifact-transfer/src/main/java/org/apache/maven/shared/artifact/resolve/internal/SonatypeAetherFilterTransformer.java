package org.apache.maven.shared.artifact.resolve.internal;

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

import org.apache.maven.shared.artifact.resolve.filter.TransformableFilter;
import org.apache.maven.shared.artifact.resolve.filter.AndFilter;
import org.apache.maven.shared.artifact.resolve.filter.ExclusionsFilter;
import org.apache.maven.shared.artifact.resolve.filter.FilterTransformer;
import org.apache.maven.shared.artifact.resolve.filter.OrFilter;
import org.apache.maven.shared.artifact.resolve.filter.ScopeFilter;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.util.filter.AndDependencyFilter;
import org.sonatype.aether.util.filter.ExclusionsDependencyFilter;
import org.sonatype.aether.util.filter.OrDependencyFilter;
import org.sonatype.aether.util.filter.ScopeDependencyFilter;

/**
 * 
 */
class SonatypeAetherFilterTransformer
    implements FilterTransformer<DependencyFilter>
{
    public AndDependencyFilter transform( AndFilter filter )
    {
        Collection<DependencyFilter> filters = new ArrayList<DependencyFilter>( filter.getFilters().size() );
        for ( TransformableFilter dependencyFilter : filter.getFilters() )
        {
            filters.add( dependencyFilter.transform( this ) );
        }
        return new AndDependencyFilter( filters );
    }


    public ExclusionsDependencyFilter transform( ExclusionsFilter filter )
    {
        return new ExclusionsDependencyFilter( filter.getExcludes() );
    }
    
    public OrDependencyFilter transform( OrFilter filter )
    {
        Collection<DependencyFilter> filters = new ArrayList<DependencyFilter>( filter.getFilters().size() );
        for ( TransformableFilter dependencyFilter : filter.getFilters() )
        {
            filters.add( dependencyFilter.transform( this ) );
        }
        return new OrDependencyFilter( filters );
    }
    
    public ScopeDependencyFilter transform( ScopeFilter filter )
    {
        return new ScopeDependencyFilter( filter.getIncluded(), filter.getExcluded() );
    }

}
