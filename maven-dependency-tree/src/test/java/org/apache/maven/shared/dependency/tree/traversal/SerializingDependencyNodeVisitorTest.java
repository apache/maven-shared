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

import java.io.StringWriter;

import org.apache.maven.shared.dependency.tree.AbstractDependencyNodeTest;
import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * Tests <code>SerializingDependencyNodeVisitor</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see SerializingDependencyNodeVisitor
 */
public class SerializingDependencyNodeVisitorTest extends AbstractDependencyNodeTest
{
    // constants --------------------------------------------------------------

    private static final String NEWLINE = System.getProperty( "line.separator" );

    // fields -----------------------------------------------------------------

    private StringWriter writer;

    private SerializingDependencyNodeVisitor serializer;

    // TestCase methods -------------------------------------------------------

    /*
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        writer = new StringWriter();

        serializer = new SerializingDependencyNodeVisitor( writer, SerializingDependencyNodeVisitor.STANDARD_TOKENS );
    }

    // tests ------------------------------------------------------------------

    public void testSingleNode()
    {
        DependencyNode rootNode = createNode( "g:p:t:1" );

        assertTree( "g:p:t:1" + NEWLINE, rootNode );
    }
    
    public void testNodeWithChild()
    {
        DependencyNode rootNode = createNode( "g:p:t:1" );
        rootNode.addChild( createNode( "g:a:t:1" ) );

        assertTree(
            "g:p:t:1" + NEWLINE + 
            "\\- g:a:t:1" + NEWLINE,
            rootNode );
    }
    
    public void testNodeWithMultipleChildren()
    {
        DependencyNode rootNode = createNode( "g:p:t:1" );
        rootNode.addChild( createNode( "g:a:t:1" ) );
        rootNode.addChild( createNode( "g:b:t:1" ) );
        rootNode.addChild( createNode( "g:c:t:1" ) );
        
        assertTree(
            "g:p:t:1" + NEWLINE + 
            "+- g:a:t:1" + NEWLINE + 
            "+- g:b:t:1" + NEWLINE + 
            "\\- g:c:t:1" + NEWLINE,
            rootNode );
    }
    
    public void testNodeWithGrandchild()
    {
        DependencyNode rootNode = createNode( "g:p:t:1" );
        DependencyNode childNode = createNode( "g:a:t:1" );
        rootNode.addChild( childNode );
        childNode.addChild( createNode( "g:b:t:1" ) );
        
        assertTree(
            "g:p:t:1" + NEWLINE + 
            "\\- g:a:t:1" + NEWLINE + 
            "   \\- g:b:t:1" + NEWLINE,
            rootNode );
    }
    
    public void testNodeWithMultipleGrandchildren()
    {
        DependencyNode rootNode = createNode( "g:p:t:1" );
        DependencyNode child1Node = createNode( "g:a:t:1" );
        rootNode.addChild( child1Node );
        child1Node.addChild( createNode( "g:b:t:1" ) );
        DependencyNode child2Node = createNode( "g:c:t:1" );
        rootNode.addChild( child2Node );
        child2Node.addChild( createNode( "g:d:t:1" ) );
        
        assertTree(
            "g:p:t:1" + NEWLINE + 
            "+- g:a:t:1" + NEWLINE + 
            "|  \\- g:b:t:1" + NEWLINE +
            "\\- g:c:t:1" + NEWLINE + 
            "   \\- g:d:t:1" + NEWLINE,
            rootNode );
    }
    
    // private methods --------------------------------------------------------

    private void assertTree( String expectedTree, DependencyNode actualNode )
    {
        actualNode.accept( serializer );

        assertEquals( expectedTree, writer.toString() );
    }
}
