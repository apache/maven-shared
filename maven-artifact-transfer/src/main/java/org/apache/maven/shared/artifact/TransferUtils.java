package org.apache.maven.shared.artifact;

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
import org.apache.maven.model.Extension;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.ReportPlugin;

/**
 * Utility class
 * 
 * @author Robert Scholte
 */
public final class TransferUtils
{
    private TransferUtils()
    {
    }

    /**
     * @param artifact {@link Artifact}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Artifact artifact )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( artifact.getGroupId() );
        coordinate.setArtifactId( artifact.getArtifactId() );
        coordinate.setVersion( artifact.getVersion() );
        coordinate.setExtension( artifact.getArtifactHandler().getExtension() );
        coordinate.setClassifier( artifact.getClassifier() );

        return coordinate;
    }

    /**
     * Special case: an extension is always of type {@code jar}, so can be transformed to an ArtifactCoordinate.
     * 
     * @param extension {@link Extension}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Extension extension )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( extension.getGroupId() );
        coordinate.setArtifactId( extension.getArtifactId() );
        coordinate.setVersion( extension.getVersion() );

        return coordinate;
    }

    /**
     * Special case: a parent is always of type {@code pom}, so can be transformed to an ArtifactCoordinate.
     * 
     * @param parent {@link Parent}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Parent parent )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( parent.getGroupId() );
        coordinate.setArtifactId( parent.getArtifactId() );
        coordinate.setVersion( parent.getVersion() );
        coordinate.setExtension( "pom" );

        return coordinate;
    }

    /**
     * Special case: a plugin is always of type {@code jar}, so can be transformed to an ArtifactCoordinate.
     * 
     * @param plugin {@link Plugin}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Plugin plugin )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( plugin.getGroupId() );
        coordinate.setArtifactId( plugin.getArtifactId() );
        coordinate.setVersion( plugin.getVersion() );

        return coordinate;
    }
    
    /**
     * Special case: a reportPlugin is always of type {@code jar}, so can be transformed to an ArtifactCoordinate.
     * 
     * @param plugin {@link ReportPlugin}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( ReportPlugin plugin )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( plugin.getGroupId() );
        coordinate.setArtifactId( plugin.getArtifactId() );
        coordinate.setVersion( plugin.getVersion() );

        return coordinate;
    }

}
