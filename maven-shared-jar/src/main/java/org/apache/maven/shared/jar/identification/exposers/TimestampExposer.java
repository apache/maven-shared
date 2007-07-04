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

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.apache.maven.shared.jar.identification.JarIdentification;
import org.apache.maven.shared.jar.identification.JarIdentificationExposer;
import org.codehaus.plexus.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;

/**
 * Exposer that examines a a JAR and uses the most recent timestamp as a potential version.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="timestamp"
 */
public class TimestampExposer
    implements JarIdentificationExposer
{
    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        List entries = jarAnalyzer.getEntries();
        SimpleDateFormat tsformat = new SimpleDateFormat( "yyyyMMdd", Locale.US ); //$NON-NLS-1$
        Bag timestamps = new HashBag();
        Iterator it = entries.iterator();
        while ( it.hasNext() )
        {
            JarEntry entry = (JarEntry) it.next();
            long time = entry.getTime();
            String timestamp = tsformat.format( new Date( time ) );
            timestamps.add( timestamp );
        }

        it = timestamps.iterator();
        String ts = "";
        int tsmax = 0;
        while ( it.hasNext() )
        {
            String timestamp = (String) it.next();
            int count = timestamps.getCount( timestamp );
            if ( count > tsmax )
            {
                ts = timestamp;
                tsmax = count;
            }
        }

        if ( StringUtils.isNotEmpty( ts ) )
        {
            identification.addVersion( ts );
        }
    }
}
