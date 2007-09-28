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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Locale;

/**
 * The basis for a Maven report.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 * @since 2.0
 */
public abstract class AbstractMavenReport
    extends AbstractMojo
    implements MavenReport
{
    /** The current sink to use */
    private Sink sink;

    /** The sink factory to use */
    private SinkFactory sinkFactory;

    /** The current report output directory to use */
    private File reportOutputDirectory;

    /**
     * This method should be never called - all reports are rendered by Maven site-plugin's
     * @see org.apache.maven.plugins.site.ReportDocumentRender
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException
    {
        throw new MojoExecutionException( "Reporting mojo's can only be called from ReportDocumentRender" );
    }

    /**
     * Generate a report.
     *
     * @param aSink the sink to use for the generation.
     * @param aLocale the wanted locale to generate the report, could be null.
     * @throws MavenReportException if any
     * @deprecated using {@link #generate(Sink, SinkFactory, Locale)} instead of
     */
    public void generate( org.codehaus.doxia.sink.Sink aSink, Locale aLocale )
        throws MavenReportException
    {
        getLog().warn( "Deprecated API called - not org.apache.maven.doxia.sink.Sink instance and no SinkFactory available. Please update this plugin." );
        generate( aSink, null, aLocale );
    }

    /**
     * Generate a report.
     *
     * @see org.apache.maven.reporting.MavenReport#generate(org.apache.maven.doxia.sink.Sink, java.util.Locale)
     * @deprecated using {@link #generate(Sink, SinkFactory, Locale)} instead of
     */
    public void generate( Sink aSink, Locale aLocale )
        throws MavenReportException
    {
        getLog().warn( "Deprecated API called - no SinkFactory available. Please update this plugin." );
        generate( aSink, null, aLocale );
    }

    /**
     * Generate a report.
     *
     * @param aSink
     * @param aSinkFactory
     * @param aLocale
     * @throws MavenReportException
     */
    public void generate( Sink aSink, SinkFactory aSinkFactory, Locale aLocale )
        throws MavenReportException
    {
        if ( sink == null )
        {
            throw new MavenReportException( "You must specify a sink." );
        }

        if ( !canGenerateReport() )
        {
            getLog().info( "This report cannot be generated as part of the current build. The report name should be referenced in this line of output." );
            return;
        }

        this.sink = aSink;

        this.sinkFactory = aSinkFactory;

        executeReport( aLocale );

        closeReport();
    }

    /** {@inheritDoc} */
    public String getCategoryName()
    {
        return CATEGORY_PROJECT_REPORTS;
    }

    /** {@inheritDoc} */
    public File getReportOutputDirectory()
    {
        if ( reportOutputDirectory == null )
        {
            reportOutputDirectory = new File( getOutputDirectory() );
        }

        return reportOutputDirectory;
    }

    /** {@inheritDoc} */
    public void setReportOutputDirectory( File reportOutputDirectory )
    {
        this.reportOutputDirectory = reportOutputDirectory;
    }

    /**
     * Actions when closing the report. By default, nothing to do.
     */
    protected void closeReport()
    {
        // nop
    }

    /**
     * @return the sink used
     */
    public Sink getSink()
    {
        return sink;
    }

    /**
     * @return the sink factory used
     */
    public SinkFactory getSinkFactory()
    {
        return sinkFactory;
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#isExternalReport()
     * @return <tt>false</tt> by default.
     */
    public boolean isExternalReport()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean canGenerateReport()
    {
        return true;
    }

    /**
     * @return the site renderer used.
     */
    protected abstract Renderer getSiteRenderer();

    /**
     * @return the output directory path.
     */
    protected abstract String getOutputDirectory();

    /**
     * @return the Maven project instance.
     */
    protected abstract MavenProject getProject();

    /**
     * Execute the generation of the report.
     *
     * @param locale the wanted locale to return the report's description, could be null.
     * @throws MavenReportException if any
     */
    protected abstract void executeReport( Locale locale )
        throws MavenReportException;
}
