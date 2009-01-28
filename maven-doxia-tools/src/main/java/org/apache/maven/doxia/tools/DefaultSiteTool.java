package org.apache.maven.doxia.tools;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.doxia.site.decoration.Banner;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Menu;
import org.apache.maven.doxia.site.decoration.MenuItem;
import org.apache.maven.doxia.site.decoration.Skin;
import org.apache.maven.doxia.site.decoration.inheritance.DecorationModelInheritanceAssembler;
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.reporting.MavenReport;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.util.interpolation.MapBasedValueSource;
import org.codehaus.plexus.util.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.util.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Default implementation of the site tool.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 *
 * @plexus.component role="org.apache.maven.doxia.tools.SiteTool" role-hint="default"
 */
public class DefaultSiteTool
    extends AbstractLogEnabled
    implements SiteTool
{
    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    /**
     * The component that is used to resolve additional artifacts required.
     *
     * @plexus.requirement
     */
    private ArtifactResolver artifactResolver;

    /**
     * The component used for creating artifact instances.
     *
     * @plexus.requirement
     */
    private ArtifactFactory artifactFactory;

    /**
     * Internationalization.
     *
     * @plexus.requirement
     */
    protected I18N i18n;

    /**
     * The component for assembling inheritance.
     *
     * @plexus.requirement
     */
    protected DecorationModelInheritanceAssembler assembler;

    /**
     * Project builder.
     *
     * @plexus.requirement
     */
    protected MavenProjectBuilder mavenProjectBuilder;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public Artifact getSkinArtifactFromRepository( ArtifactRepository localRepository, List remoteArtifactRepositories,
                                                   DecorationModel decoration )
        throws SiteToolException
    {
        if ( localRepository == null )
        {
            throw new IllegalArgumentException( "The parameter 'localRepository' can not be null" );
        }
        if ( remoteArtifactRepositories == null )
        {
            throw new IllegalArgumentException( "The parameter 'remoteArtifactRepositories' can not be null" );
        }
        if ( decoration == null )
        {
            throw new IllegalArgumentException( "The parameter 'decoration' can not be null" );
        }

        Skin skin = decoration.getSkin();

        if ( skin == null )
        {
            skin = Skin.getDefaultSkin();
        }

        String version = skin.getVersion();
        Artifact artifact;
        try
        {
            if ( version == null )
            {
                version = Artifact.RELEASE_VERSION;
            }
            VersionRange versionSpec = VersionRange.createFromVersionSpec( version );
            artifact = artifactFactory.createDependencyArtifact( skin.getGroupId(), skin.getArtifactId(), versionSpec,
                                                                 "jar", null, null );

            artifactResolver.resolve( artifact, remoteArtifactRepositories, localRepository );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw new SiteToolException( "InvalidVersionSpecificationException: The skin version '" + version
                + "' is not valid: " + e.getMessage() );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new SiteToolException( "ArtifactResolutionException: Unable to find skin", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new SiteToolException( "ArtifactNotFoundException: The skin does not exist: " + e.getMessage() );
        }

        return artifact;
    }

    /** {@inheritDoc} */
    public Artifact getDefaultSkinArtifact( ArtifactRepository localRepository, List remoteArtifactRepositories )
        throws SiteToolException
    {
        return getSkinArtifactFromRepository( localRepository, remoteArtifactRepositories, new DecorationModel() );
    }

    /** {@inheritDoc} */
    public String getRelativePath( String to, String from )
    {
        if ( to == null )
        {
            throw new IllegalArgumentException( "The parameter 'to' can not be null" );
        }
        if ( from == null )
        {
            throw new IllegalArgumentException( "The parameter 'from' can not be null" );
        }

        URL toUrl = null;
        URL fromUrl = null;

        String toPath = to;
        String fromPath = from;

        try
        {
            toUrl = new URL( to );
        }
        catch ( MalformedURLException e )
        {
            try
            {
                toUrl = new File( getNormalizedPath( to ) ).toURL();
            }
            catch ( MalformedURLException e1 )
            {
                getLogger().warn( "Unable to load a URL for '" + to + "': " + e.getMessage() );
            }
        }

        try
        {
            fromUrl = new URL( from );
        }
        catch ( MalformedURLException e )
        {
            try
            {
                fromUrl = new File( getNormalizedPath( from ) ).toURL();
            }
            catch ( MalformedURLException e1 )
            {
                getLogger().warn( "Unable to load a URL for '" + from + "': " + e.getMessage() );
            }
        }

        if ( toUrl != null && fromUrl != null )
        {
            // URLs, determine if they share protocol and domain info

            if ( ( toUrl.getProtocol().equalsIgnoreCase( fromUrl.getProtocol() ) )
                && ( toUrl.getHost().equalsIgnoreCase( fromUrl.getHost() ) )
                && ( toUrl.getPort() == fromUrl.getPort() ) )
            {
                // shared URL domain details, use URI to determine relative path

                toPath = toUrl.getFile();
                fromPath = fromUrl.getFile();
            }
            else
            {
                // dont share basic URL infomation, no relative available

                return to;
            }
        }
        else if ( ( toUrl != null && fromUrl == null ) || ( toUrl == null && fromUrl != null ) )
        {
            // one is a URL and the other isnt, no relative available.

            return to;
        }

        // either the two locations are not URLs or if they are they
        // share the common protocol and domain info and we are left
        // with their URI information

        // normalise the path delimters

        toPath = new File( toPath ).getPath();
        fromPath = new File( fromPath ).getPath();

        // strip any leading slashes if its a windows path
        if ( toPath.matches( "^\\[a-zA-Z]:" ) )
        {
            toPath = toPath.substring( 1 );
        }
        if ( fromPath.matches( "^\\[a-zA-Z]:" ) )
        {
            fromPath = fromPath.substring( 1 );
        }

        // lowercase windows drive letters.

        if ( toPath.startsWith( ":", 1 ) )
        {
            toPath = toPath.substring( 0, 1 ).toLowerCase() + toPath.substring( 1 );
        }
        if ( fromPath.startsWith( ":", 1 ) )
        {
            fromPath = fromPath.substring( 0, 1 ).toLowerCase() + fromPath.substring( 1 );
        }

        // check for the presence of windows drives. No relative way of
        // traversing from one to the other.

        if ( ( toPath.startsWith( ":", 1 ) && fromPath.startsWith( ":", 1 ) )
            && ( !toPath.substring( 0, 1 ).equals( fromPath.substring( 0, 1 ) ) ) )
        {
            // they both have drive path element but they dont match, no
            // relative path

            return to;
        }

        if ( ( toPath.startsWith( ":", 1 ) && !fromPath.startsWith( ":", 1 ) )
            || ( !toPath.startsWith( ":", 1 ) && fromPath.startsWith( ":", 1 ) ) )
        {

            // one has a drive path element and the other doesnt, no relative
            // path.

            return to;

        }

        // use tokeniser to traverse paths and for lazy checking
        StringTokenizer toTokeniser = new StringTokenizer( toPath, File.separator );
        StringTokenizer fromTokeniser = new StringTokenizer( fromPath, File.separator );

        int count = 0;

        // walk along the to path looking for divergence from the from path
        while ( toTokeniser.hasMoreTokens() && fromTokeniser.hasMoreTokens() )
        {
            if ( File.separatorChar == '\\' )
            {
                if ( !fromTokeniser.nextToken().equalsIgnoreCase( toTokeniser.nextToken() ) )
                {
                    break;
                }
            }
            else
            {
                if ( !fromTokeniser.nextToken().equals( toTokeniser.nextToken() ) )
                {
                    break;
                }
            }

            count++;
        }

        // reinitialise the tokenisers to count positions to retrieve the
        // gobbled token

        toTokeniser = new StringTokenizer( toPath, File.separator );
        fromTokeniser = new StringTokenizer( fromPath, File.separator );

        while ( count-- > 0 )
        {
            fromTokeniser.nextToken();
            toTokeniser.nextToken();
        }

        String relativePath = "";

        // add back refs for the rest of from location.
        while ( fromTokeniser.hasMoreTokens() )
        {
            fromTokeniser.nextToken();

            relativePath += "..";

            if ( fromTokeniser.hasMoreTokens() )
            {
                relativePath += File.separatorChar;
            }
        }

        if ( relativePath.length() != 0 && toTokeniser.hasMoreTokens() )
        {
            relativePath += File.separatorChar;
        }

        // add fwd fills for whatevers left of to.
        while ( toTokeniser.hasMoreTokens() )
        {
            relativePath += toTokeniser.nextToken();

            if ( toTokeniser.hasMoreTokens() )
            {
                relativePath += File.separatorChar;
            }
        }

        if ( !relativePath.equals( to ) )
        {
            getLogger().debug( "Mapped url: " + to + " to relative path: " + relativePath );
        }

        return relativePath;
    }

    /** {@inheritDoc} */
    public File getSiteDescriptorFromBasedir( String siteDirectory, File basedir, Locale locale )
    {
        if ( basedir == null )
        {
            throw new IllegalArgumentException( "The parameter 'basedir' can not be null" );
        }

        if ( siteDirectory == null )
        {
            // TODO need to be more dynamic
            siteDirectory = "src/site";
        }
        if ( locale == null )
        {
            locale = new Locale( "" );
        }

        File siteDir = new File( basedir, siteDirectory );

        File siteDescriptor = new File( siteDir, "site_" + locale.getLanguage() + ".xml" );

        if ( !siteDescriptor.isFile() )
        {
            siteDescriptor = new File( siteDir, "site.xml" );
        }
        return siteDescriptor;
    }

    /** {@inheritDoc} */
    public File getSiteDescriptorFromRepository( MavenProject project, ArtifactRepository localRepository,
                                                 List remoteArtifactRepositories, Locale locale )
        throws SiteToolException
    {
        if ( project == null )
        {
            throw new IllegalArgumentException( "The parameter 'project' can not be null" );
        }
        if ( localRepository == null )
        {
            throw new IllegalArgumentException( "The parameter 'localRepository' can not be null" );
        }
        if ( remoteArtifactRepositories == null )
        {
            throw new IllegalArgumentException( "The parameter 'remoteArtifactRepositories' can not be null" );
        }

        if ( locale == null )
        {
            locale = new Locale( "" );
        }

        try
        {
            return resolveSiteDescriptor( project, localRepository, remoteArtifactRepositories, locale );
        }
        catch ( ArtifactNotFoundException e )
        {
            getLogger().debug( "ArtifactNotFoundException: Unable to locate site descriptor: " + e );
            return null;
        }
        catch ( ArtifactResolutionException e )
        {
            throw new SiteToolException( "ArtifactResolutionException: Unable to locate site descriptor: "
                + e.getMessage() );
        }
        catch ( IOException e )
        {
            throw new SiteToolException( "IOException: Unable to locate site descriptor: " + e.getMessage() );
        }
    }

    /** {@inheritDoc} */
    public DecorationModel getDecorationModel( MavenProject project, List reactorProjects,
                                               ArtifactRepository localRepository, List repositories,
                                               String siteDirectory, Locale locale, String inputEncoding,
                                               String outputEncoding )
        throws SiteToolException
    {
        if ( project == null )
        {
            throw new IllegalArgumentException( "The parameter 'project' can not be null" );
        }
        if ( reactorProjects == null )
        {
            throw new IllegalArgumentException( "The parameter 'reactorProjects' can not be null" );
        }
        if ( localRepository == null )
        {
            throw new IllegalArgumentException( "The parameter 'localRepository' can not be null" );
        }
        if ( repositories == null )
        {
            throw new IllegalArgumentException( "The parameter 'repositories' can not be null" );
        }
        if ( inputEncoding == null )
        {
            throw new IllegalArgumentException( "The parameter 'inputEncoding' can not be null" );
        }
        if ( outputEncoding == null )
        {
            throw new IllegalArgumentException( "The parameter 'outputEncoding' can not be null" );
        }

        if ( locale == null )
        {
            locale = Locale.getDefault();
        }

        Map props = new HashMap();

        // This is to support the deprecated ${reports} and ${modules} tags.
        props.put( "reports", "<menu ref=\"reports\"/>\n" );
        props.put( "modules", "<menu ref=\"modules\"/>\n" );

        DecorationModel decorationModel = getDecorationModel( project, reactorProjects, localRepository, repositories,
                                                              siteDirectory, locale, props, inputEncoding,
                                                              outputEncoding );

        if ( decorationModel == null )
        {
            String siteDescriptorContent;

            try
            {
                // Note the default is not a super class - it is used when nothing else is found
                siteDescriptorContent =
                    IOUtil.toString( getClass().getResourceAsStream( "/default-site.xml" ), "UTF-8" );
            }
            catch ( IOException e )
            {
                throw new SiteToolException( "Error reading default site descriptor: " + e.getMessage(), e );
            }

            siteDescriptorContent = getInterpolatedSiteDescriptorContent( props, project, siteDescriptorContent,
                                                                          inputEncoding, outputEncoding );

            decorationModel = readDecorationModel( siteDescriptorContent );
        }

        MavenProject parentProject = getParentProject( project, reactorProjects, localRepository );

        if ( parentProject != null )
        {
            populateProjectParentMenu( decorationModel, locale, project, parentProject, true );
        }

        populateModules( project, reactorProjects, localRepository, decorationModel, locale, true );

        if ( decorationModel.getBannerLeft() == null )
        {
            // extra default to set
            Banner banner = new Banner();
            banner.setName( project.getName() );
            decorationModel.setBannerLeft( banner );
        }

        if ( project.getUrl() != null )
        {
            assembler.resolvePaths( decorationModel, project.getUrl() );
        }
        else
        {
            getLogger().warn( "No URL defined for the project - decoration links will not be resolved" );
        }

        return decorationModel;
    }

    /** {@inheritDoc} */
    public void populateReportsMenu( DecorationModel decorationModel, Locale locale, Map categories )
    {
        if ( decorationModel == null )
        {
            throw new IllegalArgumentException( "The parameter 'decorationModel' can not be null" );
        }
        if ( categories == null )
        {
            throw new IllegalArgumentException( "The parameter 'categories' can not be null" );
        }

        if ( locale == null )
        {
            locale = Locale.getDefault();
        }

        Menu menu = decorationModel.getMenuRef( "reports" );

        if ( menu != null )
        {
            if ( menu.getName() == null )
            {
                menu.setName( i18n.getString( "site-tool", locale, "decorationModel.menu.projectdocumentation" ) );
            }

            boolean found = false;
            if ( menu.getItems().isEmpty() )
            {
                List categoryReports = (List) categories.get( MavenReport.CATEGORY_PROJECT_INFORMATION );
                if ( !isEmptyList( categoryReports ) )
                {
                    MenuItem item = createCategoryMenu( i18n.getString( "site-tool", locale,
                                                                        "decorationModel.menu.projectinformation" ),
                                                        "/project-info.html", categoryReports, locale );
                    menu.getItems().add( item );
                    found = true;
                }

                categoryReports = (List) categories.get( MavenReport.CATEGORY_PROJECT_REPORTS );
                if ( !isEmptyList( categoryReports ) )
                {
                    MenuItem item = createCategoryMenu( i18n.getString( "site-tool", locale,
                                                                        "decorationModel.menu.projectreports" ),
                                                        "/project-reports.html", categoryReports, locale );
                    menu.getItems().add( item );
                    found = true;
                }
            }
            if ( !found )
            {
                decorationModel.removeMenuRef( "reports" );
            }
        }
    }

    /** {@inheritDoc} */
    public String getInterpolatedSiteDescriptorContent( Map props, MavenProject project, String siteDescriptorContent,
                                                        String inputEncoding, String outputEncoding )
        throws SiteToolException
    {
        if ( props == null )
        {
            throw new IllegalArgumentException( "The parameter 'props' can not be null" );
        }
        if ( project == null )
        {
            throw new IllegalArgumentException( "The parameter 'project' can not be null" );
        }
        if ( siteDescriptorContent == null )
        {
            throw new IllegalArgumentException( "The parameter 'siteDescriptorContent' can not be null" );
        }
        if ( inputEncoding == null )
        {
            throw new IllegalArgumentException( "The parameter 'inputEncoding' can not be null" );
        }
        if ( outputEncoding == null )
        {
            throw new IllegalArgumentException( "The parameter 'outputEncoding' can not be null" );
        }

        // MSITE-201: The ObjectBasedValueSource( aProject ) below will match
        // ${modules} to aProject.getModules(), so we need to interpolate that
        // first.

        Map modulesProps = new HashMap();

        // Legacy for the old ${modules} syntax
        modulesProps.put( "modules", "<menu ref=\"modules\"/>" );

        siteDescriptorContent = StringUtils.interpolate( siteDescriptorContent, modulesProps );

        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();

        try
        {
            interpolator.addValueSource( new EnvarBasedValueSource() );
        }
        catch ( IOException e )
        {
            // Prefer logging?
            throw new SiteToolException( "IOException: cannot interpolate environment properties: " + e.getMessage(),
                                         e );
        }

        interpolator.addValueSource( new ObjectBasedValueSource( project ) );

        interpolator.addValueSource( new MapBasedValueSource( project.getProperties() ) );

        siteDescriptorContent = interpolator.interpolate( siteDescriptorContent, "project" );

        props.put( "inputEncoding", inputEncoding );

        props.put( "outputEncoding", outputEncoding );

        // Legacy for the old ${parentProject} syntax
        props.put( "parentProject", "<menu ref=\"parent\"/>" );

        // Legacy for the old ${reports} syntax
        props.put( "reports", "<menu ref=\"reports\"/>" );

        return StringUtils.interpolate( siteDescriptorContent, props );
    }

    /** {@inheritDoc} */
    public MavenProject getParentProject( MavenProject project, List reactorProjects,
                                          ArtifactRepository localRepository )
    {
        if ( project == null )
        {
            throw new IllegalArgumentException( "The parameter 'project' can not be null" );
        }
        if ( reactorProjects == null )
        {
            throw new IllegalArgumentException( "The parameter 'reactorProjects' can not be null" );
        }
        if ( localRepository == null )
        {
            throw new IllegalArgumentException( "The parameter 'localRepository' can not be null" );
        }

        MavenProject parentProject = null;

        MavenProject origParent = project.getParent();
        if ( origParent != null )
        {
            Iterator reactorItr = reactorProjects.iterator();

            while ( reactorItr.hasNext() )
            {
                MavenProject reactorProject = (MavenProject) reactorItr.next();

                if ( reactorProject.getGroupId().equals( origParent.getGroupId() )
                    && reactorProject.getArtifactId().equals( origParent.getArtifactId() )
                    && reactorProject.getVersion().equals( origParent.getVersion() ) )
                {
                    parentProject = reactorProject;
                    break;
                }
            }

            if ( parentProject == null && project.getBasedir() != null )
            {
                try
                {
                    File pomFile = new File( project.getBasedir(), project.getModel().getParent().getRelativePath() );

                    if ( pomFile.isDirectory() )
                    {
                        pomFile = new File( pomFile, "pom.xml" );
                    }
                    pomFile = new File( getNormalizedPath( pomFile.getPath() ) );

                    MavenProject mavenProject = mavenProjectBuilder.build( pomFile, localRepository, null );

                    if ( mavenProject.getGroupId().equals( origParent.getGroupId() )
                        && mavenProject.getArtifactId().equals( origParent.getArtifactId() )
                        && mavenProject.getVersion().equals( origParent.getVersion() ) )
                    {
                        parentProject = mavenProject;
                    }
                }
                catch ( ProjectBuildingException e )
                {
                    getLogger().info( "Unable to load parent project from a relative path: " + e.getMessage() );
                }
            }

            if ( parentProject == null )
            {
                try
                {
                    parentProject = mavenProjectBuilder.buildFromRepository( project.getParentArtifact(), project
                        .getRemoteArtifactRepositories(), localRepository );
                    getLogger().info( "Parent project loaded from repository." );
                }
                catch ( ProjectBuildingException e )
                {
                    getLogger().warn( "Unable to load parent project from repository: " + e.getMessage() );
                }
            }

            if ( parentProject == null )
            {
                // fallback to uninterpolated value

                parentProject = origParent;
            }
        }
        return parentProject;
    }

    /** {@inheritDoc} */
    public void populateProjectParentMenu( DecorationModel decorationModel, Locale locale, MavenProject project,
                                           MavenProject parentProject, boolean keepInheritedRefs )
    {
        if ( decorationModel == null )
        {
            throw new IllegalArgumentException( "The parameter 'decorationModel' can not be null" );
        }
        if ( project == null )
        {
            throw new IllegalArgumentException( "The parameter 'project' can not be null" );
        }
        if ( parentProject == null )
        {
            throw new IllegalArgumentException( "The parameter 'parentProject' can not be null" );
        }

        if ( locale == null )
        {
            locale = Locale.getDefault();
        }

        Menu menu = decorationModel.getMenuRef( "parent" );

        if ( menu == null )
        {
            return;
        }

        if ( !keepInheritedRefs || !menu.isInheritAsRef() )
        {
            String parentUrl = parentProject.getUrl();

            if ( parentUrl != null )
            {
                if ( parentUrl.endsWith( "/" ) )
                {
                    parentUrl += "index.html";
                }
                else
                {
                    parentUrl += "/index.html";
                }

                parentUrl = getRelativePath( parentUrl, project.getUrl() );

                if ( menu.getName() == null )
                {
                    menu.setName( i18n.getString( "site-tool", locale, "decorationModel.menu.parentproject" ) );
                }

                MenuItem item = new MenuItem();
                item.setName( parentProject.getName() );
                item.setHref( parentUrl );
                menu.addItem( item );
            }
            else
            {
                decorationModel.removeMenuRef( "parent" );
            }
        }
    }

    /** {@inheritDoc} */
    public void populateModules( MavenProject project, List reactorProjects, ArtifactRepository localRepository,
                                 DecorationModel decorationModel, Locale locale, boolean keepInheritedRefs )
        throws SiteToolException
    {
        if ( project == null )
        {
            throw new IllegalArgumentException( "The parameter 'project' can not be null" );
        }
        if ( reactorProjects == null )
        {
            throw new IllegalArgumentException( "The parameter 'reactorProjects' can not be null" );
        }
        if ( localRepository == null )
        {
            throw new IllegalArgumentException( "The parameter 'localRepository' can not be null" );
        }
        if ( decorationModel == null )
        {
            throw new IllegalArgumentException( "The parameter 'decorationModel' can not be null" );
        }

        if ( locale == null )
        {
            locale = Locale.getDefault();
        }

        Menu menu = decorationModel.getMenuRef( "modules" );

        if ( menu == null )
        {
            return;
        }

        if ( !keepInheritedRefs || !menu.isInheritAsRef() )
        {
            // we require child modules and reactors to process module menu
            if ( project.getModules().size() > 0 )
            {
                List projects = reactorProjects;

                if ( menu.getName() == null )
                {
                    menu.setName( i18n.getString( "site-tool", locale, "decorationModel.menu.projectmodules" ) );
                }

                if ( projects.size() == 1 )
                {
                    getLogger().debug( "Attempting to load module information from local filesystem" );

                    // Not running reactor - search for the projects manually
                    List models = new ArrayList( project.getModules().size() );
                    for ( Iterator i = project.getModules().iterator(); i.hasNext(); )
                    {
                        String module = (String) i.next();
                        Model model;
                        File f = new File( project.getBasedir(), module + "/pom.xml" );
                        if ( f.exists() )
                        {
                            try
                            {
                                model = mavenProjectBuilder.build( f, localRepository, null ).getModel();
                            }
                            catch ( ProjectBuildingException e )
                            {
                                throw new SiteToolException( "Unable to read local module-POM", e );
                            }
                        }
                        else
                        {
                            getLogger().warn( "No filesystem module-POM available" );

                            model = new Model();
                            model.setName( module );
                            model.setUrl( module );
                        }
                        models.add( model );
                    }
                    populateModulesMenuItemsFromModels( project, models, menu );
                }
                else
                {
                    populateModulesMenuItemsFromReactorProjects( project, reactorProjects, menu );
                }
            }
            else
            {
                decorationModel.removeMenuRef( "modules" );
            }
        }
    }

    /** {@inheritDoc} */
    public List getAvailableLocales( String locales )
    {
        List localesList = new ArrayList();
        if ( locales != null )
        {
            String[] localesArray = StringUtils.split( locales, "," );

            for ( int i = 0; i < localesArray.length; i++ )
            {
                Locale locale = codeToLocale( localesArray[i] );

                if ( locale != null )
                {
                    if ( !Arrays.asList( Locale.getAvailableLocales() ).contains( locale ) )
                    {
                        if ( getLogger().isWarnEnabled() )
                        {
                            getLogger().warn( "The locale parsed defined by '" + locale
                                + "' is not available in this Java Virtual Machine ("
                                + System.getProperty( "java.version" )
                                + " from " + System.getProperty( "java.vendor" ) + ") - IGNORING" );
                        }
                        continue;
                    }

                    // Default bundles are in English
                    if ( ( !locale.getLanguage().equals( DEFAULT_LOCALE.getLanguage() ) )
                        && ( !i18n.getBundle( "site-tool", locale ).getLocale().getLanguage()
                            .equals( locale.getLanguage() ) ) )
                    {
                        StringBuffer sb = new StringBuffer();

                        sb.append( "The locale '" ).append( locale ).append( "' (" );
                        sb.append( locale.getDisplayName( Locale.ENGLISH ) );
                        sb.append( ") is not currently support by Maven - IGNORING. " );
                        sb.append( "\n" );
                        sb.append( "Contribution are welcome and greatly appreciated! " );
                        sb.append( "\n" );
                        sb.append( "If you want to contribute a new translation, please visit " );
                        sb.append( "http://maven.apache.org/plugins/maven-site-plugin/i18n.html " );
                        sb.append( "for detailed instructions." );

                        if ( getLogger().isWarnEnabled() )
                        {
                            getLogger().warn( sb.toString() );
                        }

                        continue;
                    }

                    localesList.add( locale );
                }
            }
        }

        if ( localesList.isEmpty() )
        {
            localesList = Collections.singletonList( DEFAULT_LOCALE );
        }

        return localesList;
    }

    /** {@inheritDoc} */
    public Locale codeToLocale( String localeCode )
    {
        if ( localeCode == null )
        {
            return null;
        }

        if ( "default".equalsIgnoreCase( localeCode ) )
        {
            return Locale.getDefault();
        }

        String language = "";
        String country = "";
        String variant = "";

        StringTokenizer tokenizer = new StringTokenizer( localeCode, "_" );
        final int maxTokens = 3;
        if ( tokenizer.countTokens() > maxTokens )
        {
            if ( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Invalid java.util.Locale format for '" + localeCode + "' entry - IGNORING" );
            }
            return null;
        }

        if ( tokenizer.hasMoreTokens() )
        {
            language = tokenizer.nextToken();
            if ( tokenizer.hasMoreTokens() )
            {
                country = tokenizer.nextToken();
                if ( tokenizer.hasMoreTokens() )
                {
                    variant = tokenizer.nextToken();
                }
            }
        }

        return new Locale( language, country, variant );
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /**
     * @param path could be null.
     * @return the path normalized, i.e. by eliminating "/../" and "/./" in the path.
     * @see FilenameUtils#normalize(String)
     */
    protected static String getNormalizedPath( String path )
    {
        String normalized = FilenameUtils.normalize( path );
        if ( normalized == null )
        {
            normalized = path;
        }
        return ( normalized == null ) ? null : normalized.replace( '\\', '/' );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * @param project not null
     * @param localRepository not null
     * @param repositories not null
     * @param locale not null
     * @return the resolved site descriptor
     * @throws IOException if any
     * @throws ArtifactResolutionException if any
     * @throws ArtifactNotFoundException if any
     */
    private File resolveSiteDescriptor( MavenProject project, ArtifactRepository localRepository, List repositories,
                                        Locale locale )
        throws IOException, ArtifactResolutionException, ArtifactNotFoundException
    {
        File result;

        // TODO: this is a bit crude - proper type, or proper handling as metadata rather than an artifact in 2.1?
        Artifact artifact = artifactFactory.createArtifactWithClassifier( project.getGroupId(),
                                                                          project.getArtifactId(),
                                                                          project.getVersion(), "xml", "site_"
                                                                              + locale.getLanguage() );

        boolean found = false;
        try
        {
            artifactResolver.resolve( artifact, repositories, localRepository );

            result = artifact.getFile();

            // we use zero length files to avoid re-resolution (see below)
            if ( result.length() > 0 )
            {
                found = true;
            }
            else
            {
                getLogger().debug( "Skipped site descriptor for locale " + locale.getLanguage() );
            }
        }
        catch ( ArtifactNotFoundException e )
        {
            getLogger().debug( "Unable to locate site descriptor for locale " + locale.getLanguage() + ": " + e );

            // we can afford to write an empty descriptor here as we don't expect it to turn up later in the remote
            // repository, because the parent was already released (and snapshots are updated automatically if changed)
            result = new File( localRepository.getBasedir(), localRepository.pathOf( artifact ) );
            result.getParentFile().mkdirs();
            result.createNewFile();
        }

        if ( !found )
        {
            artifact = artifactFactory.createArtifactWithClassifier( project.getGroupId(), project.getArtifactId(),
                                                                     project.getVersion(), "xml", "site" );
            try
            {
                artifactResolver.resolve( artifact, repositories, localRepository );
            }
            catch ( ArtifactNotFoundException e )
            {
                // see above regarding this zero length file
                result = new File( localRepository.getBasedir(), localRepository.pathOf( artifact ) );
                result.getParentFile().mkdirs();
                result.createNewFile();

                throw e;
            }

            result = artifact.getFile();

            // we use zero length files to avoid re-resolution (see below)
            if ( result.length() == 0 )
            {
                getLogger().debug( "Skipped remote site descriptor check" );
                result = null;
            }
        }

        return result;
    }

    /**
     * @param project not null
     * @param reactorProjects not null
     * @param localRepository not null
     * @param repositories not null
     * @param siteDirectory not null
     * @param locale not null
     * @param origProps not null
     * @param inputEncoding not null
     * @param outputEncoding not null
     * @return the decoration model depending the locale
     * @throws SiteToolException if any
     */
    private DecorationModel getDecorationModel( MavenProject project, List reactorProjects,
                                                ArtifactRepository localRepository, List repositories,
                                                String siteDirectory, Locale locale, Map origProps,
                                                String inputEncoding, String outputEncoding )
        throws SiteToolException
    {
        Map props = new HashMap( origProps );

        File siteDescriptor;
        if ( project.getBasedir() == null )
        {
            // POM is in the repository, look there for site descriptor
            try
            {
                siteDescriptor = getSiteDescriptorFromRepository( project, localRepository, repositories, locale );
            }
            catch ( SiteToolException e )
            {
                throw new SiteToolException( "The site descriptor cannot be resolved from the repository: "
                    + e.getMessage(), e );
            }
        }
        else
        {
            siteDescriptor = getSiteDescriptorFromBasedir( siteDirectory, project.getBasedir(), locale );
        }

        String siteDescriptorContent = null;
        try
        {
            if ( siteDescriptor != null && siteDescriptor.exists() )
            {
                getLogger().debug( "Reading site descriptor from " + siteDescriptor );
                Reader siteDescriptorReader = ReaderFactory.newXmlReader( siteDescriptor );
                siteDescriptorContent = IOUtil.toString( siteDescriptorReader );
            }
        }
        catch ( IOException e )
        {
            throw new SiteToolException( "The site descriptor cannot be read!", e );
        }

        DecorationModel decoration = null;
        if ( siteDescriptorContent != null )
        {
            siteDescriptorContent = getInterpolatedSiteDescriptorContent( props, project, siteDescriptorContent,
                                                                          inputEncoding, outputEncoding );

            decoration = readDecorationModel( siteDescriptorContent );
        }

        MavenProject parentProject = getParentProject( project, reactorProjects, localRepository );
        if ( parentProject != null )
        {
            getLogger().debug( "Parent project loaded ..." );
            DecorationModel parent = getDecorationModel( parentProject, reactorProjects, localRepository, repositories,
                                                         siteDirectory, locale, props, inputEncoding, outputEncoding );

            if ( decoration == null )
            {
                decoration = parent;
            }
            else
            {
                assembler.assembleModelInheritance( project.getName(), decoration, parent, project.getUrl(),
                                                    parentProject.getUrl() == null ? project.getUrl() : parentProject
                                                        .getUrl() );
            }
        }
        if ( decoration != null && decoration.getSkin() != null )
        {
            getLogger().debug( "Skin used: " + decoration.getSkin() );
        }

        return decoration;
    }

    /**
     * @param siteDescriptorContent not null
     * @return the decoration model object
     * @throws SiteToolException if any
     */
    private DecorationModel readDecorationModel( String siteDescriptorContent )
        throws SiteToolException
    {
        DecorationModel decoration;
        try
        {
            decoration = new DecorationXpp3Reader().read( new StringReader( siteDescriptorContent ) );
        }
        catch ( XmlPullParserException e )
        {
            throw new SiteToolException( "Error parsing site descriptor", e );
        }
        catch ( IOException e )
        {
            throw new SiteToolException( "Error reading site descriptor", e );
        }
        return decoration;
    }

    /**
     * @param project         not null
     * @param reactorProjects not null
     * @param menu            not null
     */
    private void populateModulesMenuItemsFromReactorProjects( MavenProject project, List reactorProjects, Menu menu )
    {
        Iterator iterator = getModuleProjects( project, reactorProjects, 1 ).iterator();
        while ( iterator.hasNext() )
        {
            MavenProject moduleProject = (MavenProject) iterator.next();

            appendMenuItem( project, menu, moduleProject.getName(), moduleProject.getUrl(),
                            moduleProject.getArtifactId() );
        }
    }

    /**
     * Return all the projects that are modules, or modules of modules, of the specified project found within the
     * reactor.
     * <p/>
     * The levels parameter controls how many descendent levels of modules are returned. With levels equal
     * to 1, only the immediate modules of the specified project are returned.
     * <p/>
     * If levels equals 2 it returns those modules' modules as well.
     * <p/>
     * If levels equals -1 it returns the entire module hierarchy beneath the specified project. Note that this is
     * simply the equivalent to the entire reactor if the specified project is the root execution project.
     *
     * @param project         the project to search under
     * @param reactorProjects The projects in the reactor
     * @param levels          the number of descendent levels to return
     * @return the list of module projects.
     */
    private List getModuleProjects( final MavenProject project, final List reactorProjects, final int levels )
    {
        List moduleProjects = new ArrayList();

        boolean infinite = ( levels == -1 );

        if ( ( reactorProjects != null ) && ( infinite || levels > 0 ) )
        {
            Iterator iterator = reactorProjects.iterator();
            while ( iterator.hasNext() )
            {
                MavenProject reactorProject = (MavenProject) iterator.next();

                if ( isModuleOfProject( project, reactorProject ) )
                {
                    moduleProjects.add( reactorProject );

                    // recurse to find the modules of this project
                    moduleProjects.addAll(
                        getModuleProjects( reactorProject, reactorProjects, infinite ? levels : levels - 1 ) );
                }
            }
        }

        return moduleProjects;
    }

    /**
     * Return <code>true</code> if the supplied potentialModule project is a module of the specified parentProject.
     *
     * @param parentProject   the parent project.
     * @param potentialModule the potential module project.
     * @return true if the potentialModule is indeed a module of the specified parent project.
     */
    private boolean isModuleOfProject( MavenProject parentProject, MavenProject potentialModule )
    {
        boolean result = false;

        List modules = parentProject.getModules();

        if ( modules != null && parentProject != potentialModule )
        {
            File parentBaseDir = parentProject.getBasedir();

            Iterator iterator = modules.iterator();
            while ( iterator.hasNext() )
            {
                String module = (String) iterator.next();

                File moduleBaseDir = new File( parentBaseDir, module );

                try
                {
                    String lhs = potentialModule.getBasedir().getCanonicalPath();
                    String rhs = moduleBaseDir.getCanonicalPath();

                    if ( lhs.equals( rhs ) )
                    {
                        result = true;
                        break;
                    }
                }
                catch ( IOException e )
                {
                    getLogger().error(
                        "Error encountered when trying to resolve canonical module paths: " + e.getMessage() );
                }
            }
        }

        return result;
    }

    /**
     * @param project not null
     * @param models not null
     * @param menu not null
     */
    private void populateModulesMenuItemsFromModels( MavenProject project, List models, Menu menu )
    {
        if ( models != null && models.size() > 1 )
        {
            Iterator reactorItr = models.iterator();

            while ( reactorItr.hasNext() )
            {
                Model model = (Model) reactorItr.next();

                String reactorUrl = model.getUrl();
                String name = model.getName();

                appendMenuItem( project, menu, name, reactorUrl, model.getArtifactId() );
            }
        }
    }

    /**
     * @param project not null
     * @param menu not null
     * @param name not null
     * @param href could be null
     * @param defaultHref not null
     */
    private void appendMenuItem( MavenProject project, Menu menu, String name, String href, String defaultHref )
    {
        String selectedHref = href;

        if ( selectedHref == null )
        {
            selectedHref = defaultHref;
        }

        MenuItem item = new MenuItem();
        item.setName( name );

        String baseUrl = project.getUrl();
        if ( baseUrl != null )
        {
            selectedHref = getRelativePath( selectedHref, baseUrl );
        }

        if ( selectedHref.endsWith( "/" ) )
        {
            item.setHref( selectedHref + "index.html" );
        }
        else
        {
            item.setHref( selectedHref + "/index.html" );
        }
        menu.addItem( item );
    }

    /**
     * @param name not null
     * @param href not null
     * @param categoryReports not null
     * @param locale not null
     * @return the menu item object
     */
    private MenuItem createCategoryMenu( String name, String href, List categoryReports, Locale locale )
    {
        MenuItem item = new MenuItem();
        item.setName( name );
        item.setCollapse( true );
        item.setHref( href );

        Collections.sort( categoryReports, new ReportComparator( locale ) );

        for ( Iterator k = categoryReports.iterator(); k.hasNext(); )
        {
            MavenReport report = (MavenReport) k.next();

            MenuItem subitem = new MenuItem();
            subitem.setName( report.getName( locale ) );
            subitem.setHref( report.getOutputName() + ".html" );
            item.getItems().add( subitem );
        }

        return item;
    }

    // ----------------------------------------------------------------------
    // static methods
    // ----------------------------------------------------------------------

    /**
     * Convenience method.
     *
     * @param list could be null
     * @return true if the list is <code>null</code> or empty
     */
    private static boolean isEmptyList( List list )
    {
        return list == null || list.isEmpty();
    }
}
