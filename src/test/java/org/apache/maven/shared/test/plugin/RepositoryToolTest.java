package org.apache.maven.shared.test.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.tools.easymock.TestFileManager;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

public class RepositoryToolTest
    extends PlexusTestCase
{

    private TestFileManager fileManager;

    public void setUp()
        throws Exception
    {
        super.setUp();
        
        fileManager = new TestFileManager( "RepositoryToolTest.", "" );
    }

    public void tearDown()
        throws Exception
    {
        super.tearDown();
        
        fileManager.cleanUp();
    }

    public void testCreateLocalRepositoryFromPlugin_ShouldWriteJarAndPom()
        throws Exception
    {
        RepositoryTool repoTool = (RepositoryTool) lookup( RepositoryTool.ROLE, "default" );
        
        File tempDir = fileManager.createTempDir();
        
        String pomContent = "<project><modelVersion>4.0.0</modelVersion></project>";
        String jarContent = "This is a test";
        
        File pom = fileManager.createFile( tempDir, "pom.xml", pomContent );
        File jar = fileManager.createFile( tempDir, "artifact-test.jar", jarContent );

        MavenProject pluginProject = new MavenProject();
        
        ArtifactFactory artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
        
        Artifact pluginArtifact = artifactFactory.createArtifact( "group", "artifact", "test", null, "jar" );
        pluginArtifact.setFile( jar );
        pluginArtifact.addMetadata( new ProjectArtifactMetadata( pluginArtifact, pom ) );
        
        pluginProject.setArtifact( pluginArtifact );
        pluginProject.setFile( pom );
        
        File targetLocalRepoBasedir = fileManager.createTempDir();

        repoTool.createLocalRepositoryFromPlugin( pluginProject, targetLocalRepoBasedir );
        
        fileManager.assertFileExistence( targetLocalRepoBasedir, "group/artifact/test/artifact-test.pom", true );
        fileManager.assertFileContents( targetLocalRepoBasedir, "group/artifact/test/artifact-test.jar", jarContent );
        
    }

}
