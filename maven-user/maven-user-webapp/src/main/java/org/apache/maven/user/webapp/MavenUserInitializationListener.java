package org.apache.maven.user.webapp;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.opensymphony.webwork.plexus.PlexusLifecycleListener;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.util.IOUtil;
import org.jpox.SchemaTool;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * MavenUserInitializationListener 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class MavenUserInitializationListener
    implements ServletContextListener
{

    public void contextDestroyed( ServletContextEvent sce )
    {
        // Ignore
    }

    public void contextInitialized( ServletContextEvent sce )
    {

        try
        {
            ServletContext ctx = sce.getServletContext();
            PlexusContainer plexus = (PlexusContainer) ctx.getAttribute( PlexusLifecycleListener.KEY );

            JdoFactory jdoFactory = (JdoFactory) plexus.lookup( JdoFactory.ROLE );
            PersistenceManagerFactory pmf = jdoFactory.getPersistenceManagerFactory();

            // Create JDO Properties needed by SchemaTool
            Properties jdoProps = new Properties();

            jdoProps.setProperty( "javax.jdo.option.ConnectionDriverName", pmf.getConnectionDriverName() );
            jdoProps.setProperty( "javax.jdo.option.ConnectionURL", pmf.getConnectionURL() );
            jdoProps.setProperty( "javax.jdo.option.ConnectionUserName", pmf.getConnectionUserName() );
            jdoProps.setProperty( "javax.jdo.option.ConnectionPassword", "" );
            jdoProps.setProperty( "javax.jdo.PersistenceManagerFactoryClass", "org.jpox.PersistenceManagerFactoryImpl" );

            // Create jdo only properties temp file.
            File userTmp = new File( System.getProperty( "user.home" ), "tmp" );
            if ( !userTmp.exists() )
            {
                userTmp.mkdirs();
            }

            File propsFile = File.createTempFile( "jdo-", ".properties", userTmp );
            FileOutputStream fos = new FileOutputStream( propsFile );
            jdoProps.store( fos, "Created by maven-user-webapp" );
            IOUtil.close( fos );

            SchemaTool.createSchemaTables( new URL[] { getClass()
                .getResource( "/org/apache/maven/user/model/package.jdo" ) }, propsFile, true );

            propsFile.delete();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Unable to initialize maven-user.", e );
        }
    }

}
