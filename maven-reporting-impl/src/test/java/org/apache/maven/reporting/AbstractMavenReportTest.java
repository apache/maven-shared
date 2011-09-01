package org.apache.maven.reporting;

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

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;

/**
 * Typical code to copy as a reporting plugin start: choose the goal name, then implement getOutputName(),
 * getName( Locale ), getDescription( Locale ) and of course executeReport( Locale ).
 * 
 * @goal test
 */
public class AbstractMavenReportTest
    extends AbstractMavenReport
{
    /**
     * The output directory for the report. Note that this parameter is only evaluated if the goal is run directly from
     * the command line. If the goal is run indirectly as part of a site generation, the output directory configured in
     * the Maven Site Plugin is used instead.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Doxia Site Renderer component.
     *
     * @component
     */
    protected Renderer siteRenderer;

    @Override
    protected String getOutputDirectory()
    {
        return outputDirectory.getAbsolutePath();
    }

    @Override
    protected MavenProject getProject()
    {
        return project;
    }

    @Override
    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    public String getOutputName()
    {
        return "abstract-maven-report-test";
    }

    public String getName( Locale locale )
    {
        return "Abstract Maven Report Test";
    }

    public String getDescription( Locale locale )
    {
        return "Abstract Maven Report Test Description";
    }

    @Override
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        getSink().body();
        getSink().text( "Abstract Maven Report Test Content" );
        getSink().body_();
    }
}
