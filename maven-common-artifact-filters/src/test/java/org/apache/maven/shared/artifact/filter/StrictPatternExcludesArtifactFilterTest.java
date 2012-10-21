package org.apache.maven.shared.artifact.filter;

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

import java.util.List;

/**
 * Tests <code>StrictPatternExcludesArtifactFilter</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see StrictPatternExcludesArtifactFilter
 */
public class StrictPatternExcludesArtifactFilterTest
    extends AbstractStrictPatternArtifactFilterTest
{
    // AbstractStrictPatternArtifactFilterTest methods ------------------------

    /*
     * @see org.apache.maven.shared.artifact.filter.AbstractStrictPatternArtifactFilterTest#createFilter(java.util.List)
     */
    protected AbstractStrictPatternArtifactFilter createFilter( List<String> patterns )
    {
        return new StrictPatternExcludesArtifactFilter( patterns );
    }

    /*
     * @see org.apache.maven.shared.artifact.filter.AbstractStrictPatternArtifactFilterTest#assertFilter(boolean,
     *      java.lang.String)
     */
    protected void assertFilter( boolean expected, String pattern )
    {
        super.assertFilter( !expected, pattern );
    }
}
