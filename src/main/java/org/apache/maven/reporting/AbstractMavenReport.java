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
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.sink.SinkFactory;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * The basis for a Maven report.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: MavenReport.java 163376 2005-02-23 00:06:06Z brett $
 */
public abstract class AbstractMavenReport
    extends AbstractMojo
    implements MavenReport
{
    /** The current sink to use */
    private Sink sink;

    private Locale locale = Locale.ENGLISH;

    protected abstract Renderer getSiteRenderer();

    protected abstract String getOutputDirectory();

    protected abstract MavenProject getProject();

    /** The current report output directory to use */
    private File reportOutputDirectory;

    /**
     * This method is called when the report is invoked directly as a Mojo, not in the
     * context of a full site generation (where maven-site-plugin:site is the Mojo
     * being executed)
     *
     * @throws MojoExecutionException always
     * @see org.apache.maven.plugins.site.ReportDocumentRender
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException
    {
        Writer writer = null;
        try
        {
            File outputDirectory = new File( getOutputDirectory() );

            String filename = getOutputName() + ".html";

            SiteRenderingContext context = new SiteRenderingContext();
            context.setDecoration( new DecorationModel() );
            context.setTemplateName( "org/apache/maven/doxia/siterenderer/resources/default-site.vm" );
            context.setLocale( locale );

            SiteRendererSink sink = SinkFactory.createSink( outputDirectory, filename );

            generate( sink, Locale.getDefault() );

            // TODO: add back when skinning support is in the site renderer
//            getSiteRenderer().copyResources( outputDirectory, "maven" );

            File outputHtml = new File( outputDirectory, filename );
            outputHtml.getParentFile().mkdirs();

            writer = WriterFactory.newXmlWriter( outputHtml );

            getSiteRenderer().generateDocument( writer, sink, context );
        }
        catch ( MavenReportException e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( locale ) + " report generation.",
                                              e );
        }
        catch ( RendererException e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation.", e );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)
     */
    public void generate( org.codehaus.doxia.sink.Sink sink, Locale locale )
        throws MavenReportException
    {
        if ( sink == null )
        {
            throw new MavenReportException( "You must specify a sink." );
        }

        this.sink = sink;

        executeReport( locale );

        closeReport();
    }

    protected abstract void executeReport( Locale locale )
        throws MavenReportException;

    protected void closeReport()
    {
        getSink().close();
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
     * @return the sink used
     */
    public Sink getSink()
    {
        return sink;
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
}
