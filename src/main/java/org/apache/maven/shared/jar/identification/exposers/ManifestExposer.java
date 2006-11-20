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

import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


/**
 * JarAnalyzer Taxon Exposer for the Manifest.mf contents.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="manifest"
 */
public class ManifestExposer
    extends AbstractJarIdentificationExposer
{
    public String getExposerName()
    {
        return "Manifest.mf";
    }

    public boolean isAuthoritative()
    {
        return false;
    }

    public void expose()
    {
        Manifest manifest = getJar().getManifest();
        if ( manifest != null )
        {
            addManifestAttributeValues( manifest.getMainAttributes() );

            Map entries = manifest.getEntries();
            Iterator itentries = entries.entrySet().iterator();
            while ( itentries.hasNext() )
            {
                Map.Entry entry = (Map.Entry) itentries.next();
                Attributes attribs = (Attributes) entry.getValue();

                addManifestAttributeValues( attribs );
            }
        }
    }

    private void addManifestAttributeValues( Attributes attribs )
    {
        addName( attribs.getValue( Attributes.Name.IMPLEMENTATION_TITLE ) );
        addVersion( attribs.getValue( Attributes.Name.IMPLEMENTATION_VERSION ) );
        addVendor( attribs.getValue( Attributes.Name.IMPLEMENTATION_VENDOR ) );

        addName( attribs.getValue( Attributes.Name.SPECIFICATION_TITLE ) );
        addVersion( attribs.getValue( Attributes.Name.SPECIFICATION_VERSION ) );
        addVendor( attribs.getValue( Attributes.Name.SPECIFICATION_VENDOR ) );

        addGroupId( attribs.getValue( Attributes.Name.EXTENSION_NAME ) );
    }
}
