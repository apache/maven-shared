package org.apache.maven.shared.project.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

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

public class ScmUtilsTest
{

    @Test
    public void resolveScmConnection()
    {
        MavenProject project = new MavenProject();
        assertEquals( "", ScmUtils.resolveScmConnection( project ) );
    }

    @Test
    public void resolveScmDeveloperConnection()
    {
        MavenProject project = new MavenProject();
        assertEquals( "", ScmUtils.resolveScmDeveloperConnection( project ) );
    }

    @Test
    public void resolveScmConnectionByParent() throws IOException
    {
        MavenProject parent = mock( MavenProject.class );
        Model parentModel = mock( Model.class );
        when( parentModel.getModules() ).thenReturn( Collections.singletonList( "module" ) );
        when( parent.getModel() ).thenReturn( parentModel );
        File parentBasedir = File.createTempFile( "tmpBasedir", null );
        when( parent.getBasedir() ).thenReturn( parentBasedir );

        MavenProject project = mock( MavenProject.class );
        when( project.hasParent() ).thenReturn( true );
        when( project.getParent() ).thenReturn( parent );
        when( project.getFile() ).thenReturn( new File( parentBasedir, "module" ) );
        Scm scm = mock( Scm.class );
        when( scm.getConnection() ).thenReturn( "parent" );
        when( project.getScm() ).thenReturn( scm );

        Model projectModel = mock( Model.class );

        when( project.getModel() ).thenReturn( projectModel );

        assertEquals( "parent/module", ScmUtils.resolveScmConnection( project ) );
    }

    @Test
    public void resolveScmDeveloperConnectionByParent() throws IOException
    {
        MavenProject parent = mock( MavenProject.class );
        Model parentModel = mock( Model.class );
        when( parentModel.getModules() ).thenReturn( Collections.singletonList( "module" ) );
        when( parent.getModel() ).thenReturn( parentModel );
        File parentBasedir = File.createTempFile( "tmpBasedir", null );
        when( parent.getBasedir() ).thenReturn( parentBasedir );

        MavenProject project = mock( MavenProject.class );
        when( project.hasParent() ).thenReturn( true );
        when( project.getParent() ).thenReturn( parent );
        when( project.getFile() ).thenReturn( new File( parentBasedir, "module" ) );
        Scm scm = mock( Scm.class );
        when( scm.getDeveloperConnection() ).thenReturn( "parent" );
        when( project.getScm() ).thenReturn( scm );

        Model projectModel = mock( Model.class );

        when( project.getModel() ).thenReturn( projectModel );

        assertEquals( "parent/module", ScmUtils.resolveScmDeveloperConnection( project ) );
    }

    @Test
    public void getScmConnectionByModel()
    {
        Model model = new Model();
        assertNull( ScmUtils.getScmConnection( model ) );
        model.setScm( new Scm() );
        assertNull( ScmUtils.getScmConnection( model ) );
        String connection = "scmConnection";
        model.getScm().setConnection( connection );
        assertEquals( connection, ScmUtils.getScmConnection( model ) );
    }

    @Test
    public void getScmDeveloperConnectionByModel()
    {
        Model model = new Model();
        assertNull( ScmUtils.getScmDeveloperConnection( model ) );
        model.setScm( new Scm() );
        assertNull( ScmUtils.getScmDeveloperConnection( model ) );
        String connection = "scmConnection";
        model.getScm().setDeveloperConnection( connection );
        assertEquals( connection, ScmUtils.getScmDeveloperConnection( model ) );
    }

    @Test
    public void getScmConnectionByMavenProject()
    {
        MavenProject project = new MavenProject();
        assertNull( ScmUtils.getScmConnection( project ) );
        project.setScm( new Scm() );
        assertNull( ScmUtils.getScmConnection( project ) );
        String connection = "scmConnection";
        project.getScm().setConnection( connection );
        assertEquals( connection, ScmUtils.getScmConnection( project ) );
    }

    @Test
    public void getScmDeveloperConnectionByMavenProject()
    {
        MavenProject project = new MavenProject();
        assertNull( ScmUtils.getScmDeveloperConnection( project ) );
        project.setScm( new Scm() );
        assertNull( ScmUtils.getScmDeveloperConnection( project ) );
        String connection = "scmConnection";
        project.getScm().setDeveloperConnection( connection );
        assertEquals( connection, ScmUtils.getScmDeveloperConnection( project ) );
    }

}
