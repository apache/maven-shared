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

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.collections.list.SetUniqueList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a BCEL class visitor that analyzes a class and collects imports.
 */
public class ImportVisitor
    extends EmptyVisitor
{
    /**
     * The list of imports discovered.
     */
    private List imports;

    /**
     * The Java class that is being analyzed.
     */
    private JavaClass javaClass;

    /**
     * Pattern to detect if the import is qualified and allows retrieval of the actual import name from the string via the group 1.
     */
    private static final Pattern QUALIFIED_IMPORT_PATTERN = Pattern.compile( "L([a-zA-Z][a-zA-Z0-9\\.]+);" );

    /**
     * Pattern that checks whether a string is valid UTF-8. Imports that are not are ignored.
     */
    private static final Pattern VALID_UTF8_PATTERN = Pattern.compile( "^[\\(\\)\\[A-Za-z0-9;/]+$" );

    /**
     * Create an Import visitor.
     *
     * @param javaClass the javaclass to work from
     */
    public ImportVisitor( JavaClass javaClass )
    {
        this.javaClass = javaClass;

        // Create a list that is guaranteed to be unique while retaining it's list qualities (LinkedHashSet does not
        // expose the list interface even if natural ordering is retained)  
        this.imports = SetUniqueList.decorate( new ArrayList() );
    }

    /**
     * Get the list of discovered imports.
     *
     * @return Returns the imports.
     */
    public List getImports()
    {
        return imports;
    }

    /**
     * Find any formally declared import in the Constant Pool.
     *
     * @see org.apache.bcel.classfile.EmptyVisitor#visitConstantClass(org.apache.bcel.classfile.ConstantClass)
     */
    public void visitConstantClass( ConstantClass constantClass )
    {
        String name = constantClass.getBytes( javaClass.getConstantPool() );

        // only strings with '/' character are to be considered.
        if ( name.indexOf( '/' ) == -1 )
        {
            return;
        }

        name = name.replace( '/', '.' );

        if ( name.endsWith( ".class" ) )
        {
            name = name.substring( 0, name.length() - 6 );
        }

        Matcher mat = QUALIFIED_IMPORT_PATTERN.matcher( name );
        if ( mat.find() )
        {
            this.imports.add( mat.group( 1 ) );
        }
        else
        {
            this.imports.add( name );
        }
    }

    /**
     * Find any package class Strings in the UTF8 String Pool.
     *
     * @see org.apache.bcel.classfile.EmptyVisitor#visitConstantUtf8(org.apache.bcel.classfile.ConstantUtf8)
     */
    public void visitConstantUtf8( ConstantUtf8 constantUtf8 )
    {
        String ret = constantUtf8.getBytes().trim();

        // empty strings are not class names.
        if ( ret.length() <= 0 )
        {
            return;
        }

        // Only valid characters please.
        if ( !VALID_UTF8_PATTERN.matcher( ret ).matches() )
        {
            return;
        }

        // only strings with '/' character are to be considered.
        if ( ret.indexOf( '/' ) == -1 )
        {
            return;
        }

        // Strings that start with '/' are bad too
        // Seen when Pool has regex patterns.
        if ( ret.charAt( 0 ) == '/' )
        {
            return;
        }

        // Make string more class-like.
        ret = ret.replace( '/', '.' );

        // Double ".." indicates a bad class fail-fast.
        // Seen when ConstantUTF8 Pool has regex patterns.
        if ( ret.indexOf( ".." ) != -1 )
        {
            return;
        }

        Matcher mat = QUALIFIED_IMPORT_PATTERN.matcher( ret );
        char prefix = ret.charAt( 0 );

        if ( prefix == '(' )
        {
            // A Method Declaration.

            // Loop for each Qualified Class found.
            while ( mat.find() )
            {
                this.imports.add( mat.group( 1 ) );
            }
        }
        else
        {
            // A Variable Declaration.
            if ( mat.find() )
            {
                // Add a UTF8 Qualified Class reference.
                this.imports.add( mat.group( 1 ) );
            }
            else
            {
                // Add a simple Class reference.
                this.imports.add( ret );
            }
        }
    }
}
