package org.apache.maven.model.converter;

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

import org.apache.maven.model.ReportPlugin;

import java.util.Comparator;

/**
 * A comparator for <code>ReportPlugin</code>s. It compares the reportplugins'
 * groupIds and artifactIds.
 *
 * @author Dennis Lundberg
 * @version $Id$
 */
public class ReportPluginComparator
    implements Comparator
{
    public int compare( Object o1, Object o2 )
    {
        // Check for null objects
        if ( o1 == null && o2 == null )
        {
            return 0;
        }
        if ( o1 == null )
        {
            return -1;
        }
        if ( o2 == null )
        {
            return 1;
        }

        // Check classes
        if ( !( o1 instanceof ReportPlugin ) && !( o2 instanceof ReportPlugin ) )
        {
            return 0;
        }
        if ( !( o1 instanceof ReportPlugin ) )
        {
            return -1;
        }
        if ( !( o2 instanceof ReportPlugin ) )
        {
            return 1;
        }
        ReportPlugin plugin1 = (ReportPlugin) o1;
        ReportPlugin plugin2 = (ReportPlugin) o2;

        // Check for null values
        if ( plugin1.getGroupId() == null && plugin2.getGroupId() == null )
        {
            return compareArtifactId( plugin1, plugin2 );
        }
        if ( plugin1.getGroupId() == null )
        {
            return -1;
        }
        if ( plugin2.getGroupId() == null )
        {
            return 1;
        }

        // Compare values
        int answer;
        answer = plugin1.getGroupId().compareTo( plugin2.getGroupId() );
        if( answer == 0)
        {
            answer = compareArtifactId( plugin1, plugin2 );
        }

        return answer;
    }

    private int compareArtifactId( ReportPlugin plugin1, ReportPlugin plugin2 )
    {
        if ( plugin1.getArtifactId() == null && plugin2.getArtifactId() == null )
        {
            return 0;
        }
        if ( plugin1.getArtifactId() == null )
        {
            return -1;
        }
        if ( plugin2.getArtifactId() == null )
        {
            return 1;
        }
        return plugin1.getArtifactId().compareTo( plugin2.getArtifactId() );
    }
}
