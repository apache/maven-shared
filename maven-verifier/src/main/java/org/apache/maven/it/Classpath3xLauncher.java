package org.apache.maven.it;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Launches an embedded Maven 3.x instance from the current class path, i.e. the Maven 3.x dependencies are assumed to
 * be present on the class path.
 *
 * @author Benjamin Bentmann
 */
class Classpath3xLauncher
    implements MavenLauncher
{

    private final Object mavenCli;

    private final Method doMain;

    public Classpath3xLauncher()
        throws LauncherException
    {
        ClassLoader coreLoader = Thread.currentThread().getContextClassLoader();

        try
        {
            Class cliClass = coreLoader.loadClass( "org.apache.maven.cli.MavenCli" );

            mavenCli = cliClass.newInstance();

            Class[] parameterTypes = { String[].class, String.class, PrintStream.class, PrintStream.class };
            doMain = cliClass.getMethod( "doMain", parameterTypes );
        }
        catch ( ClassNotFoundException e )
        {
            throw new LauncherException( e.getMessage(), e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new LauncherException( e.getMessage(), e );
        }
        catch ( InstantiationException e )
        {
            throw new LauncherException( e.getMessage(), e );
        }
        catch ( IllegalAccessException e )
        {
            throw new LauncherException( e.getMessage(), e );
        }
    }

    public int run( String[] cliArgs, String workingDirectory, File logFile )
        throws IOException, LauncherException
    {
        PrintStream out = ( logFile != null ) ? new PrintStream( new FileOutputStream( logFile ) ) : System.out;
        try
        {
            Properties originalProperties = System.getProperties();
            System.setProperties( null );
            System.setProperty( "maven.home", originalProperties.getProperty( "maven.home", "" ) );
            System.setProperty( "user.dir", new File( workingDirectory ).getAbsolutePath() );

            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader( mavenCli.getClass().getClassLoader() );
            try
            {
                Object result = doMain.invoke( mavenCli, new Object[]{ cliArgs, workingDirectory, out, out } );

                return ( (Number) result ).intValue();
            }
            finally
            {
                Thread.currentThread().setContextClassLoader( originalClassLoader );

                System.setProperties( originalProperties );
            }
        }
        catch ( IllegalAccessException e )
        {
            throw new LauncherException( "Failed to run Maven: " + e.getMessage(), e );
        }
        catch ( InvocationTargetException e )
        {
            throw new LauncherException( "Failed to run Maven: " + e.getMessage(), e );
        }
        finally
        {
            if ( logFile != null )
            {
                out.close();
            }
        }
    }

}
