package org.apache.maven.shared.jar.identification.exposers;

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

import org.apache.maven.shared.jar.AbstractJarAnalyzerTestCase;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.apache.maven.shared.jar.identification.JarIdentification;

import java.io.File;


/**
 * Test Case for Embedded Maven Model Taxon Data.
 */
public class EmbeddedMavenModelExposerTest
    extends AbstractJarAnalyzerTestCase
{
    public void testExposerWithJXR()
        throws Exception
    {
        File file = getSampleJar( "jxr.jar" );

        JarIdentification identification = new JarIdentification();

        EmbeddedMavenModelExposer exposer = new EmbeddedMavenModelExposer();
        exposer.expose( identification, new JarAnalyzer( file ) );

        assertFalse( "exposer.groupIds", identification.getPotentialGroupIds().isEmpty() );
        assertFalse( "exposer.artifactIds", identification.getPotentialArtifactIds().isEmpty() );
        assertFalse( "exposer.versions", identification.getPotentialVersions().isEmpty() );

        // TODO test others
    }

    public void testExposerWithANT()
        throws Exception
    {
        File file = getSampleJar( "ant.jar" );

        JarIdentification identification = new JarIdentification();

        EmbeddedMavenModelExposer exposer = new EmbeddedMavenModelExposer();
        exposer.expose( identification, new JarAnalyzer( file ) );

        assertTrue( "exposer.groupIds", identification.getPotentialGroupIds().isEmpty() );
        assertTrue( "exposer.artifactIds", identification.getPotentialArtifactIds().isEmpty() );
        assertTrue( "exposer.versions", identification.getPotentialVersions().isEmpty() );

        // TODO test others
    }
}
