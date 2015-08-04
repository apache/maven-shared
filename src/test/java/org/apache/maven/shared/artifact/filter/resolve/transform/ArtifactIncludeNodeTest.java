package org.apache.maven.shared.artifact.filter.resolve.transform;

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

import static org.junit.Assert.assertEquals;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.ArtifactStubFactory;
import org.apache.maven.shared.artifact.filter.resolve.Node;
import org.junit.Test;

public class ArtifactIncludeNodeTest
{
    private ArtifactStubFactory artifactFactory = new ArtifactStubFactory();

    @Test
    public void testGAV() throws Exception
    {
        Node node = new ArtifactIncludeNode( newArtifact( "g:a:v", null ) );

        Dependency dependency = node.getDependency();

        assertEquals( "g", dependency.getGroupId() );
        assertEquals( "a", dependency.getArtifactId() );
        assertEquals( "v", dependency.getVersion() );
        assertEquals( "", dependency.getClassifier() );
        // This is different compared to AetherNodes. Here it's based on artifact, which in the end always has a type.
        assertEquals( "jar", dependency.getType() );
    }

    @Test
    public void testClassifier() throws Exception
    {
        Node node = new ArtifactIncludeNode( newArtifact( "g:a::c:v", null ) );

        Dependency dependency = node.getDependency();

        assertEquals( "g", dependency.getGroupId() );
        assertEquals( "a", dependency.getArtifactId() );
        assertEquals( "v", dependency.getVersion() );
        assertEquals( "c", dependency.getClassifier() );
        // empty type stays empty type when using ArtifactStubFactory
        assertEquals( "", dependency.getType() );
    }

    @Test
    public void testType() throws Exception
    {
        Node node = new ArtifactIncludeNode( newArtifact( "g:a:pom:v", null ) );

        Dependency dependency = node.getDependency();

        assertEquals( "g", dependency.getGroupId() );
        assertEquals( "a", dependency.getArtifactId() );
        assertEquals( "v", dependency.getVersion() );
        assertEquals( null, dependency.getClassifier() );
        assertEquals( "pom", dependency.getType() );
    }

    @Test
    public void testScope() throws Exception
    {
        Node node = new ArtifactIncludeNode( newArtifact( "g:a:v", "s" ) );

        Dependency dependency = node.getDependency();

        assertEquals( "g", dependency.getGroupId() );
        assertEquals( "a", dependency.getArtifactId() );
        assertEquals( "v", dependency.getVersion() );
        assertEquals( "", dependency.getClassifier() );
        assertEquals( "jar", dependency.getType() );
        assertEquals( "s", dependency.getScope() );
    }

    private Artifact newArtifact( String coor, String scope )
        throws Exception
    {
        String[] gav = coor.split( ":" );
        if ( gav.length == 3 )
        {
            return artifactFactory.createArtifact( gav[0], gav[1], gav[2], scope );
        }
        else if ( gav.length == 4 )
        {
            return artifactFactory.createArtifact( gav[0], gav[1], gav[3], scope, gav[2], null );
        }
        else if ( gav.length == 5 )
        {
            return artifactFactory.createArtifact( gav[0], gav[1], gav[4], scope, gav[2], gav[3] );
        }
        else
        {
            throw new IllegalArgumentException( "Can't translate coor to an Artifact" );
        }
    }
}
