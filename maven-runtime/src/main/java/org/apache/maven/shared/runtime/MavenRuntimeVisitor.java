package org.apache.maven.shared.runtime;

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

import java.net.URL;

/**
 * Defines a visitor for the Maven runtime environment.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public interface MavenRuntimeVisitor
{
    /**
     * Visits the specified Maven project properties.
     * 
     * @param url
     *            a URL to the properties
     * @throws MavenRuntimeException
     *             if an error occurs visiting the stream
     */
    void visitProjectProperties( URL url ) throws MavenRuntimeException;

    /**
     * Visits the specified Maven project XML.
     * 
     * @param url
     *            a URL to the XML
     * @throws MavenRuntimeException
     *             if an error occurs visiting the stream
     */
    void visitProjectXML( URL url ) throws MavenRuntimeException;
}
