package org.apache.maven.shared.jar.identification.hash;

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
import org.apache.maven.shared.jar.JarData;
import org.codehaus.plexus.digest.Digester;
import org.codehaus.plexus.digest.DigesterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * Analyzer that calculates the hash code for the entire file. Can be used to detect an exact copy of the file.
 * <p/>
 * If you are not using Plexus, you must call {@link #setDigester(org.codehaus.plexus.digest.Digester)} before use
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.hash.JarHashAnalyzer" role-hint="file"
 */
public class JarFileHashAnalyzer
    extends AbstractLogEnabled
    implements JarHashAnalyzer
{
    /**
     * The digester to use for computing the hash. Under Plexus, the default is SHA-1.
     *
     * @plexus.requirement role-hint="sha1"
     */
    private Digester digester;

    public String computeHash( JarAnalyzer jarAnalyzer )
    {
        JarData jarData = jarAnalyzer.getJarData();

        String result = jarData.getFileHash();
        if ( result == null )
        {
            try
            {
                result = digester.calc( jarData.getFile() );
                jarData.setFileHash( result );
            }
            catch ( DigesterException e )
            {
                getLogger().warn( "Unable to calculate the hashcode.", e );
            }
        }
        return result;
    }

    public void setDigester( Digester digester )
    {
        this.digester = digester;
    }
}
