package org.apache.maven.shared.test.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.test.plugin.ProjectTool.PomInfo;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class ProjectToolTest
    extends PlexusTestCase
{
    
    public void testManglePomForTesting_ShouldPopulateOutDirAndFinalName() throws Exception
    {
        ProjectTool tool = (ProjectTool) lookup( ProjectTool.ROLE, "default" );
        
        File pomFile = new File( "pom.xml" );
        
        PomInfo info = tool.manglePomForTesting( pomFile, "test", true );
        
        assertEquals( "target", info.getBuildOutputDirectory() );
        assertEquals( "maven-plugin-testing-tools-test.jar", info.getFinalName() );
    }

    public void testPackageProjectArtifact_ShouldPopulateArtifactFileWithJarLocation() throws Exception
    {
        ProjectTool tool = (ProjectTool) lookup( ProjectTool.ROLE, "default" );

        File pomFile = new File( "pom.xml" );

        MavenProject project = tool.packageProjectArtifact( pomFile, "test", true );

        String expectedPath = "target/maven-plugin-testing-tools-test.jar";
        // be nice with windows
        String actualPath = StringUtils.replace( project.getArtifact().getFile().getPath(), "\\", "/" );

        assertEquals( expectedPath, actualPath );
    }
    
    public void testPackageProjectArtifact_ShouldPopulateWithCorrectArtifactAndMetadata() throws Exception
    {
        ProjectTool tool = (ProjectTool) lookup( ProjectTool.ROLE, "default" );
        
        File pomFile = new File( "pom.xml" );
        
        MavenProject project = tool.packageProjectArtifact( pomFile, "test", true );
        
        Artifact artifact = project.getArtifact();
        
        assertEquals( "jar", artifact.getType() );
        assertTrue( artifact.getFile().exists() );
        
        Collection metadata = artifact.getMetadataList();
        
        boolean foundPomMetadata = false;
        
        for ( Iterator it = metadata.iterator(); it.hasNext(); )
        {
            ArtifactMetadata metadataItem = (ArtifactMetadata) it.next();
            
            if ( metadataItem instanceof ProjectArtifactMetadata )
            {
                foundPomMetadata = true;
            }
        }
        
        assertTrue( foundPomMetadata );
    }
}
