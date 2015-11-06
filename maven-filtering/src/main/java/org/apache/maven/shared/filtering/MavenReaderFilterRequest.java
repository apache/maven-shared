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

import java.io.Reader;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * @author Olivier Lamy
 * @author Kristian Rosenvold
 * @since 1.0-beta-3
 */
public class MavenReaderFilterRequest
    extends AbstractMavenFilteringRequest
{

    private Reader from;

    private boolean filtering;

    /**
     * Default constructor.
     */
    public MavenReaderFilterRequest()
    {
        // nothing
    }

    /**
     * @param from To read from
     * @param filtering filter yes/no
     * @param mavenProject The Maven Project.
     * @param filters The list of filters which will be used.
     * @param escapedBackslashesInFilePath escape backslashes in file paths.
     * @param mavenSession The Maven Session.
     * @param additionalProperties supplemental properties.
     */
    public MavenReaderFilterRequest( Reader from, boolean filtering, MavenProject mavenProject, List<String> filters,
                                     boolean escapedBackslashesInFilePath, MavenSession mavenSession,
                                     Properties additionalProperties )
    {
        super( mavenProject, filters, mavenSession );
        this.from = from;
        this.filtering = filtering;
        setAdditionalProperties( additionalProperties );
        setEscapeWindowsPaths( escapedBackslashesInFilePath );
    }

    /**
     * @return where we read from.
     */
    public Reader getFrom()
    {
        return from;
    }

    /**
     * @param from set where to read from.
     */
    public void setFrom( Reader from )
    {
        this.from = from;
    }

    /**
     * @return is filtering active ({@code true}) false otherwise.
     */
    public boolean isFiltering()
    {
        return filtering;
    }

    /**
     * @param filtering turn filtering on {@code true}) or off ({@code false}).
     */
    public void setFiltering( boolean filtering )
    {
        this.filtering = filtering;
    }

}
