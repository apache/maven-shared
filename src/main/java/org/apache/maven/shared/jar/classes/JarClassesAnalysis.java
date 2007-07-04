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

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * Analyze the classes in a JAR file. This class is thread safe and immutable as it retains no state.
 * <p/>
 * Note that you must first create an instance of {@link org.apache.maven.shared.jar.JarAnalyzer} - see its Javadoc for
 * a typical use.
 *
 * @plexus.component role="org.apache.maven.shared.jar.classes.JarClassesAnalysis" role-hint="default"
 * @see #analyze(org.apache.maven.shared.jar.JarAnalyzer)
 */
public class JarClassesAnalysis
    extends AbstractLogEnabled
{
    private static final double JAVA_1_6_CLASS_VERSION = 50.0;

    private static final double JAVA_1_5_CLASS_VERSION = 49.0;

    private static final double JAVA_1_4_CLASS_VERSION = 47.0;

    private static final double JAVA_1_3_CLASS_VERSION = 46.0;

    private static final double JAVA_1_2_CLASS_VERSION = 45.65536;

    private static final double JAVA_1_1_CLASS_VERSION = 45.3;

    /**
     * Analyze a JAR and find any classes and their details. Note that if the provided JAR analyzer has previously
     * analyzed the JAR, the cached results will be returned. You must obtain a new JAR analyzer to the re-read the
     * contents of the file.
     *
     * @param jarAnalyzer the JAR to analyze. This must not yet have been closed.
     * @return the details of the classes found
     */
    public JarClasses analyze( JarAnalyzer jarAnalyzer )
    {
        JarClasses classes = jarAnalyzer.getJarData().getJarClasses();
        if ( classes == null )
        {
            String jarfilename = jarAnalyzer.getFile().getAbsolutePath();
            classes = new JarClasses();

            List classList = jarAnalyzer.getClassEntries();

            classes.setDebugPresent( false );

            double maxVersion = 0.0;

            Iterator it = classList.iterator();
            while ( it.hasNext() )
            {
                JarEntry entry = (JarEntry) it.next();
                String classname = entry.getName();

                try
                {
                    ClassParser classParser = new ClassParser( jarfilename, classname );

                    JavaClass javaClass = classParser.parse();

                    String classSignature = javaClass.getClassName();

                    if ( !classes.isDebugPresent() )
                    {
                        if ( hasDebugSymbols( javaClass ) )
                        {
                            classes.setDebugPresent( true );
                        }
                    }

                    double classVersion = javaClass.getMajor();
                    if ( javaClass.getMinor() > 0 )
                    {
                        classVersion = classVersion + 1 / (double) javaClass.getMinor();
                    }

                    if ( classVersion > maxVersion )
                    {
                        maxVersion = classVersion;
                    }

                    Method[] methods = javaClass.getMethods();
                    for ( int i = 0; i < methods.length; i++ )
                    {
                        classes.addMethod( classSignature + "." + methods[i].getName() + methods[i].getSignature() );
                    }

                    String classPackageName = javaClass.getPackageName();

                    classes.addClassName( classSignature );
                    classes.addPackage( classPackageName );

                    ImportVisitor importVisitor = new ImportVisitor( javaClass );
                    DescendingVisitor descVisitor = new DescendingVisitor( javaClass, importVisitor );
                    javaClass.accept( descVisitor );

                    classes.addImports( importVisitor.getImports() );
                }
                catch ( ClassFormatException e )
                {
                    getLogger().warn( "Unable to process class " + classname + " in JarAnalyzer File " + jarfilename,
                                      e );
                }
                catch ( IOException e )
                {
                    getLogger().warn( "Unable to process JarAnalyzer File " + jarfilename, e );
                }
            }

            // TODO: check these since they are > instead of >=
            if ( maxVersion >= JAVA_1_6_CLASS_VERSION )
            {
                classes.setJdkRevision( "1.6" );
            }
            else if ( maxVersion >= JAVA_1_5_CLASS_VERSION )
            {
                classes.setJdkRevision( "1.5" );
            }
            else if ( maxVersion > JAVA_1_4_CLASS_VERSION )
            {
                classes.setJdkRevision( "1.4" );
            }
            else if ( maxVersion > JAVA_1_3_CLASS_VERSION )
            {
                classes.setJdkRevision( "1.3" );
            }
            else if ( maxVersion > JAVA_1_2_CLASS_VERSION )
            {
                classes.setJdkRevision( "1.2" );
            }
            else if ( maxVersion > JAVA_1_1_CLASS_VERSION )
            {
                classes.setJdkRevision( "1.1" );
            }
            else if ( maxVersion > 0 )
            {
                classes.setJdkRevision( "1.0" );
            }

            jarAnalyzer.getJarData().setJarClasses( classes );
        }
        return classes;
    }

    private boolean hasDebugSymbols( JavaClass javaClass )
    {
        boolean ret = false;
        Method[] methods = javaClass.getMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            LineNumberTable linenumbers = methods[i].getLineNumberTable();
            if ( linenumbers != null && linenumbers.getLength() > 0 )
            {
                ret = true;
                break;
            }
        }
        return ret;
    }
}
