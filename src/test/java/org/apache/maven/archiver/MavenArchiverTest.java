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
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

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
            assertTrue( "Class-Path generated by addClasspath was not added to manifest", classPath.indexOf( tempFile
                .getName() ) != -1 );
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
        InputStream inputStream = null;
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

            ZipEntry zipEntry = jar.getEntry( "META-INF/MANIFEST.MF" );
            Properties manifest = new Properties();
            inputStream = jar.getInputStream( zipEntry );
            manifest.load( inputStream );

            assertFalse( manifest.containsKey( "Implementation-Version" ) );
        }
        finally
        {
            // cleanup streams
            IOUtil.close( inputStream );
            if ( jar != null )
            {
                jar.close();
            }
        }
    }
    
    public void testGenerateImplementationVersionForMANIFESTMF()
        throws Exception
    {
        InputStream inputStream = null;
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

            ZipEntry zipEntry = jar.getEntry( "META-INF/MANIFEST.MF" );
            Properties manifest = new Properties();
            inputStream = jar.getInputStream( zipEntry );
            manifest.load( inputStream );

            assertTrue( manifest.containsKey( "Implementation-Version" ) );
            assertEquals( "0.1", manifest.get( "Implementation-Version" ) );
        }
        finally
        {
            // cleanup streams
            IOUtil.close( inputStream );
            if ( jar != null )
            {
                jar.close();
            }
        }
    }    
    
    public void testCarriageReturnInManifestEntry()
        throws Exception
    {
        InputStream inputStream = null;
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
            //config.addManifestEntry( "EntryWithTab", " foo tab " + ( '\u0009' ) + ( '\u0009' ) + " bar tab" + ( '\u0009' ) );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );

            jar = new JarFile( jarFile );

            ZipEntry zipEntry = jar.getEntry( "META-INF/MANIFEST.MF" );
            Properties manifest = new Properties();
            inputStream = jar.getInputStream( zipEntry );
            manifest.load( inputStream );

            assertTrue( project.getDescription().indexOf( ls ) > 0 );
            assertFalse( manifest.getProperty( "Description" ).indexOf( ls ) > 0 );
            //System.out.println("tabEnt |" + manifest.getProperty( "EntryWithTab" ) + "|" );
            //assertFalse( manifest.getProperty( "EntryWithTab" ).indexOf( ( '\u0009' ) ) > 0 );
        }
        finally
        {
            // cleanup streams
            IOUtil.close( inputStream );
            if ( jar != null )
            {
                jar.close();
            }
        }
    }     
    
    public void testManifestEntries()
        throws Exception
    {
        InputStream inputStream = null;
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
            config.getManifest().setMainClass( "org.apache.maven.Foo" );
            archiver.createArchive( project, config );
            assertTrue( jarFile.exists() );
            jar = new JarFile( jarFile );

            ZipEntry zipEntry = jar.getEntry( "META-INF/MANIFEST.MF" );
            Properties manifest = new Properties();
            inputStream = jar.getInputStream( zipEntry );
            manifest.load( inputStream );
            assertEquals( "Apache Maven", manifest.get( "Created-By" ) );
            assertEquals( "archiver test", manifest.get( "Specification-Title" ) );
            assertEquals( "0.1", manifest.get( "Specification-Version" ) );
            assertEquals( "Apache", manifest.get( "Specification-Vendor" ) );

            assertEquals( "archiver test", manifest.get( "Implementation-Title" ) );
            assertEquals( "0.1", manifest.get( "Implementation-Version" ) );
            assertEquals( "org.apache.dummy", manifest.get( "Implementation-Vendor-Id" ) );
            assertEquals( "Apache", manifest.get( "Implementation-Vendor" ) );
            assertEquals( "org.apache.maven.Foo", manifest.get( "Main-Class" ) );
            
            assertEquals(System.getProperty( "java.version"), manifest.getProperty( "Build-Jdk" ) );
            assertEquals(System.getProperty( "user.name"), manifest.getProperty( "Built-By" ) );
        }
        finally
        {
            // cleanup streams
            IOUtil.close( inputStream );
            if ( jar != null )
            {
                jar.close();
            }
        }
    }
    
    // ----------------------------------------
    //  common methods for testing
    // ----------------------------------------
    
    private MavenProject getDummyProject()
    {
        Model model = new Model();
        model.setGroupId( "org.apache.dummy" );
        model.setArtifactId( "dummy" );
        model.setVersion( "0.1" );
        MavenProject project = new MavenProject( model );

        project.setArtifacts( Collections.EMPTY_SET );
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
        return project;
    }
}
