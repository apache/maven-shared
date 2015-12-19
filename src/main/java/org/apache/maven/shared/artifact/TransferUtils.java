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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

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
        coordinate.setType( artifact.getType() );
        coordinate.setClassifier( artifact.getClassifier() );

        return coordinate;
    }

    /**
     * @param dependency {@link Dependency}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Dependency dependency )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( dependency.getGroupId() );
        coordinate.setArtifactId( dependency.getArtifactId() );
        coordinate.setVersion( dependency.getVersion() );
        coordinate.setType( dependency.getType() );
        coordinate.setClassifier( dependency.getClassifier() );

        return coordinate;
    }

    /**
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
     * @param project {@link MavenProject}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( MavenProject project )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( project.getGroupId() );
        coordinate.setArtifactId( project.getArtifactId() );
        coordinate.setVersion( project.getVersion() );
        coordinate.setType( project.getPackaging() );

        return coordinate;
    }

    /**
     * @param parent {@link Parent}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Parent parent )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( parent.getGroupId() );
        coordinate.setArtifactId( parent.getArtifactId() );
        coordinate.setVersion( parent.getVersion() );
        coordinate.setType( "pom" );

        return coordinate;
    }

    /**
     * @param plugin {@link Plugin}
     * @return {@link ArtifactCoordinate}
     */
    public static ArtifactCoordinate toArtifactCoordinate( Plugin plugin )
    {
        DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();

        coordinate.setGroupId( plugin.getGroupId() );
        coordinate.setArtifactId( plugin.getArtifactId() );
        coordinate.setVersion( plugin.getVersion() );
        coordinate.setType( "maven-plugin" );

        return coordinate;
    }

    /**
     * @param artifact {@link Artifact}
     * @return {@link Dependency}
     */
    public static Dependency toDependency( Artifact artifact )
    {
        Dependency dependency = new Dependency();

        dependency.setGroupId( artifact.getGroupId() );
        dependency.setArtifactId( artifact.getArtifactId() );
        dependency.setVersion( artifact.getVersion() );
        dependency.setType( artifact.getType() );
        dependency.setClassifier( artifact.getClassifier() );
        dependency.setScope( artifact.getScope() );

        return dependency;
    }

    /**
     * @param coordinate {@link ArtifactCoordinate}
     * @return {@link Dependency}
     */
    public static Dependency toDependency( ArtifactCoordinate coordinate )
    {
        Dependency dependency = new Dependency();

        dependency.setGroupId( coordinate.getGroupId() );
        dependency.setArtifactId( coordinate.getArtifactId() );
        dependency.setVersion( coordinate.getVersion() );
        dependency.setType( coordinate.getType() );
        dependency.setClassifier( coordinate.getClassifier() );

        return dependency;
    }

}
