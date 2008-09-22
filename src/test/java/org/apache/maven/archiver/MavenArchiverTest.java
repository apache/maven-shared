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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

public class MavenArchiverTest
    extends TestCase
{
    static class ArtifactComparator
        implements Comparator
    {
        public int compare( Object o1, Object o2 )
        {
            return ( (Artifact) o1 ).getArtifactId().compareTo( ( (Artifact) o2 ).getArtifactId() );
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

        Model model = new Model();
        model.setArtifactId( "dummy" );

        MavenProject project = new MavenProject( model );
        // we need to sort the artifacts for test purposes
        Set artifacts = new TreeSet( new ArtifactComparator() );
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

        manifest = archiver.getManifest( project, config );

        assertNotNull( manifest.getMainSection() );

        java.util.Enumeration enume = manifest.getSectionNames();
        while ( enume.hasMoreElements() )
        {
            Manifest.Section section = manifest.getSection( enume.nextElement().toString() );
            System.out.println( section + " " + section.getAttributeValue( "Extension-List" ) );
        }

        assertEquals( null, manifest.getMainSection().getAttributeValue( "Extension-List" ) );

        MockArtifact artifact1 = new MockArtifact();
        artifact1.setGroupId( "org.apache.dummy" );
        artifact1.setArtifactId( "dummy1" );
        artifact1.setVersion( "1.0" );
        artifact1.setType( "dll" );
        artifact1.setScope( "compile" );

        artifacts.add( artifact1 );

        manifest = archiver.getManifest( project, config );

        assertEquals( null, manifest.getMainSection().getAttributeValue( "Extension-List" ) );

        MockArtifact artifact2 = new MockArtifact();
        artifact2.setGroupId( "org.apache.dummy" );
        artifact2.setArtifactId( "dummy2" );
        artifact2.setVersion( "1.0" );
        artifact2.setType( "jar" );
        artifact2.setScope( "compile" );

        artifacts.add( artifact2 );

        manifest = archiver.getManifest( project, config );

        assertEquals( "dummy2", manifest.getMainSection().getAttributeValue( "Extension-List" ) );

        MockArtifact artifact3 = new MockArtifact();
        artifact3.setGroupId( "org.apache.dummy" );
        artifact3.setArtifactId( "dummy3" );
        artifact3.setVersion( "1.0" );
        artifact3.setScope( "test" );
        artifact3.setType( "jar" );

        artifacts.add( artifact3 );

        manifest = archiver.getManifest( project, config );

        assertEquals( "dummy2", manifest.getMainSection().getAttributeValue( "Extension-List" ) );

        MockArtifact artifact4 = new MockArtifact();
        artifact4.setGroupId( "org.apache.dummy" );
        artifact4.setArtifactId( "dummy4" );
        artifact4.setVersion( "1.0" );
        artifact4.setType( "jar" );
        artifact4.setScope( "compile" );

        artifacts.add( artifact4 );

        manifest = archiver.getManifest( project, config );

        assertEquals( "dummy2 dummy4", manifest.getMainSection().getAttributeValue( "Extension-List" ) );
    }

    public void testMultiClassPath()
        throws Exception
    {
        final File tempFile = File.createTempFile( "maven-archiver-test-", ".jar" );

        try
        {
            MavenArchiver archiver = new MavenArchiver();

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

            Manifest manifest = archiver.getManifest( project, archiveConfiguration );
            String classPath = manifest.getMainSection().getAttribute( "Class-Path" ).getValue();
            assertTrue( "User specified Class-Path entry was not added to manifest", classPath.indexOf( "help/" ) != -1 );
            assertTrue( "Class-Path generated by addClasspath was not added to manifest",
                        classPath.indexOf( tempFile.getName() ) != -1 );
        }
        finally
        {
            tempFile.delete();
        }

    }

    public void testRecreation()
        throws Exception
    {
        File jarFile = new File( "target/test/dummy.jar" );
        jarFile.delete();
        assertFalse( jarFile.exists() );
        JarArchiver jarArchiver = new JarArchiver();
        jarArchiver.setDestFile( jarFile );

        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver( jarArchiver );
        archiver.setOutputFile( jarArchiver.getDestFile() );

        MavenProject project = getDummyProject();

        MavenArchiveConfiguration config = new MavenArchiveConfiguration();
        config.setForced( false );

        FileUtils.deleteDirectory( "target/maven-archiver" );
        archiver.createArchive( project, config );
        assertTrue( jarFile.exists() );
        jarFile.setLastModified( System.currentTimeMillis() - 60000L );
        long time = jarFile.lastModified();

        List files = FileUtils.getFiles( new File( "target/maven-archiver" ), "**/**", null, true );
        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            File f = (File) i.next();
            f.setLastModified( time );
        }

        archiver.createArchive( project, config );
        assertEquals( jarFile.lastModified(), time );

        config.setForced( true );
        archiver.createArchive( project, config );
        assertTrue( jarFile.lastModified() > time );
    }

    public void testNotGenerateImplementationVersionForMANIFESTMF()
        throws Exception
    {
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenProject project = getDummyProject();

            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( false );
            archiver.createArchive( project, config );
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
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenProject project = getDummyProject();
            String ls = System.getProperty( "line.separator" );
            project.setDescription( "foo " + ls + " bar " );
            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.addManifestEntry( "Description", project.getDescription() );
            archiver.createArchive( project, config );
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

    public void testCarriageReturnInManifestEntry()
        throws Exception
    {
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenProject project = getDummyProject();
            String ls = System.getProperty( "line.separator" );
            project.setDescription( "foo " + ls + " bar " );
            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.addManifestEntry( "Description", project.getDescription() );
            // config.addManifestEntry( "EntryWithTab", " foo tab " + ( '\u0009' ) + ( '\u0009' ) + " bar tab" + (
            // '\u0009' ) );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );

            jar = new JarFile( jarFile );

            Attributes attributes = jar.getManifest().getMainAttributes();
            assertTrue( project.getDescription().indexOf( ls ) > 0 );
            Attributes.Name description = new Attributes.Name( "Description" );
            String value = attributes.getValue( description );
            assertNotNull( value );
            assertFalse( value.indexOf( ls ) > 0 );
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

    public void testManifestEntries()
        throws Exception
    {
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenProject project = getDummyProject();
            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.getManifest().setAddDefaultSpecificationEntries( true );

            Map manifestEntries = new HashMap();
            manifestEntries.put( "foo", "bar" );
            manifestEntries.put( "first-name", "olivier" );
            manifestEntries.put( "keyWithEmptyValue", null );
            config.setManifestEntries( manifestEntries );

            ManifestSection manifestSection = new ManifestSection();
            manifestSection.setName( "UserSection" );
            manifestSection.addManifestEntry( "key", "value" );
            List manifestSections = new ArrayList();
            manifestSections.add( manifestSection );
            config.setManifestSections( manifestSections );
            config.getManifest().setMainClass( "org.apache.maven.Foo" );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );
            jar = new JarFile( jarFile );

            Attributes manifest = jar.getManifest().getMainAttributes();

            assertEquals( "Apache Maven", manifest.get( new Attributes.Name( "Created-By" ) ) );
            assertEquals( "archiver test", manifest.get( Attributes.Name.SPECIFICATION_TITLE ) );// "Specification-Title"
                                                                                                 // ) );
            assertEquals( "0.1", manifest.get( Attributes.Name.SPECIFICATION_VERSION ) );// "Specification-Version" ) );
            assertEquals( "Apache", manifest.get( Attributes.Name.SPECIFICATION_VENDOR ) );// "Specification-Vendor" )
                                                                                           // );

            assertEquals( "archiver test", manifest.get( Attributes.Name.IMPLEMENTATION_TITLE ) );// "Implementation-Title"
                                                                                                  // ) );
            assertEquals( "0.1", manifest.get( Attributes.Name.IMPLEMENTATION_VERSION ) );// "Implementation-Version" )
                                                                                          // );
            assertEquals( "org.apache.dummy", manifest.get( Attributes.Name.IMPLEMENTATION_VENDOR_ID ) );// "Implementation-Vendor-Id"
                                                                                                         // ) );
            assertEquals( "Apache", manifest.get( Attributes.Name.IMPLEMENTATION_VENDOR ) );// "Implementation-Vendor" )
                                                                                            // );
            assertEquals( "org.apache.maven.Foo", manifest.get( Attributes.Name.MAIN_CLASS ) );// "Main-Class" ) );

            assertEquals( "bar", manifest.get( new Attributes.Name( "foo" ) ) );
            assertEquals( "olivier", manifest.get( new Attributes.Name( "first-name" ) ) );

            assertEquals( System.getProperty( "java.version" ), manifest.get( new Attributes.Name( "Build-Jdk" ) ) );
            assertEquals( System.getProperty( "user.name" ), manifest.get( new Attributes.Name( "Built-By" ) ) );

            assertTrue( StringUtils.isEmpty( manifest.getValue( new Attributes.Name( "keyWithEmptyValue" ) ) ) );
            assertTrue( manifest.containsKey( new Attributes.Name( "keyWithEmptyValue" ) ) );

            manifest = jar.getManifest().getAttributes( "UserSection" );

            assertEquals( "value", manifest.get( new Attributes.Name( "key" ) ) );

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

    /**
     * Test to make sure that manifest sections are present in the manifest prior to the archive has been created.
     */
    public void testManifestSections()
        throws Exception
    {
        MavenArchiver archiver = new MavenArchiver();

        MavenProject project = getDummyProject();
        MavenArchiveConfiguration config = new MavenArchiveConfiguration();

        ManifestSection manifestSection = new ManifestSection();
        manifestSection.setName( "SectionOne" );
        manifestSection.addManifestEntry( "key", "value" );
        List manifestSections = new ArrayList();
        manifestSections.add( manifestSection );
        config.setManifestSections( manifestSections );

        Manifest manifest = archiver.getManifest( project, config );

        Manifest.Section section = manifest.getSection( "SectionOne" );
        assertNotNull( "The section is not present in the manifest as it should be.", section );

        Manifest.Attribute attribute = section.getAttribute( "key" );
        assertNotNull( "The attribute we are looking for is not present in the section.", attribute );
        assertEquals( "The value of the attribute is wrong.", "value", attribute.getValue() );
    }

    public void testDefaultClassPathValue()
        throws Exception
    {
        MavenProject project = getDummyProject();
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.getManifest().setAddDefaultSpecificationEntries( true );
            config.getManifest().setMainClass( "org.apache.maven.Foo" );
            config.getManifest().setAddClasspath( true );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );
            jar = new JarFile( jarFile );

            String classPath = jar.getManifest().getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
            assertNotNull( classPath );
            String[] classPathEntries = StringUtils.split( classPath, " " );
            assertEquals( "dummy1-1.0.jar", classPathEntries[0] );
            assertEquals( "dummy2-1.5.jar", classPathEntries[1] );
            assertEquals( "dummy3-2.0.jar", classPathEntries[2] );
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

    public void testMavenRepoClassPathValue()
        throws Exception
    {
        MavenProject project = getDummyProject();
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.getManifest().setAddDefaultSpecificationEntries( true );
            config.getManifest().setMainClass( "org.apache.maven.Foo" );
            config.getManifest().setAddClasspath( true );
            config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_REPOSITORY );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );
            jar = new JarFile( jarFile );

            Manifest manifest = archiver.getManifest( project, config );
            String[] classPathEntries =
                StringUtils.split(
                                   new String( manifest.getMainSection().getAttributeValue( "Class-Path" ).getBytes() ),
                                   " " );
            assertEquals( "org/apache/dummy/dummy1/1.0/dummy1-1.0.jar", classPathEntries[0] );
            assertEquals( "org/apache/dummy/foo/dummy2/1.5/dummy2-1.5.jar", classPathEntries[1] );
            assertEquals( "org/apache/dummy/bar/dummy3/2.0/dummy3-2.0.jar", classPathEntries[2] );

            String classPath = jar.getManifest().getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
            assertNotNull( classPath );
            classPathEntries = StringUtils.split( classPath, " " );
            assertEquals( "org/apache/dummy/dummy1/1.0/dummy1-1.0.jar", classPathEntries[0] );
            assertEquals( "org/apache/dummy/foo/dummy2/1.5/dummy2-1.5.jar", classPathEntries[1] );
            assertEquals( "org/apache/dummy/bar/dummy3/2.0/dummy3-2.0.jar", classPathEntries[2] );

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

    public void testCustomClassPathValue()
        throws Exception
    {
        MavenProject project = getDummyProject();
        JarFile jar = null;
        try
        {
            File jarFile = new File( "target/test/dummy.jar" );
            jarFile.delete();
            assertFalse( jarFile.exists() );
            JarArchiver jarArchiver = new JarArchiver();
            jarArchiver.setDestFile( jarFile );

            MavenArchiver archiver = new MavenArchiver();
            archiver.setArchiver( jarArchiver );
            archiver.setOutputFile( jarArchiver.getDestFile() );

            MavenArchiveConfiguration config = new MavenArchiveConfiguration();
            config.setForced( true );
            config.getManifest().setAddDefaultImplementationEntries( true );
            config.getManifest().setAddDefaultSpecificationEntries( true );
            config.getManifest().setMainClass( "org.apache.maven.Foo" );
            config.getManifest().setAddClasspath( true );
            config.getManifest().setClasspathLayoutType( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_CUSTOM );
            config.getManifest().setCustomClasspathLayout( "${artifact.groupIdPath}/${artifact.artifactId}/${artifact.version}/TEST-${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}" );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );
            jar = new JarFile( jarFile );

            Manifest manifest = archiver.getManifest( project, config );
            String[] classPathEntries =
                StringUtils.split(
                                   new String( manifest.getMainSection().getAttributeValue( "Class-Path" ).getBytes() ),
                                   " " );
            assertEquals( "org/apache/dummy/dummy1/1.0/TEST-dummy1-1.0.jar", classPathEntries[0] );
            assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
            assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );

            String classPath = jar.getManifest().getMainAttributes().getValue( Attributes.Name.CLASS_PATH );
            assertNotNull( classPath );
            classPathEntries = StringUtils.split( classPath, " " );
            assertEquals( "org/apache/dummy/dummy1/1.0/TEST-dummy1-1.0.jar", classPathEntries[0] );
            assertEquals( "org/apache/dummy/foo/dummy2/1.5/TEST-dummy2-1.5.jar", classPathEntries[1] );
            assertEquals( "org/apache/dummy/bar/dummy3/2.0/TEST-dummy3-2.0.jar", classPathEntries[2] );

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

    // ----------------------------------------
    // common methods for testing
    // ----------------------------------------

    private MavenProject getDummyProject()
    {
        Model model = new Model();
        model.setGroupId( "org.apache.dummy" );
        model.setArtifactId( "dummy" );
        model.setVersion( "0.1" );
        MavenProject project = new MavenProject( model );

        project.setPluginArtifacts( Collections.EMPTY_SET );
        project.setReportArtifacts( Collections.EMPTY_SET );
        project.setExtensionArtifacts( Collections.EMPTY_SET );
        project.setRemoteArtifactRepositories( Collections.EMPTY_LIST );
        project.setPluginArtifactRepositories( Collections.EMPTY_LIST );

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
        project.setArtifact( artifact );

        ArtifactHandler artifactHandler = new ArtifactHandler()
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

        Set artifacts = new TreeSet( new ArtifactComparator() );

        MockArtifact artifact1 = new MockArtifact();
        artifact1.setGroupId( "org.apache.dummy" );
        artifact1.setArtifactId( "dummy1" );
        artifact1.setVersion( "1.0" );
        artifact1.setType( "jar" );
        artifact1.setScope( "runtime" );
        artifact1.setFile( getClasspathFile( artifact1.getArtifactId() + "-" + artifact1.getVersion()
            + ".jar" ) );

        artifact1.setArtifactHandler( artifactHandler );

        artifacts.add( artifact1 );

        MockArtifact artifact2 = new MockArtifact();
        artifact2.setGroupId( "org.apache.dummy.foo" );
        artifact2.setArtifactId( "dummy2" );
        artifact2.setVersion( "1.5" );
        artifact2.setType( "jar" );
        artifact2.setScope( "runtime" );
        artifact2.setFile( getClasspathFile( artifact2.getArtifactId() + "-" + artifact2.getVersion()
            + ".jar" ) );

        artifact2.setArtifactHandler( artifactHandler );
        artifacts.add( artifact2 );

        MockArtifact artifact3 = new MockArtifact();
        artifact3.setGroupId( "org.apache.dummy.bar" );
        artifact3.setArtifactId( "dummy3" );
        artifact3.setVersion( "2.0" );
        artifact3.setScope( "runtime" );
        artifact3.setType( "jar" );
        artifact3.setFile( getClasspathFile( artifact3.getArtifactId() + "-" + artifact3.getVersion()
            + ".jar" ) );
        artifact3.setArtifactHandler( artifactHandler );
        artifacts.add( artifact3 );

        project.setArtifacts( artifacts );

        return project;
    }

    private File getClasspathFile( String file )
    {
        URL resource = Thread.currentThread().getContextClassLoader().getResource( file );
        if ( resource == null )
        {
            fail( "Cannot retrieve java.net.URL for file: " + file + " on the current test classpath." );
        }
        
        URI uri = new File( resource.getPath() ).toURI().normalize();
        File result = new File( uri.getPath().replaceAll( "%20", " " ) );
        
        return result;
    }
}
