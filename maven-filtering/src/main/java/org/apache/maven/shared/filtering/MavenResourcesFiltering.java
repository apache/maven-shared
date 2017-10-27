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

import java.util.List;

/**
 * @author Olivier Lamy
 */
public interface MavenResourcesFiltering
{

    /**
     * return the List of the non filtered extensions (jpg,jpeg,gif,bmp,png,ico)
     *
     * @return {@link List} of {@link String}
     */
    List<String> getDefaultNonFilteredFileExtensions();

    /**
     * @param fileName the file name
     * @param userNonFilteredFileExtensions an extra list of file extensions
     * @return true if filtering can be applied to the file (means extensions.lowerCase is in the default List or in the
     *         user defined extension List)
     */
    boolean filteredFileExtension( String fileName, List<String> userNonFilteredFileExtensions );

    /**
     * @param mavenResourcesExecution {@link MavenResourcesExecution}
     * @throws MavenFilteringException in case of failure.
     */
    void filterResources( MavenResourcesExecution mavenResourcesExecution )
        throws MavenFilteringException;
}
