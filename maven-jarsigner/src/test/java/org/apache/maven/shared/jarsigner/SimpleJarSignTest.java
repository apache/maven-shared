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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.shared.utils.cli.javatool.JavaToolResult;
import org.codehaus.plexus.PlexusTestCase;
import org.apache.maven.shared.utils.io.FileUtils;

import java.io.File;

/**
 * @author Olivier Lamy
 */
public class SimpleJarSignTest
    extends PlexusTestCase
{

    public void testSimpleSign()
        throws Exception
    {
        File file = new File( "src/test/simple.jar" );
        File target = new File( "target/simple.jar" );

        if ( target.exists() )
        {
            FileUtils.forceDelete( target );
        }

        FileUtils.copyFile( file, target );

        JarSignerSignRequest jarSignerRequest = buildJarSignerRequest( target );

        JarSigner jarSigner = (JarSigner) lookup( JarSigner.class.getName() );

        JavaToolResult jarSignerResult = jarSigner.execute( jarSignerRequest );
        assertEquals( "not exit code 0 " + jarSignerResult.getExecutionException(), 0, jarSignerResult.getExitCode() );


    }

    public void testSimpleSignAdVerify()
        throws Exception
    {
        File file = new File( "src/test/simple.jar" );
        File target = new File( "target/simple.jar" );

        if ( target.exists() )
        {
            FileUtils.forceDelete( target );
        }

        FileUtils.copyFile( file, target );

        JarSignerSignRequest jarSignerRequest = buildJarSignerRequest( target );

        JarSigner jarSigner = (JarSigner) lookup( JarSigner.class.getName() );

        JavaToolResult jarSignerResult = jarSigner.execute( jarSignerRequest );

        assertEquals( "not exit code 0 " + jarSignerResult.getExecutionException(), 0, jarSignerResult.getExitCode() );

        JarSignerVerifyRequest request = new JarSignerVerifyRequest();
        request.setCerts( true );
        request.setVerbose( true );
        request.setArchive( new File( "target/ssimple.jar" ) );
        request.setKeystore( "src/test/keystore" );
        request.setAlias( "foo_alias" );

        jarSignerResult = jarSigner.execute( request );
        assertEquals( "not exit code 0 " + jarSignerResult.getExecutionException(), 0, jarSignerResult.getExitCode() );

    }

    protected JarSignerSignRequest buildJarSignerRequest( File target )
    {
        JarSignerSignRequest jarSignerRequest = new JarSignerSignRequest();
        jarSignerRequest.setArchive( target );
        jarSignerRequest.setKeystore( "src/test/keystore" );
        jarSignerRequest.setVerbose( true );
        jarSignerRequest.setAlias( "foo_alias" );
        jarSignerRequest.setKeypass( "key-passwd" );
        jarSignerRequest.setStorepass( "changeit" );
        jarSignerRequest.setSignedjar( new File( "target/ssimple.jar" ) );
        return jarSignerRequest;
    }
}
