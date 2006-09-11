package org.apache.maven.user.acegi.acl;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import java.io.IOException;
import java.io.InputStream;

import org.acegisecurity.acl.basic.jdbc.JdbcExtendedDaoImpl;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.sql.SqlExecMojo;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.IOUtil;

/**
 * Initialize the ACL system with some default values.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class AclInitializer
    extends AbstractLogEnabled
    implements Initializable
{
    public static final String ROLE = AclInitializer.class.getName();

    private JdbcExtendedDaoImpl dao;

    private SqlExecMojo sqlMojo;

    private String sqlClasspathResource;

    public void setDao( JdbcExtendedDaoImpl dao )
    {
        this.dao = dao;
    }

    public JdbcExtendedDaoImpl getDao()
    {
        return dao;
    }

    public void setSqlMojo( SqlExecMojo sqlMojo )
    {
        this.sqlMojo = sqlMojo;
    }

    public SqlExecMojo getSqlMojo()
    {
        return sqlMojo;
    }

    public void setSqlClasspathResource( String sqlClasspathResource )
    {
        this.sqlClasspathResource = sqlClasspathResource;
    }

    /**
     * Classpath resource that contains the SQL to be executed.
     * 
     * @return classpath path
     */
    public String getSqlClasspathResource()
    {
        return sqlClasspathResource;
    }

    public void initialize()
        throws InitializationException
    {

        InputStream is = null;
        String sql = null;
        try
        {
            is = this.getClass().getClassLoader().getResourceAsStream( getSqlClasspathResource() );
            if ( is == null )
            {
                throw new InitializationException( getSqlClasspathResource() + " does not exist in the classpath" );
            }
            sql = IOUtil.toString( is );
        }
        catch ( IOException e )
        {
            throw new InitializationException( "Unable to read sql file from classpath: " + getSqlClasspathResource(),
                                               e );
        }
        finally
        {
            if ( is != null )
            {
                try
                {
                    is.close();
                }
                catch ( IOException e )
                {
                    // nothing to do here
                }
            }
        }

        getSqlMojo().addText( sql );

        if ( getSqlMojo().getPassword() == null )
        {
            getSqlMojo().setPassword( "" );
        }

        try
        {
            getSqlMojo().execute();
        }
        catch ( MojoExecutionException e )
        {
            throw new InitializationException( e.getMessage(), e );
        }

        /* execute Spring initialization callback */
        getDao().afterPropertiesSet();

        /* poor check to see if this is the first time initializing the database */
        if ( getSqlMojo().getSuccessfulStatements() >= 2 )
        {
            /* tables were created, insert default values */
            getLogger().info( "Initializing ACL database" );

            insertDefaultData();
        }

    }

    /**
     * Callback to insert default data when tables are created
     */
    protected void insertDefaultData()
    {
    }
}
