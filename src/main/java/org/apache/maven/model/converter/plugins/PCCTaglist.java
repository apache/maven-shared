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
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.Properties;

/**
 * A <code>PluginConfigurationConverter</code> for the maven-tasklist-plugin.
 *
 * @plexus.component role="org.apache.maven.model.converter.plugins.PluginConfigurationConverter" role-hint="taglist"
 *
 * @author Dennis Lundberg
 * @version $Id$
 */
public class PCCTaglist
    extends AbstractPluginConfigurationConverter
{
    /**
     * @see org.apache.maven.model.converter.plugins.AbstractPluginConfigurationConverter#getArtifactId()
     */
    public String getArtifactId()
    {
        return "maven-tasklist-plugin";
    }

    public String getType()
    {
        return TYPE_REPORT_PLUGIN;
    }

    protected void buildConfiguration( Xpp3Dom configuration, org.apache.maven.model.v3_0_0.Model v3Model,
                                       Properties projectProperties )
        throws ProjectConverterException
    {
        final String propertyName = "maven.tasklist.taskTag";
        
        String value = projectProperties.getProperty( propertyName );
        if( StringUtils.isNotEmpty( value ) ) {
            Xpp3Dom tags = new Xpp3Dom( "tags" );
            addConfigurationChild( tags, projectProperties, propertyName, "tag" );
            configuration.addChild( tags );
        }
    }
}
