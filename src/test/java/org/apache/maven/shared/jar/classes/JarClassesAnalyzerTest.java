package org.apache.maven.shared.jar.classes;

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

import org.apache.maven.shared.jar.AbstractJarTestCase;
import org.apache.maven.shared.jar.JarAnalyzer;

import java.io.File;


/**
 * JarAnalyzer Classes Test Case
 */
public class JarClassesAnalyzerTest
    extends AbstractJarTestCase
{
    private JarClasses getJarClasses( String filename )
        throws Exception
    {
        File jarfile = new File( getSampleJarsDirectory(), filename );
        JarAnalyzer jar = getJarAnalyzerFactory().getJarAnalyzer( jarfile );

        JarClasses jclass = jar.getClasses();
        assertNotNull( "JarClasses", jclass );

        return jclass;
    }

    public void testAnalyzeJXR()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "jxr.jar" );

        assertTrue( "classes.imports.length > 0", jclass.getImports().size() > 0 );
        assertTrue( "classes.packages.length > 0", jclass.getPackages().size() > 0 );

        assertNotContainsRegex( "Import List", "[\\[\\)\\(\\;]", jclass.getImports() );

        // TODO: test for classes count.

        assertContains( "classes.imports", "org.apache.maven.jxr.JXR", jclass.getImports() );
        assertContains( "classes.imports", "org.apache.oro.text.perl.Perl5Util", jclass.getImports() );
        assertContains( "classes.imports", "org.codehaus.plexus.util.IOUtil", jclass.getImports() );
        assertContains( "classes.packages", "org.apache.maven.jxr.pacman", jclass.getPackages() );
    }

    public void testAnalyzeANT()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "ant.jar" );

        assertTrue( "classes.imports.length > 0", jclass.getImports().size() > 0 );
        assertTrue( "classes.packages.length > 0", jclass.getPackages().size() > 0 );

        assertNotContainsRegex( "Import List", "[\\[\\)\\(\\;]", jclass.getImports() );

        assertContains( "classes.imports", "java.util.zip.GZIPInputStream", jclass.getImports() );
        assertContains( "classes.imports", "org.apache.tools.ant.XmlLogger$TimedElement", jclass.getImports() );
        assertContains( "classes.imports", "org.apache.tools.mail.MailMessage", jclass.getImports() );
        assertContains( "classes.packages", "org.apache.tools.ant", jclass.getPackages() );
        assertContains( "classes.packages", "org.apache.tools.bzip2", jclass.getPackages() );
    }

    public void testAnalyzeJarWithDebug()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.4-debug.jar" );

        assertTrue( "has debug", jclass.isDebugPresent() );
    }

    public void testAnalyzeJarWithoutDebug()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.4.jar" );

        assertFalse( "no debug present", jclass.isDebugPresent() );
    }

    public void testAnalyzeJarVersion15()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.5.jar" );

        assertEquals( "jdkrevision", "1.5", jclass.getJdkRevision() );
    }

    public void testAnalyzeJarVersion14()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.4.jar" );

        assertEquals( "jdkrevision", "1.4", jclass.getJdkRevision() );
    }

    public void testAnalyzeJarVersion13()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.3.jar" );

        assertEquals( "jdkrevision", "1.3", jclass.getJdkRevision() );
    }

    public void testAnalyzeJarVersion12()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.2.jar" );

        assertEquals( "jdkrevision", "1.2", jclass.getJdkRevision() );
    }

    public void testAnalyzeJarVersion11()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.1.jar" );

        assertEquals( "jdkrevision", "1.1", jclass.getJdkRevision() );
    }
}
