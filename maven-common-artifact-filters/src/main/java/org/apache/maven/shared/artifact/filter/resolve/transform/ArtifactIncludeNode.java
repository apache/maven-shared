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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.shared.artifact.filter.resolve.Node;

/**
 * 
 * @author Robert Scholte
 * @since 3.0
 */
class ArtifactIncludeNode implements Node
{
    private final Artifact artifact;
    
    public ArtifactIncludeNode( Artifact artifact )
    {
        this.artifact = artifact;
    }
    
    /**
     * Note: an artifact doesn't contain exclusion information, so it won't be available here.
     * When required switch to filtering based on Aether
     * 
     * @see EclipseAetherNode
     * @see SonatypeAetherNode
     */
    @Override
    public Dependency getDependency()
    {
        org.apache.maven.model.Dependency mavenDependency = new org.apache.maven.model.Dependency();
        mavenDependency.setGroupId( artifact.getGroupId() );
        mavenDependency.setArtifactId( artifact.getArtifactId() );
        mavenDependency.setVersion( artifact.getVersion() );
        mavenDependency.setClassifier( artifact.getClassifier() );
        mavenDependency.setType( artifact.getType() );
        mavenDependency.setScope( artifact.getScope() );
        mavenDependency.setOptional( artifact.isOptional() );
        // no setExcludes possible
        
        return mavenDependency;
    }

    
}
