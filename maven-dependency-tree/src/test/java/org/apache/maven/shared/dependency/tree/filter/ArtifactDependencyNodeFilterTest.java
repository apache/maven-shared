package org.apache.maven.shared.dependency.tree.filter;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Tests <code>ArtifactDependencyNodeFilter</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see ArtifactDependencyNodeFilter
 */
public class ArtifactDependencyNodeFilterTest extends MockObjectTestCase
{
    // fields -----------------------------------------------------------------

    private Artifact artifact;

    private DependencyNode node;

    private ArtifactDependencyNodeFilter nodeFilter;

    private ArtifactFilter artifactFilter;

    // TestCase methods -------------------------------------------------------

    /*
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        artifact = new ArtifactStub();
        node = new DependencyNode( artifact );
    }

    // tests ------------------------------------------------------------------

    public void testArtifactFilterInclude()
    {
        artifactFilter = createArtifactFilter( artifact, true );
        nodeFilter = new ArtifactDependencyNodeFilter( artifactFilter );

        assertTrue( nodeFilter.accept( node ) );
    }

    public void testArtifactFilterExclude()
    {
        artifactFilter = createArtifactFilter( artifact, false );
        nodeFilter = new ArtifactDependencyNodeFilter( artifactFilter );

        assertFalse( nodeFilter.accept( node ) );
    }

    // private methods --------------------------------------------------------

    private ArtifactFilter createArtifactFilter( Artifact artifact, boolean include )
    {
        Mock mock = mock( ArtifactFilter.class );

        mock.stubs().method( "include" ).with( same( artifact ) ).will( returnValue( include ) );

        return (ArtifactFilter) mock.proxy();
    }
}
