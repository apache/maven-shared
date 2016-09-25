package org.apache.maven.shared.jar.identification.exposers;

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
import org.apache.maven.shared.jar.identification.JarIdentification;
import org.apache.maven.shared.jar.identification.JarIdentificationExposer;

import java.util.Collections;
import java.util.List;

/**
 * Exposer that examines a a JAR for classes that have <code>Version</code> in the name and calls their
 * <code>main</code> method if it exists to obtain the version.
 *
 * @todo not currently implemented
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer"
 *                   role-hint="staticMainOutput"
 */
public class StaticMainOutputExposer
    implements JarIdentificationExposer
{
    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        List<String> staticMains = findStaticMainVersions();
        for ( String ver : staticMains )
        {
            identification.addVersion( ver );
        }
    }

    private List<String> findStaticMainVersions()
    {
        // TODO: Execute the static main methods of classes with 'Version' in their name.
        return Collections.emptyList();
    }
}
