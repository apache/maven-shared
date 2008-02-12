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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Skin;
import org.apache.maven.doxia.tools.stubs.SiteToolMavenProjectStub;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class SiteToolTest
    extends PlexusTestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /**
     * @throws Exception
     */
    protected ArtifactRepository getLocalRepo()
        throws Exception
    {
        String updatePolicyFlag = ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;
        String checksumPolicyFlag = ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN;
        ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy( true, updatePolicyFlag,
                                                                                 checksumPolicyFlag );
        ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy( true, updatePolicyFlag,
                                                                                checksumPolicyFlag );
        ArtifactRepositoryFactory artifactRepositoryFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.ROLE );
        ArtifactRepositoryLayout defaultArtifactRepositoryLayout = (ArtifactRepositoryLayout) lookup(
                                                                                                      ArtifactRepositoryLayout.ROLE,
                                                                                                      "default" );
        return artifactRepositoryFactory.createArtifactRepository( "local", getTestFile( "target/local-repo" ).toURL()
            .toString(), defaultArtifactRepositoryLayout, snapshotsPolicy, releasesPolicy );
    }

    /**
     * @throws Exception
     */
    protected File getLocalRepoDir()
        throws Exception
    {
        return new File( getLocalRepo().getBasedir() );
    }

    /**
     * @throws Exception
     */
    public void testGetDefaultSkinArtifact()
        throws Exception
    {
        SiteTool tool = (SiteTool) lookup( SiteTool.ROLE );
        assertNotNull( tool );

        SiteToolMavenProjectStub project = new SiteToolMavenProjectStub();
        assertNotNull( tool.getDefaultSkinArtifact( getLocalRepo(), project.getRemoteArtifactRepositories() ) );
    }

    /**
     * @throws Exception
     */
    public void testGetSkinArtifactFromRepository()
        throws Exception
    {
        SiteTool tool = (SiteTool) lookup( SiteTool.ROLE );
        assertNotNull( tool );

        SiteToolMavenProjectStub project = new SiteToolMavenProjectStub();
        DecorationModel decorationModel = new DecorationModel();
        Skin skin = new Skin();
        skin.setGroupId( "org.apache.maven.skins" );
        skin.setArtifactId( "maven-stylus-skin" );
        decorationModel.setSkin( skin );
        assertNotNull( tool.getSkinArtifactFromRepository( getLocalRepo(), project.getRemoteArtifactRepositories(),
                                                           decorationModel ) );
    }

    /**
     * @throws Exception
     */
    public void testGetRelativePath()
        throws Exception
    {
        SiteTool tool = (SiteTool) lookup( SiteTool.ROLE );
        assertNotNull( tool );

        String to = "http://maven.apache.org";
        String from = "http://maven.apache.org";
        assertEquals( tool.getRelativePath( to, from ), "" );
        to = "http://maven.apache.org";
        from = "http://maven.apache.org/plugins/maven-site-plugin/";
        assertEquals( tool.getRelativePath( to, from ), ".." + File.separator + ".." );
        to = "http://maven.apache.org/plugins/maven-site-plugin/";
        from = "http://maven.apache.org";
        assertEquals( tool.getRelativePath( to, from ), "plugins" + File.separator + "maven-site-plugin" );
    }

    /**
     * @throws Exception
     */
    public void testGetSiteDescriptorFromBasedir()
        throws Exception
    {
        SiteTool tool = (SiteTool) lookup( SiteTool.ROLE );
        assertNotNull( tool );

        SiteToolMavenProjectStub project = new SiteToolMavenProjectStub();
        assertEquals( tool.getSiteDescriptorFromBasedir( null, project.getBasedir(), null ).toString(),
            project.getBasedir() + File.separator + "src" + File.separator + "site" + File.separator + "site.xml" );
        assertEquals( tool.getSiteDescriptorFromBasedir( null, project.getBasedir(), Locale.ENGLISH ).toString(),
            project.getBasedir() + File.separator + "src" + File.separator + "site" + File.separator + "site.xml" );
        String siteDir = "src/blabla";
        assertEquals( tool.getSiteDescriptorFromBasedir( siteDir, project.getBasedir(), null ).toString(),
            project.getBasedir() + File.separator + "src" + File.separator + "blabla" + File.separator + "site.xml" );
    }

    /**
     * @throws Exception
     */
    public void testGetSiteDescriptorFromRepository()
        throws Exception
    {
        SiteTool tool = (SiteTool) lookup( SiteTool.ROLE );
        assertNotNull( tool );

        SiteToolMavenProjectStub project = new SiteToolMavenProjectStub();
        project.setGroupId( "org.apache.maven" );
        project.setArtifactId( "maven-site" );
        project.setVersion( "1.0" );
        String result = getLocalRepoDir() + File.separator + "org" + File.separator + "apache" + File.separator
            + "maven" + File.separator + "maven-site" + File.separator + "1.0" + File.separator
            + "maven-site-1.0-site.xml";

        assertEquals( tool.getSiteDescriptorFromRepository( project, getLocalRepo(),
                                                            project.getRemoteArtifactRepositories(), Locale.ENGLISH )
            .toString(), result );
    }

    /**
     * @throws Exception
     */
    public void testGetDecorationModel()
        throws Exception
    {
        SiteTool tool = (SiteTool) lookup( SiteTool.ROLE );
        assertNotNull( tool );

        SiteToolMavenProjectStub project = new SiteToolMavenProjectStub();
        project.setGroupId( "org.apache.maven" );
        project.setArtifactId( "maven-site" );
        project.setVersion( "1.0" );
        String siteDirectory = "src/site";
        List reactorProjects = new ArrayList();

        project.setBasedir( null ); // get it from repo

        DecorationModel model = tool.getDecorationModel( project, reactorProjects, getLocalRepo(), project
            .getRemoteArtifactRepositories(), siteDirectory, Locale.getDefault(), "ISO-8859-1", "ISO-8859-1" );
        assertNotNull( model );
        assertNotNull( model.getBannerLeft() );
        assertEquals( "Maven", model.getBannerLeft().getName() );
        assertEquals( "images/apache-maven-project-2.png", model.getBannerLeft().getSrc() );
        assertEquals( "http://maven.apache.org/", model.getBannerLeft().getHref() );
        assertNotNull( model.getBannerRight() );
        assertNull( model.getBannerRight().getName() );
        assertEquals( "images/maven-logo-2.gif", model.getBannerRight().getSrc() );
        assertNull( model.getBannerRight().getHref() );
    }
}
