package org.apache.maven.shared.jar.identification.repository;

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

import java.util.List;

/**
 * Interface for Repository Hash Searches.
 */
public interface RepositoryHashSearch
{
    /**
     * Search the repository for artifacts matching the given hash code when consider the entire contents of the file.
     *
     * @param hash the hash code to use
     * @return a list of {@link org.apache.maven.artifact.Artifact} instances that matched
     */
    List searchFileHash( String hash );

    /**
     * Search the repository for artifacts matching the given hash code when consider the bytecode of the classes in the
     * file.
     *
     * @param hash the hash code to use
     * @return a list of {@link org.apache.maven.artifact.Artifact} instances that matched
     */
    List searchBytecodeHash( String hash );
}
