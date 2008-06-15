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
 * @version $Id: PCCPmdTest.java 661727 2008-05-30 14:21:49Z bentmann $
 */
public class PCCJalopyTest
    extends AbstractPCCTest
{
    protected void setUp()
        throws Exception
    {
        super.setUp();

        pluginConfigurationConverter = new PCCJalopy();
    }

    public void testBuildConfiguration()
    {
        try
        {
            projectProperties.load( getClassLoader().getResourceAsStream( "PCCJalopyTest.properties" ) );

            pluginConfigurationConverter.buildConfiguration( configuration, v3Model, projectProperties );

            String value = configuration.getChild( "convention" ).getValue();
            Assert.assertEquals( "check convention value", "jalopy_conventions.xml", value );

            value = configuration.getChild( "failOnError" ).getValue();
            Assert.assertEquals( "check failOnError value", "false", value );

            value = configuration.getChild( "fileFormat" ).getValue();
            Assert.assertEquals( "check fileFormat value", "UNIX", value );

            value = configuration.getChild( "history" ).getValue();
            Assert.assertEquals( "check history value", "FILE", value );

            value = configuration.getChild( "srcExcludesPattern" ).getValue();
            Assert.assertEquals( "check srcExcludesPattern value", "**/*Exclude.java", value );

            value = configuration.getChild( "srcIncludesPattern" ).getValue();
            Assert.assertEquals( "check srcIncludesPattern value", "**/*.java", value );

            value = configuration.getChild( "testExcludesPattern" ).getValue();
            Assert.assertEquals( "check testExcludesPattern value", "**/*Test.java", value );

            value = configuration.getChild( "testIncludesPattern" ).getValue();
            Assert.assertEquals( "check testIncludesPattern value", "**/*TestCase.java", value );
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