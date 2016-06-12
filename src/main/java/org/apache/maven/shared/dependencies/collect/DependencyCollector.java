package org.apache.maven.shared.dependencies.collect;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.ProjectBuildingRequest;

/**
 * Will only download the pom files when not available, never the artifact. 
 * 
 * @author Robert Scholte
 *
 */
public interface DependencyCollector
{

    /**
     * @param buildingRequest {@link ProjectBuildingRequest}
     * @param root {@link Dependency}
     * @return {@link CollectorResult}
     * @throws DependencyCollectorException in case of an error.
     */
    CollectorResult collectDependencies( ProjectBuildingRequest buildingRequest, Dependency root )
        throws DependencyCollectorException;
    
}
