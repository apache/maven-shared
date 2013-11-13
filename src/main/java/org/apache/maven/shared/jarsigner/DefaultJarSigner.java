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

import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.shared.utils.cli.StreamConsumer;
import org.apache.maven.shared.utils.cli.javatool.AbstractJavaTool;
import org.apache.maven.shared.utils.cli.javatool.JavaToolException;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Default implementation of component {@link JarSigner}.
 *
 * @author tchemit <chemit@codelutin.com>
 * @version $Id$
 * @since 1.0
 */
@Component( role = JarSigner.class, hint = "default" )
public class DefaultJarSigner
    extends AbstractJavaTool<JarSignerRequest, JarSignerResult>
    implements JarSigner
{

    public DefaultJarSigner()
    {
        super( "jarsigner" );
    }

    @Override
    protected JarSignerResult createResult()
    {
        return new DefaultJarSignerResult();
    }

    @Override
    protected Commandline createCommandLine( JarSignerRequest request, String javaToolFile )
        throws JavaToolException
    {
        JarSignerCommandLineBuilder cliBuilder = new JarSignerCommandLineBuilder();
        cliBuilder.setLogger( getLogger() );
        cliBuilder.setJarSignerFile( javaToolFile );
        try
        {
            Commandline cli = cliBuilder.build( request );
            if ( request.isVerbose() )
            {
                getLogger().info( cli.toString() );
            }
            else
            {
                getLogger().debug( cli.toString() );
            }
            return cli;
        }
        catch ( CommandLineConfigurationException e )
        {
            throw new JavaToolException( "Error configuring command-line. Reason: " + e.getMessage(), e );
        }
    }

    protected StreamConsumer createSystemOutStreamConsumer( JarSignerRequest request )
    {
        StreamConsumer systemOut = request.getSystemOutStreamConsumer();

        if ( systemOut == null )
        {

            final boolean verbose = request.isVerbose();

            systemOut = new StreamConsumer()
            {

                /**
                 * {@inheritDoc}
                 */
                public void consumeLine( final String line )
                {
                    if ( verbose )
                    {
                        getLogger().info( line );
                    }
                    else
                    {
                        getLogger().debug( line );
                    }
                }

            };
        }
        return systemOut;
    }

}
