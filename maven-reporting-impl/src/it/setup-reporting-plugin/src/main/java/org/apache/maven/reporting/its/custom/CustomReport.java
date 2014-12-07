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

/**
 * Typical code to copy as a reporting plugin start: choose the goal name, then implement getOutputName(),
 * getName( Locale ), getDescription( Locale ) and of course executeReport( Locale ).
 */
@Mojo( name = "custom" )
public class CustomReport
    extends AbstractMavenReport
{
    public String getOutputName()
    {
        return "custom-report";
    }

    public String getName( Locale locale )
    {
        return "Custom Maven Report";
    }

    public String getDescription( Locale locale )
    {
        return "Custom Maven Report Description";
    }

    @Override
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        // direct report generation using Doxia: compare with CustomReportRenderer to see the benefits of using
        // ReportRenderer
        getSink().head();
        getSink().title();
        getSink().text( "Custom Report Title" );
        getSink().title_();
        getSink().head_();

        getSink().body();

        getSink().section1();
        getSink().sectionTitle1();
        getSink().text( "section" );
        getSink().sectionTitle1_();

        getSink().text( "Custom Maven Report content." );
        getSink().section1_();

        getSink().body_();
    }
}
