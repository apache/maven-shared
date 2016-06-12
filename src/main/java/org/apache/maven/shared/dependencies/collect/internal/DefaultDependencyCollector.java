package org.apache.maven.shared.dependencies.collect.internal;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependencies.collect.CollectorResult;
import org.apache.maven.shared.dependencies.collect.DependencyCollector;
import org.apache.maven.shared.dependencies.collect.DependencyCollectorException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * This DependencyCollector passes the request to the proper Maven 3.x implementation
 *  
 * @author Robert Scholte
 */
@Component( role = DependencyCollector.class, hint = "default" )
public class DefaultDependencyCollector implements DependencyCollector, Contextualizable 
{
    private PlexusContainer container;
   
    @Override
    public CollectorResult collectDependencies( ProjectBuildingRequest buildingRequest, Dependency root )
        throws DependencyCollectorException
    {
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            DependencyCollector effectiveDependencyCollector = container.lookup( DependencyCollector.class, hint );

            return effectiveDependencyCollector.collectDependencies( buildingRequest, root );
        }
        catch ( ComponentLookupException e )
        {
            throw new DependencyCollectorException( e.getMessage(), e );
        }
    }

    /**
     * @return true if the current Maven version is Maven 3.1.
     */
    protected static boolean isMaven31()
    {
        return canFindCoreClass( "org.eclipse.aether.artifact.Artifact" ); // Maven 3.1 specific
    }

    private static boolean canFindCoreClass( String className )
    {
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( className );

            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    /**
     * Injects the Plexus content.
     *
     * @param context Plexus context to inject.
     * @throws ContextException if the PlexusContainer could not be located.
     */
    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

}
