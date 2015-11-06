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

import java.io.Reader;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;

/**
 * @author Olivier Lamy
 * @author Kristian Rosenvold
 * @since 1.3
 */
public interface MavenReaderFilter
    extends DefaultFilterInfo
{
    /**
     * Provides a new reader that applies filtering using defaultFilterWrappers.
     *
     * @param from the source reader
     * @param filtering enable or not filtering
     * @param mavenProject {@link MavenProject}
     * @param mavenSession {@link MavenSession}
     * @param filters {@link java.util.List} of String which are path to a Property file
     * @param escapedBackslashesInFilePath escape backslashes in file path.
     * @return an input stream that applies the filter
     * @throws org.apache.maven.shared.filtering.MavenFilteringException in case of failure.
     * @see #getDefaultFilterWrappers(org.apache.maven.project.MavenProject, java.util.List, boolean,
     *      org.apache.maven.execution.MavenSession)
     */
    @Nonnull
    Reader filter( @Nonnull Reader from, boolean filtering, @Nullable MavenProject mavenProject, List<String> filters,
                   boolean escapedBackslashesInFilePath, MavenSession mavenSession )
                       throws MavenFilteringException;

    /**
     * Provides a new reader that applies filtering using defaultFilterWrappers.
     *
     * @param mavenFileFilterRequest The filter request
     * @throws org.apache.maven.shared.filtering.MavenFilteringException in case of failure.
     * @return an input stream that applies the filter
     * @since 1.0-beta-3
     */
    @Nonnull
    Reader filter( @Nonnull MavenReaderFilterRequest mavenFileFilterRequest )
        throws MavenFilteringException;

    /**
     * Provides a new reader that applies filtering using defaultFilterWrappers.
     *
     * @param from the source reader
     * @param filtering true to apply filtering
     * @param filterWrappers {@link java.util.List} of FileUtils.FilterWrapper
     * @return an input stream that applies the filter
     */
    @Nonnull
    Reader filter( @Nonnull Reader from, boolean filtering, @Nonnull List<FileUtils.FilterWrapper> filterWrappers );
}
