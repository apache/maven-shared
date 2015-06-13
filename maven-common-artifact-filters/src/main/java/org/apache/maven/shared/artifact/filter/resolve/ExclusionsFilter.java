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
 * A simple filter to exclude artifacts based on either artifact id or group id and artifact id.
 * 
 * @author Robert Scholte
 * @since 3.0
 * 
 * @see org.sonatype.aether.util.filter.ExclusionsDependencyFilter
 * @see org.eclipse.aether.util.filter.ExclusionsDependencyFilter
 */
public class ExclusionsFilter
    implements TransformableFilter
{
    private final Collection<String> excludes;

    /**
     * The default constructor specifying a collection of keys which must be excluded. 
     * 
     * @param excludes the keys to exclude, may not be {@code null}
     * @see Artifact#getDependencyConflictId()
     */
    public ExclusionsFilter( Collection<String> excludes )
    {
        this.excludes = Collections.unmodifiableCollection( excludes );
    }

    public final Collection<String> getExcludes()
    {
        return excludes;
    }

    /**
     * Transform this filter to a tool specific implementation
     * 
     * @param transformer the transformer, may not be {@code null}
     */
    @Override
    public <T> T transform( FilterTransformer<T> transformer )
    {
        return transformer.transform( this );
    }
}
