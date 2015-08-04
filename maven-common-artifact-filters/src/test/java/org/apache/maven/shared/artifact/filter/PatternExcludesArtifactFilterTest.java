package org.apache.maven.shared.artifact.filter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

public class PatternExcludesArtifactFilterTest
    extends AbstractPatternArtifactFilterTest
{

    @Override
    protected ArtifactFilter createFilter( final List<String> patterns )
    {
        return new PatternExcludesArtifactFilter( patterns );
    }

    @Override
    protected ArtifactFilter createFilter( final List<String> patterns, final boolean actTransitively )
    {
        return new PatternExcludesArtifactFilter( patterns, actTransitively );
    }

    @Override
    protected boolean isInclusionExpected()
    {
        return false;
    }

}
