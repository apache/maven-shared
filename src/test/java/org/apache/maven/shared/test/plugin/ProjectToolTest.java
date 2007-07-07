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
package org.apache.maven.shared.test.plugin;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.shared.test.plugin.ProjectTool.PomInfo;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.StringUtils;

public class ProjectToolTest
    extends PlexusTestCase
{

    public void testManglePomForTesting_ShouldPopulateOutDirAndFinalName()
        throws Exception
    {
        ProjectTool tool = (ProjectTool) lookup( ProjectTool.ROLE, "default" );

        File pomFile = new File( "pom.xml" );

        PomInfo info = tool.manglePomForTesting( pomFile, "test", true );

        assertEquals( "target"+File.separatorChar+"it-build-target", info.getBuildDirectory() );
        assertEquals( "maven-plugin-testing-tools-test.jar", info.getFinalName() );
        assertEquals( "target"+File.separatorChar+"it-build-target"+File.separatorChar+"classes",info.getBuildOutputDirectory() );
    }

    public void testPackageProjectArtifact_ShouldPopulateArtifactFileWithJarLocation()
        throws Exception
    {
        ProjectTool tool = (ProjectTool) lookup( ProjectTool.ROLE, "default" );

        File pomFile = new File( "pom.xml" );

        MavenProject project = tool.packageProjectArtifact( pomFile, "test", true );

        String expectedPath = "target/it-build-target/maven-plugin-testing-tools-test.jar";
        
        // be nice with windows
        String actualPath = StringUtils.replace( project.getArtifact().getFile().getPath(), "\\", "/" );
        
        assertEquals( expectedPath, actualPath );
    }

    public void testPackageProjectArtifact_ShouldPopulateWithCorrectArtifactAndMetadata()
        throws Exception
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
