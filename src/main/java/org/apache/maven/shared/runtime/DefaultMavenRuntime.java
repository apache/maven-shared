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

import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * Default implementation of <code>MavenRuntime</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see MavenRuntime
 * @plexus.component role="org.apache.maven.shared.runtime.MavenRuntime"
 */
public class DefaultMavenRuntime implements MavenRuntime
{
    // MavenRuntime methods ---------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public MavenProjectProperties getProjectProperties( Class klass ) throws MavenRuntimeException
    {
        PropertiesMavenRuntimeVisitor visitor = new PropertiesMavenRuntimeVisitor();

        MavenRuntimeVisitorUtils.accept( klass, visitor );

        return (MavenProjectProperties) first( visitor.getProjects() );
    }

    /**
     * {@inheritDoc}
     */
    public List getProjectProperties( ClassLoader classLoader ) throws MavenRuntimeException
    {
        PropertiesMavenRuntimeVisitor visitor = new PropertiesMavenRuntimeVisitor();

        MavenRuntimeVisitorUtils.accept( classLoader, visitor );

        return visitor.getProjects();
    }
    
    /**
     * {@inheritDoc}
     */
    public MavenProject getProject( Class klass ) throws MavenRuntimeException
    {
        XMLMavenRuntimeVisitor visitor = new XMLMavenRuntimeVisitor();

        MavenRuntimeVisitorUtils.accept( klass, visitor );

        return (MavenProject) first( visitor.getProjects() );
    }

    /**
     * {@inheritDoc}
     */
    public List getProjects( ClassLoader classLoader ) throws MavenRuntimeException
    {
        XMLMavenRuntimeVisitor visitor = new XMLMavenRuntimeVisitor();

        MavenRuntimeVisitorUtils.accept( classLoader, visitor );

        return visitor.getProjects();
    }

    /**
     * {@inheritDoc}
     */
    public List getSortedProjects( ClassLoader classLoader ) throws MavenRuntimeException
    {
        XMLMavenRuntimeVisitor visitor = new XMLMavenRuntimeVisitor();

        MavenRuntimeVisitorUtils.accept( classLoader, visitor );

        return visitor.getSortedProjects();
    }
    
    // private methods --------------------------------------------------------

    /**
     * Gets the first element in the specified list or <code>null</code> if it is empty.
     * 
     * @param list
     *            the list to examine
     * @return the first item in the list, or <code>null</code> if it is empty
     */
    private Object first( List list )
    {
        return !list.isEmpty() ? list.get( 0 ) : null;
    }
}
