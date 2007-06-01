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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * A visitor that parses and collects Maven project property files.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class PropertiesMavenRuntimeVisitor implements MavenRuntimeVisitor
{
    // constants --------------------------------------------------------------

    /**
     * The group id property name in a Maven project property file.
     */
    private static final String GROUP_ID_PROPERTY = "groupId";

    /**
     * The artifact id property name in a Maven project property file.
     */
    private static final String ARTIFACT_ID_PROPERTY = "artifactId";

    /**
     * The version property name in a Maven project property file.
     */
    private static final String VERSION_PROPERTY = "version";

    // fields -----------------------------------------------------------------

    /**
     * A list of the collected <code>MavenProjectProperties</code>.
     */
    private final List projects;

    // constructors -----------------------------------------------------------

    /**
     * Creates a new <code>PropertiesMavenRuntimeVisitor</code>.
     */
    public PropertiesMavenRuntimeVisitor()
    {
        projects = new ArrayList();
    }

    // MavenRuntimeVisitor methods --------------------------------------------

    /*
     * @see org.apache.maven.shared.runtime.MavenRuntimeVisitor#visitProjectProperties(java.io.InputStream)
     */
    public void visitProjectProperties( InputStream in ) throws MavenRuntimeException
    {
        MavenProjectProperties project = parseProjectProperties( in );

        projects.add( project );
    }

    /*
     * @see org.apache.maven.shared.runtime.MavenRuntimeVisitor#visitProjectXML(java.io.InputStream)
     */
    public void visitProjectXML( InputStream in ) throws MavenRuntimeException
    {
        // no-op
    }

    // public methods ---------------------------------------------------------

    /**
     * Gets the collected Maven project properties.
     * 
     * @return a list of the collected <code>MavenProjectProperties</code>
     */
    public List getProjects()
    {
        return Collections.unmodifiableList( projects );
    }

    // private methods --------------------------------------------------------

    /**
     * Parses the specified Maven project properties into a <code>MavenProjectProperties</code> object.
     * 
     * @param in
     *            an input stream to the Maven project properties
     * @return a <code>MavenProjectProperties</code> object that represents the properties
     * @throws MavenRuntimeException
     *             if an error occurs parsing the properties
     */
    private MavenProjectProperties parseProjectProperties( InputStream in ) throws MavenRuntimeException
    {
        Properties properties = new Properties();

        try
        {
            properties.load( in );
        }
        catch ( IOException exception )
        {
            throw new MavenRuntimeException( "Cannot read project properties", exception );
        }

        String groupId = properties.getProperty( GROUP_ID_PROPERTY );
        String artifactId = properties.getProperty( ARTIFACT_ID_PROPERTY );
        String version = properties.getProperty( VERSION_PROPERTY );

        return new MavenProjectProperties( groupId, artifactId, version );
    }
}
