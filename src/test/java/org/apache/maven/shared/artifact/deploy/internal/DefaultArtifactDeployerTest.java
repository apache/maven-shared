package org.apache.maven.shared.artifact.deploy.internal;

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

import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.deploy.ArtifactDeployerException;
import org.apache.maven.shared.artifact.deploy.internal.DefaultArtifactDeployer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Check the parameter contracts which have been made based on the interface {@link ArtifactDeployer}.
 * 
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmabaise@apache.org</a>
 */
public class DefaultArtifactDeployerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void deployShouldReturnIllegalArgumentExceptionForFirstParameterWithNull()
        throws ArtifactDeployerException
    {
        DefaultArtifactDeployer dap = new DefaultArtifactDeployer();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter request is not allowed to be null." );
        dap.deploy( null, Collections.<Artifact>emptyList() );
    }

    @Test
    public void deployShouldReturnIllegalArgumentExceptionForSecondParameterWithNull()
        throws ArtifactDeployerException
    {
        DefaultArtifactDeployer dap = new DefaultArtifactDeployer();
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter mavenArtifacts is not allowed to be null." );
        dap.deploy( pbr, null );
    }

    @Test
    public void deployShouldReturnIllegalArgumentExceptionForSecondParameterWithEmpty()
        throws ArtifactDeployerException
    {
        DefaultArtifactDeployer dap = new DefaultArtifactDeployer();
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The collection mavenArtifacts is not allowed to be empty." );
        dap.deploy( pbr, Collections.<Artifact>emptyList() );
    }


    @Test
    public void deploy3ParametersShouldReturnIllegalArgumentExceptionForFirstParameterWithNull()
        throws ArtifactDeployerException
    {
        DefaultArtifactDeployer dap = new DefaultArtifactDeployer();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter request is not allowed to be null." );
        dap.deploy( null, null, Collections.<Artifact>emptyList() );
    }

    @Test
    public void deploy3ParametersShouldReturnIllegalArgumentExceptionForSecondParameterWithNull()
        throws ArtifactDeployerException
    {
        DefaultArtifactDeployer dap = new DefaultArtifactDeployer();
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter mavenArtifacts is not allowed to be null." );
        dap.deploy( pbr, null, null );
    }

    @Test
    public void deploy3ParametersShouldReturnIllegalArgumentExceptionForSecondParameterWithEmpty()
        throws ArtifactDeployerException
    {
        DefaultArtifactDeployer dap = new DefaultArtifactDeployer();
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The collection mavenArtifacts is not allowed to be empty." );
        dap.deploy( pbr, null, Collections.<Artifact>emptyList() );
    }

}
