package org.apache.maven.shared.artifact;

public interface ArtifactCoordinate
{

    public abstract String getGroupId();

    public abstract String getArtifactId();

    public abstract String getVersion();

    public abstract String getType();

    public abstract String getClassifier();

}