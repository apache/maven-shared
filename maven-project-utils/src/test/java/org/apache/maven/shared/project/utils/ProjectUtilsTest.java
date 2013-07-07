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
    public void getSharedFolderNullDirectories()
        throws Exception
    {
        assertNull( ProjectUtils.getSharedFolder( null, null ) );
    }

    @Test
    public void getSharedFolderNullLeftFolder()
        throws Exception
    {
        assertNull( ProjectUtils.getSharedFolder( null, ROOT_FOLDER ) );
    }

    @Test
    public void getSharedFolderNullRightFolder()
        throws Exception
    {
        assertNull( ProjectUtils.getSharedFolder( ROOT_FOLDER, null ) );
    }

    
    @Test
    public void getSharedFolderSameDirectory()
        throws Exception
    {
        File folder = ROOT_FOLDER;

        assertEquals( folder, ProjectUtils.getSharedFolder( folder, folder ) );
    }

    @Test
    public void getSharedFolderSameFile()
        throws Exception
    {
        File pomFile = new File( "pom.xml" );

        assertEquals( pomFile.getParentFile(), ProjectUtils.getSharedFolder( pomFile, pomFile ) );
    }

    @Test
    public void getSharedFolderDeeperLeftFolder()
        throws Exception
    {
        File lhsFolder = new File( ROOT_FOLDER, "src" );
        File rhsFolder = ROOT_FOLDER;

        assertEquals( rhsFolder, ProjectUtils.getSharedFolder( lhsFolder, rhsFolder ) );
    }

    @Test
    public void getSharedFolderDeeperRightFolder()
        throws Exception
    {
        File lhsFolder = ROOT_FOLDER;
        File rhsFolder = new File( ROOT_FOLDER, "src" );

        assertEquals( lhsFolder, ProjectUtils.getSharedFolder( lhsFolder, rhsFolder ) );
    }

    @Test
    public void getSharedFolderFileAndDeeperLeftFolder()
        throws Exception
    {
        File folder = ROOT_FOLDER;
        File lhsFolder = new File( folder, "src" );
        File rhsFolder = new File( folder, "pom.xml" );

        assertEquals( folder, ProjectUtils.getSharedFolder( lhsFolder, rhsFolder ) );
    }

    @Test
    public void getSharedFolderFileAndDeeperRightFolder()
        throws Exception
    {
        File folder = ROOT_FOLDER;
        File lhsFolder = new File( folder, "pom.xml" );
        File rhsFolder = new File( folder, "src" );

        assertEquals( folder, ProjectUtils.getSharedFolder( lhsFolder, rhsFolder ) );
    }

}
