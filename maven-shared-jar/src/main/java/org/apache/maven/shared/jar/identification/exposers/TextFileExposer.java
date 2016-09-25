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
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.maven.shared.utils.io.IOUtil;
import org.apache.maven.shared.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;


/**
 * Exposer that examines a a JAR for files that contain the text <code>version</code> (case-insensitive) and
 * adds the contents as potential version(s).
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="textFile"
 */
public class TextFileExposer
    extends AbstractLogEnabled
    implements JarIdentificationExposer
{
    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        List<String> textFiles = findTextFileVersions( jarAnalyzer );
        for ( String ver : textFiles )
        {
            identification.addVersion( ver );
        }
    }

    private List<String> findTextFileVersions( JarAnalyzer jarAnalyzer )
    {
        List<String> textVersions = new ArrayList<String>();
        List<JarEntry> hits = jarAnalyzer.getVersionEntries();

        for ( JarEntry entry : hits )
        {
            // skip this entry if it's a class file.
            if ( !entry.getName().endsWith( ".class" ) ) //$NON-NLS-1$
            {
                getLogger().debug( "Version Hit: " + entry.getName() );
                InputStream is = null;
                try
                {
                    is = jarAnalyzer.getEntryInputStream( entry );
                    BufferedReader br = new BufferedReader( new InputStreamReader( is ) );

                    String line = br.readLine();
                    // TODO: check for key=value pair.
                    // TODO: maybe even for groupId entries.

                    getLogger().debug( line );
                    if ( StringUtils.isNotEmpty( line ) )
                    {
                        textVersions.add( line );
                    }
                }
                catch ( IOException e )
                {
                    getLogger().warn( "Unable to read line from " + entry.getName(), e );
                }
                finally
                {
                    IOUtil.close( is );
                }
            }
        }
        return textVersions;
    }
}
