package org.apache.maven.shared.filtering;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @version $Id$
 */
public final class FilteringUtils
{
    private static final String WINDOWS_PATH_PATTERN = "^[a-zA-Z]:\\\\(.*)";

    private static final Pattern PATTERN = Pattern.compile( WINDOWS_PATH_PATTERN) ;

    /**
     * 
     */
    private FilteringUtils()
    {
        // nothing just an util class
    }
    
    // TODO: Correct to handle relative windows paths. (http://jira.codehaus.org/browse/MSHARED-121)
    // How do we distinguish a relative windows path from some other value that happens to contain backslashes??
    public static final String escapeWindowsPath( String val )
    {
        if ( !StringUtils.isEmpty( val ) && PATTERN.matcher( val ).matches() )
        {
            // Adapted from StringUtils.replace in plexus-utils to accommodate pre-escaped backslashes.
            StringBuffer buf = new StringBuffer( val.length() );
            int start = 0, end = 0;
            while ( ( end = val.indexOf( '\\', start ) ) != -1 )
            {
                buf.append( val.substring( start, end ) ).append( "\\\\" );
                start = end + 1;
                
                if ( val.indexOf( '\\', end + 1 ) == end + 1 )
                {
                    start++;
                }
            }
            
            buf.append( val.substring( start ) );
            
            return buf.toString();
        }
        return val;
    }

}
