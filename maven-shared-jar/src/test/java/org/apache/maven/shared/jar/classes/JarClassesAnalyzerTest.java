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

import org.apache.maven.shared.jar.AbstractJarAnalyzerTestCase;
import org.apache.maven.shared.jar.JarAnalyzer;

import java.io.File;


/**
 * JarAnalyzer Classes Test Case
 */
public class JarClassesAnalyzerTest
    extends AbstractJarAnalyzerTestCase
{
    private JarClassesAnalysis analyzer;

    public void setUp()
        throws Exception
    {
        super.setUp();

        analyzer = (JarClassesAnalysis) lookup( JarClassesAnalysis.class.getName() );
    }

    public void testAnalyzeJXR()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "jxr.jar" );

        assertFalse( "classes.imports.length > 0", jclass.getImports().isEmpty() );
        assertFalse( "classes.packages.length > 0", jclass.getPackages().isEmpty() );
        assertFalse( "classes.methods.length > 0", jclass.getMethods().isEmpty() );

        assertNotContainsRegex( "Import List", "[\\[\\)\\(\\;]", jclass.getImports() );

        // TODO: test for classes, methods, etc.

        assertTrue( "classes.imports", jclass.getImports().contains( "org.apache.maven.jxr.JXR" ) );
        assertTrue( "classes.imports", jclass.getImports().contains( "org.apache.oro.text.perl.Perl5Util" ) );
        assertTrue( "classes.packages", jclass.getPackages().contains( "org.apache.maven.jxr.pacman" ) );
    }

    public void testAnalyzeANT()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "ant.jar" );

        assertFalse( "classes.imports.length > 0", jclass.getImports().isEmpty() );
        assertFalse( "classes.packages.length > 0", jclass.getPackages().isEmpty() );
        assertFalse( "classes.methods.length > 0", jclass.getMethods().isEmpty() );

        assertNotContainsRegex( "Import List", "[\\[\\)\\(\\;]", jclass.getImports() );

        assertTrue( "classes.imports", jclass.getImports().contains( "java.util.zip.GZIPInputStream" ) );
        assertTrue( "classes.imports", jclass.getImports().contains( "org.apache.tools.ant.XmlLogger$TimedElement" ) );
        assertTrue( "classes.imports", jclass.getImports().contains( "org.apache.tools.mail.MailMessage" ) );
        assertTrue( "classes.packages", jclass.getPackages().contains( "org.apache.tools.ant" ) );
        assertTrue( "classes.packages", jclass.getPackages().contains( "org.apache.tools.bzip2" ) );
    }

    public void testAnalyzeJarWithInvalidClassFile()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "invalid-class-file.jar" );

        // Doesn't fail, as exceptions are ignored.
        assertTrue( jclass.getClassNames().isEmpty() );
        assertTrue( jclass.getPackages().isEmpty() );
        assertTrue( jclass.getImports().isEmpty() );
        assertNull( jclass.getJdkRevision() );
        assertTrue( jclass.getMethods().isEmpty() );
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

    public void testAnalyzeJarVersion18()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.8.jar" );

        assertEquals( "jdkrevision", "1.8", jclass.getJdkRevision() );
    }

    public void testAnalyzeJarVersion17()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.7.jar" );

        assertEquals( "jdkrevision", "1.7", jclass.getJdkRevision() );
    }

    public void testAnalyzeJarVersion16()
        throws Exception
    {
        JarClasses jclass = getJarClasses( "helloworld-1.6.jar" );

        assertEquals( "jdkrevision", "1.6", jclass.getJdkRevision() );
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

    private JarClasses getJarClasses( String filename )
        throws Exception
    {
        File file = getSampleJar( filename );

        JarClasses jclass = analyzer.analyze( new JarAnalyzer( file ) );
        assertNotNull( "JarClasses", jclass );

        return jclass;
    }
}
