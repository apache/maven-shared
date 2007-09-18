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

import org.apache.maven.shared.dependency.tree.AbstractDependencyNodeTest;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.jmock.Mock;

/**
 * Tests <code>BuildingDependencyNodeVisitor</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see BuildingDependencyNodeVisitor
 */
public class BuildingDependencyNodeVisitorTest extends AbstractDependencyNodeTest
{
    // fields -----------------------------------------------------------------

    private BuildingDependencyNodeVisitor visitor;

    // tests ------------------------------------------------------------------

    public void testVisitNode()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithState()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1", DependencyNode.OMITTED_FOR_CYCLE );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithRelatedArtifact()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1", DependencyNode.OMITTED_FOR_CONFLICT, "g:a:t:2" );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithOriginalScope()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );
        sourceNode.setOriginalScope( "x" );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithFailedUpdateScope()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );
        sourceNode.setFailedUpdateScope( "x" );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithPremanagedVersion()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );
        sourceNode.setPremanagedVersion( "2" );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithPremanagedScope()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );
        sourceNode.setPremanagedScope( "x" );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithChild()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );
        DependencyNode sourceChildNode = createNode( "g:b:t:1" );
        sourceNode.addChild( sourceChildNode );

        visitor = new BuildingDependencyNodeVisitor();
        visitor.visit( sourceNode );
        visitor.visit( sourceChildNode );
        visitor.endVisit( sourceChildNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithVisitor()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );

        Mock nextVisitorMock = mock( DependencyNodeVisitor.class );
        nextVisitorMock.expects( once() ).method( "visit" ).with( eq( sourceNode ) ).will( returnValue( true ) ).id( "1" );
        nextVisitorMock.expects( once() ).method( "endVisit" ).with( eq( sourceNode ) ).after( "1" ).will( returnValue( true ) );
        DependencyNodeVisitor nextVisitor = (DependencyNodeVisitor) nextVisitorMock.proxy();

        visitor = new BuildingDependencyNodeVisitor( nextVisitor );
        visitor.visit( sourceNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }

    public void testVisitNodeWithChildAndVisitor()
    {
        DependencyNode sourceNode = createNode( "g:a:t:1" );
        DependencyNode sourceChildNode = createNode( "g:b:t:1" );
        sourceNode.addChild( sourceChildNode );

        Mock nextVisitorMock = mock( DependencyNodeVisitor.class );
        nextVisitorMock.expects( once() ).method( "visit" ).with( eq( sourceNode ) ).will( returnValue( true ) ).id( "1" );
        nextVisitorMock.expects( once() ).method( "visit" ).with( eq( sourceChildNode ) ).after( "1" ).will( returnValue( true ) ).id( "2" );
        nextVisitorMock.expects( once() ).method( "endVisit" ).with( eq( sourceChildNode ) ).after( "2" ).will( returnValue( true ) ).id( "3" );
        nextVisitorMock.expects( once() ).method( "endVisit" ).with( eq( sourceNode ) ).after( "3" ).will( returnValue( true ) );
        DependencyNodeVisitor nextVisitor = (DependencyNodeVisitor) nextVisitorMock.proxy();

        visitor = new BuildingDependencyNodeVisitor( nextVisitor );
        visitor.visit( sourceNode );
        visitor.visit( sourceChildNode );
        visitor.endVisit( sourceChildNode );
        visitor.endVisit( sourceNode );

        DependencyNode resultNode = visitor.getDependencyTree();
        assertNotSame( sourceNode, resultNode );
        assertEquals( sourceNode, resultNode );
    }
}
