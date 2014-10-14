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

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * @author Olivier Lamy
 * @since 1.0-beta-3
 */
public class MavenStreamFilterRequest
    extends AbstractMavenFilteringRequest
{

    private InputStream from;

    private boolean filtering;

    public MavenStreamFilterRequest()
    {
        // nothing
    }

    public MavenStreamFilterRequest(InputStream from, boolean filtering, MavenProject mavenProject,
            List<String> filters,
            boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession,
            Properties additionalProperties)
    {
        super( mavenProject, filters, encoding, mavenSession );
        this.from = from;
        this.filtering = filtering;
        setAdditionalProperties( additionalProperties );
        setEscapeWindowsPaths( escapedBackslashesInFilePath );
    }


    public InputStream getFrom()
    {
        return from;
    }

    public void setFrom( InputStream from )
    {
        this.from = from;
    }

    public boolean isFiltering()
    {
        return filtering;
    }

    public void setFiltering( boolean filtering )
    {
        this.filtering = filtering;
    }

}
