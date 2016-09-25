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

import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Exposer that examines a JAR's manifest to derive Maven metadata.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="manifest"
 */
public class ManifestExposer
    implements JarIdentificationExposer
{
    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        Manifest manifest = jarAnalyzer.getJarData().getManifest();
        if ( manifest != null )
        {
            addManifestAttributeValues( manifest.getMainAttributes(), identification );

            for ( Attributes attribs : manifest.getEntries().values() )
            {
                addManifestAttributeValues( attribs, identification );
            }
        }
    }

    private void addManifestAttributeValues( Attributes attribs, JarIdentification identification )
    {
        identification.addName( attribs.getValue( Attributes.Name.IMPLEMENTATION_TITLE ) );
        identification.addVersion( attribs.getValue( Attributes.Name.IMPLEMENTATION_VERSION ) );
        identification.addVendor( attribs.getValue( Attributes.Name.IMPLEMENTATION_VENDOR ) );

        identification.addName( attribs.getValue( Attributes.Name.SPECIFICATION_TITLE ) );
        identification.addVersion( attribs.getValue( Attributes.Name.SPECIFICATION_VERSION ) );
        identification.addVendor( attribs.getValue( Attributes.Name.SPECIFICATION_VENDOR ) );

        identification.addGroupId( attribs.getValue( Attributes.Name.EXTENSION_NAME ) );
    }
}
