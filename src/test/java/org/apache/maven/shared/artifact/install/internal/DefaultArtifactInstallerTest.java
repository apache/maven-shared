package org.apache.maven.shared.artifact.install.internal;

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
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.install.ArtifactInstaller;
import org.apache.maven.shared.artifact.install.ArtifactInstallerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Check the parameter contracts which have been made based on the interface {@link ArtifactInstaller}.
 * 
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmabaise@apache.org</a>
 */
public class DefaultArtifactInstallerTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void installShouldReturnIllegalArgumentExceptionForFirstParameterWithNull()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter request is not allowed to be null." );
        dai.install( null, Collections.<Artifact>emptyList() );
    }

    @Test
    public void installShouldReturnIllegalArgumentExceptionForSecondParameterWithNull()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter mavenArtifacts is not allowed to be null." );
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );
        dai.install( pbr, null );
    }

    @Test
    public void installShouldReturnIllegalArgumentExceptionForSecondParameterWithEmpty()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The collection mavenArtifacts is not allowed to be empty." );
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );
        dai.install( pbr, Collections.<Artifact>emptyList() );
    }

    @Test
    public void installWith3ParametersShouldReturnIllegalArgumentExceptionForFirstParameterWithNull()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter request is not allowed to be null." );
        File localRepository = mock( File.class );
        dai.install( null, localRepository, Collections.<Artifact>emptyList() );
    }

    @Test
    public void installWith3ParametersShouldReturnIllegalArgumentExceptionForSecondParameterWithNull()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter localRepository is not allowed to be null." );

        List<Artifact> singleEntryList = Collections.singletonList( mock( Artifact.class ) );
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );
        dai.install( pbr, null, singleEntryList );
    }

    @Test
    public void installWith3ParametersShouldReturnIllegalArgumentExceptionForSecondParameterNotADirectory()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter localRepository must be a directory." );
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );
        List<Artifact> singleEntryList = Collections.singletonList( mock( Artifact.class ) );

        File localRepository = mock( File.class );
        when( localRepository.isDirectory() ).thenReturn( false );
        dai.install( pbr, localRepository, singleEntryList );
    }

    @Test
    public void installWith3ParametersShouldReturnIllegalArgumentExceptionForThirdParameterWithNull()
        throws ArtifactInstallerException
    {
        DefaultArtifactInstaller dai = new DefaultArtifactInstaller();

        thrown.expect( IllegalArgumentException.class );
        thrown.expectMessage( "The parameter mavenArtifacts is not allowed to be null." );
        ProjectBuildingRequest pbr = mock( ProjectBuildingRequest.class );
        File localRepository = mock( File.class );
        dai.install( pbr, localRepository, null );
    }

}
