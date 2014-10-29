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
 * @author Olivier Lamy
 * @since 1.0-beta-3
 */
public class MavenFileFilterRequest
    extends AbstractMavenFilteringRequest
{

    private File from;

    private File to;

    private boolean filtering;

    private String encoding;

    /**
     * The constructor.
     */
    public MavenFileFilterRequest()
    {
        // nothing
    }

    /**
     * @param from The request from where.
     * @param to The request to where
     * @param filtering Filtering yes {@code true} or no {@code false}
     * @param mavenProject The Maven Project.
     * @param filters The list of given filters.
     * @param escapedBackslashesInFilePath Escape back slashes in file path.
     * @param encoding The used encoding during the filtering.
     * @param mavenSession The Maven Session.
     * @param additionalProperties Supplemental properties.
     */
    public MavenFileFilterRequest(
                                   File from,
                                   File to,
                                   boolean filtering,
                                   MavenProject mavenProject,
                                   List<String> filters,
                                   boolean escapedBackslashesInFilePath,
                                   String encoding,
                                   MavenSession mavenSession,
                                   Properties additionalProperties )
    {
        super( mavenProject, filters, mavenSession );
        this.encoding = encoding;
        this.from = from;
        this.to = to;
        this.filtering = filtering;
        setAdditionalProperties( additionalProperties );
        setEscapeWindowsPaths( escapedBackslashesInFilePath );
    }

    /**
     * Return the encoding.
     * @return Current encoding.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the value for encoding.
     * @param encoding Give the new value for encoding.
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }


    /**
     * @return to filter from.
     */
    public File getFrom()
    {
        return from;
    }

    /**
     * @param from set filter from.
     */
    public void setFrom( File from )
    {
        this.from = from;
    }

    /**
     * @return The filter to
     */
    public File getTo()
    {
        return to;
    }

    /**
     * @param to Set the target.
     */
    public void setTo( File to )
    {
        this.to = to;
    }

    /**
     * @return if we are filtering yes {@code true} no {@code false}
     */
    public boolean isFiltering()
    {
        return filtering;
    }

    /**
     * @param filtering set filtering yes / no.
     */
    public void setFiltering( boolean filtering )
    {
        this.filtering = filtering;
    }

}
