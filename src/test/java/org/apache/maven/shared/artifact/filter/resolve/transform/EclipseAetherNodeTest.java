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

import org.apache.maven.shared.artifact.filter.resolve.Node;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.junit.Test;

public class EclipseAetherNodeTest
{
    @Test
    public void testGAV()
    {
        Node node = new EclipseAetherNode( newDependencyNode( "g:a:v", null ) );
        
        org.apache.maven.model.Dependency mavenDependency = node.getDependency();
        
        assertEquals( "g", mavenDependency.getGroupId()  );
        assertEquals( "a", mavenDependency.getArtifactId()  );
        assertEquals( "v", mavenDependency.getVersion() );
        assertEquals( "", mavenDependency.getClassifier() );
        assertEquals( null, mavenDependency.getType() );
        assertEquals( "", mavenDependency.getScope() );
    }

    @Test
    public void testClassifier()
    {
        Node node = new EclipseAetherNode( newDependencyNode( "g:a::c:v", null ) );
        
        org.apache.maven.model.Dependency mavenDependency = node.getDependency();
        
        assertEquals( "g", mavenDependency.getGroupId()  );
        assertEquals( "a", mavenDependency.getArtifactId()  );
        assertEquals( "v", mavenDependency.getVersion() );
        assertEquals( "c", mavenDependency.getClassifier() );
        assertEquals( null, mavenDependency.getType() );
        assertEquals( "", mavenDependency.getScope() );
    }
    
    @Test
    public void testScope()
    {
        Node node = new EclipseAetherNode( newDependencyNode( "g:a:c:v", "s" ) );
        
        org.apache.maven.model.Dependency mavenDependency = node.getDependency();
        
        assertEquals( "g", mavenDependency.getGroupId()  );
        assertEquals( "a", mavenDependency.getArtifactId()  );
        assertEquals( "v", mavenDependency.getVersion() );
        assertEquals( "", mavenDependency.getClassifier() );
        assertEquals( null, mavenDependency.getType() );
        assertEquals( "s", mavenDependency.getScope() );
    }

    @Test
    public void testOptional()
    {
        Node node = new EclipseAetherNode( newDependencyNode( "g:a:v", null, null ) );
        
        assertEquals( null, node.getDependency().getOptional()  );
        assertEquals( false, node.getDependency().isOptional()  );
        
        node = new EclipseAetherNode( newDependencyNode( "g:a:v", null, true ) );
        assertEquals( "true", node.getDependency().getOptional()  );
        assertEquals( true, node.getDependency().isOptional()  );

        node = new EclipseAetherNode( newDependencyNode( "g:a:v", null, false ) );
        assertEquals( "false", node.getDependency().getOptional()  );
        assertEquals( false, node.getDependency().isOptional()  );
    }

    private DependencyNode newDependencyNode( String coor, String scope )
    {
        return new DefaultDependencyNode( new Dependency( new DefaultArtifact( coor ), scope ) );
    }
    
    private DependencyNode newDependencyNode( String coor, String scope, Boolean optional )
    {
        return new DefaultDependencyNode( new Dependency( new DefaultArtifact( coor ), scope, optional ) );
    }

}
