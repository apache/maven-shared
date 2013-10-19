package org.apache.maven.shared.project.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

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

public class ProjectUtilsTest
{
    private static final File ROOT_FOLDER = new File( "." );

    @Test
    public void getRootProjectForNull()
    {
        assertNull( ProjectUtils.getRootProject( null ) );
    }

    @Test
    public void getJoinedFolderNullDirectories()
        throws Exception
    {
        assertNull( ProjectUtils.getJoinedFolder( null, null ) );
    }

    @Test
    public void getJoinedFolderNullLeftFolder()
        throws Exception
    {
        assertNull( ProjectUtils.getJoinedFolder( null, ROOT_FOLDER ) );
    }

    @Test
    public void getJoinedFolderNullRightFolder()
        throws Exception
    {
        assertNull( ProjectUtils.getJoinedFolder( ROOT_FOLDER, null ) );
    }

    
    @Test
    public void getJoinedFolderSameDirectory()
        throws Exception
    {
        File folder = ROOT_FOLDER;

        assertEquals( folder, ProjectUtils.getJoinedFolder( folder, folder ) );
    }

    @Test
    public void getJoinedFolderSameFile()
        throws Exception
    {
        File pomFile = new File( "pom.xml" );

        assertEquals( pomFile.getParentFile(), ProjectUtils.getJoinedFolder( pomFile, pomFile ) );
    }

    @Test
    public void getJoinedFolderDeeperLeftFolder()
        throws Exception
    {
        File lhsFolder = new File( ROOT_FOLDER, "src" );
        File rhsFolder = ROOT_FOLDER;

        assertEquals( rhsFolder, ProjectUtils.getJoinedFolder( lhsFolder, rhsFolder ) );
    }

    @Test
    public void getJoinedFolderDeeperRightFolder()
        throws Exception
    {
        File lhsFolder = ROOT_FOLDER;
        File rhsFolder = new File( ROOT_FOLDER, "src" );

        assertEquals( lhsFolder, ProjectUtils.getJoinedFolder( lhsFolder, rhsFolder ) );
    }

    @Test
    public void getJoinedFolderFileAndDeeperLeftFolder()
        throws Exception
    {
        File folder = ROOT_FOLDER;
        File lhsFolder = new File( folder, "src" );
        File rhsFolder = new File( folder, "pom.xml" );

        assertEquals( folder, ProjectUtils.getJoinedFolder( lhsFolder, rhsFolder ) );
    }

    @Test
    public void getJoinedFolderFileAndDeeperRightFolder()
        throws Exception
    {
        File folder = ROOT_FOLDER;
        File lhsFolder = new File( folder, "pom.xml" );
        File rhsFolder = new File( folder, "src" );

        assertEquals( folder, ProjectUtils.getJoinedFolder( lhsFolder, rhsFolder ) );
    }

}
