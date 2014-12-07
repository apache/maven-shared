package org.apache.maven.reporting.its.custom;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Locale;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.reporting.MavenReportRenderer;

/**
 * Typical code to copy as a reporting plugin start: choose the goal name, then implement getOutputName(),
 * getName( Locale ), getDescription( Locale ) and of course executeReport( Locale ).
 * Notice the implementation of the rendering is done in a separate class to improve separation of concerns
 * and to benefit from helpers.
 */
@Mojo( name = "custom-renderer" )
public class CustomReportWithRenderer
    extends AbstractMavenReport
{
    public String getOutputName()
    {
        return "custom-report-with-renderer";
    }

    public String getName( Locale locale )
    {
        return "Custom Maven Report with Renderer";
    }

    public String getDescription( Locale locale )
    {
        return "Custom Maven Report with Renderer Description";
    }

    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        // use a AbstractMavenReportRenderer subclass to benefit from helpers
        MavenReportRenderer r = new CustomReportRenderer( getSink() );
        r.render();
    }
}
