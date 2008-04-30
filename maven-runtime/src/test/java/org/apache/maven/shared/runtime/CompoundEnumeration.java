package org.apache.maven.shared.runtime;

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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enumeration that spans a series of other enumerations.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @param <T>
 *            the element type of this enumeration
 */
public class CompoundEnumeration<T> implements Enumeration<T>
{
    // fields -------------------------------------------------------------

    private final Iterator<Enumeration<T>> enumerations;

    private Enumeration<T> enumeration;

    // constructors -------------------------------------------------------

    public CompoundEnumeration( Enumeration<T> enumeration1, Enumeration<T> enumeration2 )
    {
        this( toList( enumeration1, enumeration2 ) );
    }

    public CompoundEnumeration( List<Enumeration<T>> enumerations )
    {
        this.enumerations = enumerations.iterator();
    }

    // Enumeration methods ------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public boolean hasMoreElements()
    {
        return ( enumeration != null && enumeration.hasMoreElements() ) || enumerations.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public T nextElement()
    {
        T element;

        if ( enumeration != null && enumeration.hasMoreElements() )
        {
            element = enumeration.nextElement();
        }
        else if ( enumerations.hasNext() )
        {
            enumeration = enumerations.next();

            element = enumeration.nextElement();
        }
        else
        {
            throw new NoSuchElementException();
        }

        return element;
    }
    
    // private methods --------------------------------------------------------
    
    private static <T> List<T> toList( T element1, T element2 )
    {
        List<T> list = new ArrayList<T>();
        
        list.add( element1 );
        list.add( element2 );
        
        return list;
    }
}
