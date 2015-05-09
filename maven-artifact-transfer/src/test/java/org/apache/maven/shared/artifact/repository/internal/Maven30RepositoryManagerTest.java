package org.apache.maven.shared.artifact.repository.internal;

import java.io.File;

import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.shared.artifact.repository.RepositoryManager;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.impl.internal.EnhancedLocalRepositoryManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;

public class Maven30RepositoryManagerTest extends PlexusTestCase
{

    private final File localRepo = new File( "target/tests/local-repo" );
    
    private Maven30RepositoryManager repositoryManager;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        repositoryManager = (Maven30RepositoryManager) super.lookup( RepositoryManager.class, "maven3" );
    }
    
    public void testSetLocalRepositoryBasedirSimple() throws Exception
    {
        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
        repositorySession.setLocalRepositoryManager( new SimpleLocalRepositoryManager( localRepo ) );
        buildingRequest.setRepositorySession( repositorySession );

        File basedir = new File( "NEW/LOCAL/REPO" );
        
        ProjectBuildingRequest newBuildingRequest = repositoryManager.setLocalRepositoryBasedir( buildingRequest, basedir );
        
        assertEquals( basedir.getAbsoluteFile(), newBuildingRequest.getRepositorySession().getLocalRepository().getBasedir() );
        
    }

    public void testSetLocalRepositoryBasedirEnhanced() throws Exception
    {
        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
        repositorySession.setLocalRepositoryManager( new EnhancedLocalRepositoryManager( localRepo ) );
        buildingRequest.setRepositorySession( repositorySession );

        File basedir = new File( "NEW/LOCAL/REPO" );
        
        ProjectBuildingRequest newBuildingRequest = repositoryManager.setLocalRepositoryBasedir( buildingRequest, basedir );
        
        assertEquals( basedir.getAbsoluteFile(), newBuildingRequest.getRepositorySession().getLocalRepository().getBasedir() );
        
    }

}
