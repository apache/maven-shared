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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.testing.ArtifactStubFactory;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.resolve.AbstractFilter;
import org.apache.maven.shared.artifact.filter.resolve.AndFilter;
import org.apache.maven.shared.artifact.filter.resolve.ExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.Node;
import org.apache.maven.shared.artifact.filter.resolve.OrFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternExclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.PatternInclusionsFilter;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.junit.Test;

public class ArtifactIncludeFilterTransformerTest
{

    private ArtifactIncludeFilterTransformer transformer = new ArtifactIncludeFilterTransformer();

    private ArtifactStubFactory artifactFactory = new ArtifactStubFactory();

    @Test
    public void testTransformAndFilter()
        throws Exception
    {
        AndFilter filter =
            new AndFilter(
                           Arrays.<TransformableFilter>asList( ScopeFilter.including( "compile" ),
                                                        new ExclusionsFilter( Collections.singletonList( "x:a" ) ) ) );

        AndArtifactFilter dependencyFilter = (AndArtifactFilter) filter.transform( transformer );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "compile" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "x:a:v", "compile" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "g:a:v", "test" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "x:a:v", "test" ) ) );
    }

    @Test
    public void testTransformExclusionsFilter()
        throws Exception
    {
        ExclusionsFilter filter = new ExclusionsFilter( Collections.singletonList( "x:a" ) );

        ArtifactFilter dependencyFilter = (ArtifactFilter) filter.transform( transformer );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "compile" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "x:a:v", "compile" ) ) );
    }

    @Test
    public void testTransformOrFilter()
        throws Exception
    {
        OrFilter filter =
            new OrFilter( Arrays.<TransformableFilter>asList( ScopeFilter.including( "compile" ),
                                                              ScopeFilter.including( "test" ) ) );

        ArtifactFilter dependencyFilter = (ArtifactFilter) filter.transform( transformer );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "compile" ) ) );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "test" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "g:a:v", "runtime" ) ) );
    }

    @Test
    public void testTransformScopeFilter()
        throws Exception
    {
        ScopeFilter filter = ScopeFilter.including( Collections.singletonList( "runtime" ) );

        ArtifactFilter dependencyFilter = (ArtifactFilter) filter.transform( transformer );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "runtime" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "g:a:v", "compile" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "g:a:v", "test" ) ) );
    }

    @Test
    public void testTransformPatternExclusionsFilter()
        throws Exception
    {
        PatternExclusionsFilter filter = new PatternExclusionsFilter( Collections.singletonList( "x:*" ) );

        PatternExcludesArtifactFilter dependencyFilter = (PatternExcludesArtifactFilter) filter.transform( transformer );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "runtime" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "x:a:v", "runtime" ) ) );
    }

    @Test
    public void testTransformPatternInclusionsFilter()
        throws Exception
    {
        PatternInclusionsFilter filter = new PatternInclusionsFilter( Collections.singletonList( "g:*" ) );

        PatternIncludesArtifactFilter dependencyFilter = (PatternIncludesArtifactFilter) filter.transform( transformer );

        assertTrue( dependencyFilter.include( newArtifact( "g:a:v", "runtime" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "x:a:v", "runtime" ) ) );
    }
    
    @Test
    public void testTransformAbstractFilter() throws Exception
    {
        AbstractFilter snapshotFilter = new AbstractFilter()
        {
            @Override
            public boolean accept( Node node, List<Node> parents )
            {
                return ArtifactUtils.isSnapshot( node.getDependency().getVersion() );
            }
        };
        
        ArtifactFilter dependencyFilter = snapshotFilter.transform( transformer );
        
        assertTrue( dependencyFilter.include( newArtifact( "g:a:1.0-SNAPSHOT", "compile" ) ) );

        assertFalse( dependencyFilter.include( newArtifact( "g:a:1.0", "compile" ) ) );
    }

    private Artifact newArtifact( String coor, String scope )
        throws Exception
    {
        String[] gav = coor.split( ":" );
        return artifactFactory.createArtifact( gav[0], gav[1], gav[2], scope );
    }

}
