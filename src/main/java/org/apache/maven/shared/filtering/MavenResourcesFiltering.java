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

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 28 janv. 08
 * @version $Id$
 */
public interface MavenResourcesFiltering
{

    /**
     * @param resources {@link List} of {@link org.apache.maven.model.Resource}
     * @param outputDirectory parent destination directory
     * @param mavenProject
     * @param encoding 
     * @param fileFilters {@link List} of String which are path to a Property file 
     * @param nonFilteredFileExtensions {@link List} of String for non filtered file extensions
     * @throws MavenFilteringException
     */
    void filterResources( List resources, File outputDirectory, MavenProject mavenProject, String encoding,
                          List fileFilters, List nonFilteredFileExtensions )
        throws MavenFilteringException;

    /**
     * @param resources {@link List} of {@link org.apache.maven.model.Resource}
     * @param outputDirectory parent destination directory
     * @param encoding
     * @param filterWrappers {@link List} of FileUtils.FilterWrapper
     * @param resourcesBaseDirectory baseDirectory of resources
     * @param nonFilteredFileExtensions {@link List} of String for non filtered file extensions
     * @throws MavenFilteringException
     */
    void filterResources( List resources, File outputDirectory, String encoding, List filterWrappers,
                          File resourcesBaseDirectory, List nonFilteredFileExtensions )
        throws MavenFilteringException;    
}
