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

import java.io.IOException;
import java.io.File;
import java.util.Locale;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.shared.utils.io.FileUtils;

/**
 * Typical code to copy as an external reporting plugin start: choose the goal name, then implement getOutputName(),
 * getName( Locale ), getDescription( Locale ) and of course executeReport( Locale ).
 */
@Mojo( name = "external" )
public class ExternalReport
    extends AbstractMavenReport
{
    /**
     * The name of the destination directory inside the site.
     */
    @Parameter( property = "destDir", defaultValue = "external" )
    private String destDir;

    public String getOutputName()
    {
        return destDir + "/report";
    }

    public String getName( Locale locale )
    {
        return "External Maven Report";
    }

    public String getDescription( Locale locale )
    {
        return "External Maven Report Description";
    }

    public boolean isExternalReport()
    {
        return true;
    }

    @Override
    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        try
        {
            executeExternalTool( getOutputDirectory() + '/' + destDir );
        }
        catch ( IOException ioe )
        {
            throw new MavenReportException( "IO exception while executing external reporting tool", ioe );
        }
    }

    /**
     * Invoke the external tool to generate report.
     *
     * @param destination destination directory
     */
    private void executeExternalTool( String destination )
        throws IOException
    {
        // demo implementation, to be replaced with effective tool
        File dest = new File( destination );

        dest.mkdirs();

        File report = new File( dest, "report.html" );
        FileUtils.fileWrite( report, "UTF-8", "<html><body><h1>External Report</h1></body></html>" );
    }
}
