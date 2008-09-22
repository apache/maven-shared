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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PrefixedPropertiesValueSource;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Revision$ $Date$
 */
public class MavenArchiver
{
    
    public static final String SIMPLE_LAYOUT = "${artifact.artifactId}-${artifact.version}${dashClassifier?}.${artifact.extension}";

    public static final String REPOSITORY_LAYOUT = "${artifact.groupIdPath}/${artifact.artifactId}/" +
    		"${artifact.baseVersion}/${artifact.artifactId}-" +
    		"${artifact.version}${dashClassifier?}.${artifact.extension}";
    
    private static final List ARTIFACT_EXPRESSION_PREFIXES;
    
    static
    {
        List artifactExpressionPrefixes = new ArrayList();
        artifactExpressionPrefixes.add( "artifact." );
        
        ARTIFACT_EXPRESSION_PREFIXES = artifactExpressionPrefixes;
    }

    private JarArchiver archiver;

    private File archiveFile;

    /**
     * Return a pre-configured manifest
     *
     * @todo Add user attributes list and user groups list
     */
    public Manifest getManifest( MavenProject project, MavenArchiveConfiguration config )
        throws ManifestException, DependencyResolutionRequiredException
    {
        boolean hasManifestEntries = !config.isManifestEntriesEmpty();
        Map entries = hasManifestEntries ? config.getManifestEntries() : Collections.EMPTY_MAP;
        Manifest manifest = getManifest( project, config.getManifest(), entries );

        // any custom manifest entries in the archive configuration manifest?
        if ( hasManifestEntries )
        {
            Set keys = entries.keySet();
            for ( Iterator iter = keys.iterator(); iter.hasNext(); )
            {
                String key = (String) iter.next();
                String value = (String) entries.get( key );
                Manifest.Attribute attr = manifest.getMainSection().getAttribute( key );
                if ( key.equals( "Class-Path" ) && attr != null )
                {
                    // Merge the user-supplied Class-Path value with the programmatically
                    // generated Class-Path.  Note that the user-supplied value goes first
                    // so that resources there will override any in the standard Class-Path.
                    attr.setValue( value + " " + attr.getValue() );
                }
                else
                {
                    addManifestAttribute( manifest, key, value );
                }
            }
        }

        // any custom manifest sections in the archive configuration manifest?
        if ( !config.isManifestSectionsEmpty() )
        {
            List sections = config.getManifestSections();
            for ( Iterator iter = sections.iterator(); iter.hasNext(); )
            {
                ManifestSection section = (ManifestSection) iter.next();
                Manifest.Section theSection = new Manifest.Section();
                theSection.setName( section.getName() );

                if ( !section.isManifestEntriesEmpty() )
                {
                    Map sectionEntries = section.getManifestEntries();
                    Set keys = sectionEntries.keySet();
                    for ( Iterator it = keys.iterator(); it.hasNext(); )
                    {
                        String key = (String) it.next();
                        String value = (String) sectionEntries.get( key );
                        Manifest.Attribute attr = new Manifest.Attribute( key, value );
                        theSection.addConfiguredAttribute( attr );
                    }
                }

                manifest.addConfiguredSection( theSection );
            }
        }

        return manifest;
    }

    /**
     * Return a pre-configured manifest
     *
     * @todo Add user attributes list and user groups list
     */
    public Manifest getManifest( MavenProject project, ManifestConfiguration config )
        throws ManifestException, DependencyResolutionRequiredException
    {
        return getManifest( project, config, Collections.EMPTY_MAP );
    }

    private void addManifestAttribute( Manifest manifest, Map map, String key, String value )
        throws ManifestException
    {
        if ( map.containsKey( key ) )
        {
            return;  // The map value will be added later
        }
        addManifestAttribute( manifest, key, value );
    }

    private void addManifestAttribute( Manifest manifest, String key, String value )
        throws ManifestException
    {
        if ( !StringUtils.isEmpty( value ) )
        {
            Manifest.Attribute attr = new Manifest.Attribute( key, value );
            manifest.addConfiguredAttribute( attr );
        }
        else
        {
            // if the value is empty we have create an entry with an empty string 
            // to prevent null print in the manifest file
            Manifest.Attribute attr = new Manifest.Attribute( key, "" );
            manifest.addConfiguredAttribute( attr );
        }
    }

    protected Manifest getManifest( MavenProject project, ManifestConfiguration config, Map entries )
        throws ManifestException, DependencyResolutionRequiredException
    {
        // TODO: Should we replace "map" with a copy? Note, that we modify it!

        // Added basic entries
        Manifest m = new Manifest();
        addManifestAttribute( m, entries, "Created-By", "Apache Maven" );

        addCustomEntries( m, entries, config );

        if ( config.isAddClasspath() )
        {
            StringBuffer classpath = new StringBuffer();
            
            List artifacts = project.getRuntimeClasspathElements();
            String classpathPrefix = config.getClasspathPrefix();
            String layoutType = config.getClasspathLayoutType();
            String layout = config.getCustomClasspathLayout();
            
            Interpolator interpolator = new StringSearchInterpolator();

            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                File f = new File( (String) iter.next() );
                if ( f.getAbsoluteFile().isFile() )
                {
                    Artifact artifact = findArtifactWithFile( project.getArtifacts(), f );
                    
                    if ( classpath.length() > 0 )
                    {
                        classpath.append( " " );
                    }
                    classpath.append( classpathPrefix );
                    
                    // NOTE: If the artifact or layout type (from config) is null, give up and use the file name by itself.
                    if ( artifact == null || layoutType == null )
                    {
                        classpath.append( f.getName() );
                    }
                    else
                    {
                        List valueSources = new ArrayList();
                        valueSources.add( new PrefixedObjectValueSource( ARTIFACT_EXPRESSION_PREFIXES, artifact, true ) );
                        valueSources.add( new PrefixedObjectValueSource( ARTIFACT_EXPRESSION_PREFIXES, artifact == null ? null : artifact.getArtifactHandler(), true ) );
                        
                        Properties extraExpressions = new Properties();
                        if ( artifact != null )
                        {
                            // FIXME: This query method SHOULD NOT affect the internal
                            // state of the artifact version, but it does.
                            if ( !artifact.isSnapshot() )
                            {
                                extraExpressions.setProperty( "baseVersion", artifact.getVersion() );
                            }
                            
                            extraExpressions.setProperty( "groupIdPath", artifact.getGroupId().replace( '.', '/' ) );
                            if ( artifact.getClassifier() != null )
                            {
                                extraExpressions.setProperty( "dashClassifier", "-" + artifact.getClassifier() );
                                extraExpressions.setProperty( "dashClassifier?", "-" + artifact.getClassifier() );
                            }
                            else
                            {
                                extraExpressions.setProperty( "dashClassifier", "" );
                                extraExpressions.setProperty( "dashClassifier?", "" );
                            }
                        }
                        valueSources.add( new PrefixedPropertiesValueSource( ARTIFACT_EXPRESSION_PREFIXES, extraExpressions, true ) );
                        
                        for ( Iterator it = valueSources.iterator(); it.hasNext(); )
                        {
                            ValueSource vs = (ValueSource) it.next();
                            interpolator.addValueSource( vs );
                        }
                        
                        RecursionInterceptor recursionInterceptor = new PrefixAwareRecursionInterceptor( ARTIFACT_EXPRESSION_PREFIXES );
                        
                        try
                        {
                            if ( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_SIMPLE.equals( layoutType ) )
                            {
                                classpath.append( interpolator.interpolate( SIMPLE_LAYOUT, recursionInterceptor ) );
                            }
                            else if ( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_REPOSITORY.equals( layoutType ) )
                            {
                                // we use layout /$groupId[0]/../${groupId[n]/$artifactId/$version/{fileName}
                                // here we must find the Artifact in the project Artifacts to generate the maven layout
                                classpath.append( interpolator.interpolate( REPOSITORY_LAYOUT, recursionInterceptor ) );
                            }
                            else if ( ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_CUSTOM.equals( layoutType ) )
                            {
                                if ( layout == null )
                                {
                                    throw new ManifestException(
                                                                 ManifestConfiguration.CLASSPATH_LAYOUT_TYPE_CUSTOM
                                                                     + " layout type was declared, but custom layout expression was not specified. Check your <archive><manifest><customLayout/> element." );
                                }
                                
                                classpath.append( interpolator.interpolate( layout, recursionInterceptor ) );
                            }
                            else
                            {
                                throw new ManifestException( "Unknown classpath layout type: '" + layoutType
                                    + "'. Check your <archive><manifest><layoutType/> element." );
                            }
                        }
                        catch ( InterpolationException e )
                        {
                            ManifestException error =
                                new ManifestException( "Error interpolating artifact path for classpath entry: "
                                    + e.getMessage() );
                            
                            error.initCause( e );
                            throw error;
                        }
                        finally
                        {
                            for ( Iterator it = valueSources.iterator(); it.hasNext(); )
                            {
                                ValueSource vs = (ValueSource) it.next();
                                interpolator.removeValuesSource( vs );
                            }
                        }
                    }
                }
            }

            if ( classpath.length() > 0 )
            {
                // Class-Path is special and should be added to manifest even if
                // it is specified in the manifestEntries section
                addManifestAttribute( m, "Class-Path", classpath.toString() );
            }
        }

        if ( config.isAddDefaultSpecificationEntries() )
        {
            addManifestAttribute( m, entries, "Specification-Title", project.getName() );
            addManifestAttribute( m, entries, "Specification-Version", project.getVersion() );

            if ( project.getOrganization() != null )
            {
                addManifestAttribute( m, entries, "Specification-Vendor", project.getOrganization().getName() );
            }
        }

        if ( config.isAddDefaultImplementationEntries() )
        {
            addManifestAttribute( m, entries, "Implementation-Title", project.getName() );
            addManifestAttribute( m, entries, "Implementation-Version", project.getVersion() );
            // MJAR-5
            addManifestAttribute( m, entries, "Implementation-Vendor-Id", project.getGroupId() );

            if ( project.getOrganization() != null )
            {
                addManifestAttribute( m, entries, "Implementation-Vendor", project.getOrganization().getName() );
            }
        }

        String mainClass = config.getMainClass();
        if ( mainClass != null && !"".equals( mainClass ) )
        {
            addManifestAttribute( m, entries, "Main-Class", mainClass );
        }

        // Added extensions
        if ( config.isAddExtensions() )
        {
            // TODO: this is only for applets - should we distinguish them as a packaging?
            StringBuffer extensionsList = new StringBuffer();
            Set artifacts = project.getArtifacts();

            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                Artifact artifact = (Artifact) iter.next();
                if ( !Artifact.SCOPE_TEST.equals( artifact.getScope() ) )
                {
                    if ( "jar".equals( artifact.getType() ) )
                    {
                        if ( extensionsList.length() > 0 )
                        {
                            extensionsList.append( " " );
                        }
                        extensionsList.append( artifact.getArtifactId() );
                    }
                }
            }

            if ( extensionsList.length() > 0 )
            {
                addManifestAttribute( m, entries, "Extension-List", extensionsList.toString() );
            }

            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                // TODO: the correct solution here would be to have an extension type, and to read
                // the real extension values either from the artifact's manifest or some part of the POM
                Artifact artifact = (Artifact) iter.next();
                if ( "jar".equals( artifact.getType() ) )
                {
                    String ename = artifact.getArtifactId() + "-Extension-Name";
                    addManifestAttribute( m, entries, ename, artifact.getArtifactId() );
                    String iname = artifact.getArtifactId() + "-Implementation-Version";
                    addManifestAttribute( m, entries, iname, artifact.getVersion() );

                    if ( artifact.getRepository() != null )
                    {
                        iname = artifact.getArtifactId() + "-Implementation-URL";
                        String url = artifact.getRepository().getUrl() + "/" + artifact.toString();
                        addManifestAttribute( m, entries, iname, url );
                    }
                }
            }
        }

        return m;
    }

    private void addCustomEntries( Manifest m, Map entries, ManifestConfiguration config )
        throws ManifestException
    {
        addManifestAttribute( m, entries, "Built-By", System.getProperty( "user.name" ) );
        addManifestAttribute( m, entries, "Build-Jdk", System.getProperty( "java.version" ) );

/* TODO: rethink this, it wasn't working
        Artifact projectArtifact = project.getArtifact();

        if ( projectArtifact.isSnapshot() )
        {
            Manifest.Attribute buildNumberAttr = new Manifest.Attribute( "Build-Number", "" +
                project.getSnapshotDeploymentBuildNumber() );
            m.addConfiguredAttribute( buildNumberAttr );
        }

*/
        if ( config.getPackageName() != null )
        {
            addManifestAttribute( m, entries, "Package", config.getPackageName() );
        }
    }

    public JarArchiver getArchiver()
    {
        return archiver;
    }

    public void setArchiver( JarArchiver archiver )
    {
        this.archiver = archiver;
    }

    public void setOutputFile( File outputFile )
    {
        archiveFile = outputFile;
    }

    public void createArchive( MavenProject project, MavenArchiveConfiguration archiveConfiguration )
        throws ArchiverException, ManifestException, IOException, DependencyResolutionRequiredException
    {
        // we have to clone the project instance so we can write out the pom with the deployment version,
        // without impacting the main project instance...
        // TODO use clone() in Maven 2.0.9+
        MavenProject workingProject = new MavenProject( project );

        boolean forced = archiveConfiguration.isForced();
        if ( archiveConfiguration.isAddMavenDescriptor() )
        {
            // ----------------------------------------------------------------------
            // We want to add the metadata for the project to the JAR in two forms:
            //
            // The first form is that of the POM itself. Applications that wish to
            // access the POM for an artifact using maven tools they can.
            //
            // The second form is that of a properties file containing the basic
            // top-level POM elements so that applications that wish to access
            // POM information without the use of maven tools can do so.
            // ----------------------------------------------------------------------

            if ( workingProject.getArtifact().isSnapshot() )
            {
                workingProject.setVersion( workingProject.getArtifact().getVersion() );
            }

            String groupId = workingProject.getGroupId();

            String artifactId = workingProject.getArtifactId();

            archiver.addFile( project.getFile(), "META-INF/maven/" + groupId + "/" + artifactId + "/pom.xml" );

            // ----------------------------------------------------------------------
            // Create pom.properties file
            // ----------------------------------------------------------------------

            File pomPropertiesFile = archiveConfiguration.getPomPropertiesFile();
            if ( pomPropertiesFile == null )
            {
                File dir = new File( workingProject.getBuild().getDirectory(), "maven-archiver" );
                pomPropertiesFile = new File( dir, "pom.properties" );
            }
            new PomPropertiesUtil().createPomProperties( workingProject, archiver, pomPropertiesFile, forced );
        }

        // ----------------------------------------------------------------------
        // Create the manifest
        // ----------------------------------------------------------------------

        File manifestFile = archiveConfiguration.getManifestFile();

        if ( manifestFile != null )
        {
            archiver.setManifest( manifestFile );
        }

        Manifest manifest = getManifest( workingProject, archiveConfiguration );

        // Configure the jar
        archiver.addConfiguredManifest( manifest );

        archiver.setCompress( archiveConfiguration.isCompress() );

        archiver.setIndex( archiveConfiguration.isIndex() );

        archiver.setDestFile( archiveFile );

        // make the archiver index the jars on the classpath, if we are adding that to the manifest
        if ( archiveConfiguration.getManifest().isAddClasspath() )
        {
            List artifacts = project.getRuntimeClasspathElements();
            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                File f = new File( (String) iter.next() );
                archiver.addConfiguredIndexJars( f );
            }
        }

        archiver.setForced( forced );
        if ( !archiveConfiguration.isForced()  &&  archiver.isSupportingForced() )
        {
            // TODO Should issue a warning here, but how do we get a logger?
            // TODO getLog().warn( "Forced build is disabled, but disabling the forced mode isn't supported by the archiver." );
        }

        // create archive
        archiver.createArchive();
    }
    
    
    private Artifact findArtifactWithFile( Set artifacts, File file )
    {
        for ( Iterator iterator = artifacts.iterator(); iterator.hasNext(); )
        {
            Artifact artifact = (Artifact) iterator.next();
            // normally not null but we can check
            if ( artifact.getFile() != null )
            {
                if ( artifact.getFile().equals( file ) )
                {
                    return artifact;
                }
            }
        }
        return null;
    }
}
