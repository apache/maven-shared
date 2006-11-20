package org.apache.maven.shared.jar.taxon;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.shared.jar.AbstractJarTestCase;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.apache.maven.shared.jar.identification.JarIdentification;

import java.io.File;


/**
 * JarAnalyzer Taxon Analyzer Test Case
 */
public class JarIdentificationAnalyzerTest
    extends AbstractJarTestCase
{
    private JarIdentification getJarTaxon( String filename ) throws Exception
    {
        JarAnalyzer jar;

        File jarfile = new File( getSampleJarsDirectory(), filename );
        
        jar = getJarAnalyzerFactory().getJarAnalyzer( jarfile );

        JarIdentification taxon = jar.getIdentification();
        assertNotNull( "JarIdentification", taxon );

        return taxon;
    }

    public void testTaxonAnalyzerWithJXR()
        throws Exception
    {
        JarIdentification taxon = getJarTaxon( "jxr.jar" );

        assertTrue( "taxon.potentials > 0", ( taxon.getPotentials().size() > 0 ) );

        assertEquals( "taxon.groupId", "org.apache.maven", taxon.getGroupId() );
        assertEquals( "taxon.artifactId", "maven-jxr", taxon.getArtifactId() );
        assertEquals( "taxon.version", "1.1-SNAPSHOT", taxon.getVersion() );
        assertEquals( "taxon.name", "Maven JXR", taxon.getName() );
        assertEquals( "taxon.vendor", "Apache Software Foundation", taxon.getVendor() );
    }

    /**
     * Tests JarAnalyzer with No embedded pom, and no useful manifest.mf information.
     */
    public void testTaxonAnalyzerWithCODEC()
        throws Exception
    {
        JarIdentification taxon = getJarTaxon( "codec.jar" );

        assertTrue( "taxon.potentials > 0", ( taxon.getPotentials().size() > 0 ) );

        assertEquals( "taxon.groupId", "org.apache.commons.codec", taxon.getGroupId() );
        assertEquals( "taxon.artifactId", "codec", taxon.getArtifactId() );
        // assertEquals( "taxon.version", "codec_release_1_0_0_interim_20030519095102_build", taxon.getVersion() );
        assertEquals( "taxon.version", "20030519", taxon.getVersion() );
        assertEquals( "taxon.name", "codec", taxon.getName() );
        assertNull( "taxon.vendor", taxon.getVendor() );
    }

    public void testTaxonAnalyzerWithANT()
        throws Exception
    {
        JarIdentification taxon = getJarTaxon( "ant.jar" );

        assertTrue( "taxon.potentials > 0", ( taxon.getPotentials().size() > 0 ) );

        assertEquals( "taxon.groupId", "org.apache.tools.ant", taxon.getGroupId() );
        assertEquals( "taxon.artifactId", "ant", taxon.getArtifactId() );
        assertEquals( "taxon.version", "1.6.5", taxon.getVersion() );
        // assertEquals( "taxon.name", "Apache Ant", taxon.getName() );
        assertEquals( "taxon.vendor", "Apache Software Foundation", taxon.getVendor() );
    }
}
