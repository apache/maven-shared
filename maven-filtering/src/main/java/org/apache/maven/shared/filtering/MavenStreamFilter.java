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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;

/**
 * @author Olivier Lamy
 * @author Kristian Rosenvold
 */
public interface MavenStreamFilter extends DefaultFilterInfo
{

    /**
     * Will copy a file with some filtering using defaultFilterWrappers.
     *
     * @param source         file to copy/filter
     * @param filtering    enable or not filering
     * @param mavenProject the mavenproject
     * @param filters      {@link java.util.List} of String which are path to a Property file
     * @return an input stream that applies the filter
     * @throws org.apache.maven.shared.filtering.MavenFilteringException
     * @see #getDefaultFilterWrappers(org.apache.maven.project.MavenProject, java.util.List, boolean, org.apache.maven.execution.MavenSession)
     */
    InputStream filter(InputStream source, boolean filtering, MavenProject mavenProject, List<String> filters,
            boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession)
        throws MavenFilteringException;

    /**
     * @param mavenStreamFilterRequest The filter request
     * @throws org.apache.maven.shared.filtering.MavenFilteringException
     * @return an input stream that applies the filter
     * @since 1.0-beta-3
     */
    InputStream filter(MavenStreamFilterRequest mavenStreamFilterRequest)
        throws MavenFilteringException;

    /**
     * @param source The source stream to filter
     * @param filtering
     * @param filterWrappers {@link java.util.List} of FileUtils.FilterWrapper
     * @return an input stream that applies the filter
     * @throws org.apache.maven.shared.filtering.MavenFilteringException
     */
    InputStream filter(InputStream source, boolean filtering, List<FileUtils.FilterWrapper> filterWrappers,
            String encoding)
        throws MavenFilteringException;


    /**
     * @param source The source stream to filter
     * @param filtering
     * @param filterWrappers
     * @param encoding
     * @param overwrite
     * @throws org.apache.maven.shared.filtering.MavenFilteringException
     * @return an input stream that applies the filter
     * @since 1.0-beta-2
     */
    InputStream filter(InputStream source, boolean filtering, List<FileUtils.FilterWrapper> filterWrappers,
            String encoding, boolean overwrite)
        throws MavenFilteringException;

}
