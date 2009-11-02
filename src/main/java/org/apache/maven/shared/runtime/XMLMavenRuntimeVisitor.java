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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectSorter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * A visitor that parses and collects Maven project XML files.
 * 
 * @author <a href="mailto:markh@apache.org">Mark Hobson</a>
 * @version $Id$
 */
class XMLMavenRuntimeVisitor implements MavenRuntimeVisitor
{
    // fields -----------------------------------------------------------------

    /**
     * A list of the collected Maven projects.
     */
    private final List<MavenProject> projects;

    // constructors -----------------------------------------------------------

    /**
     * Creates a new {@code XMLMavenRuntimeVisitor}.
     */
    public XMLMavenRuntimeVisitor()
    {
        projects = new ArrayList<MavenProject>();
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
     * @return an unmodifiable list of the collected Maven projects
     */
    public List<MavenProject> getProjects()
    {
        return Collections.unmodifiableList( projects );
    }

    /**
     * Gets the collected Maven projects ordered by dependencies.
     * 
     * @return an unmodifiable list of the collected Maven projects ordered by dependencies
     * @throws MavenRuntimeException
     *             if an error occurred ordering the projects
     */
    public List<MavenProject> getSortedProjects() throws MavenRuntimeException
    {
        try
        {
            ProjectSorter projectSorter = new ProjectSorter( projects );

            return genericList( projectSorter.getSortedProjects(), MavenProject.class );
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
     * Parses the specified Maven project XML into a {@code MavenProject} object.
     * 
     * @param url
     *            a URL to the Maven project XML
     * @return a {@code MavenProject} object that represents the XML
     * @throws MavenRuntimeException
     *             if an error occurs parsing the XML
     */
    private MavenProject parseProjectXML( URL url ) throws MavenRuntimeException
    {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        InputStream in = null;

        try
        {
            URLConnection connection = url.openConnection();
            connection.setUseCaches( false );

            in = connection.getInputStream();

            Model model = reader.read( ReaderFactory.newXmlReader( in ) );

            return new MavenProject( model );
        }
        catch ( XmlPullParserException exception )
        {
            throw new MavenRuntimeException( "Cannot read project XML: " + url, exception );
        }
        catch ( IOException exception )
        {
            throw new MavenRuntimeException( "Cannot read project XML: " + url, exception );
        }
        finally
        {
            IOUtil.close( in );
        }
    }
    
    /**
     * Converts the specified raw list to a generic list of a specified type by explicitly casting each element.
     * 
     * @param <T>
     *            the type of the required generic list
     * @param list
     *            the raw type
     * @param type
     *            the class that represents the type of the required generic list
     * @return the generic list
     */
    private static <T> List<T> genericList( List<?> list, Class<T> type )
    {
        List<T> genericList = new ArrayList<T>();

        for ( Object element : list )
        {
            genericList.add( type.cast( element ) );
        }

        return genericList;
    }
}
