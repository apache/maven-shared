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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;

/**
 * Represents an artifact node within a Maven project's dependency tree.
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see DependencyTree
 */
public class DependencyNode
{
    // fields -----------------------------------------------------------------

    DependencyNode parent;

    Artifact artifact;

    int depth;

    List children;

    // constructors -----------------------------------------------------------

    DependencyNode()
    {
        children = new ArrayList();
    }

    // public methods ---------------------------------------------------------

    public DependencyNode getParent()
    {
        return parent;
    }

    public Artifact getArtifact()
    {
        return artifact;
    }

    public int getDepth()
    {
        return depth;
    }

    public List getChildren()
    {
        return children;
    }

    public String toString()
    {
        return toString( 0 );
    }

    public String toString( int indentDepth )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < indentDepth; i++ )
        {
            sb.append( " " );
        }

        sb.append( artifact == null ? null : artifact.toString() );
        sb.append( "\n" );

        if ( getChildren() != null )
        {
            for ( Iterator it = getChildren().iterator(); it.hasNext(); )
            {
                DependencyNode dependencyNode = (DependencyNode) it.next();
                sb.append( dependencyNode.toString( indentDepth + 2 ) );
            }
        }

        return sb.toString();
    }
}
