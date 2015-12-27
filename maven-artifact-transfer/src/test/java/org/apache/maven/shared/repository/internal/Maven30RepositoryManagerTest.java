package org.apache.maven.shared.repository.internal;

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

import java.io.File;

import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.shared.repository.RepositoryManager;
import org.apache.maven.shared.repository.internal.Maven30RepositoryManager;
import org.codehaus.plexus.PlexusTestCase;
import org.sonatype.aether.impl.internal.EnhancedLocalRepositoryManager;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;

public class Maven30RepositoryManagerTest extends PlexusTestCase
{

    private final File localRepo = new File( "target/tests/local-repo" );
    
    private Maven30RepositoryManager repositoryManager;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        repositoryManager = (Maven30RepositoryManager) super.lookup( RepositoryManager.class, "maven3" );
    }
    
    public void testSetLocalRepositoryBasedirSimple() throws Exception
    {
        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
        repositorySession.setLocalRepositoryManager( new SimpleLocalRepositoryManager( localRepo ) );
        buildingRequest.setRepositorySession( repositorySession );

        File basedir = new File( "NEW/LOCAL/REPO" );
        
        ProjectBuildingRequest newBuildingRequest = repositoryManager.setLocalRepositoryBasedir( buildingRequest, basedir );
        
        assertEquals( basedir.getAbsoluteFile(), newBuildingRequest.getRepositorySession().getLocalRepository().getBasedir() );
        
    }

    public void testSetLocalRepositoryBasedirEnhanced() throws Exception
    {
        DefaultProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest();
        MavenRepositorySystemSession repositorySession = new MavenRepositorySystemSession();
        repositorySession.setLocalRepositoryManager( new EnhancedLocalRepositoryManager( localRepo ) );
        buildingRequest.setRepositorySession( repositorySession );

        File basedir = new File( "NEW/LOCAL/REPO" );
        
        ProjectBuildingRequest newBuildingRequest = repositoryManager.setLocalRepositoryBasedir( buildingRequest, basedir );
        
        assertEquals( basedir.getAbsoluteFile(), newBuildingRequest.getRepositorySession().getLocalRepository().getBasedir() );
        
    }

}
