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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.Settings;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 24 févr. 2008
 * @version $Id$
 */
public class StubMavenSession
    extends MavenSession
{

    private Properties executionProperties;
    
    private final Settings settings;
    
    public StubMavenSession( Settings settings )
    {
        this( null, settings );
    }

    public StubMavenSession()
    {
        this( null, null );
    }

    public StubMavenSession( Properties executionProperties )
    {
        this( executionProperties, null );
    }
    
    public StubMavenSession( Properties executionProperties, Settings settings )
    {
        super( null, null, null, null, null, null, null, null, null );
        
        this.settings = settings;
        this.executionProperties = new Properties();
        if ( executionProperties != null )
        {
            this.executionProperties.putAll( executionProperties );
        }
        this.executionProperties.putAll( System.getProperties() );
    }
    
    public Settings getSettings()
    {
        return settings;
    }

    public Properties getExecutionProperties()
    {
        return this.executionProperties;
    }

}
