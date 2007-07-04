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

import org.apache.commons.collections.list.SetUniqueList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gathered facts about the classes within a JAR file.
 *
 * @see org.apache.maven.shared.jar.classes.JarClassesAnalysis#analyze(org.apache.maven.shared.jar.JarAnalyzer)
 */
public class JarClasses
{
    /**
     * The list of imports in the classes in the JAR.
     */
    private List imports;

    /**
     * A list of packages represented by classes in the JAR.
     */
    private List packages;

    /**
     * A list of the classes that in the JAR.
     */
    private List classNames;

    /**
     * A list of methods within the classes in the JAR.
     */
    private List methods;

    /**
     * Whether the JAR contains any code with debug information. If there is a mix of debug and release code, this will
     * still be true.
     */
    private boolean isDebugPresent;

    /**
     * The highest JVM revision available in any class files. While the JAR may work on earlier JVMs if particular
     * classes are not used, this is the minimum JVM that guarantees compatibility.
     */
    private String jdkRevision;

    /**
     * Constructor to create an empty instance.
     */
    public JarClasses()
    {
        // Unique list decorators are used to ensure natural ordering is retained, the list interface is availble, and
        // that duplicates are not entered.
        imports = SetUniqueList.decorate( new ArrayList() );
        packages = SetUniqueList.decorate( new ArrayList() );
        classNames = SetUniqueList.decorate( new ArrayList() );
        methods = SetUniqueList.decorate( new ArrayList() );
    }

    /**
     * Add a discovered class to the record.
     *
     * @param name the name of the class
     */
    public void addClassName( String name )
    {
        this.classNames.add( name );
    }

    /**
     * Add a discovered package to the record.
     *
     * @param name the name of the package
     */
    public void addPackage( String name )
    {
        this.packages.add( name );
    }

    /**
     * Add a discovered method to the record.
     *
     * @param name the name of the method
     */
    public void addMethod( String name )
    {
        this.methods.add( name );
    }

    /**
     * Add a list of discovered imports to the record.
     *
     * @param imports the imports to add. Each item should be a String to avoid down the line ClassCastExceptions.
     */
    public void addImports( List imports )
    {
        this.imports.addAll( imports );
    }

    public List getImports()
    {
        return Collections.unmodifiableList( imports );
    }

    public List getClassNames()
    {
        return Collections.unmodifiableList( classNames );
    }

    public List getPackages()
    {
        return Collections.unmodifiableList( packages );
    }

    public boolean isDebugPresent()
    {
        return isDebugPresent;
    }

    public void setDebugPresent( boolean hasDebugSymbols )
    {
        this.isDebugPresent = hasDebugSymbols;
    }

    public String getJdkRevision()
    {
        return jdkRevision;
    }

    public void setJdkRevision( String jdkRevision )
    {
        this.jdkRevision = jdkRevision;
    }

    public List getMethods()
    {
        return Collections.unmodifiableList( methods );
    }
}
