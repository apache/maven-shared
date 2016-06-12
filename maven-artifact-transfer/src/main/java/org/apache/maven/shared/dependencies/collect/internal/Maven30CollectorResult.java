package org.apache.maven.shared.dependencies.collect.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.dependencies.collect.CollectorResult;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * CollectorResult wrapper around {@link CollectResult} 
 * 
 * @author Robert Scholte
 *
 */
public class Maven30CollectorResult implements CollectorResult
{
    private final CollectResult collectResult;
    
    /**
     * @param collectResult {@link CollectorResult}
     */
    public Maven30CollectorResult( CollectResult collectResult )
    {
        this.collectResult = collectResult;
    }

    @Override
    public List<ArtifactRepository> getRemoteRepositories()
    {
        final Set<RemoteRepository> aetherRepositories = new HashSet<RemoteRepository>();
        
        DependencyVisitor visitor = new DependencyVisitor()
        {
            @Override
            public boolean visitEnter( DependencyNode node )
            {
                aetherRepositories.addAll( node.getRepositories() );
                return true;
            }
            
            @Override
            public boolean visitLeave( DependencyNode node )
            {
                return true;
            }
        };
        
        collectResult.getRoot().accept( visitor );
        
        List<ArtifactRepository> mavenRepositories = new ArrayList<ArtifactRepository>( aetherRepositories.size() );
        
        for ( RemoteRepository aetherRepository : aetherRepositories )
        {
            mavenRepositories.add( new Maven30ArtifactRepositoryAdapter( aetherRepository ) );
        }
        
        return mavenRepositories;
    }

}
