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
import org.apache.maven.doxia.sink.render.RenderingContext;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * The basis for a Maven report which can be generated both as part of a site generation or
 * as a direct standalone invocation.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 * @since 2.0
 * @see #execute()
 * @see #generate(Sink, SinkFactory, Locale)
 * @see #executeReport(Locale)
 */
public abstract class AbstractMavenReport
    extends AbstractMojo
    implements MavenMultiPageReport
{
    /** The current sink to use */
    private Sink sink;

    /** The sink factory to use */
    private SinkFactory sinkFactory;

    /** The current report output directory to use */
    private File reportOutputDirectory;

    /**
     * This method is called when the report generation is invoked directly as a standalone Mojo.
     *
     * @throws MojoExecutionException if an error uccurs when generating the report
     * @see org.apache.maven.plugins.site.ReportDocumentRender
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException
    {
        if ( !canGenerateReport() )
        {
            return;
        }

        Writer writer = null;
        try
        {
            File outputDirectory = new File( getOutputDirectory() );

            String filename = getOutputName() + ".html";

            Locale locale = Locale.getDefault();

            SiteRenderingContext siteContext = new SiteRenderingContext();
            siteContext.setDecoration( new DecorationModel() );
            siteContext.setTemplateName( "org/apache/maven/doxia/siterenderer/resources/default-site.vm" );
            siteContext.setLocale( locale );

            RenderingContext context = new RenderingContext( outputDirectory, filename );

            SiteRendererSink sink = new SiteRendererSink( context );

            generate( sink, null, locale );

            outputDirectory.mkdirs();

            writer = new OutputStreamWriter( new FileOutputStream( new File( outputDirectory, filename ) ), "UTF-8" );

            getSiteRenderer().generateDocument( writer, sink, siteContext );

            //getSiteRenderer().copyResources( siteContext, new File( project.getBasedir(), "src/site/resources" ),
            //                            outputDirectory );
        }
        catch ( RendererException e )
        {
            throw new MojoExecutionException(
                "An error has occurred in " + getName( Locale.ENGLISH ) + " report generation.", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException(
                "An error has occurred in " + getName( Locale.ENGLISH ) + " report generation.", e );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException(
                "An error has occurred in " + getName( Locale.ENGLISH ) + " report generation.", e );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }

    /**
     * Generate a report.
     *
     * @param aSink the sink to use for the generation.
     * @param aLocale the wanted locale to generate the report, could be null.
     * @throws MavenReportException if any
     * @deprecated use {@link #generate(Sink, SinkFactory, Locale)} instead.
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
     * @param aSink
     * @param aLocale
     * @throws MavenReportException
     * @see org.apache.maven.reporting.MavenReport#generate(org.apache.maven.doxia.sink.Sink, java.util.Locale)
     * @deprecated use {@link #generate(Sink, SinkFactory, Locale)} instead.
     */
    public void generate( Sink aSink, Locale aLocale )
        throws MavenReportException
    {
        getLog().warn( "Deprecated API called - no SinkFactory available. Please update this plugin." );
        generate( aSink, null, aLocale );
    }

    /**
     * This method is called when the report generation is invoked by maven-site-plugin.
     *
     * @param aSink
     * @param aSinkFactory
     * @param aLocale
     * @throws MavenReportException
     */
    public void generate( Sink aSink, SinkFactory aSinkFactory, Locale aLocale )
        throws MavenReportException
    {
        if ( aSink == null )
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
     * Actions when closing the report.
     */
    protected void closeReport()
    {
        getSink().close();
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
     * The output directory when the mojo is run directly from the command line. Implementors should use this method to
     * return the value of a mojo parameter that the user may use to customize the output directory.
     * <br/>
     * <strong>Note:</strong>
     * When the mojo is run as part of a site generation, Maven will set the effective output directory via
     * {@link org.apache.maven.reporting.MavenReport#setReportOutputDirectory(java.io.File)}. In this case, the return
     * value of this method is irrelevant. Therefore, developers should always call {@link #getReportOutputDirectory()}
     * to get the effective output directory for the report. The later method will eventually fallback to this method
     * if the mojo is not run as part of a site generation.
     *
     * @return The path to the output directory as specified in the plugin configuration for this report.
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
