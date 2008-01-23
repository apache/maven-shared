package org.apache.maven.doxia.tools;

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

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Skin;
import org.apache.maven.project.MavenProject;

/**
 * This tool contains some utilities methods to play with Doxia <code>DecorationModel</code>.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface SiteTool
{
    /** Plexus Role */
    String ROLE = SiteTool.class.getName();

    /**
     * @param localRepository not null
     * @param remoteArtifactRepositories not null
     * @param decoration not null
     * @return the <code>Skin</code> artifact defined in a <code>DecorationModel</code> from a given project and a local repository
     * @throws SiteToolException if any
     */
    Artifact getSkinArtifactFromRepository( ArtifactRepository localRepository, List remoteArtifactRepositories,
                                            DecorationModel decoration )
        throws SiteToolException;

    /**
     * @param localRepository not null
     * @param remoteArtifactRepositories not null
     * @param decoration not null
     * @return the default <code>Skin</code> artifact from a given project and a local repository
     * @throws SiteToolException if any
     * @see Skin#getDefaultSkin()
     */
    Artifact getDefaultSkinArtifact( ArtifactRepository localRepository, List remoteArtifactRepositories )
        throws SiteToolException;

    /**
     * For example:
     * <dl>
     * <dt>to = "http://maven.apache.org" and from = "http://maven.apache.org"</dl>
     * <dd>return ""</dd>
     * <dt>to = "http://maven.apache.org" and from = "http://maven.apache.org/plugins/maven-site-plugin/"</dl>
     * <dd>return "../.."</dd>
     * <dt>to = "http://maven.apache.org/plugins/maven-site-plugin/" and from = "http://maven.apache.org"</dl>
     * <dd>return "plugins/maven-site-plugin"</dd>
     * </dl>
     * <b>Note</b>: The file separator depends on the system.
     *
     * @param to
     * @param from
     * @return a relative path from <code>from</code> to <code>to</code>.
     */
    String getRelativePath( String to, String from );

    /**
     * @param siteDirectory containing the <code>site.xml</code> file. If null, using by default "${basedir}/src/site".
     * @param basedir not null.
     * @param locale the locale wanted for the site descriptor. If not null, searching for <code>site_<i>localeLanguage</i>.xml</code>
     * @return the site descriptor relative file, i.e. <code>src/site/site.xml</code>, depending parameters value.
     */
    File getSiteDescriptorFromBasedir( File siteDirectory, File basedir, Locale locale );

    /**
     * @param project Maven project not null
     * @param localRepository not null
     * @param repositories not null
     * @param locale the locale wanted for the site descriptor. If not null, searching for <code>site_<i>localeLanguage</i>.xml</code>
     * @return the site descriptor into the local repository after download of it from repositories or null if not found in repositories.
     * @throws SiteToolException if any
     */
    File getSiteDescriptorFromRepository( MavenProject project, ArtifactRepository localRepository,
                                          List repositories, Locale locale )
        throws SiteToolException;
}
