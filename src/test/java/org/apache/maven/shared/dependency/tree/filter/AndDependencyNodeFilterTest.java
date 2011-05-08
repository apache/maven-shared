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
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * Tests <code>AndDependencyNodeFilter</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see AndDependencyNodeFilter
 */
public class AndDependencyNodeFilterTest
    extends AbstractDependencyNodeFilterTest
{
    // fields -----------------------------------------------------------------

    private Artifact artifact;

    private DependencyNode node;

    private DependencyNodeFilter includeFilter;

    private DependencyNodeFilter excludeFilter;

    private AndDependencyNodeFilter filter;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp()
        throws Exception
    {
        artifact = new ArtifactStub();
        node = new DependencyNode( artifact );

        includeFilter = createDependencyNodeFilter( node, true );
        excludeFilter = createDependencyNodeFilter( node, false );
    }

    // tests ------------------------------------------------------------------

    public void testIncludeInclude()
    {
        filter = new AndDependencyNodeFilter( includeFilter, includeFilter );

        assertTrue( filter.accept( node ) );
    }

    public void testIncludeExclude()
    {
        filter = new AndDependencyNodeFilter( includeFilter, excludeFilter );

        assertFalse( filter.accept( node ) );
    }

    public void testExcludeInclude()
    {
        filter = new AndDependencyNodeFilter( excludeFilter, includeFilter );

        assertFalse( filter.accept( node ) );
    }

    public void testExcludeExclude()
    {
        filter = new AndDependencyNodeFilter( excludeFilter, excludeFilter );

        assertFalse( filter.accept( node ) );
    }
}
