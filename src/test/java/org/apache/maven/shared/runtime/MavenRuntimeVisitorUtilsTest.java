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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

/**
 * Tests <code>MavenRuntimeVisitorUtils</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see MavenRuntimeVisitorUtils
 */
public class MavenRuntimeVisitorUtilsTest extends TestCase
{
    // fields -----------------------------------------------------------------

    private IMocksControl mockVisitorControl;

    private MavenRuntimeVisitor mockVisitor;

    // TestCase methods -------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
        mockVisitorControl = EasyMock.createStrictControl();
        mockVisitor = (MavenRuntimeVisitor) mockVisitorControl.createMock( MavenRuntimeVisitor.class );

        mockVisitorControl.replay();
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception
    {
        mockVisitorControl.verify();
    }

    // tests ------------------------------------------------------------------

    public void testWithEmptyJar() throws IOException, MavenRuntimeException
    {
        accept( createTempFile( "file", ".jar" ) );
    }

    public void testWithUnknownFileExtension() throws IOException, MavenRuntimeException
    {
        accept( createTempFile( "file", ".unknown" ) );
    }

    // private methods -------------------------------------------------------

    private URL createTempFile( String prefix, String suffix ) throws IOException
    {
        File file = File.createTempFile( prefix, suffix );
        file.deleteOnExit();

        return file.toURI().toURL();
    }

    private void accept( URL url ) throws MavenRuntimeException
    {
        accept( new URL[] { url } );
    }

    private void accept( URL[] urls ) throws MavenRuntimeException
    {
        ClassLoader classLoader = new URLClassLoader( urls, null );

        MavenRuntimeVisitorUtils.accept( classLoader, mockVisitor );
    }
}
