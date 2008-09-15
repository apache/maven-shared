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

import java.util.Iterator;
import java.util.Properties;

/**
 * A <code>PluginConfigurationConverter</code> for the maven-site-plugin.
 *
 * @author Dennis Lundberg
 * @version $Id$
 * @plexus.component role="org.apache.maven.model.converter.plugins.PluginConfigurationConverter" role-hint="site"
 */
public class PCCSite
    extends AbstractPluginConfigurationConverter
{
    /**
     * @see AbstractPluginConfigurationConverter#getArtifactId()
     */
    public String getArtifactId()
    {
        return "maven-site-plugin";
    }

    public String getType()
    {
        return TYPE_BUILD_PLUGIN;
    }

    protected void buildConfiguration( Xpp3Dom configuration, org.apache.maven.model.v3_0_0.Model v3Model,
                                       Properties projectProperties )
        throws ProjectConverterException
    {
        Xpp3Dom xdocExcludes = getXdocExcludes( configuration );

        // Always exclude the navigation.xml file
        appendValue( xdocExcludes, "navigation.xml" );

        // Exclude the changes.xml file if the Changes report is used
        if ( hasChangesReport( v3Model ) )
        {
            appendValue( xdocExcludes, "changes.xml" );
        }
    }

    private void appendValue( Xpp3Dom xdoc, String value )
    {
        String currentValue = xdoc.getValue();
        if ( StringUtils.isEmpty( currentValue ) )
        {
            xdoc.setValue( value );
        }
        else
        {
            xdoc.setValue( currentValue + "," + value );
        }
    }

    private Xpp3Dom getXdocExcludes( Xpp3Dom configuration )
    {
        Xpp3Dom moduleExcludes = configuration.getChild( "moduleExcludes" );
        if ( moduleExcludes == null )
        {
            moduleExcludes = new Xpp3Dom( "moduleExcludes" );
            configuration.addChild( moduleExcludes );
        }

        Xpp3Dom xdoc = moduleExcludes.getChild( "xdoc" );
        if ( xdoc == null )
        {
            xdoc = new Xpp3Dom( "xdoc" );
            moduleExcludes.addChild( xdoc );
        }
        return xdoc;
    }

    private boolean hasChangesReport( org.apache.maven.model.v3_0_0.Model v3Model )
    {
        boolean hasChangesReport = false;

        if ( v3Model.getReports() != null && !v3Model.getReports().isEmpty() )
        {
            Iterator iterator = v3Model.getReports().iterator();
            while ( iterator.hasNext() )
            {
                String report = (String) iterator.next();
                if ( "maven-changes-plugin".equals( report ) )
                {
                    hasChangesReport = true;
                }
            }
        }

        return hasChangesReport;
    }
}