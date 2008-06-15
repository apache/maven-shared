package org.apache.maven.model.converter.plugins;

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

import org.apache.maven.model.converter.ProjectConverterException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * A <code>PluginConfigurationConverter</code> for the Castor plugin.
 *
 * @plexus.component role="org.apache.maven.model.converter.plugins.PluginConfigurationConverter" role-hint="castor"
 *
 * @author Dennis Lundberg
 * @version $Id$
 */
public class PCCCastor
    extends AbstractPluginConfigurationConverter
{
    /**
     * @see AbstractPluginConfigurationConverter#getArtifactId()
     */
    public String getArtifactId()
    {
        return "castor-maven-plugin";
    }

    public String getType()
    {
        return TYPE_BUILD_PLUGIN;
    }

    protected void buildConfiguration( Xpp3Dom configuration, org.apache.maven.model.v3_0_0.Model v3Model,
                                       Properties projectProperties )
        throws ProjectConverterException
    {
        addConfigurationChild( configuration, projectProperties, "maven.castor.dest", "dest" );

        // The Maven 1 plugin specifies a directory but the Maven 2 plugin wants a file 
        String value = projectProperties.getProperty( "maven.castor.properties.dir" );
        if ( value != null )
        {
            if ( !value.endsWith( "/" ) )
            {

                value += "/";
            }
            value += "castorbuilder.properties";
        }
        addConfigurationChild( configuration, "properties", value );

        addConfigurationChild( configuration, projectProperties, "maven.castor.tstamp", "tstamp" );
    }
}