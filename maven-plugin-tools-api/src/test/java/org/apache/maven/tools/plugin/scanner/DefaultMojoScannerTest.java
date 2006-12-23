package org.apache.maven.tools.plugin.scanner;

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
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jdcasey
 */
public class DefaultMojoScannerTest
    extends TestCase
{

    public void testShouldFindOneDescriptorFromTestExtractor()
        throws Exception
    {
        Map extractors = Collections.singletonMap( "test", new TestExtractor() );

        MojoScanner scanner = new DefaultMojoScanner( extractors );

        Build build = new Build();
        build.setSourceDirectory( "testdir" );

        Model model = new Model();
        model.setBuild( build );

        MavenProject project = new MavenProject( model );
        project.setFile( new File( "." ) );

        PluginDescriptor pluginDescriptor = new PluginDescriptor();
        pluginDescriptor.setGroupId( "groupId" );
        pluginDescriptor.setArtifactId( "artifactId" );
        pluginDescriptor.setVersion( "version" );
        pluginDescriptor.setGoalPrefix( "testId" );

        scanner.populatePluginDescriptor( project, pluginDescriptor );

        List descriptors = pluginDescriptor.getMojos();

        assertEquals( 1, descriptors.size() );

        MojoDescriptor desc = (MojoDescriptor) descriptors.iterator().next();
        assertEquals( pluginDescriptor, desc.getPluginDescriptor() );
        assertEquals( "testGoal", desc.getGoal() );
    }

}