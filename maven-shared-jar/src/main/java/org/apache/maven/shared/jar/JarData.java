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
import org.apache.maven.shared.jar.identification.JarIdentification;
import org.apache.maven.shared.utils.StringUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;

/**
 * Class that contains details of a single JAR file and it's entries.
 */
public final class JarData
{
    /**
     * The JAR file.
     */
    private final File file;

    /**
     * Whether the JAR file is sealed.
     */
    private final boolean sealed;

    /**
     * The hashcode for the entire file's contents.
     */
    private String fileHash;

    /**
     * The hashcode for the file's class data contents.
     */
    private String bytecodeHash;

    /**
     * The JAR's manifest.
     */
    private final Manifest manifest;

    /**
     * Information about the JAR's classes.
     */
    private JarClasses jarClasses;

    /**
     * The JAR entries.
     */
    private final List<JarEntry> entries;

    /**
     * Information about the JAR's identifying features.
     */
    private JarIdentification jarIdentification;

    /**
     * Constructor.
     *
     * @param file     the JAR file
     * @param manifest the JAR manifest
     * @param entries  the JAR entries
     */
    public JarData( File file, Manifest manifest, List<JarEntry> entries )
    {
        this.file = file;

        this.manifest = manifest;

        this.entries = Collections.unmodifiableList( entries );

        boolean sealed = false;
        if ( this.manifest != null )
        {
            String sval = this.manifest.getMainAttributes().getValue( Attributes.Name.SEALED );
            if ( StringUtils.isNotEmpty( sval ) )
            {
                sealed = "true".equalsIgnoreCase( sval.trim() );
            }
        }
        this.sealed = sealed;
    }

    public List<JarEntry> getEntries()
    {
        return entries;
    }

    public Manifest getManifest()
    {
        return manifest;
    }

    public File getFile()
    {
        return file;
    }

    public boolean isSealed()
    {
        return sealed;
    }

    public void setFileHash( String fileHash )
    {
        this.fileHash = fileHash;
    }

    public String getFileHash()
    {
        return fileHash;
    }

    public void setBytecodeHash( String bytecodeHash )
    {
        this.bytecodeHash = bytecodeHash;
    }

    public String getBytecodeHash()
    {
        return bytecodeHash;
    }

    public boolean isDebugPresent()
    {
        return jarClasses.isDebugPresent();
    }

    public void setJarClasses( JarClasses jarClasses )
    {
        this.jarClasses = jarClasses;
    }

    public int getNumEntries()
    {
        return entries.size();
    }

    public int getNumClasses()
    {
        return jarClasses.getClassNames().size();
    }

    public int getNumPackages()
    {
        return jarClasses.getPackages().size();
    }

    public String getJdkRevision()
    {
        return jarClasses.getJdkRevision();
    }

    public void setJarIdentification( JarIdentification jarIdentification )
    {
        this.jarIdentification = jarIdentification;
    }

    public JarIdentification getJarIdentification()
    {
        return jarIdentification;
    }

    public JarClasses getJarClasses()
    {
        return jarClasses;
    }
}
