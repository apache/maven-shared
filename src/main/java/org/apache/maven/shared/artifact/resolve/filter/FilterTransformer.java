package org.apache.maven.shared.artifact.resolve.filter;

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
 * Provide a mechanism to transform a Filter to a tool specific equivalent.
 * For example: Aether has its own set of filters.  
 * 
 * @author Robert Scholte
 *
 * @param <T> the tool specific filter
 */
public interface FilterTransformer<T>
{
    /**
     * 
     * @param scopeFilter the filter 
     * @return the transformed filter
     */
    T transform( ScopeFilter scopeFilter );

    /**
     * 
     * @param andFilter the filter
     * @return the transformed filter
     */
    T transform( AndFilter andFilter );

    /**
     * 
     * @param exclusionsFilter the filter
     * @return the transformed filter
     */
    T transform( ExclusionsFilter exclusionsFilter );

    /**
     * 
     * @param orFilter the filter
     * @return the transformed filter
     */
    T transform( OrFilter orFilter );
}
