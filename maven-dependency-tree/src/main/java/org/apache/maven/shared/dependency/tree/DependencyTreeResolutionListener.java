package org.apache.maven.shared.dependency.tree;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * An artifact resolution listener that constructs a dependency tree.
 * 
 * @author Edwin Punzalan
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class DependencyTreeResolutionListener implements ResolutionListener
{
    // fields -----------------------------------------------------------------

    private final Stack parents;

    private final Map artifacts;

    private DependencyNode rootNode;

    private int currentDepth;

    // constructors -----------------------------------------------------------

    public DependencyTreeResolutionListener()
    {
        parents = new Stack();
        artifacts = new HashMap();

        rootNode = null;
        currentDepth = 0;
    }

    // ResolutionListener methods ---------------------------------------------

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#testArtifact(org.apache.maven.artifact.Artifact)
     */
    public void testArtifact( Artifact artifact )
    {
        // intentionally blank
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#startProcessChildren(org.apache.maven.artifact.Artifact)
     */
    public void startProcessChildren( Artifact artifact )
    {
        DependencyNode node = (DependencyNode) artifacts.get( artifact.getDependencyConflictId() );

        node.depth = currentDepth++;

        if ( parents.isEmpty() )
        {
            rootNode = node;
        }

        parents.push( node );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#endProcessChildren(org.apache.maven.artifact.Artifact)
     */
    public void endProcessChildren( Artifact artifact )
    {
        DependencyNode check = (DependencyNode) parents.pop();

        assert artifact.equals( check.artifact );

        currentDepth--;
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#includeArtifact(org.apache.maven.artifact.Artifact)
     */
    public void includeArtifact( Artifact artifact )
    {
        if ( artifacts.containsKey( artifact.getDependencyConflictId() ) )
        {
            DependencyNode prev = (DependencyNode) artifacts.get( artifact.getDependencyConflictId() );

            if ( prev.parent != null )
            {
                prev.parent.children.remove( prev );
            }

            artifacts.remove( artifact.getDependencyConflictId() );
        }

        DependencyNode node = new DependencyNode();
        node.artifact = artifact;

        if ( !parents.isEmpty() )
        {
            node.parent = (DependencyNode) parents.peek();
            node.parent.children.add( node );
            node.depth = currentDepth;
        }

        artifacts.put( artifact.getDependencyConflictId(), node );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#omitForNearer(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact)
     */
    public void omitForNearer( Artifact omitted, Artifact kept )
    {
        assert omitted.getDependencyConflictId().equals( kept.getDependencyConflictId() );

        DependencyNode prev = (DependencyNode) artifacts.get( omitted.getDependencyConflictId() );

        if ( prev != null )
        {
            if ( prev.parent != null )
            {
                prev.parent.children.remove( prev );
            }

            artifacts.remove( omitted.getDependencyConflictId() );
        }

        includeArtifact( kept );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#updateScope(org.apache.maven.artifact.Artifact,
     *      java.lang.String)
     */
    public void updateScope( Artifact artifact, String scope )
    {
        DependencyNode node = (DependencyNode) artifacts.get( artifact.getDependencyConflictId() );

        node.artifact.setScope( scope );
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#manageArtifact(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact)
     */
    public void manageArtifact( Artifact artifact, Artifact replacement )
    {
        DependencyNode node = (DependencyNode) artifacts.get( artifact.getDependencyConflictId() );

        if ( node != null )
        {
            if ( replacement.getVersion() != null )
            {
                node.artifact.setVersion( replacement.getVersion() );
            }
            if ( replacement.getScope() != null )
            {
                node.artifact.setScope( replacement.getScope() );
            }
        }
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#omitForCycle(org.apache.maven.artifact.Artifact)
     */
    public void omitForCycle( Artifact artifact )
    {
        // TODO: Track omit for cycle
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#updateScopeCurrentPom(org.apache.maven.artifact.Artifact,
     *      java.lang.String)
     */
    public void updateScopeCurrentPom( Artifact artifact, String key )
    {
        // TODO: Track scope update
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#selectVersionFromRange(org.apache.maven.artifact.Artifact)
     */
    public void selectVersionFromRange( Artifact artifact )
    {
        // TODO: Track version selection from range
    }

    /*
     * @see org.apache.maven.artifact.resolver.ResolutionListener#restrictRange(org.apache.maven.artifact.Artifact,
     *      org.apache.maven.artifact.Artifact, org.apache.maven.artifact.versioning.VersionRange)
     */
    public void restrictRange( Artifact artifact, Artifact artifact1, VersionRange versionRange )
    {
        // TODO: Track range restriction.
    }

    // public methods ---------------------------------------------------------

    public Collection getNodes()
    {
        return artifacts.values();
    }

    public DependencyNode getRootNode()
    {
        return rootNode;
    }
}
