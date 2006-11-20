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

import org.apache.commons.collections.list.SetUniqueList;

import java.util.ArrayList;
import java.util.List;

/**
 * Facts about the classes within a JarAnalyzer File.
 */
public class JarClasses
{
    private List imports;

    private List packages;

    private List classNames;
    
    private List methods;

    private boolean isDebugPresent;

    private String jdkRevision;

    /**
     * Create Empty JarFacts.
     */
    public JarClasses()
    {
        super();
        imports = SetUniqueList.decorate( new ArrayList() );
        packages = SetUniqueList.decorate( new ArrayList() );
        classNames = SetUniqueList.decorate( new ArrayList() );
        methods = SetUniqueList.decorate( new ArrayList() );
    }

    public void addClassName( String name )
    {
        this.classNames.add( name );
    }

    /**
     * Add an Import.
     *
     * @param iname the import name
     */
    public void addImport( String iname )
    {
        this.imports.add( iname );
    }

    /**
     * Add a Package name.
     *
     * @param pname the package name
     */
    public void addPackage( String pname )
    {
        this.packages.add( pname );
    }

    public List getClassNames()
    {
        return classNames;
    }

    /**
     * @return Returns the imports.
     */
    public List getImports()
    {
        return imports;
    }

    /**
     * @return Returns the packages.
     */
    public List getPackages()
    {
        return packages;
    }

    public void setClassNames( List classes )
    {
        this.classNames = classes;
    }

    /**
     * @param imports The imports to set.
     */
    public void setImports( List imports )
    {
        this.imports = imports;
    }

    /**
     * @param packages The packages to set.
     */
    public void setPackages( List packages )
    {
        this.packages = packages;
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
    
    public void addMethod( String method )
    {
        this.methods.add( method );
    }

    public List getMethods()
    {
        return methods;
    }

    public void setMethods( List methods )
    {
        this.methods = methods;
    }
}
