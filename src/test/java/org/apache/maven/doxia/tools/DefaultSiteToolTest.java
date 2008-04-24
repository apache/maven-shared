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

import junit.framework.TestCase;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DefaultSiteToolTest
    extends TestCase
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /**
     * @throws Exception
     */
    public void testGetNormalizedPath()
        throws Exception
    {
        DefaultSiteTool tool = new DefaultSiteTool();

        assertEquals( null, tool.getNormalizedPath( null ) );
        assertEquals( "", tool.getNormalizedPath( "" ) );
        assertEquals( "foo", tool.getNormalizedPath( "foo" ) );
        assertEquals( "foo/bar", tool.getNormalizedPath( "foo/bar" ) );
        assertEquals( "foo/bar", tool.getNormalizedPath( "foo\\bar" ) );
        assertEquals( "foo/bar", tool.getNormalizedPath( "foo/./bar" ) );
        assertEquals( "bar", tool.getNormalizedPath( "foo/../bar" ) );
        assertEquals( "foo", tool.getNormalizedPath( "./foo" ) );
        assertEquals( "../foo", tool.getNormalizedPath( "../foo" ) );
        assertEquals( "../../foo", tool.getNormalizedPath( "../../foo" ) );
    }

}
