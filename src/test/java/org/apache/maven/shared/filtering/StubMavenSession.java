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

import java.util.Properties;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.sonatype.aether.RepositorySystemSession;

/**
 * @author Olivier Lamy
 * @since 1.0-beta-1
 * @version $Id$
 */
public class StubMavenSession
    extends MavenSession
{

    private Properties userProperties;

    private Properties systemProperties;

    private final Settings settings;

    public StubMavenSession( Settings settings )
    {
        this( null, null, settings );
    }

    public StubMavenSession()
    {
        this( null, null, null );
    }

    public StubMavenSession( Properties userProperties )
    {
        this( null, userProperties, null );
    }

    public StubMavenSession( Properties systemProperties, Properties userProperties, Settings settings )
    {

        super( (PlexusContainer) null, (RepositorySystemSession) null, new DefaultMavenExecutionRequest(),
               (MavenExecutionResult) null );

        this.settings = settings;

        this.systemProperties = new Properties();
        if ( systemProperties != null )
        {
            this.systemProperties.putAll( systemProperties );
        }
        this.systemProperties.putAll( System.getProperties() );

        this.userProperties = new Properties();
        if ( userProperties != null )
        {
            this.userProperties.putAll( userProperties );
        }
    }

    public Settings getSettings()
    {
        return settings;
    }

    public Properties getSystemProperties()
    {
        return this.systemProperties;
    }

    public Properties getUserProperties()
    {
        return this.userProperties;
    }

}
