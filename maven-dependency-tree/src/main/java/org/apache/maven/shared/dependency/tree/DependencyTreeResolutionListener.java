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

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.resolver.ResolutionListenerForDepMgmt;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.shared.dependency.tree.traversal.CollectingDependencyNodeVisitor;

/**
 * An artifact resolution listener that constructs a dependency tree.
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class DependencyTreeResolutionListener implements ResolutionListener, ResolutionListenerForDepMgmt
{
    // fields -----------------------------------------------------------------

    /**
     * The parent dependency nodes of the current dependency node.
     */
    private final Stack parentNodes;

    /**
     * A map of dependency nodes by their attached artifact.
     */
    private final Map nodesByArtifact;

    /**
     * The root dependency node of the computed dependency tree.
     */
    private DependencyNode rootNode;

    /**
     * The dependency node currently being processed by this listener.
     */
    private DependencyNode currentNode;
    
    /**
     * The id of the currently managed artifact, or <code>null</code> if the current artifact is not managed.
     */
    private String managedId;
    
    /**
     * The original version of the currently managed artifact, or <code>null</code> if the current artifact is not
     * managed.
     */
    private String premanagedVersion;
    
    /**
     * The original scope of the currently managed artifact, or <code>null</code> if the current artifact is not
     * managed.
     */
    private String premanagedScope;

    // constructors -----------------------------------------------------------

    /**
     * Creates a new dependency tree resolution listener.
     */
    public DependencyTreeResolutionListener()
    {
        parentNodes = new Stack();
        nodesByArtifact = new IdentityHashMap();
        rootNode = null;
        currentNode = null;
        managedId = null;
        premanagedScope = null;
        premanagedScope = null;
    }

    // ResolutionListener methods ---------------------------------------------

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#testArtifact(org.apache.maven.artifact.Artifact)
     */
    public void testArtifact( Artifact artifact )
    {
        // no-op
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#startProcessChildren(org.apache.maven.artifact.Artifact)
     */
    public void startProcessChildren( Artifact artifact )
    {
        if ( !currentNode.getArtifact().equals( artifact ) )
        {
            throw new IllegalStateException( "Artifact was expected to be " + currentNode.getArtifact() + " but was "
                            + artifact );
        }

        parentNodes.push( currentNode );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#endProcessChildren(org.apache.maven.artifact.Artifact)
     */
    public void endProcessChildren( Artifact artifact )
    {
        DependencyNode node = (DependencyNode) parentNodes.pop();

        if ( node == null )
        {
            throw new IllegalStateException( "Parent dependency node was null" );
        }

        if ( !node.getArtifact().equals( artifact ) )
        {
            throw new IllegalStateException( "Parent dependency node artifact was expected to be " + node.getArtifact()
                            + " but was " + artifact );
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#includeArtifact(org.apache.maven.artifact.Artifact)
     */
    public void includeArtifact( Artifact artifact )
    {
        DependencyNode existingNode = getNode( artifact );

        /*
         * Ignore duplicate includeArtifact calls since omitForNearer can be called prior to includeArtifact on the same
         * artifact, and we don't wish to include it twice.
         */
        if ( existingNode == null && isCurrentNodeIncluded() )
        {
            DependencyNode node = addNode( artifact );

            /*
             * Add the dependency management information cached in any prior manageArtifact calls, since includeArtifact
             * is always called after manageArtifact.
             */
            if ( premanagedVersion != null || premanagedScope != null )
            {
                if ( !managedId.equals( artifact.getId() ) )
                {
                    throw new IllegalStateException( "Managed artifact id was expected to be " + managedId + " but was " + artifact.getId() );
                }
                
                if ( premanagedVersion != null )
                {
                    node.setPremanagedVersion( premanagedVersion );
                }
                
                if ( premanagedScope != null )
                {
                    node.setPremanagedScope( premanagedScope );
                }
                
                managedId = null;
                premanagedVersion = null;
                premanagedScope = null;
            }
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#omitForNearer(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact)
     */
    public void omitForNearer( Artifact omitted, Artifact kept )
    {
        if ( !omitted.getDependencyConflictId().equals( kept.getDependencyConflictId() ) )
        {
            throw new IllegalArgumentException( "Omitted artifact dependency conflict id "
                            + omitted.getDependencyConflictId() + " differs from kept artifact dependency conflict id "
                            + kept.getDependencyConflictId() );
        }

        if ( isCurrentNodeIncluded() )
        {
            DependencyNode omittedNode = getNode( omitted );

            if ( omittedNode != null )
            {
                removeNode( omitted );
            }
            else
            {
                omittedNode = createNode( omitted );

                currentNode = omittedNode;
            }

            omittedNode.omitForConflict( kept );
            
            
            
            DependencyNode keptNode = getNode( kept );
            
            if ( keptNode == null )
            {
                addNode( kept );
            }
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#updateScope(org.apache.maven.artifact.Artifact,
     *      java.lang.String)
     */
    public void updateScope( Artifact artifact, String scope )
    {
        DependencyNode node = getNode( artifact );

        if ( node == null )
        {
            throw new IllegalStateException( "Cannot find dependency node for artifact " + artifact );
        }

        node.setOriginalScope( artifact.getScope() );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#manageArtifact(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact)
     */
    public void manageArtifact( Artifact artifact, Artifact replacement )
    {
        // TODO: remove when ResolutionListenerForDepMgmt merged into ResolutionListener
        
        if ( replacement.getVersion() != null )
        {
            manageArtifactVersion( artifact, replacement );
        }
        
        if ( replacement.getScope() != null )
        {
            manageArtifactScope( artifact, replacement );
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#omitForCycle(org.apache.maven.artifact.Artifact)
     */
    public void omitForCycle( Artifact artifact )
    {
        if ( isCurrentNodeIncluded() )
        {
            DependencyNode node = createNode( artifact );

            node.omitForCycle();
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#updateScopeCurrentPom(org.apache.maven.artifact.Artifact,
     *      java.lang.String)
     */
    public void updateScopeCurrentPom( Artifact artifact, String scope )
    {
        DependencyNode node = getNode( artifact );

        if ( node == null )
        {
            throw new IllegalStateException( "Cannot find dependency node for artifact " + artifact );
        }
        
        node.setFailedUpdateScope( scope );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#selectVersionFromRange(org.apache.maven.artifact.Artifact)
     */
    public void selectVersionFromRange( Artifact artifact )
    {
        // TODO: track version selection from range in node
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#restrictRange(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact, org.apache.maven.artifact.versioning.VersionRange)
     */
    public void restrictRange( Artifact artifact, Artifact artifact1, VersionRange versionRange )
    {
        // TODO: track range restriction in node
    }
    
    // ResolutionListenerForDepMgmt methods -----------------------------------
    
    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListenerForDepMgmt#manageArtifactVersion(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact)
     */
    public void manageArtifactVersion( Artifact artifact, Artifact replacement )
    {
        /*
         * DefaultArtifactCollector calls manageArtifact twice: first with the change; then subsequently with no change.
         * We ignore the second call when the versions are equal.
         */
        if ( isCurrentNodeIncluded() && !replacement.getVersion().equals( artifact.getVersion() ) )
        {
            /*
             * Cache management information and apply in includeArtifact, since DefaultArtifactCollector mutates the
             * artifact and then calls includeArtifact after manageArtifact.
             */
            managedId = replacement.getId();
            premanagedVersion = artifact.getVersion();
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListenerForDepMgmt#manageArtifactScope(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact)
     */
    public void manageArtifactScope( Artifact artifact, Artifact replacement )
    {
        /*
         * DefaultArtifactCollector calls manageArtifact twice: first with the change; then subsequently with no change.
         * We ignore the second call when the scopes are equal.
         */
        if ( isCurrentNodeIncluded() && !replacement.getScope().equals( artifact.getScope() ) )
        {
            /*
             * Cache management information and apply in includeArtifact, since DefaultArtifactCollector mutates the
             * artifact and then calls includeArtifact after manageArtifact.
             */
            managedId = replacement.getId();
            premanagedScope = artifact.getScope();
        }
    }

    // public methods ---------------------------------------------------------
    
    /**
     * Gets a list of all dependency nodes in the computed dependency tree.
     * 
     * @return a list of dependency nodes
     * @deprecated As of 1.1, use a {@link CollectingDependencyNodeVisitor} on the root dependency node
     */
    public Collection getNodes()
    {
        return Collections.unmodifiableCollection( nodesByArtifact.values() );
    }

    /**
     * Gets the root dependency node of the computed dependency tree.
     * 
     * @return the root node
     */
    public DependencyNode getRootNode()
    {
        return rootNode;
    }

    // private methods --------------------------------------------------------

    /**
     * Creates a new dependency node for the specified artifact and appends it to the current parent dependency node.
     * 
     * @param artifact
     *            the attached artifact for the new dependency node
     * @return the new dependency node
     */
    private DependencyNode createNode( Artifact artifact )
    {
        DependencyNode node = new DependencyNode( artifact );

        if ( !parentNodes.isEmpty() )
        {
            DependencyNode parent = (DependencyNode) parentNodes.peek();

            parent.addChild( node );
        }

        return node;
    }
    
    /**
     * Creates a new dependency node for the specified artifact, appends it to the current parent dependency node and
     * puts it into the dependency node cache.
     * 
     * @param artifact
     *            the attached artifact for the new dependency node
     * @return the new dependency node
     */
    private DependencyNode addNode( Artifact artifact )
    {
        DependencyNode node = createNode( artifact );

        DependencyNode previousNode = (DependencyNode) nodesByArtifact.put( node.getArtifact(), node );
        
        if ( previousNode != null )
        {
            throw new IllegalStateException( "Duplicate node registered for artifact: " + node.getArtifact() );
        }
        
        if ( rootNode == null )
        {
            rootNode = node;
        }

        currentNode = node;
        
        return node;
    }

    /**
     * Gets the dependency node for the specified artifact from the dependency node cache.
     * 
     * @param artifact
     *            the artifact to find the dependency node for
     * @return the dependency node, or <code>null</code> if the specified artifact has no corresponding dependency
     *         node
     */
    private DependencyNode getNode( Artifact artifact )
    {
        return (DependencyNode) nodesByArtifact.get( artifact );
    }

    /**
     * Removes the dependency node for the specified artifact from the dependency node cache.
     * 
     * @param artifact
     *            the artifact to remove the dependency node for
     */
    private void removeNode( Artifact artifact )
    {
        DependencyNode node = (DependencyNode) nodesByArtifact.remove( artifact );

        if ( !artifact.equals( node.getArtifact() ) )
        {
            throw new IllegalStateException( "Removed dependency node artifact was expected to be " + artifact
                            + " but was " + node.getArtifact() );
        }
    }

    /**
     * Gets whether the all the ancestors of the dependency node currently being processed by this listener have an
     * included state.
     * 
     * @return <code>true</code> if all the ancestors of the current dependency node have a state of
     *         <code>INCLUDED</code>
     */
    private boolean isCurrentNodeIncluded()
    {
        boolean included = true;

        for ( Iterator iterator = parentNodes.iterator(); included && iterator.hasNext(); )
        {
            DependencyNode node = (DependencyNode) iterator.next();

            if ( node.getState() != DependencyNode.INCLUDED )
            {
                included = false;
            }
        }

        return included;
    }
}
