package org.apache.maven.shared.repository;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.repository.model.DefaultRepositoryInfo;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DefaultRepositoryBuilderTest
    extends PlexusTestCase
{

    private MavenProjectBuilder projectBuilder;

    private ArtifactRepositoryLayout defaultLayout;

    private ArtifactRepositoryFactory repoFactory;

    private ArtifactFactory artifactFactory;

    private ArtifactRepository localRepository;

    private ArtifactResolver artifactResolver;

    private ArtifactMetadataSource metadataSource;

    private DefaultProfileManager profileManager;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        projectBuilder = (MavenProjectBuilder) lookup( MavenProjectBuilder.class.getName() );

        File localRepo = new File( getBasedir(), "target/local-repository" );

        defaultLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.class.getName(), "default" );
        repoFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.class.getName() );
        artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.class.getName() );
        artifactResolver = (ArtifactResolver) lookup( ArtifactResolver.class.getName() );
        metadataSource = (ArtifactMetadataSource) lookup( ArtifactMetadataSource.class.getName() );

        localRepository = repoFactory.createArtifactRepository( "local", localRepo.getAbsolutePath(), defaultLayout,
                                                                null, null );

    }

    private MavenProject getProject( String projectResource )
        throws ProjectBuildingException, IOException
    {
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        URL res = cloader.getResource( projectResource );

        File projectFile = new File( res.getPath() );

        return projectBuilder.build( projectFile, localRepository, getProfileManager() );
    }

    private ProfileManager getProfileManager() throws IOException
    {
        if ( profileManager == null )
        {
            ClassLoader cloader = Thread.currentThread().getContextClassLoader();
            URL res = cloader.getResource( "marker.txt" );

            File markerFile = new File( res.getPath() );
            markerFile = markerFile.getCanonicalFile();

            File repoDir = new File( markerFile.getParentFile(), "remote-repository" );

            Profile profile = new Profile();
            Repository repo = new Repository();
            repo.setId( "test.repo" );
            repo.setUrl( repoDir.toURL().toExternalForm() );

            repo.setReleases( new RepositoryPolicy() );
            repo.setSnapshots( new RepositoryPolicy() );

            profileManager = new DefaultProfileManager( getContainer() );
            profileManager.addProfile( profile );
            profileManager.explicitlyActivate( "test.repo" );
        }

        return profileManager;
    }

    public void test_MASSEMBLY_210_projectParentIsIncludedInRepository()
        throws ProjectBuildingException, RepositoryAssemblyException, IOException
    {
        MavenProject project = getProject( "projects/massembly-210-direct-parent/pom.xml" );
        // TODO: jdcasey, the project loaded that way as no initialized Artifact objects
        // TODO: with this non resolved content the repository assembler does not run!

        TestRepositoryBuilderConfigSource cs = new TestRepositoryBuilderConfigSource();
        cs.setProject( project );
        cs.setLocalRepository( localRepository );

        DefaultRepositoryAssembler assembler = new DefaultRepositoryAssembler( artifactFactory, artifactResolver,
                                                                               defaultLayout, repoFactory,
                                                                               metadataSource, projectBuilder );
        // TODO: NPE thrown if logger not set
        assembler.enableLogging( new ConsoleLogger( Logger.LEVEL_DEBUG, "console"));

        File repositoryDirectory = new File( getBasedir(), "target/test-repositories/massembly-210-direct-parent" );

        DefaultRepositoryInfo repoInfo = new DefaultRepositoryInfo();
        // TODO: NPE if we don't call this - no clue what it's supposed to represent
        repoInfo.setGroupVersionAlignments( new ArrayList());

        assembler.buildRemoteRepository( repositoryDirectory, repoInfo, cs );

        File parentFile = new File( repositoryDirectory, "massembly/210/parent/1.0-SNAPSHOT/parent-1.0-SNAPSHOT.pom" );

        assertTrue( parentFile.exists() );
    }

}
