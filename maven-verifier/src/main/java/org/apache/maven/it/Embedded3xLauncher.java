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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Launches an embedded Maven 3.x instance from some Maven installation directory.
 * 
 * @author Benjamin Bentmann
 */
class Embedded3xLauncher
    implements MavenLauncher
{

    private final Object mavenCli;

    private final Method doMain;

    public Embedded3xLauncher( String mavenHome )
        throws LauncherException
    {
        if ( mavenHome == null || mavenHome.length() <= 0 )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome );
        }

        System.setProperty( "maven.home", mavenHome );

        File config = new File( mavenHome, "bin/m2.conf" );

        ClassLoader bootLoader = getBootLoader( mavenHome );

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( bootLoader );
        try
        {
            Class launcherClass = bootLoader.loadClass( "org.codehaus.plexus.classworlds.launcher.Launcher" );

            Object launcher = launcherClass.newInstance();

            Method configure = launcherClass.getMethod( "configure", new Class[] { InputStream.class } );

            configure.invoke( launcher, new Object[] { new FileInputStream( config ) } );

            Method getWorld = launcherClass.getMethod( "getWorld", null );
            Object classWorld = getWorld.invoke( launcher, null );

            Method getMainClass = launcherClass.getMethod( "getMainClass", null );
            Class cliClass = (Class) getMainClass.invoke( launcher, null );

            Constructor newMavenCli = cliClass.getConstructor( new Class[] { classWorld.getClass() } );
            mavenCli = newMavenCli.newInstance( new Object[] { classWorld } );

            Class[] parameterTypes = { String[].class, String.class, PrintStream.class, PrintStream.class };
            doMain = cliClass.getMethod( "doMain", parameterTypes );
        }
        catch ( ClassNotFoundException e )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome, e );
        }
        catch ( InstantiationException e )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome, e );
        }
        catch ( IllegalAccessException e )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome, e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome, e );
        }
        catch ( InvocationTargetException e )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome, e );
        }
        catch ( IOException e )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome, e );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldClassLoader );
        }
    }

    private static ClassLoader getBootLoader( String mavenHome )
    {
        File bootDir = new File( mavenHome, "boot" );

        List urls = new ArrayList();

        addUrls( urls, bootDir );

        if ( urls.isEmpty() )
        {
            throw new IllegalArgumentException( "Invalid Maven home directory " + mavenHome );
        }

        URL[] ucp = (URL[]) urls.toArray( new URL[urls.size()] );

        return new URLClassLoader( ucp, ClassLoader.getSystemClassLoader().getParent() );
    }

    private static void addUrls( List urls, File directory )
    {
        File[] jars = directory.listFiles();

        if ( jars != null )
        {
            for ( int i = 0; i < jars.length; i++ )
            {
                File jar = jars[i];

                if ( jar.getName().endsWith( ".jar" ) )
                {
                    try
                    {
                        urls.add( jar.toURI().toURL() );
                    }
                    catch ( MalformedURLException e )
                    {
                        throw (RuntimeException) new IllegalStateException().initCause( e );
                    }
                }
            }
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

            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader( mavenCli.getClass().getClassLoader() );
            try
            {
                Object result = doMain.invoke( mavenCli, new Object[] { cliArgs, workingDirectory, out, out } );

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
