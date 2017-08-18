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

import org.apache.maven.shared.utils.StringUtils;
import org.apache.maven.shared.utils.cli.Arg;
import org.apache.maven.shared.utils.cli.Commandline;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.File;
import java.io.IOException;

/**
 * To build the command line for a given {@link JarSignerRequest}.
 *
 * @author tchemit <chemit@codelutin.com>
 * @version $Id$
 * @since 1.0
 */
public class JarSignerCommandLineBuilder
{
    private static final Logger DEFAULT_LOGGER = new ConsoleLogger( 0, JarSignerCommandLineBuilder.class.getName() );

    private Logger logger = DEFAULT_LOGGER;

    private String jarSignerFile;

    public Commandline build( JarSignerRequest request )
        throws CommandLineConfigurationException
    {
        try
        {
            checkRequiredState();
        }
        catch ( IOException e )
        {
            throw new CommandLineConfigurationException( e.getMessage(), e );
        }

        Commandline cli = new Commandline();

        cli.setExecutable( jarSignerFile );

        cli.setWorkingDirectory( request.getWorkingDirectory() );

        if ( request.isVerbose() )
        {
            cli.createArg().setValue( "-verbose" );
        }

        String keystore = request.getKeystore();
        if ( !StringUtils.isEmpty( keystore ) )
        {
            cli.createArg().setValue( "-keystore" );
            cli.createArg().setValue( keystore );
        }

        String storepass = request.getStorepass();
        if ( !StringUtils.isEmpty( storepass ) )
        {
            cli.createArg().setValue( "-storepass" );
            Arg arg = cli.createArg();
            arg.setValue( storepass );
            arg.setMask( true );
        }

        String storetype = request.getStoretype();
        if ( !StringUtils.isEmpty( storetype ) )
        {
            cli.createArg().setValue( "-storetype" );
            cli.createArg().setValue( storetype );
        }

        String providerName = request.getProviderName();
        if ( !StringUtils.isEmpty( providerName ) )
        {
            cli.createArg().setValue( "-providerName" );
            cli.createArg().setValue( providerName );
        }

        String providerClass = request.getProviderClass();
        if ( !StringUtils.isEmpty( providerClass ) )
        {
            cli.createArg().setValue( "-providerClass" );
            cli.createArg().setValue( providerClass );
        }

        String providerArg = request.getProviderArg();
        if ( !StringUtils.isEmpty( providerArg ) )
        {
            cli.createArg().setValue( "-providerArg" );
            cli.createArg().setValue( providerArg );
        }

        if ( request.isProtectedAuthenticationPath() )
        {
            cli.createArg().setValue( "-protected" );
        }

        String maxMemory = request.getMaxMemory();
        if ( StringUtils.isNotEmpty( maxMemory ) )
        {
            cli.createArg().setValue( "-J-Xmx" + maxMemory );
        }

        String[] arguments = request.getArguments();
        if ( arguments != null )
        {
            cli.addArguments( arguments );
        }

        if ( request instanceof JarSignerSignRequest )
        {
            build( (JarSignerSignRequest) request, cli );
        }

        if ( request instanceof JarSignerVerifyRequest )
        {
            build( (JarSignerVerifyRequest) request, cli );
        }

        cli.createArg().setFile( request.getArchive() );

        String alias = request.getAlias();
        if ( !StringUtils.isEmpty( alias ) )
        {
            cli.createArg().setValue( alias );
        }

        return cli;
    }

    public void setLogger( Logger logger )
    {
        this.logger = logger;
    }

    public void setJarSignerFile( String jarSignerFile )
    {
        this.jarSignerFile = jarSignerFile;
    }

    protected void checkRequiredState()
        throws IOException
    {
        if ( logger == null )
        {
            throw new IllegalStateException( "A logger instance is required." );
        }

        if ( jarSignerFile == null )
        {
            throw new IllegalStateException( "A jarSigner file is required." );
        }
    }

    protected void build( JarSignerSignRequest request, Commandline cli )
    {

        String keypass = request.getKeypass();
        if ( !StringUtils.isEmpty( keypass ) )
        {
            cli.createArg().setValue( "-keypass" );
            Arg arg = cli.createArg();
            arg.setValue( keypass );
            arg.setMask( true );
        }

        String sigfile = request.getSigfile();
        if ( !StringUtils.isEmpty( sigfile ) )
        {
            cli.createArg().setValue( "-sigfile" );
            cli.createArg().setValue( sigfile );
        }

        String tsaLocation = request.getTsaLocation();
        if ( StringUtils.isNotBlank( tsaLocation ) )
        {
            cli.createArg().setValue( "-tsa" );
            cli.createArg().setValue( tsaLocation );
        }

        String tsaAlias = request.getTsaAlias();
        if ( StringUtils.isNotBlank( tsaAlias ) )
        {
            cli.createArg().setValue( "-tsacert" );
            cli.createArg().setValue( tsaAlias );
        }

        File signedjar = request.getSignedjar();
        if ( signedjar != null )
        {
            cli.createArg().setValue( "-signedjar" );
            cli.createArg().setValue( signedjar.getAbsolutePath() );
        }
        
        final File certchain = request.getCertchain();
        if ( certchain != null )
        {
            cli.createArg().setValue( "-certchain" );
            cli.createArg().setValue( certchain.getAbsolutePath() );
        }
    }

    protected void build( JarSignerVerifyRequest request, Commandline cli )
        throws CommandLineConfigurationException
    {
        cli.createArg( true ).setValue( "-verify" );

        if ( request.isCerts() )
        {
            cli.createArg().setValue( "-certs" );
        }
    }
}
