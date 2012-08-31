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

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.apache.maven.shared.utils.StringUtils;

/**
 * Enforces that a specific version of a plugin is used throughout a build.
 *
 * @author Stephen Connolly
 * @since 03-Nov-2009 21:52:08
 */
@Component(role = AbstractMavenLifecycleParticipant.class)
public class PluginEnforcingMavenLifecycleParticipant
    extends AbstractMavenLifecycleParticipant
{

    @Requirement
    private Logger logger;

    public void afterProjectsRead( MavenSession session )
        throws MavenExecutionException
    {
        String config = session.getUserProperties().getProperty( "force.plugins" );
        if ( StringUtils.isEmpty( config ) )
        {
            logger.info( "Plugin Enforcer: Nothing to do (i.e. -Dforce.plugins undefined)" );
            return;
        }
        logger.info( StringUtils.repeat( "-", 72 ) );
        logger.info( "Plugin Enforcer" );
        logger.info( StringUtils.repeat( "-", 72 ) );
        for ( String forcePlugin : config.split( "," ) )
        {
            if ( StringUtils.isEmpty( forcePlugin ) )
            {
                continue;
            }
            String[] parts = forcePlugin.split( ":" );
            if ( parts.length < 2 || parts.length > 3 )
            {
                logger.warn( "\"" + forcePlugin + "\" does not match the format [groupId:]artifactId:version" );
                continue;
            }
            String groupId = parts.length == 3 ? parts[0] : "org.apache.maven.plugins";
            String artifactId = parts[parts.length - 2];
            String version = parts[parts.length - 1];
            if ( StringUtils.isEmpty( groupId ) || StringUtils.isEmpty( artifactId ) || StringUtils.isEmpty( version ) )
            {
                logger.warn( "\"" + forcePlugin + "\" does not match the format [groupId:]artifactId:version" );
                continue;
            }
            logger.info( "Forcing " + ArtifactUtils.versionlessKey( groupId, artifactId ) + " to " + version );
            logger.info( "" );
            for ( MavenProject project : session.getProjects() )
            {
                String name = StringUtils.isEmpty( project.getName() ) ?
                    ArtifactUtils.versionlessKey( project.getGroupId(), project.getArtifactId() ) + ":"
                        + project.getVersion() : project.getName();
                boolean projectIdentified = false;
                for ( Plugin plugin : project.getPluginManagement().getPlugins() )
                {
                    if ( StringUtils.equals( groupId, plugin.getGroupId() ) && StringUtils.equals( artifactId,
                                                                                                   plugin.getArtifactId() ) )
                    {
                        if ( !projectIdentified )
                        {
                            logger.info( "Project: " + name );
                            projectIdentified = true;
                        }
                        logger.info(
                            "Plugin Management: replacing version " + plugin.getVersion() + " with " + version );
                        plugin.setVersion( version );
                    }
                }
                for ( Plugin plugin : project.getBuildPlugins() )
                {
                    if ( StringUtils.equals( groupId, plugin.getGroupId() ) && StringUtils.equals( artifactId,
                                                                                                   plugin.getArtifactId() ) )
                    {
                        if ( !projectIdentified )
                        {
                            logger.info( "Project: " + name );
                            projectIdentified = true;
                        }
                        logger.info( "Build Plugins: replacing version " + plugin.getVersion() + " with " + version );
                        plugin.setVersion( version );
                    }
                }
                if ( projectIdentified )
                {
                    logger.info( "" );
                }
                else
                {
                    logger.warn( "No replacements Project: " + name );

                    logger.info( "" );
                }
            }
        }

    }
}
