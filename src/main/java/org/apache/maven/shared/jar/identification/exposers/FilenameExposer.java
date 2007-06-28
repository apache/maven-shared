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

import org.apache.maven.shared.jar.identification.AbstractJarIdentificationExposer;
import org.codehaus.plexus.util.FileUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * JarAnalyzer Taxon Exposer based on Filename patterns.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="filename"
 */
public class FilenameExposer
    extends AbstractJarIdentificationExposer
{
    public String getExposerName()
    {
        return "Filename";
    }

    public boolean isAuthoritative()
    {
        return false;
    }

    public void expose()
    {
        String fname = FileUtils.removeExtension( getJar().getFile().getName() );
        Pattern verSplit = Pattern.compile( "-[0-9]" ); //$NON-NLS-1$
        Matcher mat = verSplit.matcher( fname );
        if ( mat.find() )
        {
            String prefix = fname.substring( 0, mat.start() );
            addArtifactId( prefix );
            addName( prefix );
            addVersion( fname.substring( mat.end() - 1 ) );
        }
        else
        {
            addArtifactId( fname );
            addName( fname );
        }
    }
}
