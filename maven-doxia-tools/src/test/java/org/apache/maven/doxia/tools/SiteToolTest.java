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
}
