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

import junit.framework.Assert;
import org.apache.maven.model.converter.ProjectConverterException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;

/**
 * @author Dennis Lundberg
 * @version $Id$
 */
public class PCCCastorTest
    extends AbstractPCCTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();

        pluginConfigurationConverter = new PCCCastor();
    }

    public void testBuildConfiguration()
    {
        try
        {
            projectProperties.load( getClassLoader().getResourceAsStream( "PCCCastorTest.properties" ) );

            pluginConfigurationConverter.buildConfiguration( configuration, v3Model, projectProperties );

            String value = configuration.getChild( "dest" ).getValue();
            Assert.assertEquals( "check dest value", "generatedSourcesDirectory", value );

            value = configuration.getChild( "properties" ).getValue();
            Assert.assertEquals( "check properties value", "propertiesDirectory/castorbuilder.properties", value );

            value = configuration.getChild( "tstamp" ).getValue();
            Assert.assertEquals( "check tstamp value", "tstampDirectory", value );
        }
        catch ( ProjectConverterException e )
        {
            Assert.fail( e.getMessage() );
        }
        catch ( IOException e )
        {
            Assert.fail( "Unable to find the requested resource." );
        }
    }
}