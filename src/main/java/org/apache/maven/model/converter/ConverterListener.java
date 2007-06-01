package org.apache.maven.model.converter;

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

public interface ConverterListener
{
    void debug( String message );

    void debug( String message, Throwable throwable );

    void info( String message );

    void info( String message, Throwable throwable );

    void warn( String message );

    void warn( String message, Throwable throwable );

    void error( String message );

    void error( String message, Throwable throwable );

    void addDependencyEvent( String groupId, String artifactId, String version );

    void addPluginEvent( String groupId, String artifactId );

    void relocatePluginEvent( String oldGroupId, String oldArtifactId, String newGroupId, String newArtifactId );

    void removePluginEvent( String groupId, String artifactId );

    void addReportEvent( String groupId, String artifactId );

    void relocateReportEvent( String oldGroupId, String oldArtifactId, String newGroupId, String newArtifactId );

    void removeReportEvent( String groupId, String artifactId );

    void savePomEvent( File pomFileFile, boolean alreadyExist );
}
