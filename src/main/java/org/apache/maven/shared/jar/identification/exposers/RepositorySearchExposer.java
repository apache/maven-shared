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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.jar.identification.AbstractJarIdentificationExposer;
import org.apache.maven.shared.jar.identification.RepositoryHashSearch;
import org.codehaus.plexus.digest.Digester;
import org.codehaus.plexus.digest.StreamingDigester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JarAnalyzer Taxon Exposer for the hashcode hits within a repository.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.JarIdentificationExposer" role-hint="repositorySearch"
 */
public class RepositorySearchExposer
    extends AbstractJarIdentificationExposer
{
    /**
     * @plexus.requirement role-hint="sha1"
     */
    private Digester digester;

    /**
     * @plexus.requirement role-hint="sha1"
     */
    private StreamingDigester streamingDigester;

    /**
     * @plexus.requirement
     */
    private RepositoryHashSearch repositoryHashSearch;

    public String getExposerName()
    {
        return "Repository Hashcode Hit";
    }

    public boolean isAuthoritative()
    {
        return true;
    }

    public RepositoryHashSearch getRepositoryHashSearch()
    {
        return repositoryHashSearch;
    }

    public void setRepositoryHashSearch( RepositoryHashSearch repo )
    {
        this.repositoryHashSearch = repo;
    }

    public void expose()
    {
        String hash = getJar().computeFileHash( digester );
        String bytecodehash = getJar().computeBytecodeHash( streamingDigester );
        List repohits = new ArrayList();
        repohits.addAll( repositoryHashSearch.searchFileHash( hash ) );
        repohits.addAll( repositoryHashSearch.searchBytecodeHash( bytecodehash ) );
        if ( !repohits.isEmpty() )
        {
            // Found hits in the repository.
            Iterator it = repohits.iterator();
            while ( it.hasNext() )
            {
                Artifact artifact = (Artifact) it.next();
                addGroupId( artifact.getGroupId() );
                addArtifactId( artifact.getArtifactId() );
                addVersion( artifact.getVersion() );
            }
        }
    }
}
