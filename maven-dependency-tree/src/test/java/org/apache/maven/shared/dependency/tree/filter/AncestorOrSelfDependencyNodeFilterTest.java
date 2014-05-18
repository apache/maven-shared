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

import org.apache.maven.shared.dependency.tree.AbstractDependencyNodeTest;
import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * Tests <code>AncestorOrSelfDependencyNodeFilter</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see AncestorOrSelfDependencyNodeFilter
 */
public class AncestorOrSelfDependencyNodeFilterTest
    extends AbstractDependencyNodeTest
{
    // constants --------------------------------------------------------------

    private DependencyNode rootNode;

    private DependencyNode childNode1;

    private DependencyNode childNode2;

    private DependencyNode grandChildNode;

    private AncestorOrSelfDependencyNodeFilter filter;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp()
        throws Exception
    {
        /*
         * p -> a -> c -> b
         */

        rootNode = createNode( "g:p:t:1" );

        childNode1 = createNode( "g:a:t:1" );
        rootNode.addChild( childNode1 );

        childNode2 = createNode( "g:b:t:1" );
        rootNode.addChild( childNode2 );

        grandChildNode = createNode( "g:c:t:1" );
        childNode1.addChild( grandChildNode );
    }

    // tests ------------------------------------------------------------------

    public void testSelf()
    {
        filter = new AncestorOrSelfDependencyNodeFilter( rootNode );

        assertTrue( filter.accept( rootNode ) );
    }

    public void testParent()
    {
        filter = new AncestorOrSelfDependencyNodeFilter( childNode1 );

        assertTrue( filter.accept( rootNode ) );
    }

    public void testGrandParent()
    {
        filter = new AncestorOrSelfDependencyNodeFilter( grandChildNode );

        assertTrue( filter.accept( rootNode ) );
    }

    public void testCousin()
    {
        filter = new AncestorOrSelfDependencyNodeFilter( childNode2 );

        assertFalse( filter.accept( childNode1 ) );
    }

    public void testChild()
    {
        filter = new AncestorOrSelfDependencyNodeFilter( rootNode );

        assertFalse( filter.accept( childNode1 ) );
    }

    public void testGrandChild()
    {
        filter = new AncestorOrSelfDependencyNodeFilter( rootNode );

        assertFalse( filter.accept( grandChildNode ) );
    }
}
