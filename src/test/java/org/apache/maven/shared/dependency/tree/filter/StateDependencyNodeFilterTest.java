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

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * Tests <code>StateDependencyNodeFilter</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see StateDependencyNodeFilter
 */
public class StateDependencyNodeFilterTest extends TestCase
{
    // fields -----------------------------------------------------------------

    private StateDependencyNodeFilter filter;

    private DependencyNode includedNode;

    private DependencyNode omittedForDuplicateNode;

    private DependencyNode omittedForConflictNode;

    private DependencyNode omittedForCycleNode;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
        Artifact artifact = new ArtifactStub();
        Artifact relatedArtifact = new ArtifactStub();

        includedNode = new DependencyNode( artifact, DependencyNode.INCLUDED );
        omittedForDuplicateNode = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_DUPLICATE, relatedArtifact );
        omittedForConflictNode = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_CONFLICT, relatedArtifact );
        omittedForCycleNode = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_CYCLE );
    }

    // tests ------------------------------------------------------------------

    public void testIncluded()
    {
        filter = new StateDependencyNodeFilter( DependencyNode.INCLUDED );

        assertTrue( filter.accept( includedNode ) );
        assertFalse( filter.accept( omittedForDuplicateNode ) );
        assertFalse( filter.accept( omittedForConflictNode ) );
        assertFalse( filter.accept( omittedForCycleNode ) );
    }

    public void testOmittedForDuplicate()
    {
        filter = new StateDependencyNodeFilter( DependencyNode.OMITTED_FOR_DUPLICATE );

        assertFalse( filter.accept( includedNode ) );
        assertTrue( filter.accept( omittedForDuplicateNode ) );
        assertFalse( filter.accept( omittedForConflictNode ) );
        assertFalse( filter.accept( omittedForCycleNode ) );
    }

    public void testOmittedForConflict()
    {
        filter = new StateDependencyNodeFilter( DependencyNode.OMITTED_FOR_CONFLICT );

        assertFalse( filter.accept( includedNode ) );
        assertFalse( filter.accept( omittedForDuplicateNode ) );
        assertTrue( filter.accept( omittedForConflictNode ) );
        assertFalse( filter.accept( omittedForCycleNode ) );
    }

    public void testOmittedForCycle()
    {
        filter = new StateDependencyNodeFilter( DependencyNode.OMITTED_FOR_CYCLE );

        assertFalse( filter.accept( includedNode ) );
        assertFalse( filter.accept( omittedForDuplicateNode ) );
        assertFalse( filter.accept( omittedForConflictNode ) );
        assertTrue( filter.accept( omittedForCycleNode ) );
    }
}
