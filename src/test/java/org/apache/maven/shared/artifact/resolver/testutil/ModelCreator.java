package org.apache.maven.shared.artifact.resolver.testutil;

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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

public class ModelCreator
{
    
    private Model model;
    
    public ModelCreator()
    {
        model = new Model();
        model.setModelVersion( "4.0.0" );
    }
    
    public ModelCreator withCoordinate( String groupId, String artifactId, String version )
    {
        model.setGroupId( groupId );
        model.setArtifactId( artifactId );
        model.setVersion( version );
        return this;
    }
    
    public ModelCreator withDependency( String groupId, String artifactId, String version )
    {
        Dependency dep = new Dependency();
        dep.setGroupId( groupId );
        dep.setArtifactId( artifactId );
        dep.setVersion( version );
        
        model.addDependency( dep );
        return this;
    }
    
    public Model getModel()
    {
        return model;
    }

    public ModelCreator withDefaultCoordinate()
    {
        return withCoordinate( "group.id", "artifact-id", "1" );
    }
    
    public ModelCreator withArtifactId( String artifactId )
    {
        model.setArtifactId( artifactId );
        return this;
    }

    public ModelCreator withManagedDependency( String groupId, String artifactId, String version )
    {
        DependencyManagement dm = model.getDependencyManagement();
        if ( dm == null )
        {
            dm = new DependencyManagement();
            model.setDependencyManagement( dm );
        }
        
        Dependency dep = new Dependency();
        dep.setGroupId( groupId );
        dep.setArtifactId( artifactId );
        dep.setVersion( version );
        
        dm.addDependency( dep );
        
        return this;
    }
}
