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

import org.apache.maven.model.Plugin;
import org.apache.maven.reporting.MavenReport;

/**
 * <p>
 *   Since Maven 3, reporting plugins (ie {@link MavenReport}s) are not anymore prepared by Maven core.
 *   This class will store all necessary information for later {@link MavenReport} generation/execution:
 *   <ul>
 *     <li>a {@link MavenReport},</li>
 *     <li>the goal name associated to the report,</li>
 *     <li>the associated {@link ClassLoader} for the report generation,</li>
 *     <li>the {@link Plugin} associated to the {@link MavenReport}.</li>
 *   </ul> 
 * </p>
 * <p>
 *   With this bean, a plugin wanting to generate a report (= <i>"execute"</i> the report) has to call the
 *   {@link MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)}
 *   method, setting the current {@link Thread} classLoader first with {@link #classLoader}.
 * </p>
 * <p>
 *   This bean is instantiated by {@link MavenReportExecutor}.
 * </p>
 * 
 * @author Olivier Lamy
 */
public class MavenReportExecution
{
    private MavenReport mavenReport;

    private ClassLoader classLoader;

    private Plugin plugin;

    private final String goal;

    public MavenReportExecution( String goal, Plugin plugin, MavenReport mavenReport, ClassLoader classLoader )
    {
        this.goal = goal;
        this.setPlugin( plugin );
        this.mavenReport = mavenReport;
        this.classLoader = classLoader;
    }

    public MavenReportExecution( Plugin plugin, MavenReport mavenReport, ClassLoader classLoader )
    {
        this( null, plugin, mavenReport, classLoader );
    }

    public MavenReportExecution( MavenReport mavenReport )
    {
        this( null, null, mavenReport, null );
    }

    public MavenReport getMavenReport()
    {
        return mavenReport;
    }

    public void setMavenReport( MavenReport mavenReport )
    {
        this.mavenReport = mavenReport;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public void setPlugin( Plugin plugin )
    {
        this.plugin = plugin;
    }

    public Plugin getPlugin()
    {
        return plugin;
    }

    public String getGoal()
    {
        return goal;
    }
}
