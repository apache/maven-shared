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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * This component will prepare {@link org.apache.maven.reporting.MavenReport}s for later generation. If a
 * {@link org.apache.maven.reporting.MavenReport} needs to fork a goal or a lifecycle phase, this fork is executed.
 * <p>
 * Notice that Maven Reports are classical {@link org.apache.maven.plugin.Mojo Mojos} implementing the
 * {@link org.apache.maven.reporting.MavenReport} interface: when used as reports, their <code>Mojo.execute()</code>
 * method is not to be used but <code>MavenReport.generate()</code>.
 * </p>
 *
 * @author Olivier Lamy
 */
public interface MavenReportExecutor
{
    /**
     * Build the {@link org.apache.maven.reporting.MavenReport}s, with associated forked executions if necessary. 
     * 
     * @param mavenReportExecutorRequest
     * @return a list or prepared Maven report executions
     * @throws MojoExecutionException
     */
    List<MavenReportExecution> buildMavenReports( MavenReportExecutorRequest mavenReportExecutorRequest )
        throws MojoExecutionException;
}
