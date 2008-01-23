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

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Skin;

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
}
