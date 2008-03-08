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
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.Skin;
import org.apache.maven.project.MavenProject;

/**
 * Tool to play with <a href="http://maven.apache.org/doxia/">Doxia</a> objects
 * like <code>DecorationModel</code>.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface SiteTool
{
    /** Plexus Role */
    String ROLE = SiteTool.class.getName();

    /**
     * The locale by default for all default bundles
     */
    Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * @param localRepository the Maven local repository, not null.
     * @param remoteArtifactRepositories the Maven remote repositories, not null.
     * @param decoration the Doxia site descriptor model, not null.
     * @return the <code>Skin</code> artifact defined in a <code>DecorationModel</code> from a given project and a local repository
     * @throws SiteToolException if any
     */
    Artifact getSkinArtifactFromRepository( ArtifactRepository localRepository, List remoteArtifactRepositories,
                                            DecorationModel decoration )
        throws SiteToolException;

    /**
     * @param localRepository the Maven local repository, not null.
     * @param remoteArtifactRepositories the Maven remote repositories, not null.
     * @return the default <code>Skin</code> artifact from a given project and a local repository
     * @throws SiteToolException if any
     * @see Skin#getDefaultSkin()
     * @see #getSkinArtifactFromRepository(ArtifactRepository, List, DecorationModel)
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
     * @param siteDirectory The path to the directory containing the <code>site.xml</code> file, relative to the project base directory. If null, using by default "src/site".
     * @param basedir not null.
     * @param locale the locale wanted for the site descriptor. If not null, searching for <code>site_<i>localeLanguage</i>.xml</code>,
     * otherwise searching for <code>site.xml</code>.
     * @return the site descriptor relative file, i.e. <code>src/site/site.xml</code>, depending parameters value.
     */
    File getSiteDescriptorFromBasedir( String siteDirectory, File basedir, Locale locale );

    /**
     * @param project the Maven project, not null.
     * @param localRepository the Maven local repository, not null.
     * @param repositories the Maven remote repositories, not null.
     * @param locale the locale wanted for the site descriptor. If not null, searching for <code>site_<i>localeLanguage</i>.xml</code>,
     * otherwise searching for <code>site.xml</code>.
     * @return the site descriptor into the local repository after download of it from repositories or null if not found in repositories.
     * @throws SiteToolException if any
     */
    File getSiteDescriptorFromRepository( MavenProject project, ArtifactRepository localRepository, List repositories,
                                          Locale locale )
        throws SiteToolException;

    /**
     * @param project the Maven project, not null.
     * @param reactorProjects the Maven reactor projects, not null.
     * @param localRepository the Maven local repository, not null.
     * @param repositories the Maven remote repositories, not null.
     * @param siteDirectory The path to the directory containing the <code>site.xml</code> file, relative to the project base directory. If null, using by default "src/site".
     * @param locale the locale used for the i18n in DecorationModel. If null, using the default locale in the jvm.
     * @param inputEncoding the input encoding of the site descriptor, not null.
     * @param outputEncoding the output encoding wanted, not null.
     * @return the <code>DecorationModel</code> object corresponding to the <code>site.xml</code> file with some interpolations.
     * @throws SiteToolException if any
     */
    DecorationModel getDecorationModel( MavenProject project, List reactorProjects, ArtifactRepository localRepository,
                                        List repositories, String siteDirectory, Locale locale, String inputEncoding,
                                        String outputEncoding )
        throws SiteToolException;

    /**
     * Populate the report menu part of the decoration model.
     *
     * @param decorationModel the Doxia DecorationModel, not null.
     * @param locale the locale used for the i18n in DecorationModel. If null, using the default locale in the jvm.
     * @param categories a map to put on the decoration model, not null.
     */
    void populateReportsMenu( DecorationModel decorationModel, Locale locale, Map categories );

    /**
     * Interpolating several expressions in the site descriptor content. Actually, the expressions could be on
     * the project, the environment variables and the specific properties like <code>encoding</code>.
     * <p/>
     * For instance:
     * <dl>
     * <dt>${project.name}</dt>
     * <dd>The value from the POM of:
     * <p>
     * &lt;project&gt;<br>
     * &lt;name&gt;myProjectName&lt;/name&gt;<br>
     * &lt;/project&gt;
     * </p></dd>
     * <dt>${my.value}</dt>
     * <dd>The value from the POM of:
     * <p>
     * &lt;properties&gt;<br>
     * &lt;my.value&gt;hello&lt;/my.value&gt;<br>
     * &lt;/properties&gt;
     * </p></dd>
     * <dt>${JAVA_HOME}</dt>
     * <dd>The value of JAVA_HOME in the environment variables</dd>
     * </dl>
     *
     * @param props a map used for interpolation, not null.
     * @param aProject a Maven project, not null.
     * @param inputEncoding the input encoding of the site descriptor, not null.
     * @param outputEncoding the output encoding wanted, not null.
     * @param siteDescriptorContent the site descriptor file, not null.
     * @return the site descriptor content based on the <code>site.xml</code> file with interpolated strings.
     * @throws SiteToolException if errors happened during the interpolation.
     */
    String getInterpolatedSiteDescriptorContent( Map props, MavenProject aProject, String siteDescriptorContent,
                                                 String inputEncoding, String outputEncoding )
        throws SiteToolException;

    /**
     * Returns the parent POM with interpolated URLs. Attempts to source this value from the
     * <code>reactorProjects</code> parameters if available (reactor env model attributes
     * are interpolated), or if the reactor is unavailable (-N) resorts to the
     * <code>project.getParent().getUrl()</code> value which will NOT have be interpolated.
     * <p/>
     * TODO: once bug is fixed in Maven proper, remove this.
     *
     * @param aProject a Maven project, not null.
     * @param reactorProjects the Maven reactor projects, not null.
     * @param localRepository the Maven local repository, not null.
     * @return the parent project with interpolated URLs.
     */
    MavenProject getParentProject( MavenProject aProject, List reactorProjects, ArtifactRepository localRepository );

    /**
     * @param decorationModel the Doxia DecorationModel, not null.
     * @param locale the locale used for the i18n in DecorationModel. If null, using the default locale in the jvm.
     * @param project a Maven project, not null.
     * @param parentProject a Maven parent project, not null.
     * @param keepInheritedRefs used for inherited references.
     */
    void populateProjectParentMenu( DecorationModel decorationModel, Locale locale, MavenProject project,
                                    MavenProject parentProject, boolean keepInheritedRefs );

    /**
     * @param project a Maven project, not null.
     * @param reactorProjects the Maven reactor projects, not null.
     * @param localRepository the Maven local repository, not null.
     * @param decorationModel the Doxia site descriptor model, not null.
     * @param locale the locale used for the i18n in DecorationModel. If null, using the default locale in the jvm.
     * @param keepInheritedRefs used for inherited references.
     * @throws SiteToolException if any
     */
    void populateModules( MavenProject project, List reactorProjects, ArtifactRepository localRepository,
                          DecorationModel decorationModel, Locale locale, boolean keepInheritedRefs )
        throws SiteToolException;

    /**
     *
     * Init the <code>localesList</code> variable.
     * <p>If <code>locales</code> variable is available, the first valid token will be the <code>defaultLocale</code>
     * for this instance of the Java Virtual Machine.</p>
     *
     * @param locales A comma separated list of locales supported by Maven. The first valid token will be the default Locale
     * for this instance of the Java Virtual Machine.
     * @return a list of <code>Locale</code>
     */
    List getAvailableLocales( String locales );

    /**
     * Converts a locale code like "en", "en_US" or "en_US_win" to a <code>java.util.Locale</code>
     * object.
     * <p>If localeCode = <code>default</code>, return the current value of the default locale for this instance
     * of the Java Virtual Machine.</p>
     *
     * @param localeCode the locale code string.
     * @return a java.util.Locale object instancied or null if errors occurred
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/Locale.html">java.util.Locale#getDefault()</a>
     */
    Locale codeToLocale( String localeCode );
}
