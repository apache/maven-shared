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

import org.apache.maven.shared.jar.classes.JarClasses;
import org.apache.maven.shared.jar.classes.JarClassesAnalysis;
import org.apache.maven.shared.jar.identification.JarIdentification;
import org.apache.maven.shared.jar.identification.JarIdentificationAnalysis;
import org.apache.maven.shared.jar.util.JarEntryComparator;
import org.codehaus.plexus.digest.Digester;
import org.codehaus.plexus.digest.DigesterException;
import org.codehaus.plexus.digest.StreamingDigester;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A JarAnalyzer Toolbox for working with Jar Files.
 * <p/>
 * Use the {@link JarAnalyzerFactory#getJarAnalyzer(File)} to obtain a valid JarAnalyzer object.
 *
 * @plexus.component role="org.apache.maven.shared.jar.JarAnalyzer" instantiation-strategy="per-lookup"
 */
public class JarAnalyzer
    extends AbstractLogEnabled
{
    public static final String ROLE = JarAnalyzer.class.getName();

    private List entries;

    private JarFile jarfile;

    private File file;

    private JarClasses classes;

    private JarIdentification identification;

    private boolean isSealed;

    /**
     * @plexus.requirement
     * @noinspection UnusedDeclaration
     */
    private JarClassesAnalysis classesAnalyzer;

    /**
     * @plexus.requirement
     * @noinspection UnusedDeclaration
     */
    private JarIdentificationAnalysis taxonAnalyzer;

    protected void setFile( File file )
        throws JarAnalyzerException
    {
        if ( file == null )
        {
            throw new JarAnalyzerException( "file is null." );
        }

        init( file );
    }

    /**
     * Compute the HashCode for this JarAnalyzer File.
     *
     * @param digester the digester to use to calculate the hash
     * @return the hashcode, or null if not able to be computed.
     */
    public String computeFileHash( Digester digester )
    {
        try
        {
            return digester.calc( new File( getFilename() ) );
        }
        catch ( DigesterException e )
        {
            getLogger().warn( "Unable to calculate the hashcode.", e );
            return null;
        }
    }

    /**
     * Compute the HashCode for the Bytecode within this JarAnalyzer File.
     * <p/>
     * Useful to see thru a recompile, recompression, or timestamp change.
     *
     * @param digester the digester to use to calculate the hash
     * @return the hashcode, or null if not able to be computed.
     */
    public String computeBytecodeHash( StreamingDigester digester )
    {
        Iterator it = entries.iterator();

        try
        {
            digester.reset();
            while ( it.hasNext() )
            {
                JarEntry entry = (JarEntry) it.next();

                if ( entry.getName().endsWith( ".class" ) )
                {
                    // TODO: check if it needs to be closed!
                    InputStream is = jarfile.getInputStream( entry );

                    digester.update( is );
                }
            }
            return digester.calc();
        }
        catch ( DigesterException e )
        {
            getLogger().warn( "Unable to calculate the hashcode.", e );
            return null;
        }
        catch ( IOException e )
        {
            getLogger().warn( "Unable to calculate the hashcode.", e );
            return null;
        }

    }

    public InputStream getEntryInputStream( JarEntry entry )
    {
        try
        {
            return jarfile.getInputStream( entry );
        }
        catch ( IOException e )
        {
            getLogger().error( "Unable to get input stream for entry " + entry.getName() + ": " + e.getMessage() );
            return null;
        }
    }

    public File getFile()
    {
        return file;
    }

    public String getFilename()
    {
        return jarfile.getName();
    }

    public Manifest getManifest()
    {
        try
        {
            return jarfile.getManifest();
        }
        catch ( IOException e )
        {
            getLogger().error( "Unable to get manifest on " + this + ": " + e.getMessage() );
            return null;
        }
    }

    public List getNameRegexEntryList( String regex )
    {
        List ret = new ArrayList();

        Pattern pat = Pattern.compile( regex );

        Iterator it = entries.iterator();
        while ( it.hasNext() )
        {
            JarEntry entry = (JarEntry) it.next();

            Matcher mat = pat.matcher( entry.getName() );
            if ( mat.find() )
            {
                ret.add( entry );
            }
        }

        return ret;
    }

    private void init( File jfile )
        throws JarAnalyzerException
    {
        if ( !jfile.exists() )
        {
            throw new JarAnalyzerException( "File " + jfile.getAbsolutePath() + " does not exist." );
        }

        if ( !jfile.canRead() )
        {
            throw new JarAnalyzerException( "No read access to file " + jfile.getAbsolutePath() + "." );
        }

        try
        {
            this.jarfile = new JarFile( jfile );
            this.file = jfile;
        }
        catch ( IOException e )
        {
            throw new JarAnalyzerException( "Unable to open artifact " + jfile.getAbsolutePath(), e );
        }

        // Obtain entries list.

        entries = new ArrayList();
        Enumeration jarentries = jarfile.entries();
        while ( jarentries.hasMoreElements() )
        {
            JarEntry entry = (JarEntry) jarentries.nextElement();
            entries.add( entry );
        }

        // Sorting of list is done to ensure a proper Bytecode Hash.
        Collections.sort( entries, new JarEntryComparator() );

        Manifest manifest = getManifest();

        isSealed = false;

        if ( manifest != null )
        {
            String sval = manifest.getMainAttributes().getValue( Attributes.Name.SEALED );
            if ( StringUtils.isNotEmpty( sval ) )
            {
                isSealed = "true".equalsIgnoreCase( sval.trim() );
            }
        }
    }

    public String toString()
    {
        return "<JarAnalyzer:" + jarfile.getName() + ">";
    }

    public boolean isSealed()
    {
        return isSealed;
    }

    public void setSealed( boolean isSealed )
    {
        this.isSealed = isSealed;
    }

    public JarClasses getClasses()
    {
        if ( classes == null )
        {
            classesAnalyzer.analyze( this );
        }

        return classes;
    }

    public void setClasses( JarClasses classes )
    {
        this.classes = classes;
    }

    public JarIdentification getIdentification()
    {
        if ( identification == null )
        {
            taxonAnalyzer.analyze( this );
        }

        return identification;
    }

    public void setIdentification( JarIdentification taxon )
    {
        this.identification = taxon;
    }

    public List getEntries()
    {
        return entries;
    }
}
