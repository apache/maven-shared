package org.apache.maven.shared.app.company;

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

import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.app.configuration.CompanyPom;
import org.codehaus.plexus.PlexusTestCase;

import java.io.IOException;

public class CompanyPomHandlerTest
    extends PlexusTestCase
{
    private CompanyPomHandler companyPomHandler;

    private ArtifactRepository localRepository;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        this.companyPomHandler = (CompanyPomHandler) lookup( CompanyPomHandler.ROLE );

        ArtifactRepositoryFactory factory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.ROLE );

        localRepository = factory.createDeploymentArtifactRepository( "local", getTestFile(
            "target/test-repository" ).toURL().toString(), new DefaultRepositoryLayout(), false );
    }

    public void testSaveVersionIncrement()
        throws IOException, ArtifactInstallationException
    {
        Model model = new Model();
        model.setGroupId( "org.apache.maven.test" );
        model.setArtifactId( "test-artifact" );
        model.setVersion( "1" );

        companyPomHandler.save( model, localRepository );

        assertEquals( "check model version is incremented", "2", model.getVersion() );
    }

    public void testSaveVersionNew()
        throws IOException, ArtifactInstallationException
    {
        Model model = new Model();
        model.setGroupId( "org.apache.maven.test" );
        model.setArtifactId( "test-artifact" );

        companyPomHandler.save( model, localRepository );

        assertEquals( "check model version is incremented", "1", model.getVersion() );
    }

    public void testSaveVersionIncrementWithOtherVersionParts()
        throws IOException, ArtifactInstallationException
    {
        Model model = new Model();
        model.setGroupId( "org.apache.maven.test" );
        model.setArtifactId( "test-artifact" );
        model.setVersion( "2.0.1-alpha-2" );

        companyPomHandler.save( model, localRepository );

        assertEquals( "check model version is incremented", "3", model.getVersion() );
    }

    public void testGetDifferentModel()
        throws ArtifactMetadataRetrievalException, ProjectBuildingException, ArtifactInstallationException, IOException
    {
        Model model = new Model();
        model.setGroupId( "org.apache.maven.test" );
        model.setArtifactId( "test-artifact" );
        model.setVersion( "2.0.1-alpha-2" );

        companyPomHandler.save( model, localRepository );

        CompanyPom pom = new CompanyPom();
        pom.setGroupId( "blah" );
        pom.setArtifactId( "blah" );

        assertNull( companyPomHandler.getCompanyPomModel( pom, localRepository ) );
    }
}
