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

import org.apache.maven.shared.jar.classes.JarClasses;
import org.apache.maven.shared.jar.classes.JarClassesAnalyzer;
import org.apache.maven.shared.jar.taxon.AbstractJarTaxonExposer;

import java.util.Iterator;


/**
 * Jar Taxon Exposer for the information from JarClasses.
 *
 * @plexus.component role="org.apache.maven.shared.jar.taxon.JarTaxonExposer" role-hint="jarClasses"
 */
public class JarClassesExposer
    extends AbstractJarTaxonExposer
{
    public String getExposerName()
    {
        return "Jar Classes";
    }

    public boolean isAuthoritative()
    {
        return false;
    }

    public void expose()
    {
        JarClasses jarclasses = getJar().getClasses();

        if ( jarclasses == null )
        {
            JarClassesAnalyzer analyzer = new JarClassesAnalyzer();
            analyzer.analyze( getJar() );
            jarclasses = getJar().getClasses();
        }

        if ( jarclasses == null )
        {
            getLogger().error( "Unable to process null JarClasses on " + getJar().getFilename() );
            return;
        }

        Iterator it = jarclasses.getPackages().iterator();
        while ( it.hasNext() )
        {
            String packagename = (String) it.next();
            addGroupId( packagename );
        }
    }
}
