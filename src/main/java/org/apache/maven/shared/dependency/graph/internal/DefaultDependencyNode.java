package org.apache.maven.shared.dependency.graph.internal;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

public class DefaultDependencyNode
    implements DependencyNode
{
    private final Artifact artifact;

    private final DependencyNode parent;

    private List<DependencyNode> children;

    public DefaultDependencyNode( DependencyNode parent, Artifact artifact )
    {
        this.parent = parent;
        this.artifact = artifact;
    }

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

    public Artifact getArtifact()
    {
        return artifact;
    }

    public void setChildren( List<DependencyNode> children )
    {
        this.children = children;
    }

    public List<DependencyNode> getChildren()
    {
        return children;
    }

    public DependencyNode getParent()
    {
        return parent;
    }
}
