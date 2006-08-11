package org.apache.maven.shared.jar.classes;

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

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.maven.shared.jar.AbstractJarTestCase;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Import Visitor Test
 */
public class ImportVisitorTest
    extends AbstractJarTestCase
{
    public void testImportsJxr()
        throws ClassFormatException, IOException
    {
        File jxrjar = new File( getSampleJarsDirectory(), "jxr.jar" );
        String classname = "org/apache/maven/jxr/DirectoryIndexer.class";
        ClassParser classParser = new ClassParser( jxrjar.getAbsolutePath(), classname );
        JavaClass javaClass = classParser.parse();

        ImportVisitor importVisitor = new ImportVisitor( javaClass );
        DescendingVisitor descVisitor = new DescendingVisitor( javaClass, importVisitor );
        javaClass.accept( descVisitor );

        List imports = importVisitor.getImports();
        assertNotNull( "Import List", imports );

        assertNotContainsRegex( "Import List", "[\\[\\)\\(\\;]", imports );

        assertContains( "imports", "org.apache.maven.jxr.pacman.PackageType", imports );
        assertContains( "imports", "org.codehaus.plexus.util.IOUtil", imports );
        assertContains( "imports", "org.apache.oro.text.perl.Perl5Util", imports );
    }

    public void testImportsAnt()
        throws ClassFormatException, IOException
    {
        File jxrjar = new File( getSampleJarsDirectory(), "ant.jar" );
        String classname = "org/apache/tools/ant/Target.class";
        ClassParser classParser = new ClassParser( jxrjar.getAbsolutePath(), classname );
        JavaClass javaClass = classParser.parse();

        ImportVisitor importVisitor = new ImportVisitor( javaClass );
        DescendingVisitor descVisitor = new DescendingVisitor( javaClass, importVisitor );
        javaClass.accept( descVisitor );

        List imports = importVisitor.getImports();
        assertNotNull( "Import List", imports );

        assertNotContainsRegex( "Import List", "[\\[\\)\\(\\;]", imports );

        assertContains( "imports", "org.apache.tools.ant.Location", imports );
        assertContains( "imports", "org.apache.tools.ant.Task", imports );
    }
}
