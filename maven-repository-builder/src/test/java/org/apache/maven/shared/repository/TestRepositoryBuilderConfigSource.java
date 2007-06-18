package org.apache.maven.shared.repository;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;

public class TestRepositoryBuilderConfigSource
    implements RepositoryBuilderConfigSource
{

    private ArtifactRepository localRepository;

    private MavenProject project;

    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    public MavenProject getProject()
    {
        return project;
    }

}
