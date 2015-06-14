package org.apache.maven.shared.artifact.filter.collection;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id$
 */
public class FilterArtifacts
{
    private List<ArtifactsFilter> filters;

    public FilterArtifacts()
    {
        filters = new ArrayList<ArtifactsFilter>();
    }

    /**
     * Removes all of the elements from this list. The list will be empty after this call returns.
     */
    public void clearFilters()
    {
        filters.clear();
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * @param filter element to be appended to this list.
     */
    public void addFilter( ArtifactsFilter filter )
    {
        if ( filter != null )
        {
            filters.add( filter );
        }
    }

    /**
     * Inserts the specified element at the specified position in this list. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right (adds one to their indices).
     * 
     * @param index at which index the specified filter is to be inserted.
     * @param filter the filter to be inserted.
     * @throws IndexOutOfBoundsException if index is out of range <tt>(index &lt; 0 || index &gt; size())</tt>.
     */
    public void addFilter( int index, ArtifactsFilter filter )
    {
        if ( filter != null )
        {
            filters.add( index, filter );
        }
    }

    public Set<Artifact> filter( Set<Artifact> artifacts )
        throws ArtifactFilterException
    {
        // apply filters
        for ( ArtifactsFilter filter : filters )
        {
            // log(artifacts,log);
            try
            {
                artifacts = filter.filter( artifacts );
            }
            catch ( NullPointerException e )
            {
                // don't do anything, just skip this.
                continue;
            }
        }

        return artifacts;
    }

    /**
     * @return the filters.
     */
    public List<ArtifactsFilter> getFilters()
    {
        return this.filters;
    }

    /**
     * @param filters The filters to set.
     */
    public void setFilters( List<ArtifactsFilter> filters )
    {
        this.filters = filters;
    }
}
