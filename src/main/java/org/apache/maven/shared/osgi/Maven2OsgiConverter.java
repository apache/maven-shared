package org.apache.maven.shared.osgi;

import org.apache.maven.artifact.Artifact;

/**
 * Converter from Maven groupId,artifactId and versions to OSGi Bundle-SymbolicName and version
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public interface Maven2OsgiConverter
{

    /**
     * Get the OSGi symbolic name for the artifact
     * 
     * @param artifact
     * @return the Bundle-SymbolicName manifest property
     */
    String getBundleSymbolicName( Artifact artifact );

    String getBundleFileName( Artifact artifact );

    /**
     * Convert a Maven version into an OSGi compliant version
     * 
     * @param artifact Maven artifact
     * @return the OSGi version
     */
    String getVersion( Artifact artifact );

    /**
     * Convert a Maven version into an OSGi compliant version
     * 
     * @param version Maven version
     * @return the OSGi version
     */
    String getVersion( String version );

}