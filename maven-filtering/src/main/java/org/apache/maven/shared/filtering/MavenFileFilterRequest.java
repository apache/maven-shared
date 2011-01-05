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
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:olamy@apache">olamy</a>
 * @since 1.0-beta-3
 */
public class MavenFileFilterRequest
    extends AbstractMavenFilteringRequest
{
    
    private File from;

    private File to;
    
    private boolean filtering;

    public MavenFileFilterRequest()
    {
        // nothing
    }

    public MavenFileFilterRequest( File from, File to, boolean filtering, MavenProject mavenProject, List filters,
                                   boolean escapedBackslashesInFilePath, String encoding, MavenSession mavenSession,
                                   Properties additionalProperties )
    {
        super( mavenProject, filters, encoding, mavenSession );
        this.from = from;
        this.to = to;
        this.filtering = filtering;
        setAdditionalProperties( additionalProperties );
        setEscapeWindowsPaths( escapedBackslashesInFilePath );
    }


    public File getFrom()
    {
        return from;
    }

    public void setFrom( File from )
    {
        this.from = from;
    }

    public File getTo()
    {
        return to;
    }

    public void setTo( File to )
    {
        this.to = to;
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
