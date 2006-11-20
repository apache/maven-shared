package org.apache.maven.shared.jar.identification.exposers;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.shared.jar.identification.AbstractJarIdentificationExposer;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;


/**
 * JarAnalyzer Taxon Exposer based on Text File contents.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="textFile"
 */
public class TextFileExposer
    extends AbstractJarIdentificationExposer
{
    public String getExposerName()
    {
        return "Text File";
    }

    public boolean isAuthoritative()
    {
        return false;
    }

    public void expose()
    {
        List textFiles = findTextFileVersions();
        if ( !textFiles.isEmpty() )
        {
            Iterator ithits = textFiles.iterator();
            while ( ithits.hasNext() )
            {
                String ver = (String) ithits.next();
                addVersion( ver );
            }
        }
    }

    private List findTextFileVersions()
    {
        List textVersions = new ArrayList();
        List hits = getJar().getNameRegexEntryList( "[Vv][Ee][Rr][Ss][Ii][Oo][Nn]" ); //$NON-NLS-1$

        int hitcount = 0;

        Iterator it = hits.iterator();
        while ( it.hasNext() )
        {
            JarEntry entry = (JarEntry) it.next();

            if ( entry.getName().endsWith( ".class" ) ) //$NON-NLS-1$
            {
                // skip this entry. as it's a class file.
                continue;
            }

            getLogger().debug( "Version Hit: " + entry.getName() );
            InputStream is = getJar().getEntryInputStream( entry );
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            try
            {
                String line = br.readLine();
                // TODO: check for key=value pair.
                // TODO: maybe even for groupId entries.

                getLogger().debug( line );
                if ( StringUtils.isNotEmpty( line ) )
                {
                    textVersions.add( line );
                }
                hitcount++;
            }
            catch ( IOException e )
            {
                getLogger().warn( "Unable to read line from " + entry.getName(), e );
            }
        }

        return textVersions;
    }
}
