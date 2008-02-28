package org.apache.maven.shared.runtime;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectSorter;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * A visitor that parses and collects Maven project XML files.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class XMLMavenRuntimeVisitor implements MavenRuntimeVisitor
{
    // fields -----------------------------------------------------------------

    /**
     * A list of the collected <code>MavenProject</code>s.
     */
    private final List projects;

    // constructors -----------------------------------------------------------

    /**
     * Creates a new <code>XMLMavenRuntimeVisitor</code>.
     */
    public XMLMavenRuntimeVisitor()
    {
        projects = new ArrayList();
    }

    // MavenRuntimeVisitor methods --------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void visitProjectProperties( URL url ) throws MavenRuntimeException
    {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void visitProjectXML( URL url ) throws MavenRuntimeException
    {
        MavenProject project = parseProjectXML( url );

        projects.add( project );
    }

    // public methods ---------------------------------------------------------

    /**
     * Gets the collected Maven projects.
     * 
     * @return an unmodifiable list of the collected <code>MavenProject</code>s
     */
    public List getProjects()
    {
        return Collections.unmodifiableList( projects );
    }

    /**
     * Gets the collected Maven projects ordered by dependencies.
     * 
     * @return an unmodifiable list of the collected <code>MavenProject</code>s ordered by dependencies
     * @throws MavenRuntimeException
     *             if an error occurred ordering the projects
     */
    public List getSortedProjects() throws MavenRuntimeException
    {
        try
        {
            ProjectSorter projectSorter = new ProjectSorter( projects );

            return projectSorter.getSortedProjects();
        }
        catch ( CycleDetectedException exception )
        {
            throw new MavenRuntimeException( "Cannot sort projects", exception );
        }
        catch ( DuplicateProjectException exception )
        {
            throw new MavenRuntimeException( "Cannot sort projects", exception );
        }
    }

    // private methods --------------------------------------------------------

    /**
     * Parses the specified Maven project XML into a <code>MavenProject</code> object.
     * 
     * @param url
     *            a URL to the Maven project XML
     * @return a <code>MavenProject</code> object that represents the XML
     * @throws MavenRuntimeException
     *             if an error occurs parsing the XML
     */
    private MavenProject parseProjectXML( URL url ) throws MavenRuntimeException
    {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        try
        {
            Model model = reader.read( ReaderFactory.newXmlReader( url ) );

            return new MavenProject( model );
        }
        catch ( XmlPullParserException exception )
        {
            throw new MavenRuntimeException( "Cannot read project XML", exception );
        }
        catch ( IOException exception )
        {
            throw new MavenRuntimeException( "Cannot read project XML", exception );
        }
    }
}
