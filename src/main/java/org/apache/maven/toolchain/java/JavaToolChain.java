package org.apache.maven.toolchain.java;

import org.apache.maven.toolchain.ToolChain;

import java.util.List;

/**
 * @author Jason van Zyl
 */
public interface JavaToolChain
    extends ToolChain
{
    /**
     * Returns a list of {@link java.io.File}s which represents the bootstrap libraries for the
     * runtime environment. The Bootstrap libraries include libraries in JRE's
     * extension directory, if there are any.
     *
     * @return List
     */
    List getBootstrapLibraries();

    /**
     * Returns a list of {@link java.io.File}s which represent the libraries recognized by
     * default by the platform. Usually it corresponds to contents of CLASSPATH
     * environment variable.
     *
     * @return List
     */
    List getStandardLibraries();

    /**
     * Returns a {@link java.io.File}s which represent the locations of the source of the JDK,
     * or an empty collection when the location is not set or is invalid.
     *
     * @return List
     */
    List getSourceDirectories();

    /**
     * Returns a {@link java.io.File}s which represent the locations of the Javadoc for this platform,
     * or empty collection if the location is not set or invalid
     *
     * @return List
     */
    List getJavadocFolders();
}
