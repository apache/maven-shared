package org.apache.maven.shared.project.utils;

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

import org.apache.maven.shared.project.runtime.MavenUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Ansi color utils, to enable colors only if Maven version is at least 3.4.
 */
public class AnsiUtils
{
    private static final float MINIMUM_MAVEN_VERSION = 3.4f; // color added in Maven 3.4.0: see MNG-3507

    private AnsiUtils()
    {
    }

    public static void systemInstall()
    {
        AnsiConsole.systemInstall();
        if ( MavenUtils.getMavenVersionAsFloat() < MINIMUM_MAVEN_VERSION )
        {
            // ANSI color support was added in Maven 3.4.0: don't enable color if executing older Maven versions
            Ansi.setEnabled( false );
        }
    }

    public static void systemUninstall()
    {
        AnsiConsole.systemUninstall();
    }
}
