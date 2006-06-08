package org.apache.maven.shared.io.location;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.shared.io.logging.MessageHolder;

public class ArtifactLocatorStrategy
    implements LocatorStrategy
{
    private final ArtifactFactory factory;

    private final ArtifactResolver resolver;

    private String defaultArtifactType = "jar";

    private final ArtifactRepository localRepository;

    private final List remoteRepositories;

    public ArtifactLocatorStrategy( ArtifactFactory factory, ArtifactResolver resolver,
                                    ArtifactRepository localRepository, List remoteRepositories )
    {
        this.factory = factory;
        this.resolver = resolver;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
    }

    public ArtifactLocatorStrategy( ArtifactFactory factory, ArtifactResolver resolver,
                                    ArtifactRepository localRepository, List remoteRepositories,
                                    String defaultArtifactType )
    {
        this.factory = factory;
        this.resolver = resolver;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.defaultArtifactType = defaultArtifactType;
    }

    /**
     * Assumes artifact identity is given in a set of comma-delimited tokens of
     * the form: <code>groupId:artifactId:version:type:classifier</code>, where
     * type and classifier are optional.
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

            String classifier = null;
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
