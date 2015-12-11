package org.apache.maven.shared.io.scan.mapping;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.shared.io.scan.InclusionScanException;
import org.junit.Test;

/**
 * @author jdcasey
 */
public class SuffixMappingTest
{
    @Test
    public void testShouldReturnSingleClassFileForSingleJavaFile()
        throws InclusionScanException
    {
        String base = "path/to/file";

        File basedir = new File( "." );

        System.out.println( "basedir:" + basedir.getAbsolutePath() );
        SuffixMapping mapping = new SuffixMapping( ".java", ".class" );

        Set<File> results = mapping.getTargetFiles( basedir, base + ".java" );

        assertEquals( "Returned wrong number of target files.", 1, results.size() );

        assertEquals( "Target file is wrong.", new File( basedir, base + ".class" ), results.iterator().next() );
    }

    @Test
    public void testShouldNotReturnClassFileWhenSourceFileHasWrongSuffix()
        throws InclusionScanException
    {
        String base = "path/to/file";

        File basedir = new File( "." );

        SuffixMapping mapping = new SuffixMapping( ".java", ".class" );

        Set<File> results = mapping.getTargetFiles( basedir, base + ".xml" );

        assertTrue( "Returned wrong number of target files.", results.isEmpty() );
    }

    @Test
    public void testShouldReturnOneClassFileAndOneXmlFileForSingleJavaFile()
        throws InclusionScanException
    {
        String base = "path/to/file";

        File basedir = new File( "." );

        Set<String> targets = new HashSet<String>();
        targets.add( ".class" );
        targets.add( ".xml" );

        SuffixMapping mapping = new SuffixMapping( ".java", targets );

        Set<File> results = mapping.getTargetFiles( basedir, base + ".java" );

        assertEquals( "Returned wrong number of target files.", 2, results.size() );

        assertTrue( "Targets do not contain class target.", results.contains( new File( basedir, base + ".class" ) ) );

        assertTrue( "Targets do not contain class target.", results.contains( new File( basedir, base + ".xml" ) ) );
    }

    @Test
    public void testShouldReturnNoTargetFilesWhenSourceFileHasWrongSuffix()
        throws InclusionScanException
    {
        String base = "path/to/file";

        File basedir = new File( "." );

        Set<String> targets = new HashSet<String>();
        targets.add( ".class" );
        targets.add( ".xml" );

        SuffixMapping mapping = new SuffixMapping( ".java", targets );

        Set<File> results = mapping.getTargetFiles( basedir, base + ".apt" );

        assertTrue( "Returned wrong number of target files.", results.isEmpty() );
    }

    @Test
    public void testSingleTargetMapper()
        throws InclusionScanException
    {
        String base = "path/to/file";

        File basedir = new File( "target/" );

        SingleTargetMapping mapping = new SingleTargetMapping( ".cs", "/foo" );

        Set<File> results = mapping.getTargetFiles( basedir, base + ".apt" );

        assertTrue( results.isEmpty() );

        results = mapping.getTargetFiles( basedir, base + ".cs" );

        assertEquals( 1, results.size() );
    }
}
