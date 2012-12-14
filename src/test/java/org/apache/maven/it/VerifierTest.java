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

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

public class VerifierTest
    extends TestCase
{

    public void testExtractMavenVersion()
    {
        assertEquals( "2.0.6",
                      Verifier.extractMavenVersion( Arrays.asList( new String[]{ "Maven version: 2.0.6" } ) ) );
        assertEquals( "2.0.10", Verifier.extractMavenVersion( Arrays.asList(
            new String[]{ "Maven version: 2.0.10", "Java version: 1.5.0_22",
                "OS name: \"windows 7\" version: \"6.1\" arch: \"x86\" Family: \"windows\"" } ) ) );
        assertEquals( "3.0", Verifier.extractMavenVersion( Arrays.asList(
            new String[]{ "Apache Maven 3.0 (r1004208; 2010-10-04 13:50:56+0200)", "Java version: 1.5.0_22",
                "OS name: \"windows 7\" version: \"6.1\" arch: \"x86\" Family: \"windows\"" } ) ) );
    }

    public void testFileInJarPresent()
        throws VerificationException
    {
        File file = new File( "src/test/resources/mshared104.jar!fud.xml" );
        Verifier verifier = new Verifier( "src/test/resources" );
        verifier.assertFilePresent( "mshared104.jar!/pom.xml" );
        verifier.assertFileNotPresent( "mshared104.jar!/fud.xml" );
    }

}
