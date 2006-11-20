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

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.apache.maven.shared.jar.identification.AbstractJarIdentificationExposer;
import org.codehaus.plexus.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;


/**
 * JarAnalyzer Taxon Exposer - using Majority Timestamp of classes.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="timestamp"
 */
public class TimestampExposer
    extends AbstractJarIdentificationExposer
{
    public String getExposerName()
    {
        return "Timestamp";
    }

    public boolean isAuthoritative()
    {
        return false;
    }

    public void expose()
    {
        List entries = getJar().getNameRegexEntryList( ".*" ); //$NON-NLS-1$
        SimpleDateFormat tsformat = new SimpleDateFormat( "yyyyMMdd" ); //$NON-NLS-1$
        Bag timestamps = new HashBag();
        Iterator it = entries.iterator();
        while ( it.hasNext() )
        {
            JarEntry entry = (JarEntry) it.next();
            long time = entry.getTime();
            String timestamp = tsformat.format( new Date( time ) );
            timestamps.add( timestamp );
        }

        String ts = "";
        int tsmax = 0;
        it = timestamps.iterator();
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
            addVersion( ts );
        }
    }
}
