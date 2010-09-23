package org.apache.maven.util.pluginenforcer;
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

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.logging.Logger;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 * Unit tests of {@link org.apache.maven.util.pluginenforcer.PluginEnforcingMavenLifecycleParticipant}.
 *
 * @author Stephen Connolly
 * @since Sep 23, 2010 3:30:12 PM
 */
public class PluginEnforcingMavenLifecycleParticipantTest
{

    private PluginEnforcingMavenLifecycleParticipant newInstance( Logger logger )
        throws NoSuchFieldException, IllegalAccessException
    {
        PluginEnforcingMavenLifecycleParticipant instance = new PluginEnforcingMavenLifecycleParticipant();
        final Field loggerField = PluginEnforcingMavenLifecycleParticipant.class.getDeclaredField( "logger" );
        loggerField.setAccessible( true );
        loggerField.set( instance, logger );
        return instance;
    }

    @Test
    public void nothingToDo()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Logger logger = mock( Logger.class );
        PluginEnforcingMavenLifecycleParticipant instance = newInstance( logger );
        when( session.getUserProperties() ).thenReturn( new Properties() );
        instance.afterProjectsRead( session );
        verify( logger ).info( contains( "Nothing to do" ) );
    }

    @Test
    public void malformedTooSmall()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Logger logger = mock( Logger.class );
        PluginEnforcingMavenLifecycleParticipant instance = newInstance( logger );
        final Properties properties = new Properties();
        properties.setProperty( "force.plugins", "true" );
        when( session.getUserProperties() ).thenReturn( properties );
        instance.afterProjectsRead( session );
        verify( logger, times(1) ).warn( contains( "does not match the format" ) );
    }

    @Test
    public void malformedTooBig()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Logger logger = mock( Logger.class );
        PluginEnforcingMavenLifecycleParticipant instance = newInstance( logger );
        final Properties properties = new Properties();
        properties.setProperty( "force.plugins", "g:a:v:c" );
        when( session.getUserProperties() ).thenReturn( properties );
        instance.afterProjectsRead( session );
        verify( logger, times(1) ).warn( contains( "does not match the format" ) );
    }

    @Test
    public void malformedEmpty()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Logger logger = mock( Logger.class );
        PluginEnforcingMavenLifecycleParticipant instance = newInstance( logger );
        final Properties properties = new Properties();
        properties.setProperty( "force.plugins", "g::v" );
        when( session.getUserProperties() ).thenReturn( properties );
        instance.afterProjectsRead( session );
        verify( logger, times( 1 ) ).warn( contains( "does not match the format" ) );
    }

    @Test
    public void parseSingle()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Logger logger = mock( Logger.class );
        PluginEnforcingMavenLifecycleParticipant instance = newInstance( logger );
        final Properties properties = new Properties();
        properties.setProperty( "force.plugins", "org.mytest:myplugin:1.0" );
        when( session.getUserProperties() ).thenReturn( properties );
        instance.afterProjectsRead( session );
        verify( logger, atLeastOnce() ).info( matches( "^(.(?!([fF]orcing)))*$" ) );
        verify( logger, times( 1 ) ).info( contains( "Forcing org.mytest:myplugin to 1.0" ) );
    }

    @Test
    public void parseMultiple()
        throws Exception
    {
        MavenSession session = mock( MavenSession.class );
        Logger logger = mock( Logger.class );
        PluginEnforcingMavenLifecycleParticipant instance = newInstance( logger );
        final Properties properties = new Properties();
        properties.setProperty( "force.plugins", "org.mytest:myplugin:1.0,org.mytest.some:foobar:3.0" );
        when( session.getUserProperties() ).thenReturn( properties );
        instance.afterProjectsRead( session );
        verify( logger, atLeastOnce() ).info( matches( "^(.(?!([fF]orcing)))*$" ) );
        verify( logger, times( 1 ) ).info( contains( "Forcing org.mytest:myplugin to 1.0" ) );
        verify( logger, times( 1 ) ).info( contains( "Forcing org.mytest.some:foobar to 3.0" ) );
    }


}
