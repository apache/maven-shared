package org.apache.maven.shared.dependency.graph.internal;

import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

public abstract class AbstractDependencyNode
    implements DependencyNode
{
    /**
     * Applies the specified dependency node visitor to this dependency node and its children.
     * 
     * @param visitor
     *            the dependency node visitor to use
     * @return the visitor result of ending the visit to this node
     * @since 1.1
     */
    public boolean accept( DependencyNodeVisitor visitor )
    {
        if ( visitor.visit( this ) )
        {
            for ( DependencyNode child : getChildren() )
            {
                if ( !child.accept( visitor ) )
                {
                    break;
                }
            }
        }

        return visitor.endVisit( this );
    }
}
