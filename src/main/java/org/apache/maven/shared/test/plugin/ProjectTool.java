package org.apache.maven.shared.test.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @plexus.component role="org.apache.maven.shared.test.plugin.ProjectTool" role-hint="default"
 * @author jdcasey
 *
 */
public class ProjectTool
{
    public static final String ROLE = ProjectTool.class.getName();

    /**
     * @plexus.requirement role-hint="default"
     */
    private BuildTool buildTool;

    /**
     * @plexus.requirement role-hint="default"
     */
    private RepositoryTool repositoryTool;

    /**
     * @plexus.requirement
     */
    private MavenProjectBuilder projectBuilder;
    
    /**
     * @plexus.requirement
     */
    private ArtifactHandlerManager artifactHandlerManager;

    /**
     * @plexus.requirement
     */
    private ArtifactFactory artifactFactory;

    public MavenProject readProject( File pomFile )
        throws TestToolsException
    {
        return readProject( pomFile, repositoryTool.findLocalRepositoryDirectory() );
    }

    public MavenProject readProject( File pomFile, File localRepositoryBasedir )
        throws TestToolsException
    {
        try
        {
            ArtifactRepository localRepository = repositoryTool
                .createLocalArtifactRepositoryInstance( localRepositoryBasedir );

            return projectBuilder.build( pomFile, localRepository, null );
        }
        catch ( ProjectBuildingException e )
        {
            throw new TestToolsException( "Error building MavenProject instance from test pom: " + pomFile, e );
        }
    }

    public MavenProject packageProjectArtifact( File pomFile, String testVersion, boolean skipUnitTests )
        throws TestToolsException
    {
        return packageProjectArtifact( pomFile, testVersion, skipUnitTests, null );
    }

    public MavenProject packageProjectArtifact( File pomFile, String testVersion, boolean skipUnitTests, File logFile )
        throws TestToolsException
    {
        PomInfo pomInfo = manglePomForTesting( pomFile, testVersion, skipUnitTests );

        Properties properties = new Properties();

        List goals = new ArrayList();
        goals.add( "package" );

        File buildLog = logFile == null ? pomInfo.getBuildLogFile() : logFile;

        buildTool.executeMaven( pomInfo.getPomFile(), properties, goals, buildLog );

        File artifactFile = new File( pomInfo.getBuildOutputDirectory() + "/" + pomInfo.getFinalName() );

        try
        {
            MavenProject project = projectBuilder.build( pomInfo.getPomFile(), repositoryTool
                .createLocalArtifactRepositoryInstance(), null );

            Artifact artifact = artifactFactory.createArtifact( project.getGroupId(), project.getArtifactId(), project.getVersion(), null, project.getPackaging() );
            
            artifact.setFile( artifactFile );
            artifact.addMetadata( new ProjectArtifactMetadata( artifact, project.getFile() ) );
            
            project.setArtifact( artifact );

            return project;
        }
        catch ( ProjectBuildingException e )
        {
            throw new TestToolsException(
                                          "Error building MavenProject instance from test pom: " + pomInfo.getPomFile(),
                                          e );
        }
    }

    protected PomInfo manglePomForTesting( File pomFile, String testVersion, boolean skipUnitTests )
        throws TestToolsException
    {
        File input = new File( "pom.xml" );

        File output = new File( "pom-test.xml" );
        output.deleteOnExit();

        FileReader reader = null;
        FileWriter writer = null;

        Model model = null;
        String finalName = null;
        String buildOutputDirectory = null;

        try
        {
            reader = new FileReader( input );
            writer = new FileWriter( output );

            model = new MavenXpp3Reader().read( reader );

            model.setVersion( testVersion );

            Build build = model.getBuild();
            if ( build == null )
            {
                build = new Build();
                model.setBuild( build );
            }

            finalName = build.getFinalName();
            
            if ( finalName == null )
            {
                ArtifactHandler handler = artifactHandlerManager.getArtifactHandler( model.getPackaging() );
                
                String ext = handler.getExtension();
                
                finalName = model.getArtifactId() + "-" + model.getVersion() + "." + ext;
            }
            
            buildOutputDirectory = build.getOutputDirectory();
            
            if ( buildOutputDirectory == null )
            {
                buildOutputDirectory = "target";
            }

            if ( skipUnitTests )
            {
                List plugins = build.getPlugins();
                Plugin plugin = null;
                for ( Iterator iter = plugins.iterator(); iter.hasNext(); )
                {
                    Plugin plug = (Plugin) iter.next();

                    if ( "maven-surefire-plugin".equals( plug.getArtifactId() ) )
                    {
                        plugin = plug;
                        break;
                    }
                }

                if ( plugin == null )
                {
                    plugin = new Plugin();
                    plugin.setArtifactId( "maven-surefire-plugin" );
                    build.addPlugin( plugin );
                }

                Xpp3Dom configDom = (Xpp3Dom) plugin.getConfiguration();
                if ( configDom == null )
                {
                    configDom = new Xpp3Dom( "configuration" );
                    plugin.setConfiguration( configDom );
                }

                Xpp3Dom skipDom = new Xpp3Dom( "skip" );
                skipDom.setValue( "true" );

                configDom.addChild( skipDom );
            }

            new MavenXpp3Writer().write( writer, model );
        }
        catch ( IOException e )
        {
            throw new TestToolsException( "Error creating test-time version of POM for: " + input, e );
        }
        catch ( XmlPullParserException e )
        {
            throw new TestToolsException( "Error creating test-time version of POM for: " + input, e );
        }
        finally
        {
            IOUtil.close( reader );
            IOUtil.close( writer );
        }

        return new PomInfo( output, model.getGroupId(), model.getArtifactId(), model.getVersion(),
                            buildOutputDirectory, finalName );
    }

    static final class PomInfo
    {
        private final File pomFile;

        private final String groupId;

        private final String artifactId;

        private final String version;

        private final String finalName;

        private final String buildOutputDirectory;

        PomInfo( File pomFile, String groupId, String artifactId, String version, String buildOutputDirectory,
                 String finalName )
        {
            this.pomFile = pomFile;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.buildOutputDirectory = buildOutputDirectory;
            this.finalName = finalName;
        }

        public File getPomFile()
        {
            return pomFile;
        }

        public String getBuildOutputDirectory()
        {
            return buildOutputDirectory;
        }

        public String getFinalName()
        {
            return finalName;
        }

        public File getBuildLogFile()
        {
            return new File( buildOutputDirectory + "/test-build-logs/" + groupId + "_" + artifactId + "_" + version
                + ".build.log" );
        }

    }

}
