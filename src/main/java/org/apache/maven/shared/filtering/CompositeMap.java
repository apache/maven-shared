package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Map composed with some others (optional adding SystemProperties and envvar)
 * The get Method look in the Map list to return the corresponding value
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 15 janv. 08
 * @version $Id$
 */
public class CompositeMap
    extends AbstractMap
{
    
    private List /*Map*/maps;

    private boolean systemPropertiesFirst;

    /**
     * 
     * @param maps
     * @throws IOException if getting envvars failed
     */
    public CompositeMap( List /* Map */maps )
    {
        this( maps, false, false );
    }

    /**
     * @param maps an orderer {@link List} of {@link Map}
     * @param useSystemProperties using or not the System Properties
     * @param systemPropertiesFirst if with get( key ) the sysProps must wins (the internal ordered {@link List} 
     *        will have in first the System Properties)
     */
    public CompositeMap( List /*Map*/maps, boolean useSystemProperties, boolean systemPropertiesFirst )
    {
        this.systemPropertiesFirst = systemPropertiesFirst;
        if ( systemPropertiesFirst && !useSystemProperties )
        {
            throw new IllegalArgumentException( "systemPropertiesFirst can't be true if useSystemProperties is false" );
        }
        this.maps = new ArrayList();
        if ( useSystemProperties && !systemPropertiesFirst )
        {
            if ( maps != null )
            {
                this.maps.addAll( maps );
            }
            this.maps.add( System.getProperties() );
        }
        else if ( useSystemProperties && systemPropertiesFirst )
        {
            this.maps.add( System.getProperties() );
            if ( maps != null )
            {
                this.maps.addAll( maps );
            }
        }
        else
        {
            if ( maps != null )
            {
                this.maps.addAll( maps );
            }
        }
    }

    public Object get( Object key )
    {
        if ( this.maps != null )
        {
            for ( Iterator iterator = this.maps.iterator(); iterator.hasNext(); )
            {
                Map map = (Map) iterator.next();
                Object value = map.get( key );
                if ( value != null )
                {
                    return value;
                }
            }
        }
        return null;
    }

    /** 
     * @see java.util.AbstractMap#entrySet()
     */
    public Set entrySet()
    {
        throw new UnsupportedOperationException( "Cannot enumerate properties in a composite map" );
    }

    public List getMaps()
    {
        return maps;
    }

    public void addMap( Map map )
    {
        // see constructors internal Map can't be null
        this.maps.add( map );
    }

    public boolean isSystemPropertiesFirst()
    {
        return systemPropertiesFirst;
    }

    public void setSystemPropertiesFirst( boolean systemPropertiesFirst )
    {
        this.systemPropertiesFirst = systemPropertiesFirst;
    }

}
