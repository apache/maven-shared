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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.jar.JarAnalyzer;
import org.apache.maven.shared.jar.identification.JarIdentification;
import org.apache.maven.shared.jar.identification.JarIdentificationExposer;
import org.apache.maven.shared.jar.identification.hash.JarHashAnalyzer;
import org.apache.maven.shared.jar.identification.repository.RepositoryHashSearch;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Exposer that examines a Maven repository for identical files to the JAR being analyzed. It will look for both
 * identical files, and files with identical classes.
 * <p/>
 * Note: if not using Plexus, you must call the following methods to be able to expose any data from the class:
 * {@link #setBytecodeHashAnalyzer(org.apache.maven.shared.jar.identification.hash.JarHashAnalyzer)},
 * {@link #setFileHashAnalyzer(org.apache.maven.shared.jar.identification.hash.JarHashAnalyzer)},
 * {@link #setRepositoryHashSearch(org.apache.maven.shared.jar.identification.repository.RepositoryHashSearch)}
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer"
 *                   role-hint="repositorySearch"
 */
public class RepositorySearchExposer
    extends AbstractLogEnabled
    implements JarIdentificationExposer
{
    /**
     * The repository searcher to use.
     *
     * @plexus.requirement
     * @todo this currently only provides for the 'empty' repository search, which isn't very useful
     */
    private RepositoryHashSearch repositoryHashSearch;

    /**
     * The hash analyzer for the entire file.
     *
     * @plexus.requirement role-hint="file"
     */
    private JarHashAnalyzer fileHashAnalyzer;

    /**
     * The hash analyzer for the file's bytecode.
     *
     * @plexus.requirement role-hint="bytecode"
     */
    private JarHashAnalyzer bytecodeHashAnalyzer;

    public void expose( JarIdentification identification, JarAnalyzer jarAnalyzer )
    {
        List repohits = new ArrayList();

        String hash = fileHashAnalyzer.computeHash( jarAnalyzer );
        if ( hash != null )
        {
            repohits.addAll( repositoryHashSearch.searchFileHash( hash ) );
        }

        String bytecodehash = bytecodeHashAnalyzer.computeHash( jarAnalyzer );
        if ( bytecodehash != null )
        {
            repohits.addAll( repositoryHashSearch.searchBytecodeHash( bytecodehash ) );
        }

        if ( !repohits.isEmpty() )
        {
            // Found hits in the repository.
            Iterator it = repohits.iterator();
            while ( it.hasNext() )
            {
                Artifact artifact = (Artifact) it.next();
                identification.addAndSetGroupId( artifact.getGroupId() );
                identification.addAndSetArtifactId( artifact.getArtifactId() );
                identification.addAndSetVersion( artifact.getVersion() );
            }
        }
    }

    public void setRepositoryHashSearch( RepositoryHashSearch repo )
    {
        this.repositoryHashSearch = repo;
    }

    public void setFileHashAnalyzer( JarHashAnalyzer fileHashAnalyzer )
    {
        this.fileHashAnalyzer = fileHashAnalyzer;
    }

    public void setBytecodeHashAnalyzer( JarHashAnalyzer bytecodeHashAnalyzer )
    {
        this.bytecodeHashAnalyzer = bytecodeHashAnalyzer;
    }
}
