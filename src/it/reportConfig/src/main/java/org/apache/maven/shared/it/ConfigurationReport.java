package org.apache.maven.shared.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.util.Locale;

/**
 * Report that displays effective parameter values: will be used to check how report values
 * are inherited/overridden from build.pluginManagement and not build.plugins.
 * 
 * @goal test
 * @phase site
 */
public class ConfigurationReport
    extends AbstractMavenReport
{
    /**
     * Parameter with value defined in pluginManagement
     *
     * @parameter default-value="default"
     */
    private String pluginManagement;

    /**
     * Parameter with value defined in build.plugins
     *
     * @parameter default-value="default"
     */
    private String buildPlugin;

    /**
     * Parameter with value defined both in build.plugins and pluginManagement
     *
     * @parameter default-value="default"
     */
    private String buildAndManagement;

    /**
     * Parameter with value defined both in build.plugins, pluginManagement and reporting.plugin.
     *
     * @parameter default-value="default"
     */
    private String reportingPlugin;

    /**
     * Parameter with value defined both in build.plugins, pluginManagement, reporting.plugin and
     * reporting.plugin.reportSet.
     *
     * @parameter default-value="default"
     */
    private String reportingPluginReportSet;

    public String getOutputName()
    {
        return "configuration";
    }

    public String getName( Locale locale )
    {
        return "Configuration";
    }

    public String getDescription( Locale locale )
    {
        return "Report Configuration";
    }

    @Override
    protected Renderer getSiteRenderer()
    {
        return null;
    }

    @Override
    protected String getOutputDirectory()
    {
        return null;
    }

    @Override
    protected MavenProject getProject()
    {
        return null;
    }

    @Override
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        final Sink s = getSink();
        s.verbatim( null );
        s.text( "pluginManagement = " + pluginManagement + "\n" );
        s.text( "buildPlugin = " + buildPlugin + "\n" );
        s.text( "buildAndManagement = " + buildAndManagement + "\n" );
        s.text( "reportingPlugin = " + reportingPlugin + "\n" );
        s.text( "reportingPluginReportSet = " + reportingPluginReportSet );
        s.verbatim_();
    }
}
