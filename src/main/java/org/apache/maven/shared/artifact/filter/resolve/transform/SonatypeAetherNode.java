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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.artifact.filter.resolve.Node;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.util.artifact.ArtifactProperties;

/**
 * Adapter of a Sonatype Aether DependencyNode for common Node
 * 
 * @author Robert Scholte
 * @since 3.0
 */
class SonatypeAetherNode implements Node
{

    private final DependencyNode node;

    SonatypeAetherNode( DependencyNode node )
    {
        this.node = node;
    }


    @Override
    public org.apache.maven.model.Dependency getDependency()
    {
        Dependency nodeDependency = node.getDependency();

        if ( nodeDependency == null )
        {
            return null;
        }
        
        org.apache.maven.model.Dependency mavenDependency = new org.apache.maven.model.Dependency();
        mavenDependency.setGroupId( nodeDependency.getArtifact().getGroupId() );
        mavenDependency.setArtifactId( nodeDependency.getArtifact().getArtifactId() );
        mavenDependency.setVersion( nodeDependency.getArtifact().getVersion() );
        mavenDependency.setClassifier( nodeDependency.getArtifact().getClassifier() );
        mavenDependency.setType( nodeDependency.getArtifact().getProperty( ArtifactProperties.TYPE, null ) );
        mavenDependency.setScope( nodeDependency.getScope() );
        mavenDependency.setOptional( nodeDependency.isOptional() );
        if ( nodeDependency.getExclusions() != null )
        {
            List<org.apache.maven.model.Exclusion> mavenExclusions =
                new ArrayList<org.apache.maven.model.Exclusion>( nodeDependency.getExclusions().size() );

            for ( Exclusion aetherExclusion : nodeDependency.getExclusions() )
            {
                org.apache.maven.model.Exclusion mavenExclusion = new org.apache.maven.model.Exclusion();

                mavenExclusion.setGroupId( aetherExclusion.getGroupId() );
                mavenExclusion.setArtifactId( aetherExclusion.getArtifactId() );
                // that's all folks, although Aether has more metadata

                mavenExclusions.add( mavenExclusion );
            }

            mavenDependency.setExclusions( mavenExclusions );
        }

        return mavenDependency;
    }
     
}
