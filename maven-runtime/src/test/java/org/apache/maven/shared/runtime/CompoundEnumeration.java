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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enumeration that spans a series of other enumerations.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class CompoundEnumeration implements Enumeration
{
    // fields -------------------------------------------------------------

    private final Iterator enumerations;

    private Enumeration enumeration;

    // constructors -------------------------------------------------------

    public CompoundEnumeration( Enumeration enumeration1, Enumeration enumeration2 )
    {
        this( new Enumeration[] { enumeration1, enumeration2 } );
    }

    public CompoundEnumeration( Enumeration[] enumerations )
    {
        this( Arrays.asList( enumerations ) );
    }

    public CompoundEnumeration( List enumerations )
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
    public Object nextElement()
    {
        Object element;

        if ( enumeration != null && enumeration.hasMoreElements() )
        {
            element = enumeration.nextElement();
        }
        else if ( enumerations.hasNext() )
        {
            enumeration = (Enumeration) enumerations.next();

            element = enumeration.nextElement();
        }
        else
        {
            throw new NoSuchElementException();
        }

        return element;
    }
}
