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

import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.apache.maven.shared.jar.identification.JarIdentification;
import org.apache.maven.shared.jar.identification.JarIdentificationExposer;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.jar.JarEntry;


/**
 * Exposer that examines a JAR file for any embedded Maven metadata for identification.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="embeddedMavenModel"
 */
public class EmbeddedMavenModelExposer
    extends AbstractLogEnabled
    implements JarIdentificationExposer
{
    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        List entries = jarAnalyzer.getMavenPomEntries();
        if ( entries.isEmpty() )
        {
            return;
        }

        if ( entries.size() > 1 )
        {
            getLogger().warn(
                "More than one Maven model entry was found in the JAR, using only the first of: " + entries );
        }

        JarEntry pom = (JarEntry) entries.get( 0 );
        MavenXpp3Reader pomreader = new MavenXpp3Reader();
        InputStream is = null;
        try
        {
            is = jarAnalyzer.getEntryInputStream( pom );
            InputStreamReader isreader = new InputStreamReader( is );
            Model model = pomreader.read( isreader );

            identification.addAndSetGroupId( model.getGroupId() );
            identification.addAndSetArtifactId( model.getArtifactId() );
            identification.addAndSetVersion( model.getVersion() );
            identification.addAndSetName( model.getName() );

            // TODO: suboptimal - we are reproducing Maven's built in default
            if ( model.getName() == null )
            {
                identification.addAndSetName( model.getArtifactId() );
            }

            Organization org = model.getOrganization();
            if ( org != null )
            {
                identification.addAndSetVendor( org.getName() );
            }
        }
        catch ( IOException e )
        {
            getLogger().error( "Unable to read model " + pom.getName() + " in " + jarAnalyzer.getFile() + ".", e );
        }
        catch ( XmlPullParserException e )
        {
            getLogger().error( "Unable to parse model " + pom.getName() + " in " + jarAnalyzer.getFile() + ".", e );
        }
        finally
        {
            IOUtil.close( is );
        }
    }
}
