package org.apache.maven.shared.dependency.tree;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.jmock.MockObjectTestCase;

/**
 * Provides utility methods for testing dependency nodes.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public abstract class AbstractDependencyNodeTest extends MockObjectTestCase
{
    // protected methods ------------------------------------------------------

    protected DependencyNode createNode( String id )
    {
        return new DependencyNode( createArtifact( id ) );
    }

    protected Artifact createArtifact( String id )
    {
        String[] tokens = id.split( ":" );

        return createArtifact( get( tokens, 0 ), get( tokens, 1 ), get( tokens, 2 ), get( tokens, 3 ), get( tokens, 4 ) );
    }

    protected Artifact createArtifact( String groupId, String artifactId, String version )
    {
        return createArtifact( groupId, artifactId, "jar", version );
    }

    protected Artifact createArtifact( String groupId, String artifactId, String type, String version )
    {
        return createArtifact( groupId, artifactId, type, version, null );
    }

    protected Artifact createArtifact( String groupId, String artifactId, String type, String version, String scope )
    {
        ArtifactStub artifact = new ArtifactStub();

        artifact.setGroupId( groupId );
        artifact.setArtifactId( artifactId );
        artifact.setType( type );
        artifact.setVersion( version );
        artifact.setScope( scope );

        return artifact;
    }

    // private methods --------------------------------------------------------

    private String get( String[] array, int index )
    {
        return ( index < array.length ) ? array[index] : null;
    }
}
