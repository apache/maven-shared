package org.apache.maven.shared.jar.taxon.exposers;

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

import org.apache.maven.shared.jar.taxon.AbstractJarTaxonExposer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Jar Taxon Exposer - Static Main Class Output
 *
 * @plexus.component role="org.apache.maven.shared.jar.taxon.JarTaxonExposer" role-hint="staticMainOutput"
 */
public class StaticMainOutputExposer
    extends AbstractJarTaxonExposer
{
    public String getExposerName()
    {
        return "Static Main Output";
    }

    public boolean isAuthoritative()
    {
        return false;
    }

    public void expose()
    {
        List staticMains = findStaticMainVersions();
        if ( !staticMains.isEmpty() )
        {
            Iterator itvers = staticMains.iterator();
            while ( itvers.hasNext() )
            {
                String ver = (String) itvers.next();
                addVersion( ver );
            }
        }
    }

    private List findStaticMainVersions()
    {
        // TODO: Execute the static main methods of classes with 'Version' in their name.
        return Collections.EMPTY_LIST;
    }
}
