package org.apache.maven.shared.dependency.tree;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;

/**
 * Tests <code>DependencyNode</code>.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see DependencyNode
 */
public class DependencyNodeTest
    extends AbstractDependencyNodeTest
{
    private DependencyNode rootNode, node1, node2, node3, node4, node5, node6, node7;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        /*
         * ------1------ ----2---- 3 4 5 7 6
         */

        node1 = createNode( 1 );
        node2 = createNode( node1, 2 );
        node3 = createNode( node1, 3 );
        node4 = createNode( node2, 4 );
        node5 = createNode( node2, 5 );
        node6 = createNode( node5, 6 );
        node7 = createNode( node3, 7 );

        rootNode = node1;
    }

    private void assertNode( Iterator<DependencyNode> it, DependencyNode node )
    {
        assertTrue( it.hasNext() );
        assertSame( node, it.next() );
    }

    public void testPreorderIterator()
    {
        Iterator<DependencyNode> it = rootNode.iterator();

        assertNode( it, node1 );
        assertNode( it, node2 );
        assertNode( it, node4 );
        assertNode( it, node5 );
        assertNode( it, node6 );
        assertNode( it, node3 );
        assertNode( it, node7 );
        assertFalse( it.hasNext() );
    }

    public void testInverseIterator()
    {
        Iterator<DependencyNode> it = rootNode.inverseIterator();

        assertNode( it, node7 );
        assertNode( it, node3 );
        assertNode( it, node6 );
        assertNode( it, node5 );
        assertNode( it, node4 );
        assertNode( it, node2 );
        assertNode( it, node1 );
        assertFalse( it.hasNext() );
    }

    public void testToNodeStringIncluded()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact );

        assertEquals( "g:a:t:1:s", node.toNodeString() );
    }

    public void testToNodeStringIncludedWithManagedVersion()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact );
        node.setPremanagedVersion( "2" );

        assertEquals( "g:a:t:1:s (version managed from 2)", node.toNodeString() );
    }

    public void testToNodeStringIncludedWithManagedScope()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact );
        node.setPremanagedScope( "x" );

        assertEquals( "g:a:t:1:s (scope managed from x)", node.toNodeString() );
    }

    public void testToNodeStringIncludedWithUpdatedScope()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact );
        node.setOriginalScope( "x" );

        assertEquals( "g:a:t:1:s (scope updated from x)", node.toNodeString() );
    }

    public void testToNodeStringOmittedForDuplicate()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        Artifact duplicateArtifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_DUPLICATE, duplicateArtifact );

        assertEquals( "(g:a:t:1:s - omitted for duplicate)", node.toNodeString() );
    }

    public void testToNodeStringOmittedForDuplicateWithUpdatedScope()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        Artifact duplicateArtifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_DUPLICATE, duplicateArtifact );
        node.setOriginalScope( "x" );

        assertEquals( "(g:a:t:1:s - scope updated from x; omitted for duplicate)", node.toNodeString() );
    }

    public void testToNodeStringOmittedForConflict()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        Artifact conflictArtifact = createArtifact( "g:a:t:2:s" );
        DependencyNode node = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_CONFLICT, conflictArtifact );

        assertEquals( "(g:a:t:1:s - omitted for conflict with 2)", node.toNodeString() );
    }

    public void testToNodeStringOmittedForCycle()
    {
        Artifact artifact = createArtifact( "g:a:t:1:s" );
        DependencyNode node = new DependencyNode( artifact, DependencyNode.OMITTED_FOR_CYCLE );

        assertEquals( "(g:a:t:1:s - omitted for cycle)", node.toNodeString() );
    }

    public void testToString()
        throws Exception
    {
        BufferedReader reader = new BufferedReader( new StringReader( node1.toString() ) );

        assertLine( reader, 1, 0 );
        assertLine( reader, 2, 1 );
        assertLine( reader, 4, 2 );
        assertLine( reader, 5, 2 );
        assertLine( reader, 6, 3 );
        assertLine( reader, 3, 1 );
        assertLine( reader, 7, 2 );
    }

    public void testOmitForConflict()
    {
        Artifact relatedArtifact = createArtifact( createArtifactId( 2, "3" ) );
        node2.omitForConflict( relatedArtifact );

        assertEquals( DependencyNode.OMITTED_FOR_CONFLICT, node2.getState() );
        assertEquals( relatedArtifact, node2.getRelatedArtifact() );

        assertTrue( node2.getChildren().isEmpty() );
        assertNull( node4.getParent() );
        assertNull( node5.getParent() );
    }

    public void testOmitForConflictWithDuplicate()
    {
        Artifact relatedArtifact = createArtifact( createArtifactId( 2 ) );
        node2.omitForConflict( relatedArtifact );

        assertEquals( DependencyNode.OMITTED_FOR_DUPLICATE, node2.getState() );
        assertEquals( relatedArtifact, node2.getRelatedArtifact() );

        assertTrue( node2.getChildren().isEmpty() );
        assertNull( node4.getParent() );
        assertNull( node5.getParent() );
    }

    public void testOmitForCycle()
    {
        node2.omitForCycle();

        assertEquals( DependencyNode.OMITTED_FOR_CYCLE, node2.getState() );

        assertTrue( node2.getChildren().isEmpty() );
        assertNull( node4.getParent() );
        assertNull( node5.getParent() );
    }

    /**
     * @deprecated
     */
    public void testGetDepth()
    {
        assertEquals( 0, rootNode.getDepth() );
        assertEquals( 0, node1.getDepth() );
        assertEquals( 1, node2.getDepth() );
        assertEquals( 1, node3.getDepth() );
        assertEquals( 2, node4.getDepth() );
        assertEquals( 2, node5.getDepth() );
        assertEquals( 3, node6.getDepth() );
        assertEquals( 2, node7.getDepth() );
    }

    private void assertLine( BufferedReader reader, int i, int depth )
        throws IOException
    {
        String line = reader.readLine();
        StringBuffer sb = new StringBuffer();
        for ( int j = 0; j < depth; j++ )
        {
            sb.append( "   " );
        }
        sb.append( "groupId" );
        sb.append( i );
        sb.append( ":artifactId" );
        sb.append( i );
        sb.append( ":jar:" );
        sb.append( i );
        sb.append( ":compile" );
        assertEquals( sb.toString(), line );
    }

    private DependencyNode createNode( DependencyNode parent, int i )
    {
        DependencyNode node = createNode( i );

        parent.addChild( node );

        return node;
    }

    private DependencyNode createNode( int i )
    {
        return createNode( createArtifactId( i ) );
    }

    private String createArtifactId( int i )
    {
        return createArtifactId( i, Integer.toString( i ) );
    }

    private String createArtifactId( int i, String version )
    {
        return "groupId" + i + ":artifactId" + i + ":jar:" + version + ":compile";
    }
}
