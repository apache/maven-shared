package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author John Casey
 * @author Dennis Lundberg
 * @since 1.0
 * @version $Id$
 */
public class FilteringUtilsTest
    extends PlexusTestCase
{
    public void testEscapeWindowsPathStartingWithDrive()
    {
        assertEquals( "C:\\\\Users\\\\Administrator", FilteringUtils.escapeWindowsPath( "C:\\Users\\Administrator" ) );
    }

    public void testEscapeWindowsPathMissingDriveLetter()
    {
        assertEquals( ":\\Users\\Administrator", FilteringUtils.escapeWindowsPath( ":\\Users\\Administrator" ) );
    }

    public void testEscapeWindowsPathInvalidDriveLetter()
    {
        assertEquals( "4:\\Users\\Administrator", FilteringUtils.escapeWindowsPath( "4:\\Users\\Administrator" ) );
    }

    // This doesn't work, see MSHARED-121
    /*
    public void testEscapeWindowsPathStartingWithDrivelessAbsolutePath()
    {
        assertEquals( "\\\\Users\\\\Administrator", FilteringUtils.escapeWindowsPath( "\\Users\\Administrator" ) );
    }
    */

    // This doesn't work, see MSHARED-121
    /*
    public void testEscapeWindowsPathStartingWithExpression()
    {
        assertEquals( "${pathExpr}\\\\Documents", FilteringUtils.escapeWindowsPath( "${pathExpr}\\Documents" ) );
    }
    */

    // MSHARED-179
    public void testEscapeWindowsPathNotAtBeginning()
        throws Exception
    {
        assertEquals( "jdbc:derby:C:\\\\Users\\\\Administrator/test;create=true", FilteringUtils.escapeWindowsPath( "jdbc:derby:C:\\Users\\Administrator/test;create=true" ) );
    }
}
