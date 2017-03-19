package org.apache.maven.reporting.exec;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Bean which contains necessary informations to build {@link MavenReportExecution} with {@link MavenReportExecutor}: 
 * the intent is to store some informations regarding the current Maven execution.
 * 
 * @author Olivier Lamy
 * @version $Id$
 */
public class MavenReportExecutorRequest
{
    private ArtifactRepository localRepository;

    private MavenSession mavenSession;

    private MavenProject project;

    private ReportPlugin[] reportPlugins;

    public ArtifactRepository getLocalRepository()
    {
        return localRepository;
    }

    public void setLocalRepository( ArtifactRepository localRepository )
    {
        this.localRepository = localRepository;
    }

    public MavenSession getMavenSession()
    {
        return mavenSession;
    }

    public void setMavenSession( MavenSession mavenSession )
    {
        this.mavenSession = mavenSession;
    }

    public MavenProject getProject()
    {
        return project;
    }

    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    public ReportPlugin[] getReportPlugins()
    {
        return reportPlugins;
    }

    public void setReportPlugins( ReportPlugin[] reportPlugins )
    {
        this.reportPlugins = reportPlugins;
    }

    /**
     * Set the report plugin directly from <code>${project.reporting.plugins}</code> parameter value.
     *
     * @param reportPlugins the report plugins from <code>&lt;reporting&gt;</code> section
     * @since 1.4
     */
    public void setReportPlugins( org.apache.maven.model.ReportPlugin[] reportPlugins )
    {
        setReportPlugins( new ReportPlugin[reportPlugins.length] );

        int i = 0;
        for ( org.apache.maven.model.ReportPlugin r : reportPlugins )
        {
            ReportPlugin p = new ReportPlugin();
            p.setGroupId( r.getGroupId() );
            p.setArtifactId( r.getArtifactId() );
            p.setVersion( r.getVersion() );
            if ( r.getConfiguration() != null )
            {
                p.setConfiguration( new XmlPlexusConfiguration( (Xpp3Dom) r.getConfiguration() ) );
            }

            List<ReportSet> prs = new ArrayList<ReportSet>();
            for ( org.apache.maven.model.ReportSet rs : r.getReportSets() )
            {
                ReportSet ps = new ReportSet();
                ps.setId( rs.getId() );
                ps.setReports( rs.getReports() );
                if ( rs.getConfiguration() != null )
                {
                    ps.setConfiguration( new XmlPlexusConfiguration( (Xpp3Dom) rs.getConfiguration() ) );
                }
                prs.add( ps );
            }
            p.setReportSets( prs );

            this.reportPlugins[i++] = p;
        }
    }
}
