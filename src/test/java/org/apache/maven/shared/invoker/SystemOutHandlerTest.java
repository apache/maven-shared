package org.apache.maven.shared.invoker;

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

import org.junit.Test;

public class SystemOutHandlerTest
{

    @Test
    public void testConsumeWithoutAlwaysFlush()
    {
        logTestStart();
        new SystemOutHandler( false ).consumeLine( "This is a test." );
    }

    @Test
    public void testConsumeWithAlwaysFlush()
    {
        logTestStart();
        new SystemOutHandler( true ).consumeLine( "This is a test." );
    }

    @Test
    public void testConsumeNullLine()
    {
        logTestStart();
        new SystemOutHandler().consumeLine( null );
    }

    // this is just a debugging helper for separating unit test output...
    private void logTestStart()
    {
        NullPointerException npe = new NullPointerException();
        StackTraceElement element = npe.getStackTrace()[1];

        System.out.println( "Starting: " + element.getMethodName() );
    }

}
