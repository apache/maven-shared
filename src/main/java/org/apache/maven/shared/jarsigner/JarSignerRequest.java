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

import org.apache.maven.shared.utils.cli.javatool.JavaToolRequest;

import java.io.File;

/**
 * Specifies the common parameters used to control a JarSigner tool invocation.
 *
 * @author tchemit <chemit@codelutin.com>
 * @version $Id$
 * @since 1.0
 */
public interface JarSignerRequest
    extends JavaToolRequest
{

    /**
     * Gets the value of the {@code verbose} field.
     *
     * @return the value of the {@code verbose} field.
     */
    boolean isVerbose();

    /**
     * Gets the value of the {@code keystore} field.
     *
     * @return the value of the {@code keystore} field.
     */
    String getKeystore();

    /**
     * Gets the value of the {@code alias} field.
     *
     * @return the value of the {@code alias} field.
     */
    String getAlias();

    /**
     * Gets the value of the {@code maxMemory} field.
     *
     * @return the value of the {@code maxMemory} field.
     */
    String getMaxMemory();

    /**
     * Gets the value of the {@code maxMemory} field.
     *
     * @return the value of the {@code maxMemory} field.
     */
    String[] getArguments();

    /**
     * Gets the value of the {@code workingDirectory} field.
     *
     * @return the value of the {@code workingDirectory} field.
     */
    File getWorkingDirectory();

    /**
     * Gets the value of the {@code archive} field.
     * <p/>
     * The archive field is in fact the file on which the jarsigner request will be executed.
     *
     * @return the value of the {@code archive} field.
     */
    File getArchive();

    /**
     * Gets the value of the command line tool parameter <pre>protected</pre>
     *
     * @return true iff the password must be given via a protected
     *         authentication path such as a dedicated PIN reader
     */
    boolean isProtectedAuthenticationPath();

    /**
     * Sets the new given value to the field {@code verbose} of the request.
     *
     * @param verbose the new value of the field {@code verbose}.
     */
    void setVerbose( boolean verbose );

    /**
     * Sets the new given value to the field {@code keystore} of the request.
     *
     * @param keystore the new value of the field {@code keystore}.
     */
    void setKeystore( String keystore);

    /**
     * Sets the new given value to the field {@code alias} of the request.
     *
     * @param alias the new value of the field {@code alias}.
     */
    void setAlias( String alias);

    /**
     * Sets the new given value to the field {@code maxMemory} of the request.
     *
     * @param maxMemory the new value of the field {@code maxMemory}.
     */
    void setMaxMemory( String maxMemory );

    /**
     * Sets the new given value to the field {@code arguments} of the request.
     *
     * @param arguments the new value of the field {@code arguments}.
     */
    void setArguments( String... arguments );

    /**
     * Sets the new given value to the field {@code workingDirectory} of the request.
     *
     * @param workingDirectory the new value of the field {@code workingDirectory}.
     */
    void setWorkingDirectory( File workingDirectory );

    /**
     * Sets the new given value to the field {@code archive} of the request.
     *
     * @param archive the new value of the field {@code archive}.
     */
    void setArchive( File archive );

    /**
     * Sets the value of the command line tool parameter <pre>protected</pre>
     *
     * @param protectedAuthenticationPath iff the password must be given via a protected
     *                                    authentication path such as a dedicated PIN reader
     */
    void setProtectedAuthenticationPath( boolean protectedAuthenticationPath );

}
