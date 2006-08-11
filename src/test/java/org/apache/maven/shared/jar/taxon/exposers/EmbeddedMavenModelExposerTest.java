package org.apache.maven.shared.jar.taxon.exposers;

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
import org.apache.maven.shared.jar.Jar;
import org.apache.maven.shared.jar.JarException;

import java.io.File;


/**
 * Test Case for Embedded Maven Model Taxon Data.
 */
public class EmbeddedMavenModelExposerTest
    extends AbstractJarTestCase
{
    public void testExposerWithJXR()
        throws JarException
    {
        File jxrfile = new File( getSampleJarsDirectory(), "jxr.jar" );
        Jar jxrjar = new Jar( jxrfile );

        EmbeddedMavenModelExposer exposer = new EmbeddedMavenModelExposer();
        exposer.setJar( jxrjar );
        exposer.expose();

        assertTrue( "exposer.isAuthoritative", exposer.isAuthoritative() );

        assertNotNull( "exposer.groupIds", exposer.getGroupIds() );
        assertFalse( "exposer.groupIds", exposer.getGroupIds().isEmpty() );

        assertNotNull( "exposer.artifactIds", exposer.getArtifactIds() );
        assertFalse( "exposer.artifactIds", exposer.getArtifactIds().isEmpty() );

        assertNotNull( "exposer.versions", exposer.getVersions() );
        assertFalse( "exposer.versions", exposer.getVersions().isEmpty() );
    }

    public void testExposerWithANT()
        throws JarException
    {
        File antfile = new File( getSampleJarsDirectory(), "ant.jar" );
        Jar antjar = new Jar( antfile );

        EmbeddedMavenModelExposer exposer = new EmbeddedMavenModelExposer();
        exposer.setJar( antjar );
        exposer.expose();

        assertTrue( "exposer.isAuthoritative", exposer.isAuthoritative() );

        assertNull( "exposer.groupIds", exposer.getGroupIds() );
        assertNull( "exposer.artifactIds", exposer.getArtifactIds() );
        assertNull( "exposer.versions", exposer.getVersions() );
    }
}
