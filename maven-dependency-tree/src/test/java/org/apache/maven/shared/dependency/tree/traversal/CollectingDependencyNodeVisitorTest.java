package org.apache.maven.shared.dependency.tree.traversal;

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
import java.util.List;

import org.apache.maven.shared.dependency.tree.AbstractDependencyNodeTest;
import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * Tests <code>CollectingDependencyNodeVisitor</code>.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see CollectingDependencyNodeVisitor
 */
public class CollectingDependencyNodeVisitorTest extends AbstractDependencyNodeTest
{
    // fields -----------------------------------------------------------------
    
    private CollectingDependencyNodeVisitor visitor;
    
    private DependencyNode node1;

    private DependencyNode node2;

    private DependencyNode node3;

    // TestCase methods -------------------------------------------------------
    
    /*
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        visitor = new CollectingDependencyNodeVisitor();
        node1 = createNode( "g:a:t:1" );
        node2 = createNode( "g:b:t:1" );
        node3 = createNode( "g:c:t:1" );
    }
    
    // tests ------------------------------------------------------------------
    
    public void testVisitSingleNode()
    {
        assertEmptyNodes();
        assertTrue( visitor.visit( node1 ) );
        assertNodes( node1 );
    }

    public void testVisitMultipleNodes()
    {
        assertEmptyNodes();
        assertTrue( visitor.visit( node1 ) );
        assertTrue( visitor.visit( node2 ) );
        assertTrue( visitor.visit( node3 ) );
        assertNodes( Arrays.asList( new Object[] { node1, node2, node3 } ) );
    }

    public void testEndVisit()
    {
        assertEmptyNodes();
        assertTrue( visitor.endVisit( node1 ) );
        assertEmptyNodes();
    }

    // private methods --------------------------------------------------------
    
    private void assertEmptyNodes()
    {
        assertNodes( Collections.EMPTY_LIST );
    }
    
    private void assertNodes( DependencyNode node )
    {
        assertNodes( Collections.singletonList( node ) );
    }
    
    private void assertNodes( List expectedNodes )
    {
        assertEquals( "Collected nodes", expectedNodes, visitor.getNodes() );
    }
}
