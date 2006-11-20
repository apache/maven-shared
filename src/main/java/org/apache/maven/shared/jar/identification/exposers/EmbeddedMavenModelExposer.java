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

import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.jar.identification.AbstractJarIdentificationExposer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.jar.JarEntry;


/**
 * JarAnalyzer Taxon Exposer for the Embedded Maven Model.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="embeddedMavenModel"
 */
public class EmbeddedMavenModelExposer
    extends AbstractJarIdentificationExposer
{
    public void expose()
    {
        List entries = getJar().getNameRegexEntryList( "META-INF/maven/.*/pom\\.xml$" ); //$NON-NLS-1$
        if ( entries.isEmpty() )
        {
            return;
        }

        JarEntry pom = (JarEntry) entries.get( 0 );
        MavenXpp3Reader pomreader = new MavenXpp3Reader();
        try
        {
            InputStream istream = getJar().getEntryInputStream( pom );
            InputStreamReader isreader = new InputStreamReader( istream );
            Model model = pomreader.read( isreader );

            addGroupId( model.getGroupId() );
            addArtifactId( model.getArtifactId() );
            addVersion( model.getVersion() );
            addName( model.getName() );

            Organization org = model.getOrganization();
            if ( org != null )
            {
                addVendor( org.getName() );
            }
        }
        catch ( IOException e )
        {
            getLogger().error( "Unable to read model " + pom.getName() + " in " + getJar().getFilename() + ".", e );
        }
        catch ( XmlPullParserException e )
        {
            getLogger().error( "Unable to parse model " + pom.getName() + " in " + getJar().getFilename() + ".", e );
        }
    }

    public String getExposerName()
    {
        return "Embedded Model";
    }

    public boolean isAuthoritative()
    {
        return true;
    }
}
