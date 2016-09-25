package org.apache.maven.shared.jar;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

/**
 * Open a JAR file to be analyzed. Note that once created, the {@link #closeQuietly()} method should be called to
 * release the associated file handle.
 * <p/>
 * Typical usage:
 * <pre>
 *  JarAnalyzer jar = new JarAnalyzer( jarFile );
 * <p/>
 *  try
 *  {
 *      // do some analysis, such as:
 *      jarClasses = jarClassAnalyzer.analyze( jar );
 *  }
 *  finally
 *  {
 *      jar.closeQuietly();
 *  }
 * <p/>
 *  // use jar.getJarData() in some way, or the data returned by the JAR analyzer. jar itself can no longer be used.
 * </pre>
 * <p/>
 * Note: that the actual data is separated from this class by design to minimise the chance of forgetting to close the
 * JAR file. The {@link org.apache.maven.shared.jar.JarData} class exposed, as well as any data returned by actual
 * analyzers that use this class, can be used safely once this class is out of scope.
 *
 * @see org.apache.maven.shared.jar.identification.JarIdentificationAnalysis#analyze(JarAnalyzer)
 * @see org.apache.maven.shared.jar.classes.JarClassesAnalysis#analyze(JarAnalyzer)
 */
public class JarAnalyzer
{
    /**
     * Pattern to filter JAR entries for class files.
     *
     * @todo why are inner classes and other potentially valid classes omitted? (It flukes it by finding everything after $)
     */
    private static final Pattern CLASS_FILTER = Pattern.compile( "[A-Za-z0-9]*\\.class$" );

    /**
     * Pattern to filter JAR entries for Maven POM files.
     */
    private static final Pattern MAVEN_POM_FILTER = Pattern.compile( "META-INF/maven/.*/pom\\.xml$" );

    /**
     * Pattern to filter JAR entries for text files that may contain a version.
     */
    private static final Pattern VERSION_FILTER = Pattern.compile( "[Vv][Ee][Rr][Ss][Ii][Oo][Nn]" );

    /**
     * The associated JAR file.
     */
    private final JarFile jarFile;

    /**
     * Contains information about the data collected so far.
     */
    private final JarData jarData;

    /**
     * Constructor. Opens the JAR file, so should be matched by a call to {@link #closeQuietly()}.
     *
     * @param file the JAR file to open
     * @throws java.io.IOException if there is a problem opening the JAR file, or reading the manifest. The JAR file
     *             will be closed if this occurs.
     */
    public JarAnalyzer( File file )
        throws IOException
    {
        try
        {
            this.jarFile = new JarFile( file );
        }
        catch ( ZipException e )
        {
            ZipException ioe = new ZipException( "Failed to open file " + file + " : " + e.getMessage() );
            ioe.initCause( e );
            throw ioe;
        }

        // Obtain entries list.
        List<JarEntry> entries = Collections.list( jarFile.entries() );

        // Sorting of list is done by name to ensure a bytecode hash is always consistent.
        Collections.sort( entries, new Comparator<JarEntry>()
        {
            public int compare( JarEntry entry1, JarEntry entry2 )
            {
                return entry1.getName().compareTo( entry2.getName() );
            }
        } );

        Manifest manifest;
        try
        {
            manifest = jarFile.getManifest();
        }
        catch ( IOException e )
        {
            closeQuietly();
            throw e;
        }
        this.jarData = new JarData( file, manifest, entries );
    }

    /**
     * Get the data for an individual entry in the JAR. The caller should closeQuietly the input stream, and should not
     * retain the stream as the JAR file may be closed elsewhere.
     *
     * @param entry the JAR entry to read from
     * @return the input stream of the individual JAR entry.
     * @throws java.io.IOException if there is a problem opening the individual entry
     */
    public InputStream getEntryInputStream( JarEntry entry )
        throws IOException
    {
        return jarFile.getInputStream( entry );
    }

    /**
     * Close the associated JAR file, ignoring any errors that may occur.
     */
    public void closeQuietly()
    {
        try
        {
            jarFile.close();
        }
        catch ( IOException e )
        {
            // not much we can do about it but ignore it
        }
    }

    /**
     * Filter a list of JAR entries against the pattern.
     *
     * @param pattern the pattern to filter against
     * @return the list of files found, in {@link java.util.jar.JarEntry} elements
     */
    public List<JarEntry> filterEntries( Pattern pattern )
    {
        List<JarEntry> ret = new ArrayList<JarEntry>();

        for ( JarEntry entry : getEntries() )
        {
            Matcher mat = pattern.matcher( entry.getName() );
            if ( mat.find() )
            {
                ret.add( entry );
            }
        }
        return ret;
    }

    /**
     * Get all the classes in the JAR.
     *
     * @return the list of files found, in {@link java.util.jar.JarEntry} elements
     */
    public List<JarEntry> getClassEntries()
    {
        return filterEntries( CLASS_FILTER );
    }

    /**
     * Get all the Maven POM entries in the JAR.
     *
     * @return the list of files found, in {@link java.util.jar.JarEntry} elements
     */
    public List<JarEntry> getMavenPomEntries()
    {
        return filterEntries( MAVEN_POM_FILTER );
    }

    /**
     * Get all the version text files in the JAR.
     *
     * @return the list of files found, in {@link java.util.jar.JarEntry} elements
     */
    public List<JarEntry> getVersionEntries()
    {
        return filterEntries( VERSION_FILTER );
    }

    /**
     * Get all the contained files in the JAR.
     *
     * @return the list of files found, in {@link java.util.jar.JarEntry} elements
     */
    public List<JarEntry> getEntries()
    {
        return jarData.getEntries();
    }

    /**
     * Get the file that was opened by this analyzer.
     *
     * @return the JAR file reference
     */
    public File getFile()
    {
        return jarData.getFile();
    }

    public JarData getJarData()
    {
        return jarData;
    }
}
