package org.apache.maven.archiver;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.monitor.event.EventDispatcher;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@SuppressWarnings( "ResultOfMethodCallIgnored" )
public class MavenArchiverTest
    extends TestCase
{
    static class ArtifactComparator
        implements Comparator<Artifact>
    {
        public int compare( Artifact o1, Artifact o2 )
        {
            return o1.getArtifactId().compareTo( o2.getArtifactId() );
        }

        public boolean equals( Object o )
        {
            return false;
        }
    }

    public void testGetManifestExtensionList()
        throws Exception
    {
        MavenArchiver archiver = new MavenArchiver();

        MavenSession session = getDummySession();

        Model model = new Model();
        model.setArtifactId( "dummy" );

        MavenProject project = new MavenProject( model );
        // we need to sort the artifacts for test purposes
        Set<Artifact> artifacts = new TreeSet<Artifact>( new ArtifactComparator() );
        project.setArtifacts( artifacts );

        // there should be a mock or a setter for this field.
        ManifestConfiguration config = new ManifestConfiguration()
        {
            public boolean isAddExtensions()
            {
                return true;
            }
        };

        Manifest manifest;

        manifest = archiver.getManifest( session, project, config );

        assertNotNull( manifest.getMainAttributes() );

        for ( Map.Entry<String, Attributes> entry : manifest.getEntries().entrySet() )
        {
            System.out.println( entry.getKey() + " " + entry.getValue().getValue( "Extension-List" ) );

        }

        assertEquals( null, manifest.getMainAttributes().getValue( "Extension-List" ) );

        MockArtifact artifact1 = new MockArtifact();
        artifact1.setGroupId( "org.apache.dummy" );
        artifact1.setArtifactId( "dummy1" );
        artifact1.setVersion( "1.0" );
        artifact1.setType( "dll" );
        artifact1.setScope( "compile" );

        artifacts.add( artifact1 );

        manifest = archiver.getManifest( session, project, config );

        assertEquals( null, manifest.getMainAttributes().getValue( "Extension-List" ) );

        MockArtifact artifact2 = new MockArtifact();
        artifact2.setGroupId( "org.apache.dummy" );
        artifact2.setArtifactId( "dummy2" );
        artifact2.setVersion( "1.0" );
        artifact2.setType( "jar" );
        artifact2.setScope( "compile" );

        artifacts.add( artifact2 );

        manifest = archiver.getManifest( session, project, config );

        assertEquals( "dummy2", manifest.getMainAttributes().getValue( "Extension-List" ) );

        MockArtifact artifact3 = new MockArtifact();
        artifact3.setGroupId( "org.apache.dummy" );
        artifact3.setArtifactId( "dummy3" );
        artifact3.setVersion( "1.0" );
        artifact3.setScope( "test" );
        artifact3.setType( "jar" );

        artifacts.add( artifact3 );

        manifest = archiver.getManifest( session, project, config );

        assertEquals( "dummy2", manifest.getMainAttributes().getValue( "Extension-List" ) );

        MockArtifact artifact4 = new MockArtifact();
        artifact4.setGroupId( "org.apache.dummy" );
        artifact4.setArtifactId( "dummy4" );
        artifact4.setVersion( "1.0" );
        artifact4.setType( "jar" );
        artifact4.setScope( "compile" );

        artifacts.add( artifact4 );

        manifest = archiver.getManifest( session, project, config );

        assertEquals( "dummy2 dummy4", manifest.getMainAttributes().getValue( "Extension-List" ) );
    }

    public void testMultiClassPath()
        throws Exception
    {
        final File tempFile = File.createTempFile( "maven-archiver-test-", ".jar" );

        try
        {
            MavenArchiver archiver = new MavenArchiver();

            MavenSession session = getDummySession();

            Model model = new Model();
            model.setArtifactId( "dummy" );

            MavenProject project = new MavenProject( model )
            {
                public List getRuntimeClasspathElements()
                {
                    return Collections.singletonList( tempFile.getAbsolutePath() );
                }
            };

            // there should be a mock or a setter for this field.
            ManifestConfiguration manifestConfig = new ManifestConfiguration()
            {
                public boolean isAddClasspath()
                {
                    return true;
                }
            };

            MavenArchiveConfiguration archiveConfiguration = new MavenArchiveConfiguration();
            archiveConfiguration.setManifest( manifestConfig );
            archiveConfiguration.addManifestEntry( "Class-Path", "help/" );

            Manifest manifest = archiver.getManifest( session, project, archiveConfiguration );
            String classPath = manifest.getMainAttributes().getValue( "Class-Path" );
            assertTrue( "User specified Class-Path entry was not added to manifest", classPath.contains( "help/" ) );
            assertTrue( "Class-Path generated by addClasspath was not added to manifest",
                        classPath.contains( tempFile.getName() ) );
        }
        finally
        {
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }
    }

    public void testRecreation()
        throws Exception
    {
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( false );

        FileUtils.deleteDirectory( "target/maven-archiver" );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
        jarFile.setLastModified( System.currentTimeMillis() - 60000L );
        long time = jarFile.lastModified();

        List files = FileUtils.getFiles( new File( "target/maven-archiver" ), "**/**", null, true );
        for ( Object file : files )
        {
            File f = (File) file;
            f.setLastModified( time );
        }

        archiver.createArchive( session, project, config );
        assertEquals( jarFile.lastModified(), time );

        config.setForced( true );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.lastModified() > time );
    }

    public void testNotGenerateImplementationVersionForMANIFESTMF()
        throws Exception
    {
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            JarArchiver jarArchiver = getCleanJarArciver( jarFile );

            MavenArchiver archiver = getMavenArchiver( jarArchiver );

            MavenSession session = getDummySession();
            MavenProject project = getDummyProject();

            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( false );
            archiver.createArchive( session, project, config );
            assertTrue( jarFile.exists() );

            jar = new JarFile( jarFile );
            Map entries = jar.getManifest().getMainAttributes();
            assertFalse( entries.containsKey( Attributes.Name.IMPLEMENTATION_VERSION ) ); // "Implementation-Version"
        }
        finally
        {
            // cleanup streams
            if ( jar != null )
            {
                jar.close();
            }
        }
    }

    public void testGenerateImplementationVersionForMANIFESTMF()
        throws Exception
    {
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            JarArchiver jarArchiver = getCleanJarArciver( jarFile );

            MavenArchiver archiver = getMavenArchiver( jarArchiver );

            MavenSession session = getDummySession();
            MavenProject project = getDummyProject();

            String ls = System.getProperty( "line.separator" );
            project.setDescription( "foo " + ls + " bar " );
            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.addManifestEntry( "Description", project.getDescription() );
            archiver.createArchive( session, project, config );
            assertTrue( jarFile.exists() );

            jar = new JarFile( jarFile );

            Map entries = jar.getManifest().getMainAttributes();

            assertTrue( entries.containsKey( Attributes.Name.IMPLEMENTATION_VERSION ) );
            assertEquals( "0.1", entries.get( Attributes.Name.IMPLEMENTATION_VERSION ) );
        }
        finally
        {
            // cleanup streams
            if ( jar != null )
            {
                jar.close();
            }
        }
    }

    private MavenArchiver getMavenArchiver( JarArchiver jarArchiver )
    {
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( jarArchiver );
        archiver.setOutputFile( jarArchiver.getDestFile() );
        return archiver;
    }

    public void testDashesInClassPath_MSHARED_134()
        throws IOException, ManifestException, DependencyResolutionRequiredException
    {
        File jarFile = new File( "target/test/dummyWithDashes.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();

        Set<Artifact> artifacts =
            getArtifacts( getMockArtifact1(), getArtifactWithDot(), getMockArtifact2(), getMockArtifact3() );

        project.setArtifacts( artifacts );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( false );

        final ManifestConfiguration mftConfig = config.getManifest();
        mftConfig.setMainClass( "org.apache.maven.Foo" );
        mftConfig.setAddClasspath( true );
        mftConfig.setAddExtensions( true );
        mftConfig.setClasspathPrefix( "./lib/" );

        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
    }

    public void testDashesInClassPath_MSHARED_182()
        throws IOException, ManifestException, DependencyResolutionRequiredException
    {
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );
        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();

        Set<Artifact> artifacts =
            getArtifacts( getMockArtifact1(), getArtifactWithDot(), getMockArtifact2(), getMockArtifact3() );

        project.setArtifacts( artifacts );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( false );

        final ManifestConfiguration mftConfig = config.getManifest();
        mftConfig.setMainClass( "org.apache.maven.Foo" );
        mftConfig.setAddClasspath( true );
        mftConfig.setAddExtensions( true );
        mftConfig.setClasspathPrefix( "./lib/" );
        config.addManifestEntry( "Key1", "value1" );
        config.addManifestEntry( "key2", "value2" );

        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
        final Attributes mainAttributes = getJarFileManifest( jarFile ).getMainAttributes();
        assertEquals( "value1", mainAttributes.getValue( "Key1" ) );
        assertEquals( "value2", mainAttributes.getValue( "Key2" ) );
    }

    public void testCarriageReturnInManifestEntry()
        throws Exception
    {
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();

        String ls = System.getProperty( "line.separator" );
        project.setDescription( "foo " + ls + " bar " );
        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.addManifestEntry( "Description", project.getDescription() );
        // config.addManifestEntry( "EntryWithTab", " foo tab " + ( '\u0009' ) + ( '\u0009' ) + " bar tab" + (
        // '\u0009' ) );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );

        final Manifest manifest = getJarFileManifest( jarFile );
        Attributes attributes = manifest.getMainAttributes();
        assertTrue( project.getDescription().indexOf( ls ) > 0 );
        Attributes.Name description = new Attributes.Name( "Description" );
        String value = attributes.getValue( description );
        assertNotNull( value );
        assertFalse( value.indexOf( ls ) > 0 );
    }

    public void testDeprecatedCreateArchiveAPI()
        throws Exception
    {
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenProject project = getDummyProject();
        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );

        //noinspection deprecation
        archiver.createArchive( project, config );
        assertTrue( jarFile.exists() );
        Attributes manifest = getJarFileManifest( jarFile ).getMainAttributes();

        assertEquals( "Apache Maven", manifest.get( new Attributes.Name( "Created-By" ) ) ); // no version number

        assertEquals( "archiver test", manifest.get( Attributes.Name.SPECIFICATION_TITLE ) );
        assertEquals( "0.1", manifest.get( Attributes.Name.SPECIFICATION_VERSION ) );
        assertEquals( "Apache", manifest.get( Attributes.Name.SPECIFICATION_VENDOR ) );

        assertEquals( "archiver test", manifest.get( Attributes.Name.IMPLEMENTATION_TITLE ) );
        assertEquals( "0.1", manifest.get( Attributes.Name.IMPLEMENTATION_VERSION ) );
        assertEquals( "org.apache.dummy", manifest.get( Attributes.Name.IMPLEMENTATION_VENDOR_ID ) );
        assertEquals( "Apache", manifest.get( Attributes.Name.IMPLEMENTATION_VENDOR ) );

        assertEquals( System.getProperty( "java.version" ), manifest.get( new Attributes.Name( "Build-Jdk" ) ) );
        assertEquals( System.getProperty( "user.name" ), manifest.get( new Attributes.Name( "Built-By" ) ) );
    }

    public void testManifestEntries()
        throws Exception
    {
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();
        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );

        Map<String, String> manifestEntries = new HashMap<String, String>();
        manifestEntries.put( "foo", "bar" );
        manifestEntries.put( "first-name", "olivier" );
        manifestEntries.put( "keyWithEmptyValue", null );
        config.setManifestEntries( manifestEntries );

        ManifestSection manifestSection = new ManifestSection();
        manifestSection.setName( "UserSection" );
        manifestSection.addManifestEntry( "key", "value" );
        List<ManifestSection> manifestSections = new ArrayList<ManifestSection>();
        manifestSections.add( manifestSection );
        config.setManifestSections( manifestSections );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );

        final Manifest jarFileManifest = getJarFileManifest( jarFile );
        Attributes manifest = jarFileManifest.getMainAttributes();

        assertEquals( "Apache Maven 3.0.4", manifest.get( new Attributes.Name( "Created-By" ) ) );

        assertEquals( "archiver test", manifest.get( Attributes.Name.SPECIFICATION_TITLE ) );
        assertEquals( "0.1", manifest.get( Attributes.Name.SPECIFICATION_VERSION ) );
        assertEquals( "Apache", manifest.get( Attributes.Name.SPECIFICATION_VENDOR ) );

        assertEquals( "archiver test", manifest.get( Attributes.Name.IMPLEMENTATION_TITLE ) );
        assertEquals( "0.1", manifest.get( Attributes.Name.IMPLEMENTATION_VERSION ) );
        assertEquals( "org.apache.dummy", manifest.get( Attributes.Name.IMPLEMENTATION_VENDOR_ID ) );
        assertEquals( "Apache", manifest.get( Attributes.Name.IMPLEMENTATION_VENDOR ) );

        assertEquals( "org.apache.maven.Foo", manifest.get( Attributes.Name.MAIN_CLASS ) );

        assertEquals( "bar", manifest.get( new Attributes.Name( "foo" ) ) );
        assertEquals( "olivier", manifest.get( new Attributes.Name( "first-name" ) ) );

        assertEquals( System.getProperty( "java.version" ), manifest.get( new Attributes.Name( "Build-Jdk" ) ) );
        assertEquals( System.getProperty( "user.name" ), manifest.get( new Attributes.Name( "Built-By" ) ) );

        assertTrue( StringUtils.isEmpty( manifest.getValue( new Attributes.Name( "keyWithEmptyValue" ) ) ) );
        assertTrue( manifest.containsKey( new Attributes.Name( "keyWithEmptyValue" ) ) );

        manifest = jarFileManifest.getAttributes( "UserSection" );

        assertEquals( "value", manifest.get( new Attributes.Name( "key" ) ) );
    }

    public void testCreatedByManifestEntryWithoutMavenVersion()
        throws Exception
    {
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenSession session = getDummySessionWithoutMavenVersion();
        MavenProject project = getDummyProject();

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );

        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );

        final Manifest manifest = getJarFileManifest( jarFile );
        Map entries = manifest.getMainAttributes();

        assertEquals( "Apache Maven", entries.get( new Attributes.Name( "Created-By" ) ) );
    }

    /*
     * Test to make sure that manifest sections are present in the manifest prior to the archive has been created.
     */
    public void testManifestSections()
        throws Exception
    {
        MavenArchiver archiver = new MavenArchiver();

        MavenSession session = getDummySession();

        MavenProject project = getDummyProject();
        MavenArchiveConfiguration config = new MavenArchiveConfiguration();

        ManifestSection manifestSection = new ManifestSection();
        manifestSection.setName( "SectionOne" );
        manifestSection.addManifestEntry( "key", "value" );
        List<ManifestSection> manifestSections = new ArrayList<ManifestSection>();
        manifestSections.add( manifestSection );
        config.setManifestSections( manifestSections );

        Manifest manifest = archiver.getManifest( session, project, config );

        Attributes section = manifest.getAttributes( "SectionOne" );
        assertNotNull( "The section is not present in the manifest as it should be.", section );

        String attribute = section.getValue( "key" );
        assertNotNull( "The attribute we are looking for is not present in the section.", attribute );
        assertEquals( "The value of the attribute is wrong.", "value", attribute );
    }

    @SuppressWarnings( "ResultOfMethodCallIgnored" )
    public void testDefaultClassPathValue()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
        final Manifest manifest = getJarFileManifest( jarFile );
        String classPath = manifest.getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        String[] classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "dummy1-1.0.jar", classPathEntries[0] );
        assertEquals( "dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "dummy3-2.0.jar", classPathEntries[2] );
    }

    @SuppressWarnings( "ResultOfMethodCallIgnored" )
    private void deleteAndAssertNotPresent( File jarFile )
    {
        jarFile.delete();
        assertFalse( jarFile.exists() );
    }

    @SuppressWarnings( "ResultOfMethodCallIgnored" )
    public void testDefaultClassPathValue_WithSnapshot()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProjectWithSnapshot();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );

        final Manifest manifest = getJarFileManifest( jarFile );
        String classPath = manifest.getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        String[] classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "dummy1-1.1-20081022.112233-1.jar", classPathEntries[0] );
        assertEquals( "dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "dummy3-2.0.jar", classPathEntries[2] );
    }

    public void testMavenRepoClassPathValue()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_REPOSITORY );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
        Manifest manifest = archiver.getManifest( session, project, config );
        String[] classPathEntries =
            StringUtils.split( new String( manifest.getMainAttributes().getValue( "Class-Path" ).getBytes() ), " " );
        assertEquals( "org/apache/dummy/dummy1/1.0/dummy1-1.0.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/dummy3-2.0.jar", classPathEntries[2] );

        String classPath = getJarFileManifest( jarFile ).getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "org/apache/dummy/dummy1/1.0/dummy1-1.0.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/dummy3-2.0.jar", classPathEntries[2] );
    }

    public void testMavenRepoClassPathValue_WithSnapshot()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProjectWithSnapshot();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_REPOSITORY );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );

        Manifest manifest = archiver.getManifest( session, project, config );
        String[] classPathEntries =
            StringUtils.split( new String( manifest.getMainAttributes().getValue( "Class-Path" ).getBytes() ), " " );
        assertEquals( "org/apache/dummy/dummy1/1.1-SNAPSHOT/dummy1-1.1-20081022.112233-1.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/dummy3-2.0.jar", classPathEntries[2] );

        String classPath = getJarFileManifest( jarFile ).getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "org/apache/dummy/dummy1/1.1-SNAPSHOT/dummy1-1.1-20081022.112233-1.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/dummy3-2.0.jar", classPathEntries[2] );
    }

    public void testCustomClassPathValue()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProject();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_CUSTOM );
        config.getManifest().setCustomClasspathLayout(
            "${artifact.groupIdPath}/${artifact.artifactId}/${artifact.version}/TEST-${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}" );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
        Manifest manifest = archiver.getManifest( session, project, config );
        String[] classPathEntries =
            StringUtils.split( new String( manifest.getMainAttributes().getValue( "Class-Path" ).getBytes() ), " " );
        assertEquals( "org/apache/dummy/dummy1/1.0/TEST-dummy1-1.0.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );

        final Manifest manifest1 = getJarFileManifest( jarFile );
        String classPath = manifest1.getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "org/apache/dummy/dummy1/1.0/TEST-dummy1-1.0.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );
    }

    public void testCustomClassPathValue_WithSnapshotResolvedVersion()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProjectWithSnapshot();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );
        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_CUSTOM );
        config.getManifest().setCustomClasspathLayout(
            "${artifact.groupIdPath}/${artifact.artifactId}/${artifact.baseVersion}/TEST-${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}" );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );

        Manifest manifest = archiver.getManifest( session, project, config );
        String[] classPathEntries =
            StringUtils.split( new String( manifest.getMainAttributes().getValue( "Class-Path" ).getBytes() ), " " );
        assertEquals( "org/apache/dummy/dummy1/1.1-SNAPSHOT/TEST-dummy1-1.1-20081022.112233-1.jar",
                      classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );

        String classPath = getJarFileManifest( jarFile ).getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "org/apache/dummy/dummy1/1.1-SNAPSHOT/TEST-dummy1-1.1-20081022.112233-1.jar",
                      classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );
    }

    public void testCustomClassPathValue_WithSnapshotForcingBaseVersion()
        throws Exception
    {
        MavenSession session = getDummySession();
        MavenProject project = getDummyProjectWithSnapshot();
        File jarFile = new File( "target/test/dummy.jar" );
        JarArchiver jarArchiver = getCleanJarArciver( jarFile );

        MavenArchiver archiver = getMavenArchiver( jarArchiver );

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( true );
        config.getManifest().setAddDefaultImplementationEntries( true );
        config.getManifest().setAddDefaultSpecificationEntries( true );
        config.getManifest().setMainClass( "org.apache.maven.Foo" );
        config.getManifest().setAddClasspath( true );
        config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_CUSTOM );
        config.getManifest().setCustomClasspathLayout(
            "${artifact.groupIdPath}/${artifact.artifactId}/${artifact.baseVersion}/TEST-${artifact.artifactId}-${artifact.baseVersion}${dashClassifier?}.${artifact.extension}" );
        archiver.createArchive( session, project, config );
        assertTrue( jarFile.exists() );
        Manifest manifest = archiver.getManifest( session, project, config );
        String[] classPathEntries =
            StringUtils.split( new String( manifest.getMainAttributes().getValue( "Class-Path" ).getBytes() ), " " );
        assertEquals( "org/apache/dummy/dummy1/1.1-SNAPSHOT/TEST-dummy1-1.1-SNAPSHOT.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );

        String classPath = getJarFileManifest( jarFile ).getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
        assertNotNull( classPath );
        classPathEntries = StringUtils.split( classPath, " " );
        assertEquals( "org/apache/dummy/dummy1/1.1-SNAPSHOT/TEST-dummy1-1.1-SNAPSHOT.jar", classPathEntries[0] );
        assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
        assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );
    }

    private JarArchiver getCleanJarArciver( File jarFile )
    {
        deleteAndAssertNotPresent( jarFile );
        JarArchiver jarArchiver = new JarArchiver();
        jarArchiver.setDestFile( jarFile );
        return jarArchiver;
    }

    // ----------------------------------------
    // common methods for testing
    // ----------------------------------------

    private MavenProject getDummyProject()
    {
        MavenProject project = getMavenProject();
        File pomFile = new File( "src/test/resources/pom.xml" );
        pomFile.setLastModified( System.currentTimeMillis() - 60000L );
        project.setFile( pomFile );
        Build build = new Build();
        build.setDirectory( "target" );
        build.setOutputDirectory( "target" );
        project.setBuild( build );
        project.setName( "archiver test" );
        Organization organization = new Organization();
        organization.setName( "Apache" );
        project.setOrganization( organization );
        MockArtifact artifact = new MockArtifact();
        artifact.setGroupId( "org.apache.dummy" );
        artifact.setArtifactId( "dummy" );
        artifact.setVersion( "0.1" );
        artifact.setType( "jar" );
        artifact.setArtifactHandler( new DefaultArtifactHandler( "jar" ) );
        project.setArtifact( artifact );

        Set<Artifact> artifacts = getArtifacts( getMockArtifact1Release(), getMockArtifact2(), getMockArtifact3() );
        project.setArtifacts( artifacts );

        return project;
    }

    private MavenProject getMavenProject()
    {
        Model model = new Model();
        model.setGroupId( "org.apache.dummy" );
        model.setArtifactId( "dummy" );
        model.setVersion( "0.1" );

        final MavenProject project = new MavenProject( model );
        project.setPluginArtifacts( Collections.EMPTY_SET );
        project.setReportArtifacts( Collections.EMPTY_SET );
        project.setExtensionArtifacts( Collections.EMPTY_SET );
        project.setRemoteArtifactRepositories( Collections.EMPTY_LIST );
        project.setPluginArtifactRepositories( Collections.EMPTY_LIST );
        return project;
    }


    private MockArtifact getMockArtifact3()
    {
        MockArtifact artifact3 = new MockArtifact();
        artifact3.setGroupId( "org.apache.dummy.bar" );
        artifact3.setArtifactId( "dummy3" );
        artifact3.setVersion( "2.0" );
        artifact3.setScope( "runtime" );
        artifact3.setType( "jar" );
        artifact3.setFile( getClasspathFile( artifact3.getArtifactId() + "-" + artifact3.getVersion() + ".jar" ) );
        return artifact3;
    }

    private MavenProject getDummyProjectWithSnapshot()
    {
        MavenProject project = getMavenProject();
        File pomFile = new File( "src/test/resources/pom.xml" );
        pomFile.setLastModified( System.currentTimeMillis() - 60000L );
        project.setFile( pomFile );
        Build build = new Build();
        build.setDirectory( "target" );
        build.setOutputDirectory( "target" );
        project.setBuild( build );
        project.setName( "archiver test" );
        Organization organization = new Organization();
        organization.setName( "Apache" );
        project.setOrganization( organization );

        MockArtifact artifact = new MockArtifact();
        artifact.setGroupId( "org.apache.dummy" );
        artifact.setArtifactId( "dummy" );
        artifact.setVersion( "0.1" );
        artifact.setType( "jar" );
        artifact.setArtifactHandler( new DefaultArtifactHandler( "jar" ) );
        project.setArtifact( artifact );

        Set<Artifact> artifacts = getArtifacts( getMockArtifact1(), getMockArtifact2(), getMockArtifact3() );

        project.setArtifacts( artifacts );

        return project;
    }

    private ArtifactHandler getMockArtifactHandler()
    {
        return new ArtifactHandler()
        {

            public String getClassifier()
            {
                return null;
            }

            public String getDirectory()
            {
                return null;
            }

            public String getExtension()
            {
                return "jar";
            }

            public String getLanguage()
            {
                return null;
            }

            public String getPackaging()
            {
                return null;
            }

            public boolean isAddedToClasspath()
            {
                return true;
            }

            public boolean isIncludesDependencies()
            {
                return false;
            }

        };
    }

    private MockArtifact getMockArtifact2()
    {
        MockArtifact artifact2 = new MockArtifact();
        artifact2.setGroupId( "org.apache.dummy.foo" );
        artifact2.setArtifactId( "dummy2" );
        artifact2.setVersion( "1.5" );
        artifact2.setType( "jar" );
        artifact2.setScope( "runtime" );
        artifact2.setFile( getClasspathFile( artifact2.getArtifactId() + "-" + artifact2.getVersion() + ".jar" ) );
        return artifact2;
    }

    private MockArtifact getArtifactWithDot()
    {
        MockArtifact artifact2 = new MockArtifact();
        artifact2.setGroupId( "org.apache.dummy.foo" );
        artifact2.setArtifactId( "dummy.dot" );
        artifact2.setVersion( "1.5" );
        artifact2.setType( "jar" );
        artifact2.setScope( "runtime" );
        artifact2.setFile( getClasspathFile( artifact2.getArtifactId() + "-" + artifact2.getVersion() + ".jar" ) );
        return artifact2;
    }

    private MockArtifact getMockArtifact1()
    {
        MockArtifact artifact1 = new MockArtifact();
        artifact1.setGroupId( "org.apache.dummy" );
        artifact1.setArtifactId( "dummy1" );
        artifact1.setSnapshotVersion( "1.1-20081022.112233-1", "1.1-SNAPSHOT" );
        artifact1.setType( "jar" );
        artifact1.setScope( "runtime" );
        artifact1.setFile( getClasspathFile( artifact1.getArtifactId() + "-" + artifact1.getVersion() + ".jar" ) );
        return artifact1;
    }

    private MockArtifact getMockArtifact1Release()
    {
        MockArtifact artifact1 = new MockArtifact();
        artifact1.setGroupId( "org.apache.dummy" );
        artifact1.setArtifactId( "dummy1" );
        artifact1.setVersion( "1.0" );
        artifact1.setType( "jar" );
        artifact1.setScope( "runtime" );
        artifact1.setFile( getClasspathFile( artifact1.getArtifactId() + "-" + artifact1.getVersion() + ".jar" ) );
        return artifact1;
    }

    private File getClasspathFile( String file )
    {
        URL resource = Thread.currentThread().getContextClassLoader().getResource( file );
        if ( resource == null )
        {
            fail( "Cannot retrieve java.net.URL for file: " + file + " on the current test classpath." );
        }

        URI uri = new File( resource.getPath() ).toURI().normalize();

        return new File( uri.getPath().replaceAll( "%20", " " ) );
    }

    private MavenSession getDummySession()
    {
        Properties executionProperties = new Properties();
        executionProperties.put( "maven.version", "3.0.4" );

        return getDummySession( executionProperties );
    }

    private MavenSession getDummySessionWithoutMavenVersion()
    {
        return getDummySession( new Properties() );
    }

    private MavenSession getDummySession( Properties executionProperties )
    {
        PlexusContainer container = null;
        Settings settings = null;
        ArtifactRepository localRepo = null;
        EventDispatcher eventDispatcher = null;
        ReactorManager reactorManager = null;
        List goals = null;
        String executionRootDir = null;
        Date startTime = new Date();

        return new MavenSession( container, settings, localRepo, eventDispatcher, reactorManager, goals,
                                 executionRootDir, executionProperties, startTime );
    }

    private Set<Artifact> getArtifacts( Artifact... artifacts )
    {
        final ArtifactHandler mockArtifactHandler = getMockArtifactHandler();
        Set<Artifact> result = new TreeSet<Artifact>( new ArtifactComparator() );
        for ( Artifact artifact : artifacts )
        {
            artifact.setArtifactHandler( mockArtifactHandler );
            result.add( artifact );
        }
        return result;
    }

    public Manifest getJarFileManifest( File jarFile )
        throws IOException
    {
        JarFile jar = null;
        try
        {
            jar = new JarFile( jarFile );
            return jar.getManifest();
        }
        finally
        {
            if ( jar != null )
            {
                jar.close();
            }
        }

    }
}
