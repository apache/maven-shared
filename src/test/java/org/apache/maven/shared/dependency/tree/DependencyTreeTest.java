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
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;

/**
 * Tests for {@link DependencyTree} and {@link DependencyNode}
 *  
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class DependencyTreeTest
    extends TestCase
{
    private DependencyTree tree;
    private DependencyNode node1, node2, node3, node4, node5, node6, node7;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        /*
         *     ------1------
         * ----2----       3
         * 4       5       7
         *         6
         */

        node1 = getDependencyNode( 1 );
        node2 = getDependencyNode( 2 );
        node3 = getDependencyNode( 3 );
        node4 = getDependencyNode( 4 );
        node5 = getDependencyNode( 5 );
        node6 = getDependencyNode( 6 );
        node7 = getDependencyNode( 7 );

        node3.children.add( node7 );

        node5.children.add( node6 );

        node1.children.add( node2 );
        node1.children.add( node3 );

        node2.children.add( node4 );
        node2.children.add( node5 );

        tree = new DependencyTree( node1, Arrays.asList( new DependencyNode[] {
            node1,
            node2,
            node3,
            node4,
            node5,
            node6,
            node7 } ) );
    }

    private void assertNode( Iterator it, DependencyNode node )
    {
        assertTrue( it.hasNext() );
        assertSame( node, it.next() );
    }

    public void testPreorderIterator()
    {
        Iterator it = tree.iterator();

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
        Iterator it = tree.inverseIterator();

        assertNode( it, node7 );
        assertNode( it, node3 );
        assertNode( it, node6 );
        assertNode( it, node5 );
        assertNode( it, node4 );
        assertNode( it, node2 );
        assertNode( it, node1 );
        assertFalse( it.hasNext() );
    }

    public void testToString()
        throws Exception
    {
        System.out.println( node1 );

        BufferedReader reader = new BufferedReader( new StringReader( node1.toString() ) );

        assertLine( reader, 1, 0 );
        assertLine( reader, 2, 1 );
        assertLine( reader, 4, 2 );
        assertLine( reader, 5, 2 );
        assertLine( reader, 6, 3 );
        assertLine( reader, 3, 1 );
        assertLine( reader, 7, 2 );
    }

    private void assertLine( BufferedReader reader, int i, int depth )
        throws IOException
    {
        String line = reader.readLine();
        StringBuffer sb = new StringBuffer();
        for ( int j = 0; j < depth; j++ )
        {
            sb.append( "  " );
        }
        sb.append( "groupId" );
        sb.append( i );
        sb.append( ":artifactId" );
        sb.append( i );
        sb.append( ":jar:" );
        sb.append( i );
        assertEquals( sb.toString(), line );
    }

    private DependencyNode getDependencyNode( int i )
    {
        DependencyNode node = new DependencyNode();
        node.artifact = getArtifact( i );
        return node;
    }

    private Artifact getArtifact( int i )
    {
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId( "groupId" + i );
        artifact.setArtifactId( "artifactId" + i );
        artifact.setVersion( new Integer( i ).toString() );
        artifact.setType( "jar" );
        return artifact;
    }
}
