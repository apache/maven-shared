package org.apache.maven.shared.artifact.filter.collection;

/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

/**
 * 
 */

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.ArtifactStubFactory;

/**
 * @author clove TestCases for GroupIdFilter
 * @see org.apache.maven.plugin.dependency.testUtils.AbstractArtifactFeatureFilterTestCase
 */
public class TestGroupIdFilter
    extends AbstractArtifactFeatureFilterTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
        filterClass = GroupIdFilter.class;
        ArtifactStubFactory factory = new ArtifactStubFactory( null, false );
        artifacts = factory.getGroupIdArtifacts();
    }

    public void testParsing()
        throws Exception
    {
        parsing();
    }

    public void testFiltering()
        throws Exception
    {
        Set<Artifact> result = filtering();
        for ( Artifact artifact : result )
        {
            assertTrue( artifact.getGroupId().equals( "one" ) || artifact.getGroupId().equals( "two" ) );
        }
    }

    public void testFiltering2()
        throws Exception
    {
        Set<Artifact> result = filtering2();
        for ( Artifact artifact : result )
        {
            assertTrue( artifact.getGroupId().equals( "two" ) || artifact.getGroupId().equals( "four" ) );
        }
    }

    public void testFiltering3()
        throws Exception
    {
        filtering3();
    }

    public void testFiltering4()
        throws Exception
    {
        // include o* from groupIds one,two should leave one
        Set<Artifact> result = filtering();
        assertEquals( 1, result.size() );
        GroupIdFilter filter = new GroupIdFilter( "o", null );
        result = filter.filter( result );
        for ( Artifact artifact : result )
        {
            assertTrue( artifact.getGroupId().equals( "one" ) );

        }

        // exclude on* from groupIds one,two should leave two
        result = filtering();
        assertEquals( 1, result.size() );
        filter = new GroupIdFilter( null, "on" );
        result = filter.filter( result );
        for ( Artifact artifact : result )
        {
            assertTrue( artifact.getGroupId().equals( "two" ) );

        }
    }

    public void testMultipleInclude()
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException,
        IllegalAccessException, InvocationTargetException, ArtifactFilterException
    {
        ArtifactsFilter filter = new GroupIdFilter( "one,two", null );

        assertEquals( 4, artifacts.size() );

        Set<Artifact> result = filter.filter( artifacts );

        assertEquals( 2, result.size() );
    }

    public void testMultipleExclude()
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException,
        IllegalAccessException, InvocationTargetException, ArtifactFilterException
    {
        ArtifactsFilter filter = new GroupIdFilter( null, "one,two" );

        assertEquals( 4, artifacts.size() );

        Set<Artifact> result = filter.filter( artifacts );

        assertEquals( 2, result.size() );
    }
}
