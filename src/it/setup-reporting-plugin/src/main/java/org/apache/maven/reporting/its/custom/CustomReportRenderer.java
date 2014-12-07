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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

/**
 * A report renderer, subclassing AbstractMavenReportRenderer to benefit from helpers.
 */
public class CustomReportRenderer
    extends AbstractMavenReportRenderer
{
    public CustomReportRenderer( Sink sink )
    {
        super( sink );
    }

    public String getTitle()
    {
        return "Custom Report Renderer Title";
    }

    public void renderBody()
    {
        startSection( "section" );

        text( "Custom Maven Report with Renderer content." );

        endSection();
    }
}
