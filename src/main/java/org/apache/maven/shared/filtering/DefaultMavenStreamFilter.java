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

import java.io.*;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils.FilterWrapper;
import org.apache.maven.shared.utils.io.IOUtil;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * @author Olivier Lamy
 *
 * @plexus.component role="org.apache.maven.shared.filtering.MavenFileFilter"
 * role-hint="default"
 */
public class DefaultMavenStreamFilter
    extends BaseFilter
    implements MavenStreamFilter
{

    /**
     * @plexus.requirement
     */
    private BuildContext buildContext;

    public InputStream filter( InputStream from, boolean filtering, MavenProject mavenProject, List<String> filters,
                          boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession )
        throws MavenFilteringException
    {
        MavenResourcesExecution mre = new MavenResourcesExecution();
        mre.setMavenProject( mavenProject );
        mre.setFileFilters( filters );
        mre.setEscapeWindowsPaths( escapedBackslashesInFilePath );
        mre.setMavenSession( mavenSession );
        mre.setInjectProjectBuildFilters( true );

        List<FilterWrapper> filterWrappers = getDefaultFilterWrappers( mre );
        return filter(from, filtering, filterWrappers, encoding);
    }


    public InputStream filter( MavenStreamFilterRequest mavenFileFilterRequest )
        throws MavenFilteringException
    {
        List<FilterWrapper> filterWrappers = getDefaultFilterWrappers( mavenFileFilterRequest );

        return filter(mavenFileFilterRequest.getFrom(),
                mavenFileFilterRequest.isFiltering(), filterWrappers, mavenFileFilterRequest.getEncoding());
    }


    public InputStream filter( InputStream from,boolean filtering, List<FilterWrapper> filterWrappers,
                          String encoding )
        throws MavenFilteringException
    {
        // overwrite forced to false to preserve backward comp
        return filter( from, filtering, filterWrappers, encoding, false );
    }


    public InputStream filter( InputStream from, boolean filtering, List<FilterWrapper> filterWrappers,
                          String encoding, boolean overwrite )
        throws MavenFilteringException
    {
        try
        {
            if ( filtering )
            {
                FilterWrapper[] wrappers = filterWrappers.toArray(
                    new FilterWrapper[filterWrappers.size()] );
                return filterWrap(from, encoding, wrappers, false);
            }
            else
            {
                return filterWrap(from, encoding, new FilterWrapper[0], overwrite);
            }

        }
        catch ( IOException e )
        {
            throw new MavenFilteringException( e.getMessage(), e );
        }

    }


    public static InputStream filterWrap(@Nonnull InputStream from, @Nullable String encoding,
            @Nullable FilterWrapper[] wrappers, boolean overwrite)
            throws IOException
    {
        if ( wrappers != null && wrappers.length > 0 )
        {
            // buffer so it isn't reading a byte at a time!
            Reader fileReader = null;
            Writer fileWriter = null;
            try
            {
                if ( encoding == null || encoding.length() < 1 )
                {
                    fileReader = new BufferedReader( new InputStreamReader(from) );
                    fileWriter = new FileWriter( to );
                }
                else
                {
                    FileInputStream instream = new FileInputStream( from );

                    FileOutputStream outstream = new FileOutputStream( to );

                    fileReader = new BufferedReader( new InputStreamReader( instream, encoding ) );

                    fileWriter = new OutputStreamWriter( outstream, encoding );
                }

                Reader reader = fileReader;
                for ( FilterWrapper wrapper : wrappers )
                {
                    reader = wrapper.getReader( reader );
                }

                IOUtil.copy(reader, fileWriter);
            }
            finally
            {
                IOUtil.close( fileReader );
                IOUtil.close( fileWriter );
            }
        }
        else
        {
            if ( to.lastModified() < from.lastModified() || overwrite )
            {
                filterWrap(from, to);
            }
        }
    }


}
