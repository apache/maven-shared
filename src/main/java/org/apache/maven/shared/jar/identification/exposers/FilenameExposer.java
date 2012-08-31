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
import org.apache.maven.shared.utils.io.FileUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Exposer that examines a JAR file to derive Maven metadata from the pattern of the JAR's filename.
 * Will match the format <i>artifactId</i>-<i>version</i>.jar.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="filename"
 */
public class FilenameExposer
    implements JarIdentificationExposer
{
    private static final Pattern VERSION_PATTERN = Pattern.compile( "-[0-9]" );

    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        String filename = FileUtils.removeExtension( jarAnalyzer.getFile().getName() );
        Matcher mat = VERSION_PATTERN.matcher( filename );
        if ( mat.find() )
        {
            String prefix = filename.substring( 0, mat.start() );
            identification.addArtifactId( prefix );
            identification.addName( prefix );
            identification.addVersion( filename.substring( mat.end() - 1 ) );
        }
        else
        {
            identification.addArtifactId( filename );
            identification.addName( filename );
        }
    }
}
