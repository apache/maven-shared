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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.project.MavenProject;

/**
 * Utilities for working with resolution nodes.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see ResolutionNode
 */
public final class ResolutionNodeUtils
{
    // constructors -----------------------------------------------------------
    
    private ResolutionNodeUtils()
    {
        // private constructor for utility class
    }

    // public methods ---------------------------------------------------------
    
    public static List getRootChildrenResolutionNodes( MavenProject project, ArtifactResolutionResult resolutionResult )
    {
        Set resolutionNodes = resolutionResult.getArtifactResolutionNodes();

        // obtain root children nodes
        
        Map rootChildrenResolutionNodesByArtifact = new HashMap();
        
        for ( Iterator iterator = resolutionNodes.iterator(); iterator.hasNext(); )
        {
            ResolutionNode resolutionNode = (ResolutionNode) iterator.next();
            
            if ( resolutionNode.isChildOfRootNode() )
            {
                rootChildrenResolutionNodesByArtifact.put( resolutionNode.getArtifact(), resolutionNode );
            }
        }
        
        // order root children by project dependencies
        
        List rootChildrenResolutionNodes = new ArrayList();
        
        for ( Iterator iterator = project.getDependencyArtifacts().iterator(); iterator.hasNext(); )
        {
            Artifact artifact = (Artifact) iterator.next();
            ResolutionNode resolutionNode = (ResolutionNode) rootChildrenResolutionNodesByArtifact.get( artifact );
            
            rootChildrenResolutionNodes.add( resolutionNode );
        }

        return rootChildrenResolutionNodes;
    }
    
    public static String toString( MavenProject project, ArtifactResolutionResult result )
    {
        StringBuffer buffer = new StringBuffer();
        
        append( buffer, project, result );
        
        return buffer.toString();
    }
    
    public static StringBuffer append( StringBuffer buffer, MavenProject project, ArtifactResolutionResult result )
    {
        ResolutionNode rootNode = new ResolutionNode( project.getArtifact(), Collections.EMPTY_LIST );
        append( buffer, rootNode, 0 );

        List rootChildrenNodes = getRootChildrenResolutionNodes( project, result );
        append( buffer, rootChildrenNodes.iterator(), 1 );
        
        return buffer;
    }
    
    // private methods --------------------------------------------------------
    
    private static StringBuffer append( StringBuffer buffer, Iterator nodesIterator, int depth )
    {
        while ( nodesIterator.hasNext() )
        {
            ResolutionNode node = (ResolutionNode) nodesIterator.next();
            
            append( buffer, node, depth );
        }
        
        return buffer;
    }

    private static StringBuffer append( StringBuffer buffer, ResolutionNode node, int depth )
    {
        for ( int i = 0; i < depth; i++ )
        {
            buffer.append( "   " );
        }
        
        buffer.append( node );
        buffer.append( System.getProperty( "line.separator" ) );
        
        if ( node.isResolved() )
        {
            append( buffer, node.getChildrenIterator(), depth + 1 );
        }
        
        return buffer;
    }
}
