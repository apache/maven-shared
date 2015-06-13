package org.apache.maven.shared.artifact.filter.resolve;

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

import java.util.Collection;
import java.util.Collections;

/**
 * Filter based on scope. <em>Note:<em> There's no logic for inherited scoped
 * 
 * @author Robert Scholte
 * @since 3.0
 * 
 * @see org.sonatype.aether.util.filter.ScopeDependencyFilter
 * @see org.eclipse.aether.util.filter.ScopeDependencyFilter
 */
public class ScopeFilter implements TransformableFilter
{
    private final Collection<String> excluded;

    private final Collection<String> included;

    /**
     * 
     * @param included specific scopes to include or {@null} to include all
     * @param excluded specific scopes to exclude or {@null} to exclude none
     */
    public ScopeFilter( Collection<String> included, Collection<String> excluded )
    {
        this.included = ( included == null ? null : Collections.unmodifiableCollection( included ) );
        this.excluded = ( excluded == null ? null : Collections.unmodifiableCollection( excluded ) );
    }
    
    /**
     * Construct a ScopeFilter based on included scopes  
     * 
     * @param included the scopes to include, may be {@code null}
     * @return the filter, never {@code null}
     */
    public static ScopeFilter including( Collection<String> included ) 
    {
        return new ScopeFilter( included, null );
    }

    /**
     * Construct a ScopeFilter based on excluded scopes
     * 
     * @param excluded the scopes to exclude, may be {@code null}
     * @return the filter, never {@code null}
     */
    public static ScopeFilter excluding( Collection<String> excluded ) 
    {
        return new ScopeFilter( null, excluded );
    }

    /**
     * Get the excluded scopes
     * 
     * @return the scopes to exclude, may be {@code null}
     */
    public final Collection<String> getExcluded()
    {
        return excluded;
    }
    
    /**
     * Get the included scopes
     * 
     * @return the scopes to include, may be {@code null}
     */
    public final Collection<String> getIncluded()
    {
        return included;
    }
    
    /**
     * Transform this filter to a tool specific implementation
     * 
     * @param transformer the transformer, must not be {@code null}
     */
    public <T> T transform ( FilterTransformer<T> transformer )
    {
        return transformer.transform( this );
    }
}
