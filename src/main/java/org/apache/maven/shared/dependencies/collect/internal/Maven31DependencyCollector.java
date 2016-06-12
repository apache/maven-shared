package org.apache.maven.shared.dependencies.collect.internal;

import java.util.List;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependencies.collect.CollectorResult;
import org.apache.maven.shared.dependencies.collect.DependencyCollector;
import org.apache.maven.shared.dependencies.collect.DependencyCollectorException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Maven 3.1+ implementation of the {@link DependencyCollector}
 * 
 * @author Robert Scholte
 *
 */
@Component( role = DependencyCollector.class, hint = "maven31" )
public class Maven31DependencyCollector
    implements DependencyCollector
{
    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement
    private ArtifactHandlerManager artifactHandlerManager;

    @Override
    public CollectorResult collectDependencies( final ProjectBuildingRequest buildingRequest,
                                                org.apache.maven.model.Dependency root )
        throws DependencyCollectorException
    {
        ArtifactTypeRegistry typeRegistry =
            (ArtifactTypeRegistry) Invoker.invoke( RepositoryUtils.class, "newArtifactTypeRegistry",
                                                   ArtifactHandlerManager.class, artifactHandlerManager );

        Class<?>[] argClasses = new Class<?>[] { org.apache.maven.model.Dependency.class, ArtifactTypeRegistry.class };
        Object[] args = new Object[] { root, typeRegistry };
        Dependency aetherRoot = (Dependency) Invoker.invoke( RepositoryUtils.class, "toDependency", argClasses, args );

        return collectDependencies( buildingRequest, aetherRoot );
    }

    private CollectorResult collectDependencies( ProjectBuildingRequest buildingRequest, Dependency aetherRoot )
        throws DependencyCollectorException
    {
        CollectRequest request = new CollectRequest();
        request.setRoot( aetherRoot );

        RepositorySystemSession session =
            (RepositorySystemSession) Invoker.invoke( buildingRequest, "getRepositorySession" );

        @SuppressWarnings( "unchecked" )
        List<RemoteRepository> aetherRepositories =
            (List<RemoteRepository>) Invoker.invoke( RepositoryUtils.class, "toRepos", List.class,
                                                     buildingRequest.getRemoteRepositories() );
        request.setRepositories( aetherRepositories );

        try
        {
            return new Maven31CollectorResult( repositorySystem.collectDependencies( session, request ) );
        }
        catch ( DependencyCollectionException e )
        {
            throw new DependencyCollectorException( e.getMessage(), e );
        }
    }

}
