package org.apache.maven.shared.repository;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;

public interface RepositoryBuilderConfigSource
{
    
    MavenProject getProject();
    
    ArtifactRepository getLocalRepository();

}
