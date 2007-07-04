package org.apache.maven.shared.jar.identification;

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

import java.io.File;


/**
 * JarAnalyzer Taxon Analyzer Test Case
 *
 * @todo test the exposers individually instead of in aggregate here (and test the normalize, etc. methods here instead with controlled exposers)
 */
public class JarIdentificationAnalyzerTest
    extends AbstractJarAnalyzerTestCase
{
    private JarIdentification getJarTaxon( String filename )
        throws Exception
    {
        File jarfile = getSampleJar( filename );

        JarIdentificationAnalysis analyzer =
            (JarIdentificationAnalysis) lookup( JarIdentificationAnalysis.class.getName() );
        JarIdentification taxon = analyzer.analyze( new JarAnalyzer( jarfile ) );
        assertNotNull( "JarIdentification", taxon );

        return taxon;
    }

    public void testTaxonAnalyzerWithJXR()
        throws Exception
    {
        JarIdentification taxon = getJarTaxon( "jxr.jar" );

        assertEquals( "identification.groupId", "org.apache.maven", taxon.getGroupId() );
        assertEquals( "identification.artifactId", "maven-jxr", taxon.getArtifactId() );
        assertEquals( "identification.version", "1.1-SNAPSHOT", taxon.getVersion() );
        assertEquals( "identification.name", "Maven JXR", taxon.getName() );
        assertEquals( "identification.vendor", "Apache Software Foundation", taxon.getVendor() );

        // TODO assert potentials too
    }

    /**
     * Tests JarAnalyzer with No embedded pom, and no useful manifest.mf information.
     *
     * @throws Exception failures
     */
    public void testTaxonAnalyzerWithCODEC()
        throws Exception
    {
        JarIdentification taxon = getJarTaxon( "codec.jar" );

        assertEquals( "identification.groupId", "org.apache.commons.codec", taxon.getGroupId() );
        assertEquals( "identification.artifactId", "codec", taxon.getArtifactId() );
        // TODO fix assertion
        // assertEquals( "identification.version", "codec_release_1_0_0_interim_20030519095102_build", identification.getVersion() );
        assertEquals( "identification.version", "20030519", taxon.getVersion() );
        assertEquals( "identification.name", "codec", taxon.getName() );
        assertNull( "identification.vendor", taxon.getVendor() );

        // TODO assert potentials too
    }

    public void testTaxonAnalyzerWithANT()
        throws Exception
    {
        JarIdentification taxon = getJarTaxon( "ant.jar" );

        assertEquals( "identification.groupId", "org.apache.tools.ant", taxon.getGroupId() );
        assertEquals( "identification.artifactId", "ant", taxon.getArtifactId() );
        assertEquals( "identification.version", "1.6.5", taxon.getVersion() );
        // TODO fix assertion
        // assertEquals( "identification.name", "Apache Ant", identification.getName() );
        assertEquals( "identification.vendor", "Apache Software Foundation", taxon.getVendor() );

        // TODO assert potentials too
    }
}
