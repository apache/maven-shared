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
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * JarAnalyzer Classes Analyzer
 *
 * @plexus.component role="org.apache.maven.shared.jar.classes.JarClassesAnalysis"
 */
public class JarClassesAnalysis
    extends AbstractLogEnabled
{
    /**
     * Analyze and populate the <code>jar.information.classes</code> object.
     */
    public void analyze( JarAnalyzer jar )
    {
        try
        {
            String jarfilename = jar.getFile().getAbsolutePath();
            JarClasses classes = new JarClasses();

            List classList = jar.getNameRegexEntryList( "[A-Za-z0-9]*\\.class$" );

            classes.setDebugPresent( false );

            double maxVersion = 0.0;

            Iterator it = classList.iterator();
            while ( it.hasNext() )
            {
                JarEntry entry = (JarEntry) it.next();
                String classname = entry.getName();

                ClassParser classParser = new ClassParser( jarfilename, classname );
                
                JavaClass javaClass;
                try
                {
                    javaClass = classParser.parse();
                }
                catch ( ClassFormatException e )
                {
                    getLogger().warn( "Unable to process class " + classname + " in JarAnalyzer File " + jar.getFile(), e );
                    continue;
                }

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
                    classVersion = classVersion + ( 1 / (double) javaClass.getMinor() );
                }

                if ( classVersion > maxVersion )
                {
                    maxVersion = classVersion;
                }
                
                Method methods[] = javaClass.getMethods();
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

                addImports( classes, importVisitor.getImports() );
            }

            if ( maxVersion >= 50.0 )
            {
                classes.setJdkRevision( "1.6" );
            }
            else if ( maxVersion >= 49.0 )
            {
                classes.setJdkRevision( "1.5" );
            }
            else if ( maxVersion > 47.0 )
            {
                classes.setJdkRevision( "1.4" );
            }
            else if ( maxVersion > 46.0 )
            {
                classes.setJdkRevision( "1.3" );
            }
            else if ( maxVersion > 45.65536 )
            {
                classes.setJdkRevision( "1.2" );
            }
            else if ( maxVersion > 45.3 )
            {
                classes.setJdkRevision( "1.1" );
            }
            else
            {
                classes.setJdkRevision( "1.0" );
            }

            jar.setClasses( classes );
        }
        catch ( IOException e )
        {
            getLogger().warn( "Unable to process JarAnalyzer File " + jar.getFile(), e );
        }
    }

    private boolean hasDebugSymbols( JavaClass javaClass )
    {
        boolean ret = false;
        Method methods[] = javaClass.getMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            LineNumberTable linenumbers = methods[i].getLineNumberTable();
            if ( ( linenumbers != null ) && ( linenumbers.getLength() > 0 ) )
            {
                ret = true;
                break;
            }
        }

        return ret;
    }

    private void addImports( JarClasses facts, List imports )
    {
        Iterator it = imports.iterator();
        while ( it.hasNext() )
        {
            facts.addImport( (String) it.next() );
        }
    }
}
