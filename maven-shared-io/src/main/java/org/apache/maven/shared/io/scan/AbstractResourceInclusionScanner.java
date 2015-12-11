package org.apache.maven.shared.io.scan;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.shared.io.scan.mapping.SourceMapping;
import org.apache.maven.shared.utils.io.DirectoryScanner;

/**
 * @author jdcasey
 * @version $Id$
 */
public abstract class AbstractResourceInclusionScanner
    implements ResourceInclusionScanner
{
    private final List<SourceMapping> sourceMappings = new ArrayList<SourceMapping>();

    /** {@inheritDoc} */
    public final void addSourceMapping( SourceMapping sourceMapping )
    {
        sourceMappings.add( sourceMapping );
    }

    /**
     * @return The source mapping.
     */
    protected final List<SourceMapping> getSourceMappings()
    {
        return Collections.unmodifiableList( sourceMappings );
    }

    /**
     * @param sourceDir {@link File}
     * @param sourceIncludes source includes.
     * @param sourceExcludes source excludes.
     * @return The resulting sources.
     */
    protected String[] scanForSources( File sourceDir, Set<String> sourceIncludes, Set<String> sourceExcludes )
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setFollowSymlinks( true );
        ds.setBasedir( sourceDir );

        String[] includes;
        if ( sourceIncludes.isEmpty() )
        {
            includes = new String[0];
        }
        else
        {
            includes = (String[]) sourceIncludes.toArray( new String[sourceIncludes.size()] );
        }

        ds.setIncludes( includes );

        String[] excludes;
        if ( sourceExcludes.isEmpty() )
        {
            excludes = new String[0];
        }
        else
        {
            excludes = (String[]) sourceExcludes.toArray( new String[sourceExcludes.size()] );
        }

        ds.setExcludes( excludes );
        ds.addDefaultExcludes();

        ds.scan();

        return ds.getIncludedFiles();
    }
}
