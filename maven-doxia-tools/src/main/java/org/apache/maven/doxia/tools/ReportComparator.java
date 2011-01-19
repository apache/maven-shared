package org.apache.maven.doxia.tools;

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

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.apache.maven.reporting.MavenReport;

/**
 * Sorts reports.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 * @todo move to reporting API?
 * @todo allow reports to define their order in some other way?
 */
public class ReportComparator
    implements Comparator<MavenReport>
{
    /** the local */
    private final Locale locale;

    /**
     * Default constructor.
     *
     * @param locale not null
     */
    public ReportComparator( Locale locale )
    {
        if ( locale == null )
        {
            throw new IllegalArgumentException( "locale should be defined" );
        }
        this.locale = locale;
    }

    /** {@inheritDoc} */
    public int compare( MavenReport r1, MavenReport r2 )
    {
        Collator collator = Collator.getInstance( locale );
        return collator.compare( r1.getName( locale ), r2.getName( locale ) );
    }
}
