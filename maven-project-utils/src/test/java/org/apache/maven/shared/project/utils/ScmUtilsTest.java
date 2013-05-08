package org.apache.maven.shared.project.utils;

import static org.junit.Assert.*;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

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
        assertEquals( "",  ScmUtils.resolveScmConnection( project ) );
    }

    @Test
    public void resolveScmDeveloperConnection()
    {
        MavenProject project = new MavenProject();
        assertEquals( "",  ScmUtils.resolveScmDeveloperConnection( project ) );
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
