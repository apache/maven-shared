package org.apache.maven.shared.io.location;

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

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.shared.io.logging.MessageHolder;

/**
 * The locator strategy.
 *
 */
public class ArtifactLocatorStrategy
    implements LocatorStrategy
{
    private final ArtifactFactory factory;

    private final ArtifactResolver resolver;

    private String defaultArtifactType = "jar";

    private final ArtifactRepository localRepository;

    private final List<ArtifactRepository> remoteRepositories;

    private String defaultClassifier = null;

    /**
     * @param factory {@link ArtifactFactory}
     * @param resolver {@link ArtifactResolver}
     * @param localRepository {@link ArtifactRepository}
     * @param remoteRepositories {@link ArtifactRepository}
     */
    public ArtifactLocatorStrategy( ArtifactFactory factory, ArtifactResolver resolver,
                                    ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories )
    {
        this.factory = factory;
        this.resolver = resolver;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
    }

    /**
     * @param factory {@link ArtifactFactory}
     * @param resolver {@link ArtifactResolver}
     * @param localRepository {@link ArtifactRepository}
     * @param remoteRepositories {@link ArtifactRepository}
     * @param defaultArtifactType default artifact type.
     */
    public ArtifactLocatorStrategy( ArtifactFactory factory, ArtifactResolver resolver,
                                    ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories,
                                    String defaultArtifactType )
    {
        this.factory = factory;
        this.resolver = resolver;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.defaultArtifactType = defaultArtifactType;
    }

    /**
     * @param factory {@link ArtifactFactory}
     * @param resolver {@link ArtifactResolver}
     * @param localRepository {@link ArtifactRepository}
     * @param remoteRepositories {@link ArtifactRepository}
     * @param defaultArtifactType default artifact type.
     * @param defaultClassifier default classifier.
     */
    public ArtifactLocatorStrategy( ArtifactFactory factory, ArtifactResolver resolver,
                                    ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories,
                                    String defaultArtifactType, String defaultClassifier )
    {
        this.factory = factory;
        this.resolver = resolver;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.defaultArtifactType = defaultArtifactType;
        this.defaultClassifier = defaultClassifier;
    }

    /**
     * Assumes artifact identity is given in a set of comma-delimited tokens of
     * the form: <code>groupId:artifactId:version:type:classifier</code>, where
     * type and classifier are optional.
     * @param locationSpecification location spec.
     * @param messageHolder {@link MessageHolder}
     * @return location.
     */
    public Location resolve( String locationSpecification, MessageHolder messageHolder )
    {
        String[] parts = locationSpecification.split( ":" );

        Location location = null;

        if ( parts.length > 2 )
        {
            String groupId = parts[0];
            String artifactId = parts[1];
            String version = parts[2];

            String type = defaultArtifactType;
            if ( parts.length > 3 )
            {
                if ( parts[3].trim().length() > 0 )
                {
                    type = parts[3];
                }
            }

            String classifier = defaultClassifier;
            if ( parts.length > 4 )
            {
                classifier = parts[4];
            }

            if ( parts.length > 5 )
            {
                messageHolder.newMessage().append( "Location specification has unused tokens: \'" );

                for ( int i = 5; i < parts.length; i++ )
                {
                    messageHolder.append( ":" + parts[i] );
                }
            }

            Artifact artifact;
            if ( classifier == null )
            {
                artifact = factory.createArtifact( groupId, artifactId, version, null, type );
            }
            else
            {
                artifact = factory.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
            }

            try
            {
                resolver.resolve( artifact, remoteRepositories, localRepository );

                if ( artifact.getFile() != null )
                {
                    location = new ArtifactLocation( artifact, locationSpecification );
                }
                else
                {
                    messageHolder.addMessage( "Supposedly resolved artifact: " + artifact.getId()
                        + " does not have an associated file." );
                }
            }
            catch ( ArtifactResolutionException e )
            {
                messageHolder.addMessage( "Failed to resolve artifact: " + artifact.getId() + " for location: "
                    + locationSpecification, e );
            }
            catch ( ArtifactNotFoundException e )
            {
                messageHolder.addMessage( "Failed to resolve artifact: " + artifact.getId() + " for location: "
                    + locationSpecification, e );
            }
        }
        else
        {
            messageHolder.addMessage( "Invalid artifact specification: \'" + locationSpecification
                + "\'. Must contain at least three fields, separated by ':'." );
        }

        return location;
    }

}
