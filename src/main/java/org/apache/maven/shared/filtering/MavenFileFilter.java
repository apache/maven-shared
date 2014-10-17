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
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;

/**
 * @author Olivier Lamy
 */
public interface MavenFileFilter
    extends DefaultFilterInfo
{

    /**
     * Will copy a file with some filtering using defaultFilterWrappers.
     *
     * @param from file to copy/filter
     * @param to destination file
     * @param filtering enable or not filtering
     * @param mavenProject the mavenproject
     * @param mavenSession The maven session.
     * @param filters {@link List} of String which are path to a Property file
     * @param encoding The encoding which is used during the filtering process.
     * @throws MavenFilteringException
     * @see #getDefaultFilterWrappers(MavenProject, List, boolean, MavenSession)
     */
    void copyFile( File from, final File to, boolean filtering, MavenProject mavenProject, List<String> filters,
                   boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession )
        throws MavenFilteringException;

    /**
     * @param mavenFileFilterRequest the request
     * @throws MavenFilteringException
     * @since 1.0-beta-3
     */
    void copyFile( MavenFileFilterRequest mavenFileFilterRequest )
        throws MavenFilteringException;

    /**
     * @param from The source file
     * @param to The target file
     * @param filtering true to apply filtering
     * @param filterWrappers {@link List} of FileUtils.FilterWrapper
     * @param encoding The encoding used during the filtering.
     * @throws MavenFilteringException In case of an error.
     */
    void copyFile( File from, final File to, boolean filtering, List<FileUtils.FilterWrapper> filterWrappers,
                   String encoding )
        throws MavenFilteringException;

    /**
     * @param from The source file
     * @param to The destination file
     * @param filtering true to apply filtering
     * @param filterWrappers The filters to be applied.
     * @param encoding The encoding to use
     * @param overwrite Overwrite to file ?
     * @throws MavenFilteringException In case of an error.
     * @since 1.0-beta-2
     */
    void copyFile( File from, final File to, boolean filtering, List<FileUtils.FilterWrapper> filterWrappers,
                   String encoding, boolean overwrite )
        throws MavenFilteringException;
}
