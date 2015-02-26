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

import org.apache.maven.shared.utils.io.IOUtil;

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

    private Embedded3xLauncher( Object mavenCli, Method doMain )
    {
        this.mavenCli = mavenCli;
        this.doMain = doMain;
    }

    /**
     * Launches an embedded Maven 3.x instance from some Maven installation directory.
     */
    public static Embedded3xLauncher createFromMavenHome( String mavenHome, String classworldConf, List<URL> classpath )
        throws LauncherException
    {
        if ( mavenHome == null || mavenHome.length() <= 0 )
        {
            throw new LauncherException( "Invalid Maven home directory " + mavenHome );
        }

        System.setProperty( "maven.home", mavenHome );

        File config;
        if ( classworldConf != null )
        {
            config = new File( classworldConf );
        }
        else
        {
            config = new File( mavenHome, "bin/m2.conf" );
        }

        ClassLoader bootLoader = getBootLoader( mavenHome, classpath );

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( bootLoader );
        try
        {
            Class<?> launcherClass = bootLoader.loadClass( "org.codehaus.plexus.classworlds.launcher.Launcher" );

            Object launcher = launcherClass.newInstance();

            Method configure = launcherClass.getMethod( "configure", new Class[] { InputStream.class } );

            configure.invoke( launcher, new Object[] { new FileInputStream( config ) } );

            Method getWorld = launcherClass.getMethod( "getWorld", null );
            Object classWorld = getWorld.invoke( launcher, null );

            Method getMainClass = launcherClass.getMethod( "getMainClass", null );
            Class<?> cliClass = (Class<?>) getMainClass.invoke( launcher, null );

            Constructor<?> newMavenCli = cliClass.getConstructor( new Class[] { classWorld.getClass() } );
            Object mavenCli = newMavenCli.newInstance( new Object[] { classWorld } );

            Class<?>[] parameterTypes = { String[].class, String.class, PrintStream.class, PrintStream.class };
            Method doMain = cliClass.getMethod( "doMain", parameterTypes );

            return new Embedded3xLauncher( mavenCli, doMain );
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

    /**
     * Launches an embedded Maven 3.x instance from the current class path, i.e. the Maven 3.x dependencies are assumed
     * to be present on the class path.
     */
    public static Embedded3xLauncher createFromClasspath()
        throws LauncherException
    {
        ClassLoader coreLoader = Thread.currentThread().getContextClassLoader();

        try
        {
            Class<?> cliClass = coreLoader.loadClass( "org.apache.maven.cli.MavenCli" );

            Object mavenCli = cliClass.newInstance();

            Class<?>[] parameterTypes = { String[].class, String.class, PrintStream.class, PrintStream.class };
            Method doMain = cliClass.getMethod( "doMain", parameterTypes );

            return new Embedded3xLauncher( mavenCli, doMain );
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

    private static ClassLoader getBootLoader( String mavenHome, List<URL> classpath )
    {
        List<URL> urls = classpath;

        if ( urls == null )
        {
            urls = new ArrayList<URL>();

            File bootDir = new File( mavenHome, "boot" );
            addUrls( urls, bootDir );
        }

        if ( urls.isEmpty() )
        {
            throw new IllegalArgumentException( "Invalid Maven home directory " + mavenHome );
        }

        URL[] ucp = (URL[]) urls.toArray( new URL[urls.size()] );

        return new URLClassLoader( ucp, ClassLoader.getSystemClassLoader().getParent() );
    }

    private static void addUrls( List<URL> urls, File directory )
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

    public int run( String[] cliArgs, Properties systemProperties, String workingDirectory, File logFile )
        throws IOException, LauncherException
    {
        PrintStream out = ( logFile != null ) ? new PrintStream( new FileOutputStream( logFile ) ) : System.out;
        try
        {
            Properties originalProperties = System.getProperties();
            System.setProperties( null );
            System.setProperty( "maven.home", originalProperties.getProperty( "maven.home", "" ) );
            System.setProperty( "user.dir", new File( workingDirectory ).getAbsolutePath() );

            for ( Object o : systemProperties.keySet() )
            {
                String key = (String) o;
                String value = systemProperties.getProperty( key );
                System.setProperty( key, value );
            }

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

    public String getMavenVersion()
        throws LauncherException
    {
        Properties props = new Properties();

        InputStream is =
            mavenCli.getClass().getResourceAsStream( "/META-INF/maven/org.apache.maven/maven-core/pom.properties" );
        if ( is != null )
        {
            try
            {
                props.load( is );
            }
            catch ( IOException e )
            {
                throw new LauncherException( "Failed to read Maven version", e );
            }
            finally
            {
                IOUtil.close( is );
            }
        }

        String version = props.getProperty( "version" );
        if ( version != null )
        {
            return version;
        }

        throw new LauncherException( "Could not determine embedded Maven version" );
    }

}
