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
 * Testing tool used to read MavenProject instances from pom.xml files, and to create plugin jar
 * files (package phase of the normal build process) for distribution to a test local repository
 * directory.
 * 
 * @plexus.component role="org.apache.maven.shared.test.plugin.ProjectTool" role-hint="default"
 * @author jdcasey
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

    /**
     * Construct a MavenProject instance from the specified POM file.
     */
    public MavenProject readProject( File pomFile )
        throws TestToolsException
    {
        return readProject( pomFile, repositoryTool.findLocalRepositoryDirectory() );
    }

    /**
     * Construct a MavenProject instance from the specified POM file, using the specified local
     * repository directory to resolve ancestor POMs as needed.
     */
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

    /**
     * Run the plugin's Maven build up to the package phase, in order to produce a jar file for 
     * distribution to a test-time local repository. The testVersion parameter specifies the version
     * to be used in the &lt;version/&gt; element of the plugin configuration, and also in fully
     * qualified, unambiguous goal invocations (as in 
     * org.apache.maven.plugins:maven-eclipse-plugin:test:eclipse).
     * 
     * @param pomFile The plugin's POM
     * @param testVersion The version to use for testing this plugin. To promote test resiliency, 
     *   this version should remain unchanged, regardless of what plugin version is under 
     *   development.
     * @param skipUnitTests In cases where test builds occur during the unit-testing phase (usually
     *   a bad testing smell), the plugin jar must be produced <b>without</b> running unit tests.
     *   Otherwise, the testing process will result in a recursive loop of building a plugin jar and
     *   trying to unit test it during the build. In these cases, set this flag to <code>true</code>.
     * @return The resulting MavenProject, after the test version and skip flag (for unit tests) 
     *   have been appropriately configured.
     */
    public MavenProject packageProjectArtifact( File pomFile, String testVersion, boolean skipUnitTests )
        throws TestToolsException
    {
        return packageProjectArtifact( pomFile, testVersion, skipUnitTests, null );
    }

    /**
     * Run the plugin's Maven build up to the package phase, in order to produce a jar file for 
     * distribution to a test-time local repository. The testVersion parameter specifies the version
     * to be used in the &lt;version/&gt; element of the plugin configuration, and also in fully
     * qualified, unambiguous goal invocations (as in 
     * org.apache.maven.plugins:maven-eclipse-plugin:test:eclipse).
     * 
     * @param pomFile The plugin's POM
     * @param testVersion The version to use for testing this plugin. To promote test resiliency, 
     *   this version should remain unchanged, regardless of what plugin version is under 
     *   development.
     * @param skipUnitTests In cases where test builds occur during the unit-testing phase (usually
     *   a bad testing smell), the plugin jar must be produced <b>without</b> running unit tests.
     *   Otherwise, the testing process will result in a recursive loop of building a plugin jar and
     *   trying to unit test it during the build. In these cases, set this flag to <code>true</code>.
     * @param logFile The file to which build output should be logged, in order to allow later 
     *   inspection in case this build fails.
     * @return The resulting MavenProject, after the test version and skip flag (for unit tests) 
     *   have been appropriately configured.
     */
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

    /**
     * Inject a special version for testing, to allow tests to unambiguously reference the plugin
     * currently under test. If test builds will be executed from the unit-testing phase, also inject
     * &lt;skip&gt;true&lt;/skip&gt; into the configuration of the <code>maven-surefire-plugin</code>
     * to allow production of a test-only version of the plugin jar without running unit tests.
     * 
     * @param pomFile The plugin POM
     * @param testVersion The version that allows test builds to reference the plugin under test
     * @param skipUnitTests If true, configure the surefire plugin to skip unit tests
     * @return Information about mangled POM, including the temporary file to which it was written.
     */
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

        File getPomFile()
        {
            return pomFile;
        }

        String getBuildOutputDirectory()
        {
            return buildOutputDirectory;
        }

        String getFinalName()
        {
            return finalName;
        }

        File getBuildLogFile()
        {
            return new File( buildOutputDirectory + "/test-build-logs/" + groupId + "_" + artifactId + "_" + version
                + ".build.log" );
        }

    }

}
