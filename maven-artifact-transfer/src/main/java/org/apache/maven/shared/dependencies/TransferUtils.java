package org.apache.maven.shared.dependencies;

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
import org.apache.maven.model.ReportPlugin;

/**
 * Utility class to convert different things like {@link Dependency}, {@link Extension}, {@link Model}, {@link Parent},
 * {@link Plugin} and {@link ReportPlugin} into {@link DependableCoordinate}.
 * 
 * @author Robert Scholte
 */
public final class TransferUtils
{
    private TransferUtils()
    {
    }
    
    /**
     * @param dependency {@link Dependency} to be converted to {@link DependableCoordinate}
     * @return {@link DependableCoordinate}
     */
    public static DependableCoordinate toDependableCoordinate( Dependency dependency )
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        
        coordinate.setGroupId( dependency.getGroupId() );
        coordinate.setArtifactId( dependency.getArtifactId() );
        coordinate.setVersion( dependency.getVersion() );
        coordinate.setType( dependency.getType() );
        coordinate.setClassifier( dependency.getClassifier() );
        
        return coordinate;
    }

    /**
     * @param extension {@link Extension} to be converted to {@link DependableCoordinate}
     * @return {@link DependableCoordinate}
     */
    public static DependableCoordinate toDependableCoordinate( Extension extension )
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        
        coordinate.setGroupId( extension.getGroupId() );
        coordinate.setArtifactId( extension.getArtifactId() );
        coordinate.setVersion( extension.getVersion() );
        
        return coordinate;
    }

    /**
     * @param model {@link Model} coordinates to be converted to {@link DependableCoordinate}.
     * @return {@link DependableCoordinate}
     */
    public static DependableCoordinate toDependableCoordinate( Model model )
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        
        coordinate.setGroupId( model.getGroupId() );
        coordinate.setArtifactId( model.getArtifactId() );
        coordinate.setVersion( model.getVersion() );
        coordinate.setType( model.getPackaging() );
        
        return coordinate;
    }

    /**
     * @param parent {@link Parent parent} coordinates to be converted to {@link DependableCoordinate}.
     * @return {@link DependableCoordinate}.
     */
    public static DependableCoordinate toDependableCoordinate( Parent parent )
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        
        coordinate.setGroupId( parent.getGroupId() );
        coordinate.setArtifactId( parent.getArtifactId() );
        coordinate.setVersion( parent.getVersion() );
        coordinate.setType( "pom" );
        
        return coordinate;
    }

    /**
     * @param plugin The {@link Plugin plugin} coordiantes which should be converted. 
     * @return {@link DependableCoordinate}.
     */
    public static DependableCoordinate toDependableCoordinate( Plugin plugin )
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        
        coordinate.setGroupId( plugin.getGroupId() );
        coordinate.setArtifactId( plugin.getArtifactId() );
        coordinate.setVersion( plugin.getVersion() );
        
        return coordinate;
    }
    
    /**
     * Convert {@link ReportPlugin plugin} coordinates to {@link DependableCoordinate}.
     * @param plugin The {@link ReportPlugin plugin} to be converted to {@link DependableCoordinate}.
     * @return The converted {@link DependableCoordinate} coordinates.
     */
    public static DependableCoordinate toDependableCoordinate( ReportPlugin plugin )
    {
        DefaultDependableCoordinate coordinate = new DefaultDependableCoordinate();
        
        coordinate.setGroupId( plugin.getGroupId() );
        coordinate.setArtifactId( plugin.getArtifactId() );
        coordinate.setVersion( plugin.getVersion() );
        
        return coordinate;
    }

}
