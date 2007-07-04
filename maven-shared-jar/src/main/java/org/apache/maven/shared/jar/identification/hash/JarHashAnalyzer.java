package org.apache.maven.shared.jar.identification.hash;

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

import org.apache.maven.shared.jar.JarAnalyzer;

/**
 * Classes that can calculate various hash signatures for a JAR file to later uniquely identify them.
 */
public interface JarHashAnalyzer
{
    /**
     * Compute the hash for the JAR. The hashcode will then be cached in the JAR data class for later use.
     *
     * @param jarAnalyzer the JAR analyzer to use to obtain the entries to hash
     * @return the hash, or null if not able to be computed due to an exception.
     */
    String computeHash( JarAnalyzer jarAnalyzer );
}
