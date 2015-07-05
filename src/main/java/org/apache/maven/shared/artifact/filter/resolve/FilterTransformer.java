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

/**
 * Provide a mechanism to transform a Filter to a tool specific equivalent using the visitor pattern.
 * For example: Aether has its own set of filters.  
 * 
 * @author Robert Scholte
 *
 * @param <T> the tool specific filter
 * @since 3.0
 */
public interface FilterTransformer<T>
{
    /**
     * Transform the scopeFilter to T specific implementation
     * 
     * @param scopeFilter the filter 
     * @return the transformed filter, never {@code null}
     */
    T transform( ScopeFilter scopeFilter );

    /**
     * Transform the andFilter to T specific implementation
     * 
     * @param andFilter the filter
     * @return the transformed filter, never {@code null}
     */
    T transform( AndFilter andFilter );

    /**
     * Transform the exclusionsFilter to T specific implementation
     * 
     * @param exclusionsFilter the filter
     * @return the transformed filter, never {@code null}
     */
    T transform( ExclusionsFilter exclusionsFilter );

    /**
     * Transform the orFilter to T specific implementation
     * 
     * @param orFilter the filter
     * @return the transformed filter, never {@code null}
     */
    T transform( OrFilter orFilter );

    /**
     * Transform the patternExclusionsFilter to T specific implementation
     * 
     * @param patternExclusionsFilter the filter
     * @return the transformed filter, never {@code null}
     */
    T transform( PatternExclusionsFilter patternExclusionsFilter );

    /**
     * Transform the paternInclusionsFilter to T specific implementation
     * 
     * @param patternInclusionsFilter the filter
     * @return the transformed filter, never {@code null}
     */
    T transform( PatternInclusionsFilter patternInclusionsFilter );

    /**
     * Transform a custom filter to T specific implementation 
     * 
     * @param abstractFilter the filter
     * @return the transformed filter, never {@code null}
     */
    T transform( AbstractFilter abstractFilter );
}
