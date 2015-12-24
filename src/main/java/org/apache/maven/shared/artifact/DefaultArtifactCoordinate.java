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

/**
 * Common usage of an ArtifactCoordinate for a Mojo
 * 
 * <pre>
 * &#64;Parameter
 * private DefaultArtifactCoordinate[] artifacts;
 * </pre>
 * 
 * and
 * 
 * <pre>
 * private DefaultArtifactCoordinate artifact = new DefaultArtifactCoordinate();
 * 
 * &#64;Parameter( property = "groupId" )
 * private String groupId;
 * 
 * &#64;Parameter( property = "artifactId" )
 * private String artifactId;
 * 
 * &#64;Parameter( property = "version" )
 * private String version;
 * 
 * &#64;Parameter( property = "classifier" )
 * private String classifier;
 * 
 * &#64;Parameter( property = "type" )
 * private String type;
 * 
 * public void setGroupId( String groupId )
 * {
 *     this.artifact.setGroupId( groupId );
 * }
 * 
 * public void setArtifactId( String artifactId )
 * {
 *     this.artifact.setArtifactId( artifactId );
 * }
 * 
 * public void setVersion( String version )
 * {
 *     this.artifact.setVersion( version );
 * }
 * 
 * public void setClassifier( String classifier )
 * {
 *     this.artifact.setClassifier( classifier );
 * }
 * 
 * public void setType( String type )
 * {
 *     this.artifact.setType( type );
 * }
 * </pre>
 * 
 * <strong>Note: </strong> type is not the same as extension! {@link org.apache.maven.artifact.handler.ArtifactHandler}s
 * are used to map a type to an extension.
 * 
 * @author Robert Scholte
 * @since 3.0
 */
public class DefaultArtifactCoordinate
    implements ArtifactCoordinate
{
    private String groupId;

    private String artifactId;

    private String version;

    private String extension;

    private String classifier;

    @Override
    public final String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId The groupId to be used.
     */
    public final void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    @Override
    public final String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @param artifactId The artifactId to be used.
     */
    public final void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    @Override
    public final String getVersion()
    {
        return version;
    }

    /**
     * @param version The version to be used.
     */
    public final void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public final String getExtension()
    {
        return extension != null ? extension : "jar";
    }

    /**
     * @param extension The extension to be used.
     */
    public final void setExtension( String extension )
    {
        this.extension = extension;
    }

    @Override
    public final String getClassifier()
    {
        return classifier;
    }

    /**
     * @param classifier The classifier to be used.
     */
    public final void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    /**
     * @see org.apache.maven.artifact.DefaultArtifact#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder().append( groupId ).append( ':' )
                        .append( artifactId ).append( ':' ).append( getExtension() );

        if ( classifier != null )
        {
            sb.append( ':' ).append( classifier );
        }

        sb.append( ':' ).append( version );

        return sb.toString();
    }

}
