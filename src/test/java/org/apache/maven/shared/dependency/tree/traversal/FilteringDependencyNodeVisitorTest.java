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

import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.filter.AbstractDependencyNodeFilterTest;
import org.apache.maven.shared.dependency.tree.filter.DependencyNodeFilter;
import org.jmock.Mock;

/**
 * Tests <code>FilteringDependencyNodeVisitor</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see FilteringDependencyNodeVisitor
 */
public class FilteringDependencyNodeVisitorTest
    extends AbstractDependencyNodeFilterTest
{
    // fields -----------------------------------------------------------------

    private FilteringDependencyNodeVisitor visitor;

    private DependencyNode node;

    private DependencyNodeFilter acceptingFilter;

    private DependencyNodeFilter rejectingFilter;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp()
        throws Exception
    {
        node = new DependencyNode( new ArtifactStub() );

        acceptingFilter = createDependencyNodeFilter( node, true );
        rejectingFilter = createDependencyNodeFilter( node, false );
    }

    // tests ------------------------------------------------------------------

    public void testVisitAcceptTrue()
    {
        Mock filteredVisitorMock = mock( DependencyNodeVisitor.class );
        filteredVisitorMock.expects( once() ).method( "visit" ).with( eq( node ) ).will( returnValue( true ) );
        DependencyNodeVisitor filteredVisitor = (DependencyNodeVisitor) filteredVisitorMock.proxy();

        visitor = new FilteringDependencyNodeVisitor( filteredVisitor, acceptingFilter );
        assertTrue( visitor.visit( node ) );
    }

    public void testVisitAcceptFalse()
    {
        Mock filteredVisitorMock = mock( DependencyNodeVisitor.class );
        filteredVisitorMock.expects( once() ).method( "visit" ).with( eq( node ) ).will( returnValue( false ) );
        DependencyNodeVisitor filteredVisitor = (DependencyNodeVisitor) filteredVisitorMock.proxy();

        visitor = new FilteringDependencyNodeVisitor( filteredVisitor, acceptingFilter );
        assertFalse( visitor.visit( node ) );
    }

    public void testVisitReject()
    {
        DependencyNodeVisitor filteredVisitor = (DependencyNodeVisitor) mock( DependencyNodeVisitor.class ).proxy();

        visitor = new FilteringDependencyNodeVisitor( filteredVisitor, rejectingFilter );
        assertTrue( visitor.visit( node ) );
    }

    public void testEndVisitAcceptTrue()
    {
        Mock filteredVisitorMock = mock( DependencyNodeVisitor.class );
        filteredVisitorMock.expects( once() ).method( "endVisit" ).with( eq( node ) ).will( returnValue( true ) );
        DependencyNodeVisitor filteredVisitor = (DependencyNodeVisitor) filteredVisitorMock.proxy();

        visitor = new FilteringDependencyNodeVisitor( filteredVisitor, acceptingFilter );
        assertTrue( visitor.endVisit( node ) );
    }

    public void testEndVisitAcceptFalse()
    {
        Mock filteredVisitorMock = mock( DependencyNodeVisitor.class );
        filteredVisitorMock.expects( once() ).method( "endVisit" ).with( eq( node ) ).will( returnValue( false ) );
        DependencyNodeVisitor filteredVisitor = (DependencyNodeVisitor) filteredVisitorMock.proxy();

        visitor = new FilteringDependencyNodeVisitor( filteredVisitor, acceptingFilter );
        assertFalse( visitor.endVisit( node ) );
    }

    public void testEndVisitReject()
    {
        DependencyNodeVisitor filteredVisitor = (DependencyNodeVisitor) mock( DependencyNodeVisitor.class ).proxy();

        visitor = new FilteringDependencyNodeVisitor( filteredVisitor, rejectingFilter );
        assertTrue( visitor.endVisit( node ) );
    }
}
