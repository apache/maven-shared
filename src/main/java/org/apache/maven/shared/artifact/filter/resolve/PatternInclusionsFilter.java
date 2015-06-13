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
 * A simple filter to include artifacts from a list of patterns. The artifact pattern syntax is of the form:
 * 
 * <pre>
 * [groupId]:[artifactId]:[extension]:[version]
 * </pre>
 * <p>
 * Where each pattern segment is optional and supports full and partial <code>*</code> wildcards. An empty pattern
 * segment is treated as an implicit wildcard. Version can be a range in case a {@code VersionScheme} is specified.
 * </p>
 * <p>
 * For example, <code>org.apache.*</code> would match all artifacts whose group id started with
 * <code>org.apache.</code> , and <code>:::*-SNAPSHOT</code> would match all snapshot artifacts.
 * </p>
 * 
 * @author Robert Scholte
 * @since 3.0
 * 
 * @see org.sonatype.aether.util.filter.PatternInclusionsDependencyFilter
 * @see org.eclipse.aether.util.filter.PatternInclusionsDependencyFilter
 * @see org.sonatype.aether.version.VersionScheme
 * @see org.eclipse.aether.version.VersionScheme
 */
public class PatternInclusionsFilter implements TransformableFilter
{
    
    private final Collection<String> includes;
    
    /**
     * The default constructor specifying a collection of pattern based keys which must be included.
     * 
     * @param includes the includes
     */
    public PatternInclusionsFilter( Collection<String> includes )
    {
        this.includes = Collections.unmodifiableCollection( includes );
    }
    
    /**
     * Get the includes
     * 
     * @return the includes, 
     */
    public final Collection<String> getIncludes()
    {
        return includes;
    }

    /**
     * Transform this filter to a tool specific implementation
     * 
     * @param transformer the transformer, must not be {@code null}
     */
    @Override
    public <T> T transform( FilterTransformer<T> transformer )
    {
        return transformer.transform( this );
    }
}
