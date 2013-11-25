package org.apache.maven.it;

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

import java.util.Arrays;

import junit.framework.TestCase;

public class VerifierTest
    extends TestCase
{
    private void check( String expected, String... lines )
    {
        assertEquals( expected, ForkedLauncher.extractMavenVersion( Arrays.asList( lines ) ) );
    }

    public void testExtractMavenVersion()
    {
        check( "2.0.6", "Maven version: 2.0.6" );

        check( "2.0.10", "Maven version: 2.0.10", "Java version: 1.5.0_22",
               "OS name: \"windows 7\" version: \"6.1\" arch: \"x86\" Family: \"windows\"" );

        check( "3.0", "Apache Maven 3.0 (r1004208; 2010-10-04 13:50:56+0200)", "Java version: 1.5.0_22",
               "OS name: \"windows 7\" version: \"6.1\" arch: \"x86\" Family: \"windows\"" );

        check( "3.0.5", "Apache Maven 3.0.5 (r01de14724cdef164cd33c7c8c2fe155faf9602da; 2013-02-19 14:51:28+0100)",
               "Java version: 1.7.0_25",
               "OS name: \"linux\" version: \"3.11.0-13-generic\" arch: \"amd64\" Family: \"unix\"" );
    }

    public void testFileInJarPresent()
        throws VerificationException
    {
        //File file = new File( "src/test/resources/mshared104.jar!fud.xml" );
        Verifier verifier = new Verifier( "src/test/resources" );
        verifier.assertFilePresent( "mshared104.jar!/pom.xml" );
        verifier.assertFileNotPresent( "mshared104.jar!/fud.xml" );
    }

}
