package org.apache.maven.shared.jarsigner;

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

import org.apache.maven.shared.utils.cli.StreamConsumer;

import java.io.File;

/**
 * Specifies the commons parameters used to control a jar signer invocation.
 *
 * @author tchemit <chemit@codelutin.com>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractJarSignerRequest
    implements JarSignerRequest
{
    /**
     * See <a href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/jarsigner.html#Options">options</a>.
     */
    private boolean verbose;

    /**
     * The maximum memory available to the JAR signer, e.g. <code>256M</code>. See <a
     * href="http://docs.oracle.com/javase/6/docs/technotes/tools/windows/java.html#Xms">-Xmx</a> for more details.
     */
    private String maxMemory;

    /**
     * List of additional arguments to append to the jarsigner command line.
     */
    private String[] arguments;

    /**
     * Location of the working directory.
     */
    private File workingDirectory;

    /**
     * Archive to treat.
     */
    private File archive;

    /**
     * Optional system out stream consumer used by the commandline execution.
     */
    private StreamConsumer systemOutStreamConsumer;

    /**
     * Optional system error stream consumer used by the commandline execution.
     */
    private StreamConsumer systemErrorStreamConsumer;

    /**
     * {@inheritDoc}
     */
    public boolean isVerbose()
    {
        return verbose;
    }

    /**
     * {@inheritDoc}
     */
    public String getMaxMemory()
    {
        return maxMemory;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getArguments()
    {
        return arguments;
    }

    /**
     * {@inheritDoc}
     */
    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public File getArchive()
    {
        return archive;
    }

    /**
     * {@inheritDoc}
     */
    public StreamConsumer getSystemOutStreamConsumer()
    {
        return systemOutStreamConsumer;
    }

    /**
     * {@inheritDoc}
     */
    public StreamConsumer getSystemErrorStreamConsumer()
    {
        return systemErrorStreamConsumer;
    }

    /**
     * {@inheritDoc}
     */
    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxMemory( String maxMemory )
    {
        this.maxMemory = maxMemory;
    }

    /**
     * {@inheritDoc}
     */
    public void setArguments( String... arguments )
    {
        this.arguments = arguments;
    }

    /**
     * {@inheritDoc}
     */
    public void setWorkingDirectory( File workingDirectory )
    {
        this.workingDirectory = workingDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public void setArchive( File archive )
    {
        this.archive = archive;
    }

    /**
     * {@inheritDoc}
     */
    public void setSystemOutStreamConsumer( StreamConsumer systemOutStreamConsumer )
    {
        this.systemOutStreamConsumer = systemOutStreamConsumer;
    }

    /**
     * {@inheritDoc}
     */
    public void setSystemErrorStreamConsumer( StreamConsumer systemErrorStreamConsumer )
    {
        this.systemErrorStreamConsumer = systemErrorStreamConsumer;
    }
}
