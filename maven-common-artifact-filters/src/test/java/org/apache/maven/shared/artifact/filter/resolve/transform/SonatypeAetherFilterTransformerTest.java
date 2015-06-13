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

import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.shared.artifact.filter.resolve.AndFilter;
import org.apache.maven.shared.artifact.filter.resolve.ExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.OrFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternInclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.sonatype.aether.util.filter.AndDependencyFilter;
import org.sonatype.aether.util.filter.ExclusionsDependencyFilter;
import org.sonatype.aether.util.filter.OrDependencyFilter;
import org.sonatype.aether.util.filter.PatternExclusionsDependencyFilter;
import org.sonatype.aether.util.filter.PatternInclusionsDependencyFilter;
import org.sonatype.aether.util.filter.ScopeDependencyFilter;
import org.junit.Test;

public class SonatypeAetherFilterTransformerTest
{

    private SonatypeAetherFilterTransformer transformer = new SonatypeAetherFilterTransformer();

    @Test
    public void testTransformAndFilter()
    {
        AndFilter filter = new AndFilter( Arrays.<TransformableFilter>asList( ScopeFilter.including( "compile" ), 
                                                                              ScopeFilter.including( "test" ) ) );

        AndDependencyFilter dependencyFilter = (AndDependencyFilter) filter.transform( transformer );
    }

    @Test
    public void testTransformExclusionsFilter()
    {
        ExclusionsFilter filter = new ExclusionsFilter( Collections.singletonList( "runtime" ) );

        ExclusionsDependencyFilter dependencyFilter = (ExclusionsDependencyFilter) filter.transform( transformer );
    }

    @Test
    public void testTransformOrFilter()
    {
        OrFilter filter = new OrFilter( Arrays.<TransformableFilter>asList( ScopeFilter.including( "compile" ), 
                                                                            ScopeFilter.including( "test" ) ) );

        OrDependencyFilter dependencyFilter = (OrDependencyFilter) filter.transform( transformer );

    }

    @Test
    public void testTransformScopeFilter()
    {
        ScopeFilter filter = ScopeFilter.including( Collections.singletonList( "runtime" ) );

        ScopeDependencyFilter dependencyFilter = (ScopeDependencyFilter) filter.transform( transformer );
    }

    @Test
    public void testTransformPatternExclusionsFilter()
    {
        PatternExclusionsFilter filter =
            new PatternExclusionsFilter( Collections.singletonList( "org.apache.maven:*" ) );

        PatternExclusionsDependencyFilter dependencyFilter =
            (PatternExclusionsDependencyFilter) filter.transform( transformer );
    }

    @Test
    public void testTransformPatternInclusionsFilter()
    {
        PatternInclusionsFilter filter =
            new PatternInclusionsFilter( Collections.singletonList( "org.apache.maven:*" ) );

        PatternInclusionsDependencyFilter dependencyFilter =
            (PatternInclusionsDependencyFilter) filter.transform( transformer );
    }

}
