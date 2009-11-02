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

import java.util.Enumeration;

/**
 * Enumeration that spans two other enumerations.
 * 
 * @author <a href="mailto:markh@apache.org">Mark Hobson</a>
 * @version $Id$
 * @param <T>
 *            the element type of this enumeration
 */
public class CompositeEnumeration<T> implements Enumeration<T>
{
    // fields -------------------------------------------------------------

    private final Enumeration<T> enumeration1;

    private final Enumeration<T> enumeration2;

    // constructors -------------------------------------------------------

    public CompositeEnumeration( Enumeration<T> enumeration1, Enumeration<T> enumeration2 )
    {
        this.enumeration1 = enumeration1;
        this.enumeration2 = enumeration2;
    }

    // Enumeration methods ------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public boolean hasMoreElements()
    {
        return enumeration1.hasMoreElements() || enumeration2.hasMoreElements();
    }

    /**
     * {@inheritDoc}
     */
    public T nextElement()
    {
        T element;

        if ( enumeration1.hasMoreElements() )
        {
            element = enumeration1.nextElement();
        }
        else
        {
            element = enumeration2.nextElement();
        }

        return element;
    }
}
