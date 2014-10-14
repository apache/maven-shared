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

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;

public interface DefaultFilterInfo {
	/**
	 * Will return the default FileUtils.FilterWrappers.
	 * <p/>
	 * <ul>
	 * <li>interpolate with token ${} and values from sysProps, project.properties, filters and project filters.</li>
	 * <li>interpolate with token @ @ and values from sysProps, project.properties, filters and project filters.</li>
	 * <li>interpolate with token ${} and values from mavenProject interpolation.</li>
	 * <li>interpolation with token @ @ and values from mavenProject interpolation</li>
	 * </ul>
	 * <b>This method is now deprecated and no escape mechanism will be used.</b>
	 *
	 * @param mavenProject
	 * @param filters      {@link java.util.List} of properties file
	 * @return {@link java.util.List} of FileUtils.FilterWrapper
	 * @deprecated use {@link #getDefaultFilterWrappers(org.apache.maven.project.MavenProject, java.util.List, boolean, org.apache.maven.execution.MavenSession, org.apache.maven.shared.filtering.MavenResourcesExecution)}
	 */
	List<FileUtils.FilterWrapper> getDefaultFilterWrappers(MavenProject mavenProject, List<String> filters,
			boolean escapedBackslashesInFilePath,
			MavenSession mavenSession)
			throws MavenFilteringException;

	/**
	 * @param mavenProject
	 * @param filters
	 * @param escapedBackslashesInFilePath
	 * @param mavenSession
	 * @param mavenResourcesExecution
	 * @return {@link java.util.List} of FileUtils.FilterWrapper
	 * @throws org.apache.maven.shared.filtering.MavenFilteringException
	 * @since 1.0-beta-2
	 */
	List<FileUtils.FilterWrapper> getDefaultFilterWrappers(MavenProject mavenProject, List<String> filters,
			boolean escapedBackslashesInFilePath,
			MavenSession mavenSession,
			MavenResourcesExecution mavenResourcesExecution)
			throws MavenFilteringException;

	/**
	 * @param request
	 * @return {@link java.util.List} of FileUtils.FilterWrapper
	 * @throws org.apache.maven.shared.filtering.MavenFilteringException
	 * @since 1.0-beta-3
	 */
	List<FileUtils.FilterWrapper> getDefaultFilterWrappers(AbstractMavenFilteringRequest request)
			throws MavenFilteringException;
}
