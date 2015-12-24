package org.apache.maven.shared.dependency;

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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

/**
 * Utility class
 * 
 * @author Robert Scholte
 *
 */
public final class TransferUtils
{
    private TransferUtils()
    {
    }
    
    public static DependencyCoordinate toDependencyCoordinate( Dependency dependency )
    {
        DefaultDependencyCoordinate coordinate = new DefaultDependencyCoordinate();
        
        coordinate.setGroupId( dependency.getGroupId() );
        coordinate.setArtifactId( dependency.getArtifactId() );
        coordinate.setVersion( dependency.getVersion() );
        coordinate.setType( dependency.getType() );
        coordinate.setClassifier( dependency.getClassifier() );
        
        return coordinate;
    }

    public static DependencyCoordinate toDependencyCoordinate( Extension extension )
    {
        DefaultDependencyCoordinate coordinate = new DefaultDependencyCoordinate();
        
        coordinate.setGroupId( extension.getGroupId() );
        coordinate.setArtifactId( extension.getArtifactId() );
        coordinate.setVersion( extension.getVersion() );
        
        return coordinate;
    }

    public static DependencyCoordinate toDependencyCoordinate( MavenProject project )
    {
        DefaultDependencyCoordinate coordinate = new DefaultDependencyCoordinate();
        
        coordinate.setGroupId( project.getGroupId() );
        coordinate.setArtifactId( project.getArtifactId() );
        coordinate.setVersion( project.getVersion() );
        coordinate.setType( project.getPackaging() );
        
        return coordinate;
    }

    public static DependencyCoordinate toDependencyCoordinate( Model model )
    {
        DefaultDependencyCoordinate coordinate = new DefaultDependencyCoordinate();
        
        coordinate.setGroupId( model.getGroupId() );
        coordinate.setArtifactId( model.getArtifactId() );
        coordinate.setVersion( model.getVersion() );
        coordinate.setType( model.getPackaging() );
        
        return coordinate;
    }

    public static DependencyCoordinate toDependencyCoordinate( Parent parent )
    {
        DefaultDependencyCoordinate coordinate = new DefaultDependencyCoordinate();
        
        coordinate.setGroupId( parent.getGroupId() );
        coordinate.setArtifactId( parent.getArtifactId() );
        coordinate.setVersion( parent.getVersion() );
        coordinate.setType( "pom" );
        
        return coordinate;
    }

    public static DependencyCoordinate toDependencyCoordinate( Plugin plugin )
    {
        DefaultDependencyCoordinate coordinate = new DefaultDependencyCoordinate();
        
        coordinate.setGroupId( plugin.getGroupId() );
        coordinate.setArtifactId( plugin.getArtifactId() );
        coordinate.setVersion( plugin.getVersion() );
        
        return coordinate;
    }
}
