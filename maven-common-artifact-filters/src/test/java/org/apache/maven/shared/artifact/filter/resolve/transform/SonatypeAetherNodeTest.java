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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.maven.shared.artifact.filter.resolve.Node;
import org.junit.Test;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.DefaultDependencyNode;

public class SonatypeAetherNodeTest
{
    @Test
    public void testGAV()
    {
        Node node = new SonatypeAetherNode( newDependencyNode( "g:a:v", null ) );
        
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
        Node node = new SonatypeAetherNode( newDependencyNode( "g:a::c:v", null ) );
        
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
        Node node = new SonatypeAetherNode( newDependencyNode( "g:a:v", "s" ) );
        
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
        Node node = new SonatypeAetherNode( newDependencyNode( "g:a:v", null ) );
        
        assertEquals( "false", node.getDependency().getOptional()  );
        assertEquals( false, node.getDependency().isOptional()  );
        
        node = new SonatypeAetherNode( newDependencyNode( "g:a:v", null, true ) );
        assertEquals( "true", node.getDependency().getOptional()  );
        assertEquals( true, node.getDependency().isOptional()  );

        node = new SonatypeAetherNode( newDependencyNode( "g:a:v", null, false ) );
        assertEquals( "false", node.getDependency().getOptional()  );
        assertEquals( false, node.getDependency().isOptional()  );
    }

    @Test
    public void testExclusions()
    {
        Node node = new SonatypeAetherNode( newDependencyNode( "g:a:v", null, Collections.singletonList( "eg:ea" ) ) );
        assertEquals( 1, node.getDependency().getExclusions().size() );
        
        org.apache.maven.model.Exclusion mavenExclusion = node.getDependency().getExclusions().get( 0 );
        assertEquals( "eg", mavenExclusion.getGroupId() );
        assertEquals( "ea", mavenExclusion.getArtifactId() );
    }

    private DependencyNode newDependencyNode( String string, String scope )
    {
        return new DefaultDependencyNode( new Dependency( new DefaultArtifact( string ), scope ) );
    }
    
    private DependencyNode newDependencyNode( String coor, String scope, boolean optional )
    {
        return new DefaultDependencyNode( new Dependency( new DefaultArtifact( coor ), scope, optional ) );
    }

    private DependencyNode newDependencyNode( String coor, String scope, Collection<String> exclusions )
    {
        Dependency dependency = new Dependency( new DefaultArtifact( coor ), scope );
        
        Collection<Exclusion> aetherExclusions = new ArrayList<Exclusion>( exclusions.size() );
        for ( String exclusion : exclusions )
        {
            String[] ga = exclusion.split( ":" );
            aetherExclusions.add( new Exclusion( ga[0], ga[1], null, null ) );
        }
        dependency = dependency.setExclusions( aetherExclusions );
        
        return new DefaultDependencyNode( dependency );
    }
}
